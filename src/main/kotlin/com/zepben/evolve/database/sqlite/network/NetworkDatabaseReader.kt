/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.database.sqlite.common.DatabaseReader
import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.common.MetadataEntryReader
import com.zepben.evolve.database.sqlite.common.metadataDatabaseTables
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.extensions.nameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*


/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 * @property getPreparedStatement provider of prepared statements for the connection.
 * @property savedCommonMRIDs Note this doesn't work if it's not common across all Service based database writers
 */
class NetworkDatabaseReader(
    val networkService: NetworkService,
    metadataCollection: MetadataCollection,
    databaseFile: String,
    getConnection: (String) -> Connection = DriverManager::getConnection,
    getStatement: (Connection) -> Statement = Connection::createStatement,
    upgradeRunner: UpgradeRunner = UpgradeRunner(getConnection, getStatement),
    metadataCollectionReader: MetadataCollectionReader = MetadataCollectionReader(
        metadataDatabaseTables,
        MetadataEntryReader(metadataCollection)
    ) { getStatement(getConnection("jdbc:sqlite:$databaseFile")) },
    networkServiceReader: NetworkServiceReader = NetworkServiceReader(
        networkDatabaseTables,
        NetworkCIMReader(networkService)
    ) { getStatement(getConnection("jdbc:sqlite:$databaseFile")) },
    private val setDirection: SetDirection = Tracing.setDirection(),
    private val setPhases: SetPhases = Tracing.setPhases(),
    private val phaseInferrer: PhaseInferrer = Tracing.phaseInferrer(),
    private val assignToFeeders: AssignToFeeders = Tracing.assignEquipmentToFeeders(),
    private val assignToLvFeeders: AssignToLvFeeders = Tracing.assignEquipmentToLvFeeders()
) : DatabaseReader<NetworkServiceReader>(
    networkDatabaseTables,
    networkServiceReader,
    databaseFile,
    metadataCollectionReader,
    upgradeRunner,
) {

    override fun postLoad(): Boolean {
        logger.info("Applying feeder direction to network...")
        setDirection.run(networkService)
        logger.info("Feeder direction applied to network.")

        logger.info("Applying phases to network...")
        setPhases.run(networkService)
        phaseInferrer.run(networkService)
        logger.info("Phasing applied to network.")

        logger.info("Assigning equipment to feeders...")
        assignToFeeders.run(networkService)
        logger.info("Equipment assigned to feeders.")

        logger.info("Assigning equipment to LV feeders...")
        assignToLvFeeders.run(networkService)
        logger.info("Equipment assigned to LV feeders.")

        logger.info("Validating that each equipment is assigned to a container...")
        validateEquipmentContainers(networkService)
        logger.info("Equipment containers validated.")

        logger.info("Validating primary sources vs feeders...")
        validateSources(networkService)
        logger.info("Sources vs feeders validated.")

        return true
    }

    private fun validateEquipmentContainers(networkService: NetworkService) {
        networkService.sequenceOf<Equipment>()
            .filter { it.containers.isEmpty() }
            .forEach { equipment ->
                logger.warn(
                    "Equipment ${equipment.nameAndMRID()} was not assigned to any equipment container."
                )
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
                && Collections.disjoint(
                feederStartPoints,
                NetworkService.connectedEquipment(energySource)
                    .mapNotNull(ConnectivityResult::to)
                    .map(ConductingEquipment::mRID)
                    .toSet()
            )
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

    private fun EnergySource.isOnFeeder(): Boolean {
        return normalFeeders.isNotEmpty() || currentFeeders.isNotEmpty()
    }
}
