/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.Feeder
import com.zepben.cimbend.cim.iec61970.base.wires.EnergySource
import com.zepben.cimbend.common.extensions.nameAndMRID
import com.zepben.cimbend.common.meta.MetadataCollection
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.database.MissingTableConfigException
import com.zepben.cimbend.database.sqlite.readers.*
import com.zepben.cimbend.database.sqlite.upgrade.UpgradeRunner
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.tracing.ConnectivityResult
import com.zepben.cimbend.network.tracing.Tracing
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
    private val upgradeRunner: UpgradeRunner = UpgradeRunner()
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
            logger.error("Failed to connect to the database for saving: " + e.message, e)
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
        logger.info("Applying phases to network...")
        Tracing.setPhases().run(networkService)
        Tracing.phaseInferrer().run(networkService)
        logger.info("Phasing applied to network.")

        logger.info("Assigning equipment to feeders...")
        Tracing.assignEquipmentContainersToFeeders().run(networkService)
        logger.info("Equipment assigned to feeders.")

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
            (energySource.numPhases() > 0)
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
                    "Primary source ${es.nameAndMRID()} has been assigned to the following feeders: normal [${es.normalFeeders.joinToString { it.mRID }}], " +
                        "current [${es.currentFeeders.joinToString { it.mRID }}]"
                )
            }
    }

    private fun EnergySource.isOnFeeder(): Boolean {
        return normalFeeders.isNotEmpty() || currentFeeders.isNotEmpty()
    }
}
