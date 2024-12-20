/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.network

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.database.sqlite.cim.CimDatabaseReader
import com.zepben.evolve.database.sqlite.cim.metadata.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.cim.tables.tableCimVersion
import com.zepben.evolve.database.sqlite.cim.upgrade.UpgradeRunner
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.services.common.extensions.nameAndMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import java.sql.Connection
import java.util.*

/**
 * A class for reading the [NetworkService] objects and [MetadataCollection] from our network database.
 *
 * NOTE: The network database must be loaded first if you are using a pre-split database you wish to upgrade as it was the only database at the time
 *   and will create the other databases as part of the upgrade. This warning can be removed once we set a new minimum version of the database and
 *   remove the split database logic - Check [UpgradeRunner] to see if this is still required.
 *
 * @param connection The connection to the database.
 * @param service The [NetworkService] to populate with CIM objects from the database.
 * @param databaseDescription The description of the database for logging (e.g. filename).
 */
class NetworkDatabaseReader internal constructor(
    connection: Connection,
    override val service: NetworkService,
    databaseDescription: String,
    private val inferPhases: Boolean,
    metadataReader: MetadataCollectionReader,
    serviceReader: NetworkServiceReader,
    tableVersion: TableVersion = tableCimVersion,
    private val setFeederDirection: SetDirection = Tracing.setDirection(),
    private val setPhases: SetPhases = Tracing.setPhases(),
    private val phaseInferrer: PhaseInferrer = Tracing.phaseInferrer(),
    private val assignToFeeders: AssignToFeeders = Tracing.assignEquipmentToFeeders(),
    private val assignToLvFeeders: AssignToLvFeeders = Tracing.assignEquipmentToLvFeeders(),
) : CimDatabaseReader(connection, metadataReader, serviceReader, service, databaseDescription, tableVersion) {

    @JvmOverloads
    constructor(
        connection: Connection,
        service: NetworkService,
        databaseDescription: String,
        inferPhases: Boolean = true
    ) : this(
        connection,
        service,
        databaseDescription,
        inferPhases,
        MetadataCollectionReader(service, NetworkDatabaseTables(), connection),
        NetworkServiceReader(service, NetworkDatabaseTables(), connection),
    )

    override fun postLoad(): Boolean =
        super.postLoad().also {
            logger.info("Applying feeder direction to network...")
            setFeederDirection.run(service, NetworkStateOperators.NORMAL)
            setFeederDirection.run(service, NetworkStateOperators.CURRENT)
            logger.info("Feeder direction applied to network.")

            logger.info("Applying phases to network...")
            setPhases.run(service, NetworkStateOperators.NORMAL)
            setPhases.run(service, NetworkStateOperators.CURRENT)
            if (inferPhases) {
                logInferredPhases(
                    phaseInferrer.run(service, NetworkStateOperators.NORMAL),
                    phaseInferrer.run(service, NetworkStateOperators.CURRENT)
                )
            }
            logger.info("Phasing applied to network.")

            logger.info("Assigning equipment to feeders...")
            assignToFeeders.run(service, NetworkStateOperators.NORMAL)
            assignToFeeders.run(service, NetworkStateOperators.CURRENT)
            logger.info("Equipment assigned to feeders.")

            logger.info("Assigning equipment to LV feeders...")
            assignToLvFeeders.run(service, NetworkStateOperators.NORMAL)
            assignToLvFeeders.run(service, NetworkStateOperators.CURRENT)
            logger.info("Equipment assigned to LV feeders.")

            logger.info("Validating that each equipment is assigned to a container...")
            validateEquipmentContainers(service)
            logger.info("Equipment containers validated.")

            logger.info("Validating primary sources vs feeders...")
            validateSources(service)
            logger.info("Sources vs feeders validated.")
        }

    private fun logInferredPhases(
        normalInferredPhases: Collection<PhaseInferrer.InferredPhase>,
        currentInferredPhases: Collection<PhaseInferrer.InferredPhase>
    ) {
        val inferredPhases = normalInferredPhases.associateBy { it.conductingEquipment }.toMutableMap()
        currentInferredPhases.forEach {
            inferredPhases.merge(it.conductingEquipment, it) { left, right -> left.takeIf { left.suspect } ?: right }
        }

        inferredPhases.values.forEach { logger.warn("*** Action Required *** ${it.description()}") }
    }

    private fun validateEquipmentContainers(networkService: NetworkService) {
        val missingContainers = networkService.listOf<Equipment> { it.containers.isEmpty() }
        val countByClass = missingContainers.groupingBy { it.javaClass.simpleName }.eachCount()
        countByClass.forEach { (className, count) ->
            logger.warn("$count ${className}s were missing an equipment container.")
        }
        if (countByClass.isNotEmpty())
            logger.warn("A total of ${missingContainers.size} equipment had no associated equipment container. Debug logging will show more details.")
        missingContainers.forEach { equipment ->
            logger.debug("${equipment.typeNameAndMRID()} was not assigned to any equipment container.")
        }
    }

    private fun validateSources(networkService: NetworkService) {
        // We do not want to warn about sources attached directly to the feeder start point.
        val feederStartPoints = networkService
            .sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal?.conductingEquipment?.mRID }
            .toSet()

        val hasBeenAssignedToFeeder = { energySource: EnergySource ->
            energySource.isExternalGrid
                && energySource.isOnFeeder()
                && Collections.disjoint(feederStartPoints, NetworkService.connectedEquipment(energySource).mapNotNull { it.to?.mRID })
        }

        networkService.sequenceOf<EnergySource>()
            .filter(hasBeenAssignedToFeeder)
            .forEach { es ->
                logger.warn(
                    "External grid source ${es.nameAndMRID()} has been assigned to the following feeders: normal [${es.normalFeeders.joinToString { it.mRID }}], " +
                        "current [${es.currentFeeders.joinToString { it.mRID }}]"
                )
            }
    }

    private fun EnergySource.isOnFeeder(): Boolean =
        normalFeeders.isNotEmpty() || currentFeeders.isNotEmpty()

}
