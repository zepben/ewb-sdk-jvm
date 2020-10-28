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
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.database.DuplicateMRIDException
import com.zepben.cimbend.database.MRIDLookupException
import com.zepben.cimbend.database.MissingTableConfigException
import com.zepben.cimbend.database.executeConfiguredQuery
import com.zepben.cimbend.database.sqlite.readers.CustomerServiceReader
import com.zepben.cimbend.database.sqlite.readers.DiagramServiceReader
import com.zepben.cimbend.database.sqlite.readers.NetworkServiceReader
import com.zepben.cimbend.database.sqlite.tables.SqliteTable
import com.zepben.cimbend.database.sqlite.tables.associations.*
import com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo.TableCableInfo
import com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo.TableOverheadWireInfo
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TableAssetOwners
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TablePoles
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TableStreetlights
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableLocations
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TablePositionPoints
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableTariffs
import com.zepben.cimbend.database.sqlite.tables.iec61968.metering.TableMeters
import com.zepben.cimbend.database.sqlite.tables.iec61968.metering.TableUsagePoints
import com.zepben.cimbend.database.sqlite.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.*
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas.TableAccumulators
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas.TableAnalogs
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas.TableControls
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas.TableDiscretes
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.cimbend.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.cimbend.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.cimbend.database.sqlite.upgrade.UpgradeRunner
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.tracing.ConnectivityResult
import com.zepben.cimbend.network.tracing.Tracing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.sql.*
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
    private val databaseTables = DatabaseTables()

    private var hasBeenUsed: Boolean = false

    fun load(
        networkService: NetworkService,
        diagramService: DiagramService,
        customerService: CustomerService
    ): Boolean {
        if (hasBeenUsed) {
            logger.error("You can only use the database reader once.")
            return false
        }
        hasBeenUsed = true

        val networkServiceReader = NetworkServiceReader(networkService)
        val diagramServiceReader = DiagramServiceReader(diagramService)
        val customerServiceReader = CustomerServiceReader(customerService)

        val databaseVersion = preLoad()
        if (databaseVersion == null) {
            closeConnection()
            return false
        }

        logger.info("Loading from database version v$databaseVersion")
        val status = try {
            load(networkServiceReader)
                && load(diagramServiceReader)
                && load(customerServiceReader)
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

    private fun load(reader: NetworkServiceReader): Boolean {
        var status = true

        status = status and loadEach<TableCableInfo>("cable info") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableOverheadWireInfo>("overhead wire info") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableLocations>("locations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableOrganisations>("organisations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableAssetOwners>("asset owners") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePoles>("poles") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableStreetlights>("streetlights") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableMeters>("meters") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableUsagePoints>("usage points") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableOperationalRestrictions>("operational restrictions") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableBaseVoltages>("base voltages") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableConnectivityNodes>("connectivity nodes") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableGeographicalRegions>("geographical regions") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableSubGeographicalRegions>("sub-geographical regions") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableSubstations>("substations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableSites>("sites") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePerLengthSequenceImpedances>("per length sequence impedances") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableAcLineSegments>("AC line segments") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableBreakers>("breakers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableDisconnectors>("disconnectors") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEnergyConsumers>("energy consumers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEnergyConsumerPhases>("energy consumer phases") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEnergySources>("energy sources") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEnergySourcePhases>("energy source phases") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableFuses>("fuses") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableJumpers>("jumpers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableJunctions>("junctions") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableLinearShuntCompensators>("linear shunt compensators") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePowerTransformers>("power transformers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableReclosers>("reclosers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableTerminals>("terminals") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePowerTransformerEnds>("power transformer ends") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableRatioTapChangers>("ratio tap changers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableFaultIndicators>("fault indicators") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableFeeders>("feeders") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableLoops>("loops") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableCircuits>("circuits") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePositionPoints>("position points") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableLocationStreetAddresses>("location street addresses") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableAssetOrganisationRolesAssets>("asset organisation role to asset associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableUsagePointsEndDevices>("usage point to end device associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEquipmentUsagePoints>("equipment to usage point associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEquipmentOperationalRestrictions>("equipment to operational restriction associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableEquipmentEquipmentContainers>("equipment to equipment container associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableCircuitsSubstations>("circuit to substation associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableCircuitsTerminals>("circuit to terminal associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableLoopsSubstations>("loop to substation associations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableControls>("controls") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableRemoteControls>("remote controls") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableRemoteSources>("remote sources") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableAnalogs>("analogs") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableAccumulators>("accumulators") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableDiscretes>("discretes") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }

        return status
    }

    private fun load(reader: DiagramServiceReader): Boolean {
        var status = true

        status = status and loadEach<TableDiagrams>("diagrams") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableDiagramObjects>("diagram objects") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }

        status = status and loadEach<TableDiagramObjectPoints>("diagram object points") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }

        return status
    }

    private fun load(reader: CustomerServiceReader): Boolean {
        var status = true

        status = status and loadEach<TableOrganisations>("organisations") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableCustomers>("customers") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableCustomerAgreements>("customer agreements") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TablePricingStructures>("pricing structures") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }
        status = status and loadEach<TableTariffs>("tariffs") { rs, tbl, setMRID -> reader.load(tbl, rs, setMRID) }

        status = status and loadEach<TableCustomerAgreementsPricingStructures>("customer agreement to pricing structure associations") { rs, tbl, setMRID ->
            reader.load(tbl, rs, setMRID)
        }
        status = status and loadEach<TablePricingStructuresTariffs>("pricing structure to tariff associations") { rs, tbl, setMRID ->
            reader.load(tbl, rs, setMRID)
        }

        return status
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

    private inline fun <reified T : SqliteTable> loadEach(
        description: String,
        crossinline processRow: (ResultSet, T, (String) -> String) -> Boolean
    ): Boolean {
        return loadTable<T>(description) { table, results ->
            var lastMRID: String? = null
            val setLastMRID = { mrid: String -> lastMRID = mrid; mrid }

            try {
                var count = 0
                while (results.next()) {
                    if (processRow(results, table, setLastMRID)) {
                        ++count
                    }
                }

                return@loadTable count
            } catch (e: SQLException) {
                logger.error("Failed to load '" + lastMRID + "' from '" + table.name() + "': " + e.message)
                throw e
            }
        }
    }

    private inline fun <reified T : SqliteTable> loadTable(
        description: String,
        processRows: (T, ResultSet) -> Int
    ): Boolean {
        logger.info("Loading $description...")

        val table = databaseTables.getTable(T::class.java)
        val thrown = try {
            val count = getStatement(loadConnection).use { statement ->
                statement.executeConfiguredQuery(table.selectSql()).use { results ->
                    processRows(table, results)
                }
            }
            logger.info("Successfully loaded $count $description.")
            return true
        } catch (t: Throwable) {
            when (t) {
                is SQLException,
                is IllegalArgumentException,
                is MRIDLookupException,
                is DuplicateMRIDException -> t
                else -> throw t
            }
        }

        logger.error("Failed to read the $description from '${table.name()}': ${thrown.message}", thrown)
        return false
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
                && Collections.disjoint(feederStartPoints,
                NetworkService.connectedEquipment(energySource)
                    .mapNotNull(ConnectivityResult::to)
                    .map(ConductingEquipment::mRID)
                    .toSet())
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
