/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.database.sqlite.readers.*
import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.extensions.nameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.util.*

/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 */
class DatabaseReader @JvmOverloads constructor(
    private val databaseFile: String,
    private val getConnection: (String) -> Connection = DriverManager::getConnection,
    private val getStatement: (Connection) -> Statement = Connection::createStatement,
    private val upgradeRunner: UpgradeRunner = UpgradeRunner(),
    private val setDirection: SetDirection = Tracing.setDirection(),
    private val setPhases: SetPhases = Tracing.setPhases(),
    private val phaseInferrer: PhaseInferrer = Tracing.phaseInferrer(),
    private val assignToFeeders: AssignToFeeders = Tracing.assignEquipmentToFeeders(),
    private val assignToLvFeeders: AssignToLvFeeders = Tracing.assignEquipmentToLvFeeders()
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"

    private lateinit var loadConnection: Connection

    private var hasBeenUsed: Boolean = false

    fun load(
        metadataCollection: MetadataCollection,
        networkService: NetworkService,
        diagramService: DiagramService,
        customerService: CustomerService
    ): Boolean {
        if (hasBeenUsed) {
            logger.error("You can only use the database reader once.")
            return false
        }
        hasBeenUsed = true

        val metadataReader = MetadataEntryReader(metadataCollection)
        val networkServiceReader = NetworkCIMReader(networkService)
        val diagramServiceReader = DiagramCIMReader(diagramService)
        val customerServiceReader = CustomerCIMReader(customerService)

        val databaseVersion = preLoad()
        if (databaseVersion == null) {
            closeConnection()
            return false
        }

        logger.info("Loading from database version v$databaseVersion")
        val status = try {
            MetadataCollectionReader { getStatement(loadConnection) }.load(metadataReader)
                && NetworkServiceReader { getStatement(loadConnection) }.load(networkServiceReader)
                && DiagramServiceReader { getStatement(loadConnection) }.load(diagramServiceReader)
                && CustomerServiceReader { getStatement(loadConnection) }.load(customerServiceReader)
        } catch (e: MissingTableConfigException) {
            logger.error("Unable to load database: " + e.message)
            closeConnection()
            return false
        }

        return status and postLoad(networkService)
    }

    private fun preLoad(): Int? {
        return try {
            upgradeRunner.connectAndUpgrade(databaseDescriptor, Paths.get(databaseFile))
                .also { loadConnection = it.connection }
                .version
        } catch (e: UpgradeRunner.UpgradeException) {
            logger.error("Failed to connect to the database for reading: " + e.message, e)
            closeConnection()
            null
        }
    }

    private fun closeConnection() {
        try {
            if (::loadConnection.isInitialized)
                loadConnection.close()
        } catch (e: SQLException) {
            logger.error("Failed to close connection to database: " + e.message)
        }
    }

    private fun postLoad(networkService: NetworkService): Boolean {
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

        logger.info("Validating primary sources vs feeders...")
        validateSources(networkService)
        logger.info("Sources vs feeders validated.")

        closeConnection()
        return true
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
