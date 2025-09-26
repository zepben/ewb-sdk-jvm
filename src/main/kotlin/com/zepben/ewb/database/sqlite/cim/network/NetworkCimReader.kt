/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.extensions.iec61968.common.ContactMethodType
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.VectorGroup
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.*
import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.StreetlightLampKind
import com.zepben.ewb.cim.iec61968.metering.*
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.ewb.cim.iec61970.base.generation.production.*
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.*
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemotePoint
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.database.sql.extensions.*
import com.zepben.ewb.database.sqlite.cim.CimReader
import com.zepben.ewb.database.sqlite.cim.tables.associations.*
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.assetinfo.TableRecloseDelays
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.assetinfo.TableRelayInfo
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetails
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetailsElectronicAddresses
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetailsStreetAddresses
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetailsTelephoneNumbers
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.metering.TablePanDemandResponseFunctions
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.core.TableSites
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.feeder.TableLoops
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.feeder.TableLvFeeders
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.generation.production.TableEvChargingUnits
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.protection.*
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.wires.TableBatteryControls
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.wires.TablePowerTransformerEndRatings
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassets.TablePoles
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableEndDeviceFunctions
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableEndDevices
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableMeters
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableUsagePointContactDetails
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableUsagePoints
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.equivalents.TableEquivalentEquipment
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TableBatteryUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePhotoVoltaicUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePowerElectronicsUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePowerElectronicsWindUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.protection.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.scada.TableRemotePoints
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.ewb.database.sqlite.common.MRIDLookupException
import com.zepben.ewb.database.sqlite.extensions.*
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.extensions.*
import com.zepben.ewb.services.network.NetworkService
import java.sql.ResultSet
import java.sql.SQLException

/**
 * A class for reading the [NetworkService] tables from the database.
 */
internal class NetworkCimReader : CimReader<NetworkService>() {

    // ##################################
    // # Extensions IEC61968 Asset Info #
    // ##################################

    /**
     * Create a [RelayInfo] and populate its fields from [TableRelayInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [RelayInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [RelayInfo].
     * @param setIdentifier A callback to register the mRID of this [RelayInfo] for logging purposes.
     *
     * @return true if the [RelayInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableRelayInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val relayInfo = RelayInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            curveSetting = resultSet.getNullableString(table.CURVE_SETTING.queryIndex)
            recloseFast = resultSet.getNullableBoolean(table.RECLOSE_FAST.queryIndex)
        }

        return readAssetInfo(relayInfo, table, resultSet) && service.addOrThrow(relayInfo)
    }

    /**
     * Adds a delay to a [RelayInfo] and populate its fields from [TableRecloseDelays].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the delay fields from.
     * @param resultSet The record in the database table containing the fields for this delay.
     * @param setIdentifier A callback to register the mRID of this delay for logging purposes.
     *
     * @return true if the delay was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableRecloseDelays, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        // Note TableRecloseDelays.selectSql ensures we process ratings in the correct order.
        val relayInfoMRID = resultSet.getString(table.RELAY_INFO_MRID.queryIndex)
        val recloseDelay = resultSet.getDouble(table.RECLOSE_DELAY.queryIndex)
        setIdentifier("$relayInfoMRID.s$recloseDelay")

        val cri = service.ensureGet<RelayInfo>(relayInfoMRID, "$relayInfoMRID.s$recloseDelay")
        cri?.addDelay(recloseDelay)

        return true
    }

    // ##############################
    // # Extensions IEC61968 Common #
    // ##############################

    // FIXME: this is needed as there is no way to look up objects that arent [IdentifiedObjects] from the network
    private val contactDetailsById = mutableMapOf<String, ContactDetails>()

    @Throws(SQLException::class)
    private fun readContactDetails(contactDetails: ContactDetails, table: TableContactDetails, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        contactDetails.apply {
            contactType = resultSet.getString(table.CONTACT_TYPE.queryIndex)
            firstName = resultSet.getString(table.FIRST_NAME.queryIndex)
            lastName = resultSet.getString(table.LAST_NAME.queryIndex)
            preferredContactMethod = ContactMethodType.valueOf(resultSet.getString(table.PREFERRED_CONTACT_METHOD.queryIndex))
            isPrimary = resultSet.getBoolean(table.IS_PRIMARY.queryIndex)
            businessName = resultSet.getString(table.BUSINESS_NAME.queryIndex)
        }
        contactDetailsById[contactDetails.id] = contactDetails

        return true
    }

    /**
     * Create a [ElectronicAddress] and populate its fields from [TableContactDetailsElectronicAddresses].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ContactDetails] fields from.
     * @param resultSet The record in the database table containing the fields for this [ContactDetails].
     * @param setIdentifier A callback to register the mRID of this [ContactDetails] for logging purposes.
     *
     * @return true if the [ContactDetails] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     *
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableContactDetailsElectronicAddresses, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val contactDetailsId = resultSet.getString(table.CONTACT_DETAILS_ID.queryIndex)
        setIdentifier("${contactDetailsId}-ElectronicAddress")

        val contactDetails = contactDetailsById[contactDetailsId] ?: throw MRIDLookupException("Failed to find ${ContactDetails::class.simpleName} with ID $contactDetailsId while looking up ElectronicAddress")

        contactDetails.addElectronicAddress(readElectronicAddress(table, resultSet))

        return true
    }

    /**
     * Create a [StreetAddress] and populate its fields from [TableContactDetailsStreetAddresses].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ContactDetails] fields from.
     * @param resultSet The record in the database table containing the fields for this [ContactDetails].
     * @param setIdentifier A callback to register the mRID of this [ContactDetails] for logging purposes.
     *
     * @return true if the [ContactDetails] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     *
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableContactDetailsStreetAddresses, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val contactDetailsId = resultSet.getString(table.CONTACT_DETAILS_ID.queryIndex)
        setIdentifier("${contactDetailsId}-StreetAddress")

        val contactDetails = contactDetailsById[contactDetailsId] ?: throw MRIDLookupException("Failed to find ${ContactDetails::class.simpleName} with ID $contactDetailsId while looking up StreetAddress")

        contactDetails.contactAddress = readStreetAddress(table, resultSet)

        return true
    }

    /**
     * Create a [TelephoneNumber] and populate its fields from [TableContactDetailsTelephoneNumbers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ContactDetails] fields from.
     * @param resultSet The record in the database table containing the fields for this [ContactDetails].
     * @param setIdentifier A callback to register the mRID of this [ContactDetails] for logging purposes.
     *
     * @return true if the [ContactDetails] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     *
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableContactDetailsTelephoneNumbers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val contactDetailsId = resultSet.getString(table.CONTACT_DETAILS_ID.queryIndex)
        setIdentifier("${contactDetailsId}-TelephoneNumber")

        val contactDetails = contactDetailsById[contactDetailsId] ?: throw MRIDLookupException("Failed to find ${ContactDetails::class.simpleName} with ID $contactDetailsId while looking up TelephoneNumber")

        contactDetails.addTelephoneNumber(readTelephoneNumber(table, resultSet))

        return true
    }

    // ################################
    // # Extensions IEC61968 Metering #
    // ################################

    /**
     * Create a [PanDemandResponseFunction] and populate its fields from [TablePanDemandResponseFunctions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PanDemandResponseFunction] fields from.
     * @param resultSet The record in the database table containing the fields for this [PanDemandResponseFunction].
     * @param setIdentifier A callback to register the mRID of this [PanDemandResponseFunction] for logging purposes.
     *
     * @return true if the [PanDemandResponseFunction] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePanDemandResponseFunctions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val panDemandResponseFunction = PanDemandResponseFunction(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            kind = EndDeviceFunctionKind.valueOf(resultSet.getString(table.KIND.queryIndex))
            applianceBitmask = resultSet.getNullableInt(table.APPLIANCE.queryIndex)
        }

        return readEndDeviceFunction(panDemandResponseFunction, table, resultSet) && service.addOrThrow(panDemandResponseFunction)
    }

    // #################################
    // # Extensions IEC61970 Base Core #
    // #################################

    /**
     * Create a [Site] and populate its fields from [TableSites].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Site] fields from.
     * @param resultSet The record in the database table containing the fields for this [Site].
     * @param setIdentifier A callback to register the mRID of this [Site] for logging purposes.
     *
     * @return true if the [Site] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSites, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val site = Site(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readEquipmentContainer(service, site, table, resultSet) && service.addOrThrow(site)
    }

    // ###################################
    // # Extensions IEC61970 Base Feeder #
    // ###################################

    /**
     * Create a [Loop] and populate its fields from [TableLoops].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Loop] fields from.
     * @param resultSet The record in the database table containing the fields for this [Loop].
     * @param setIdentifier A callback to register the mRID of this [Loop] for logging purposes.
     *
     * @return true if the [Loop] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLoops, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val loop = Loop(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readIdentifiedObject(loop, table, resultSet) && service.addOrThrow(loop)
    }

    /**
     * Create a [LvFeeder] and populate its fields from [TableLvFeeders].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [LvFeeder] fields from.
     * @param resultSet The record in the database table containing the fields for this [LvFeeder].
     * @param setIdentifier A callback to register the mRID of this [LvFeeder] for logging purposes.
     *
     * @return true if the [LvFeeder] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLvFeeders, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val lvFeeder = LvFeeder(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            normalHeadTerminal = service.ensureGet(
                resultSet.getNullableString(table.NORMAL_HEAD_TERMINAL_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readEquipmentContainer(service, lvFeeder, table, resultSet) && service.addOrThrow(lvFeeder)
    }

    // ##################################################
    // # Extensions IEC61970 Base Generation Production #
    // ##################################################

    /**
     * Create an [EvChargingUnit] and populate its fields from [TableEvChargingUnits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EvChargingUnit] fields from.
     * @param resultSet The record in the database table containing the fields for this [EvChargingUnit].
     * @param setIdentifier A callback to register the mRID of this [EvChargingUnit] for logging purposes.
     *
     * @return true if the [EvChargingUnit] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEvChargingUnits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val evChargingUnit = EvChargingUnit(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readPowerElectronicsUnit(service, evChargingUnit, table, resultSet) && service.addOrThrow(evChargingUnit)
    }

    // #######################################
    // # Extensions IEC61970 Base Protection #
    // #######################################

    /**
     * Create a [DirectionalCurrentRelay] and populate its fields from [TableDirectionalCurrentRelays].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [DirectionalCurrentRelay] fields from.
     * @param resultSet The record in the database table containing the fields for this [DirectionalCurrentRelay].
     * @param setIdentifier A callback to register the mRID of this [DirectionalCurrentRelay] for logging purposes.
     *
     * @return true if the [DirectionalCurrentRelay] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableDirectionalCurrentRelays, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val directionalCurrentRelay = DirectionalCurrentRelay(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            directionalCharacteristicAngle = resultSet.getNullableDouble(table.DIRECTIONAL_CHARACTERISTIC_ANGLE.queryIndex)
            polarizingQuantityType = PolarizingQuantityType.valueOf(resultSet.getString(table.POLARIZING_QUANTITY_TYPE.queryIndex))
            relayElementPhase = PhaseCode.valueOf(resultSet.getString(table.RELAY_ELEMENT_PHASE.queryIndex))
            minimumPickupCurrent = resultSet.getNullableDouble(table.MINIMUM_PICKUP_CURRENT.queryIndex)
            currentLimit1 = resultSet.getNullableDouble(table.CURRENT_LIMIT_1.queryIndex)
            inverseTimeFlag = resultSet.getNullableBoolean(table.INVERSE_TIME_FLAG.queryIndex)
            timeDelay1 = resultSet.getNullableDouble(table.TIME_DELAY_1.queryIndex)
        }

        return readProtectionRelayFunction(service, directionalCurrentRelay, table, resultSet) && service.addOrThrow(directionalCurrentRelay)

    }

    /**
     * Create a [DistanceRelay] and populate its fields from [TableDistanceRelays].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [DistanceRelay] fields from.
     * @param resultSet The record in the database table containing the fields for this [DistanceRelay].
     * @param setIdentifier A callback to register the mRID of this [DistanceRelay] for logging purposes.
     *
     * @return true if the [DistanceRelay] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableDistanceRelays, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val distanceRelay = DistanceRelay(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            backwardBlind = resultSet.getNullableDouble(table.BACKWARD_BLIND.queryIndex)
            backwardReach = resultSet.getNullableDouble(table.BACKWARD_REACH.queryIndex)
            backwardReactance = resultSet.getNullableDouble(table.BACKWARD_REACTANCE.queryIndex)
            forwardBlind = resultSet.getNullableDouble(table.FORWARD_BLIND.queryIndex)
            forwardReach = resultSet.getNullableDouble(table.FORWARD_REACH.queryIndex)
            forwardReactance = resultSet.getNullableDouble(table.FORWARD_REACTANCE.queryIndex)
            operationPhaseAngle1 = resultSet.getNullableDouble(table.OPERATION_PHASE_ANGLE1.queryIndex)
            operationPhaseAngle2 = resultSet.getNullableDouble(table.OPERATION_PHASE_ANGLE2.queryIndex)
            operationPhaseAngle3 = resultSet.getNullableDouble(table.OPERATION_PHASE_ANGLE3.queryIndex)
        }

        return readProtectionRelayFunction(service, distanceRelay, table, resultSet) && service.addOrThrow(distanceRelay)
    }

    @Throws(SQLException::class)
    private fun readProtectionRelayFunction(
        service: NetworkService,
        protectionRelayFunction: ProtectionRelayFunction,
        table: TableProtectionRelayFunctions,
        resultSet: ResultSet
    ): Boolean {
        protectionRelayFunction.apply {
            assetInfo = service.ensureGet(
                resultSet.getNullableString(table.RELAY_INFO_MRID.queryIndex),
                typeNameAndMRID()
            )
            model = resultSet.getNullableString(table.MODEL.queryIndex)
            reclosing = resultSet.getNullableBoolean(table.RECLOSING.queryIndex)
            relayDelayTime = resultSet.getNullableDouble(table.RELAY_DELAY_TIME.queryIndex)
            protectionKind = ProtectionKind.valueOf(resultSet.getString(table.PROTECTION_KIND.queryIndex))
            directable = resultSet.getNullableBoolean(table.DIRECTABLE.queryIndex)
            powerDirection = PowerDirectionKind.valueOf(resultSet.getString(table.POWER_DIRECTION.queryIndex))
        }

        return readPowerSystemResource(service, protectionRelayFunction, table, resultSet)
    }

    /**
     * Adds a time limit to a [ProtectionRelayFunction] and populate its fields from [TableProtectionRelayFunctionTimeLimits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the time limit fields from.
     * @param resultSet The record in the database table containing the fields for this time limit.
     * @param setIdentifier A callback to register the mRID of this time limit for logging purposes.
     *
     * @return true if the time limit was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelayFunctionTimeLimits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        // Note TableProtectionRelayFunctionTimeLimits.selectSql ensures we process ratings in the correct order.
        val protectionRelayFunctionMRID = setIdentifier(resultSet.getString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex))
        val sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)
        val timeLimit = resultSet.getDouble(table.TIME_LIMIT.queryIndex)
        setIdentifier("$protectionRelayFunctionMRID time limit $sequenceNumber")

        val protectionRelayFunction = service.getOrThrow<ProtectionRelayFunction>(
            protectionRelayFunctionMRID,
            "$protectionRelayFunctionMRID time limit $timeLimit"
        )
        protectionRelayFunction.addTimeLimit(timeLimit)

        return true
    }

    /**
     * Create a [ProtectionRelayScheme] and populate its fields from [TableProtectionRelaySchemes].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ProtectionRelayScheme] fields from.
     * @param resultSet The record in the database table containing the fields for this [ProtectionRelayScheme].
     * @param setIdentifier A callback to register the mRID of this [ProtectionRelayScheme] for logging purposes.
     *
     * @return true if the [ProtectionRelayScheme] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelaySchemes, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val protectionRelayScheme = ProtectionRelayScheme(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            system = service.ensureGet(resultSet.getString(table.SYSTEM_MRID.queryIndex), typeNameAndMRID())
            system?.addScheme(this)
        }

        return readIdentifiedObject(protectionRelayScheme, table, resultSet) && service.addOrThrow(protectionRelayScheme)
    }

    /**
     * Create a [ProtectionRelaySystem] and populate its fields from [TableProtectionRelaySystems].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ProtectionRelaySystem] fields from.
     * @param resultSet The record in the database table containing the fields for this [ProtectionRelaySystem].
     * @param setIdentifier A callback to register the mRID of this [ProtectionRelaySystem] for logging purposes.
     *
     * @return true if the [ProtectionRelaySystem] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelaySystems, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val protectionRelaySystem = ProtectionRelaySystem(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            protectionKind = ProtectionKind.valueOf(resultSet.getString(table.PROTECTION_KIND.queryIndex))
        }

        return readEquipment(service, protectionRelaySystem, table, resultSet) && service.addOrThrow(protectionRelaySystem)
    }

    /**
     * Create a [RelaySetting] and populate its fields from [TableProtectionRelayFunctionThresholds].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [RelaySetting] fields from.
     * @param resultSet The record in the database table containing the fields for this [RelaySetting].
     * @param setIdentifier A callback to register the mRID of this [RelaySetting] for logging purposes.
     *
     * @return true if the [RelaySetting] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelayFunctionThresholds, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val protectionRelayFunctionMRID = setIdentifier(resultSet.getString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex))
        val sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)

        val id = setIdentifier("$protectionRelayFunctionMRID-threshold$sequenceNumber")
        val protectionRelayFunction = service.getOrThrow<ProtectionRelayFunction>(
            protectionRelayFunctionMRID,
            "ProtectionRelayFunction to RelaySetting association $id"
        )

        protectionRelayFunction.addThreshold(
            RelaySetting(
                UnitSymbol.valueOf(resultSet.getString(table.UNIT_SYMBOL.queryIndex)),
                resultSet.getDouble(table.VALUE.queryIndex),
                resultSet.getNullableString(table.NAME.queryIndex)
            ),
            sequenceNumber
        )

        return true
    }

    /**
     * Create a [VoltageRelay] and populate its fields from [TableVoltageRelays].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [VoltageRelay] fields from.
     * @param resultSet The record in the database table containing the fields for this [VoltageRelay].
     * @param setIdentifier A callback to register the mRID of this [VoltageRelay] for logging purposes.
     *
     * @return true if the [VoltageRelay] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableVoltageRelays, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val voltageRelay = VoltageRelay(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readProtectionRelayFunction(service, voltageRelay, table, resultSet) && service.addOrThrow(voltageRelay)
    }

    // ##################################
    // # Extensions IEC61970 Base Wires #
    // ##################################

    /**
     * Create a [BatteryControl] and populate its fields from [TableBatteryControls].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [BatteryControl] fields from.
     * @param resultSet The record in the database table containing the fields for this [BatteryControl].
     * @param setIdentifier A callback to register the mRID of this [BatteryControl] for logging purposes.
     *
     * @return true if the [BatteryControl] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBatteryControls, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val batteryControl = BatteryControl(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            chargingRate = resultSet.getNullableDouble(table.CHARGING_RATE.queryIndex)
            dischargingRate = resultSet.getNullableDouble(table.DISCHARGING_RATE.queryIndex)
            reservePercent = resultSet.getNullableDouble(table.RESERVE_PERCENT.queryIndex)
            controlMode = BatteryControlMode.valueOf(resultSet.getString(table.CONTROL_MODE.queryIndex))
        }

        return readRegulatingControl(service, batteryControl, table, resultSet) && service.addOrThrow(batteryControl)
    }

    /**
     * Adds a rating to a [PowerTransformerEnd] from [TablePowerTransformerEndRatings].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the rating fields from.
     * @param resultSet The record in the database table containing the fields for this rating.
     * @param setIdentifier A callback to register the mRID of this rating for logging purposes.
     *
     * @return true if the rating was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerTransformerEndRatings, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        // Note TablePowerTransformerEndRatings.selectSql ensures we process ratings in the correct order.
        val powerTransformerEndMRID = resultSet.getString(table.POWER_TRANSFORMER_END_MRID.queryIndex)
        val ratedS = resultSet.getInt(table.RATED_S.queryIndex)
        setIdentifier("$powerTransformerEndMRID.s$ratedS")

        val pte = service.ensureGet<PowerTransformerEnd>(powerTransformerEndMRID, "$powerTransformerEndMRID.s$ratedS")
        val coolingType = TransformerCoolingType.valueOf(resultSet.getString(table.COOLING_TYPE.queryIndex))
        pte?.addRating(ratedS, coolingType)

        return true
    }

    // #######################
    // # IEC61968 Asset Info #
    // #######################

    /**
     * Create a [CableInfo] and populate its fields from [TableCableInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [CableInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [CableInfo].
     * @param setIdentifier A callback to register the mRID of this [CableInfo] for logging purposes.
     *
     * @return true if the [CableInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCableInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val cableInfo = CableInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readWireInfo(cableInfo, table, resultSet) && service.addOrThrow(cableInfo)
    }

    /**
     * Create a [NoLoadTest] and populate its fields from [TableNoLoadTests].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [NoLoadTest] fields from.
     * @param resultSet The record in the database table containing the fields for this [NoLoadTest].
     * @param setIdentifier A callback to register the mRID of this [NoLoadTest] for logging purposes.
     *
     * @return true if the [NoLoadTest] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableNoLoadTests, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val noLoadTest = NoLoadTest(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            energisedEndVoltage = resultSet.getNullableInt(table.ENERGISED_END_VOLTAGE.queryIndex)
            excitingCurrent = resultSet.getNullableDouble(table.EXCITING_CURRENT.queryIndex)
            excitingCurrentZero = resultSet.getNullableDouble(table.EXCITING_CURRENT_ZERO.queryIndex)
            loss = resultSet.getNullableInt(table.LOSS.queryIndex)
            lossZero = resultSet.getNullableInt(table.LOSS_ZERO.queryIndex)
        }

        return readTransformerTest(noLoadTest, table, resultSet) && service.addOrThrow(noLoadTest)
    }

    /**
     * Create an [OpenCircuitTest] and populate its fields from [TableOpenCircuitTests].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [OpenCircuitTest] fields from.
     * @param resultSet The record in the database table containing the fields for this [OpenCircuitTest].
     * @param setIdentifier A callback to register the mRID of this [OpenCircuitTest] for logging purposes.
     *
     * @return true if the [OpenCircuitTest] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableOpenCircuitTests, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val openCircuitTest = OpenCircuitTest(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            energisedEndStep = resultSet.getNullableInt(table.ENERGISED_END_STEP.queryIndex)
            energisedEndVoltage = resultSet.getNullableInt(table.ENERGISED_END_VOLTAGE.queryIndex)
            openEndStep = resultSet.getNullableInt(table.OPEN_END_STEP.queryIndex)
            openEndVoltage = resultSet.getNullableInt(table.OPEN_END_VOLTAGE.queryIndex)
            phaseShift = resultSet.getNullableDouble(table.PHASE_SHIFT.queryIndex)
        }

        return readTransformerTest(openCircuitTest, table, resultSet) && service.addOrThrow(openCircuitTest)
    }

    /**
     * Create an [OverheadWireInfo] and populate its fields from [TableOverheadWireInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [OverheadWireInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [OverheadWireInfo].
     * @param setIdentifier A callback to register the mRID of this [OverheadWireInfo] for logging purposes.
     *
     * @return true if the [OverheadWireInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableOverheadWireInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val overheadWireInfo = OverheadWireInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readWireInfo(overheadWireInfo, table, resultSet) && service.addOrThrow(overheadWireInfo)
    }

    /**
     * Create a [PowerTransformerInfo] and populate its fields from [TablePowerTransformerInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerTransformerInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerTransformerInfo].
     * @param setIdentifier A callback to register the mRID of this [PowerTransformerInfo] for logging purposes.
     *
     * @return true if the [PowerTransformerInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerTransformerInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerTransformerInfo = PowerTransformerInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readAssetInfo(powerTransformerInfo, table, resultSet) && service.addOrThrow(powerTransformerInfo)
    }

    /**
     * Create a [ShortCircuitTest] and populate its fields from [TableShortCircuitTests].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ShortCircuitTest] fields from.
     * @param resultSet The record in the database table containing the fields for this [ShortCircuitTest].
     * @param setIdentifier A callback to register the mRID of this [ShortCircuitTest] for logging purposes.
     *
     * @return true if the [ShortCircuitTest] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableShortCircuitTests, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val shortCircuitTest = ShortCircuitTest(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            current = resultSet.getNullableDouble(table.CURRENT.queryIndex)
            energisedEndStep = resultSet.getNullableInt(table.ENERGISED_END_STEP.queryIndex)
            groundedEndStep = resultSet.getNullableInt(table.GROUNDED_END_STEP.queryIndex)
            leakageImpedance = resultSet.getNullableDouble(table.LEAKAGE_IMPEDANCE.queryIndex)
            leakageImpedanceZero = resultSet.getNullableDouble(table.LEAKAGE_IMPEDANCE_ZERO.queryIndex)
            loss = resultSet.getNullableInt(table.LOSS.queryIndex)
            lossZero = resultSet.getNullableInt(table.LOSS_ZERO.queryIndex)
            power = resultSet.getNullableInt(table.POWER.queryIndex)
            voltage = resultSet.getNullableDouble(table.VOLTAGE.queryIndex)
            voltageOhmicPart = resultSet.getNullableDouble(table.VOLTAGE_OHMIC_PART.queryIndex)
        }

        return readTransformerTest(shortCircuitTest, table, resultSet) && service.addOrThrow(shortCircuitTest)
    }

    /**
     * Create a [ShuntCompensatorInfo] and populate its fields from [TableShuntCompensatorInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ShuntCompensatorInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [ShuntCompensatorInfo].
     * @param setIdentifier A callback to register the mRID of this [ShuntCompensatorInfo] for logging purposes.
     *
     * @return true if the [ShuntCompensatorInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableShuntCompensatorInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val shuntCompensatorInfo = ShuntCompensatorInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            maxPowerLoss = resultSet.getNullableInt(table.MAX_POWER_LOSS.queryIndex)
            ratedCurrent = resultSet.getNullableInt(table.RATED_CURRENT.queryIndex)
            ratedReactivePower = resultSet.getNullableInt(table.RATED_REACTIVE_POWER.queryIndex)
            ratedVoltage = resultSet.getNullableInt(table.RATED_VOLTAGE.queryIndex)
        }

        return readAssetInfo(shuntCompensatorInfo, table, resultSet) && service.addOrThrow(shuntCompensatorInfo)
    }

    /**
     * Create a [SwitchInfo] and populate its fields from [TableSwitchInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [SwitchInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [SwitchInfo].
     * @param setIdentifier A callback to register the mRID of this [SwitchInfo] for logging purposes.
     *
     * @return true if the [SwitchInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSwitchInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val switchInfo = SwitchInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            ratedInterruptingTime = resultSet.getNullableDouble(table.RATED_INTERRUPTING_TIME.queryIndex)
        }

        return readAssetInfo(switchInfo, table, resultSet) && service.addOrThrow(switchInfo)
    }

    /**
     * Create a [TransformerEndInfo] and populate its fields from [TableTransformerEndInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [TransformerEndInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [TransformerEndInfo].
     * @param setIdentifier A callback to register the mRID of this [TransformerEndInfo] for logging purposes.
     *
     * @return true if the [TransformerEndInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableTransformerEndInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val transformerEndInfo = TransformerEndInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            connectionKind = WindingConnection.valueOf(resultSet.getString(table.CONNECTION_KIND.queryIndex))
            emergencyS = resultSet.getNullableInt(table.EMERGENCY_S.queryIndex)
            endNumber = resultSet.getInt(table.END_NUMBER.queryIndex)
            insulationU = resultSet.getNullableInt(table.INSULATION_U.queryIndex)
            phaseAngleClock = resultSet.getNullableInt(table.PHASE_ANGLE_CLOCK.queryIndex)
            r = resultSet.getNullableDouble(table.R.queryIndex)
            ratedS = resultSet.getNullableInt(table.RATED_S.queryIndex)
            ratedU = resultSet.getNullableInt(table.RATED_U.queryIndex)
            shortTermS = resultSet.getNullableInt(table.SHORT_TERM_S.queryIndex)

            transformerTankInfo = service.ensureGet(resultSet.getString(table.TRANSFORMER_TANK_INFO_MRID.queryIndex), typeNameAndMRID())
            energisedEndNoLoadTests = service.ensureGet(resultSet.getString(table.ENERGISED_END_NO_LOAD_TESTS.queryIndex), typeNameAndMRID())
            energisedEndShortCircuitTests = service.ensureGet(resultSet.getString(table.ENERGISED_END_SHORT_CIRCUIT_TESTS.queryIndex), typeNameAndMRID())
            groundedEndShortCircuitTests = service.ensureGet(resultSet.getString(table.GROUNDED_END_SHORT_CIRCUIT_TESTS.queryIndex), typeNameAndMRID())
            openEndOpenCircuitTests = service.ensureGet(resultSet.getString(table.OPEN_END_OPEN_CIRCUIT_TESTS.queryIndex), typeNameAndMRID())
            energisedEndOpenCircuitTests = service.ensureGet(resultSet.getString(table.ENERGISED_END_OPEN_CIRCUIT_TESTS.queryIndex), typeNameAndMRID())

            transformerTankInfo?.addTransformerEndInfo(this)
        }

        return readAssetInfo(transformerEndInfo, table, resultSet) && service.addOrThrow(transformerEndInfo)
    }

    /**
     * Create a [TransformerTankInfo] and populate its fields from [TableTransformerTankInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [TransformerTankInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [TransformerTankInfo].
     * @param setIdentifier A callback to register the mRID of this [TransformerTankInfo] for logging purposes.
     *
     * @return true if the [TransformerTankInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableTransformerTankInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val transformerTankInfo = TransformerTankInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            powerTransformerInfo =
                service.ensureGet<PowerTransformerInfo>(resultSet.getString(table.POWER_TRANSFORMER_INFO_MRID.queryIndex), typeNameAndMRID())
                    ?.addTransformerTankInfo(this)
        }

        return readAssetInfo(transformerTankInfo, table, resultSet) && service.addOrThrow(transformerTankInfo)
    }

    @Throws(SQLException::class)
    private fun readTransformerTest(transformerTest: TransformerTest, table: TableTransformerTest, resultSet: ResultSet): Boolean {
        transformerTest.apply {
            basePower = resultSet.getNullableInt(table.BASE_POWER.queryIndex)
            temperature = resultSet.getNullableDouble(table.TEMPERATURE.queryIndex)
        }

        return readIdentifiedObject(transformerTest, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readWireInfo(wireInfo: WireInfo, table: TableWireInfo, resultSet: ResultSet): Boolean {
        wireInfo.apply {
            ratedCurrent = resultSet.getNullableInt(table.RATED_CURRENT.queryIndex)
            material = WireMaterialKind.valueOf(resultSet.getString(table.MATERIAL.queryIndex))
        }

        return readAssetInfo(wireInfo, table, resultSet)
    }

    // ###################
    // # IEC61968 Assets #
    // ###################

    @Throws(SQLException::class)
    private fun readAsset(service: NetworkService, asset: Asset, table: TableAssets, resultSet: ResultSet): Boolean {
        asset.apply {
            location =
                service.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
        }
        return readIdentifiedObject(asset, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readAssetContainer(service: NetworkService, assetContainer: AssetContainer, table: TableAssetContainers, resultSet: ResultSet): Boolean =
        readAsset(service, assetContainer, table, resultSet)

    @Throws(SQLException::class)
    private fun readAssetFunction(assetFunction: AssetFunction, table: TableAssetFunctions, resultSet: ResultSet): Boolean =
        readIdentifiedObject(assetFunction, table, resultSet)

    @Throws(SQLException::class)
    private fun readAssetInfo(assetInfo: AssetInfo, table: TableAssetInfo, resultSet: ResultSet): Boolean =
        readIdentifiedObject(assetInfo, table, resultSet)

    @Throws(SQLException::class)
    private fun readAssetOrganisationRole(
        service: NetworkService,
        assetOrganisationRole: AssetOrganisationRole,
        table: TableAssetOrganisationRoles,
        resultSet: ResultSet
    ): Boolean =
        readOrganisationRole(service, assetOrganisationRole, table, resultSet)

    /**
     * Create an [AssetOwner] and populate its fields from [TableAssetOwners].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [AssetOwner] fields from.
     * @param resultSet The record in the database table containing the fields for this [AssetOwner].
     * @param setIdentifier A callback to register the mRID of this [AssetOwner] for logging purposes.
     *
     * @return true if the [AssetOwner] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAssetOwners, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val assetOwner = AssetOwner(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readAssetOrganisationRole(service, assetOwner, table, resultSet) && service.addOrThrow(assetOwner)
    }

    /**
     * Create a [Streetlight] and populate its fields from [TableStreetlights].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Streetlight] fields from.
     * @param resultSet The record in the database table containing the fields for this [Streetlight].
     * @param setIdentifier A callback to register the mRID of this [Streetlight] for logging purposes.
     *
     * @return true if the [Streetlight] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableStreetlights, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val streetlight = Streetlight(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            lampKind = StreetlightLampKind.valueOf(resultSet.getString(table.LAMP_KIND.queryIndex))
            lightRating = resultSet.getNullableInt(table.LIGHT_RATING.queryIndex)
            pole = service.ensureGet(resultSet.getString(table.POLE_MRID.queryIndex), typeNameAndMRID())
            pole?.addStreetlight(this)
        }

        return readAsset(service, streetlight, table, resultSet) && service.addOrThrow(streetlight)
    }

    @Throws(SQLException::class)
    private fun readStructure(
        service: NetworkService,
        structure: Structure,
        table: TableStructures,
        resultSet: ResultSet
    ): Boolean =
        readAssetContainer(service, structure, table, resultSet)

    // ###################
    // # IEC61968 Common #
    // ###################

    @Throws(SQLException::class)
    private fun readElectronicAddress(table: TableElectronicAddresses, resultSet: ResultSet): ElectronicAddress =
        ElectronicAddress(
            resultSet.getString(table.EMAIL_1.queryIndex).emptyIfNull().internEmpty(),
            resultSet.getBoolean(table.IS_PRIMARY.queryIndex),
            resultSet.getString(table.DESCRIPTION.queryIndex).emptyIfNull().internEmpty(),
        )

    /**
     * Create a [Location] and populate its fields from [TableLocations].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Location] fields from.
     * @param resultSet The record in the database table containing the fields for this [Location].
     * @param setIdentifier A callback to register the mRID of this [Location] for logging purposes.
     *
     * @return true if the [Location] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLocations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val location = Location(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readIdentifiedObject(location, table, resultSet) && service.addOrThrow(location)
    }

    /**
     * Create a [StreetAddress] and populate its fields from [TableLocationStreetAddresses].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [StreetAddress] fields from.
     * @param resultSet The record in the database table containing the fields for this [StreetAddress].
     * @param setIdentifier A callback to register the mRID of this [StreetAddress] for logging purposes.
     *
     * @return true if the [StreetAddress] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLocationStreetAddresses, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val locationMRID = setIdentifier(resultSet.getString(table.LOCATION_MRID.queryIndex))
        val field = TableLocationStreetAddressField.valueOf(resultSet.getString(table.ADDRESS_FIELD.queryIndex))

        val id = setIdentifier("$locationMRID-to-$field")
        val location = service.getOrThrow<Location>(locationMRID, "Location to StreetAddress association $id")

        when (field) {
            TableLocationStreetAddressField.mainAddress -> location.mainAddress = readStreetAddress(table, resultSet)
        }

        return true
    }

    /**
     * Create a [PositionPoint] and populate its fields from [TablePositionPoints].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PositionPoint] fields from.
     * @param resultSet The record in the database table containing the fields for this [PositionPoint].
     * @param setIdentifier A callback to register the mRID of this [PositionPoint] for logging purposes.
     *
     * @return true if the [PositionPoint] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePositionPoints, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val locationMRID = setIdentifier(resultSet.getString(table.LOCATION_MRID.queryIndex))
        val sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)

        val id = setIdentifier("$locationMRID-point$sequenceNumber")
        val location = service.getOrThrow<Location>(locationMRID, "Location to PositionPoint association $id")

        location.addPoint(
            PositionPoint(
                resultSet.getDouble(table.X_POSITION.queryIndex),
                resultSet.getDouble(table.Y_POSITION.queryIndex)
            ),
            sequenceNumber
        )

        return true
    }

    @Throws(SQLException::class)
    private fun readStreetAddress(table: TableStreetAddresses, resultSet: ResultSet): StreetAddress =
        StreetAddress(
            resultSet.getNullableString(table.POSTAL_CODE.queryIndex),
            readTownDetail(table, resultSet),
            resultSet.getNullableString(table.PO_BOX.queryIndex),
            readStreetDetail(table, resultSet)
        )

    @Throws(SQLException::class)
    private fun readStreetDetail(table: TableStreetAddresses, resultSet: ResultSet): StreetDetail? =
        StreetDetail(
            resultSet.getNullableString(table.BUILDING_NAME.queryIndex),
            resultSet.getNullableString(table.FLOOR_IDENTIFICATION.queryIndex),
            resultSet.getNullableString(table.STREET_NAME.queryIndex),
            resultSet.getNullableString(table.NUMBER.queryIndex),
            resultSet.getNullableString(table.SUITE_NUMBER.queryIndex),
            resultSet.getNullableString(table.TYPE.queryIndex),
            resultSet.getNullableString(table.DISPLAY_ADDRESS.queryIndex)
        ).takeUnless { it.allFieldsNull() }

    @Throws(SQLException::class)
    private fun readTelephoneNumber(table: TableTelephoneNumbers, resultSet: ResultSet): TelephoneNumber =
        TelephoneNumber(
            resultSet.getString(table.AREA_CODE.queryIndex),
            resultSet.getString(table.CITY_CODE.queryIndex),
            resultSet.getString(table.COUNTRY_CODE.queryIndex),
            resultSet.getString(table.DIAL_OUT.queryIndex),
            resultSet.getString(table.EXTENSION.queryIndex),
            resultSet.getString(table.INTERNATIONAL_PREFIX.queryIndex),
            resultSet.getString(table.LOCAL_NUMBER.queryIndex),
            resultSet.getBoolean(table.IS_PRIMARY.queryIndex),
            resultSet.getString(table.DESCRIPTION.queryIndex),
        )

    @Throws(SQLException::class)
    private fun readTownDetail(table: TableTownDetails, resultSet: ResultSet): TownDetail? =
        TownDetail(
            resultSet.getNullableString(table.TOWN_NAME.queryIndex),
            resultSet.getNullableString(table.STATE_OR_PROVINCE.queryIndex)
        ).takeUnless { it.allFieldsNull() }

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    /**
     * Create a [CurrentTransformerInfo] and populate its fields from [TableCurrentTransformerInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [CurrentTransformerInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [CurrentTransformerInfo].
     * @param setIdentifier A callback to register the mRID of this [CurrentTransformerInfo] for logging purposes.
     *
     * @return true if the [CurrentTransformerInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCurrentTransformerInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val currentTransformerInfo = CurrentTransformerInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            accuracyClass = resultSet.getNullableString(table.ACCURACY_CLASS.queryIndex)
            accuracyLimit = resultSet.getNullableDouble(table.ACCURACY_LIMIT.queryIndex)
            coreCount = resultSet.getNullableInt(table.CORE_COUNT.queryIndex)
            ctClass = resultSet.getNullableString(table.CT_CLASS.queryIndex)
            kneePointVoltage = resultSet.getNullableInt(table.KNEE_POINT_VOLTAGE.queryIndex)
            maxRatio = resultSet.getNullableRatio(table.MAX_RATIO_NUMERATOR.queryIndex, table.MAX_RATIO_DENOMINATOR.queryIndex)
            nominalRatio = resultSet.getNullableRatio(table.NOMINAL_RATIO_NUMERATOR.queryIndex, table.NOMINAL_RATIO_DENOMINATOR.queryIndex)
            primaryRatio = resultSet.getNullableDouble(table.PRIMARY_RATIO.queryIndex)
            ratedCurrent = resultSet.getNullableInt(table.RATED_CURRENT.queryIndex)
            secondaryFlsRating = resultSet.getNullableInt(table.SECONDARY_FLS_RATING.queryIndex)
            secondaryRatio = resultSet.getNullableDouble(table.SECONDARY_RATIO.queryIndex)
            usage = resultSet.getNullableString(table.USAGE.queryIndex)
        }

        return readAssetInfo(currentTransformerInfo, table, resultSet) && service.addOrThrow(currentTransformerInfo)
    }

    /**
     * Create a [PotentialTransformerInfo] and populate its fields from [TablePotentialTransformerInfo].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PotentialTransformerInfo] fields from.
     * @param resultSet The record in the database table containing the fields for this [PotentialTransformerInfo].
     * @param setIdentifier A callback to register the mRID of this [PotentialTransformerInfo] for logging purposes.
     *
     * @return true if the [PotentialTransformerInfo] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePotentialTransformerInfo, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val potentialTransformerInfo = PotentialTransformerInfo(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            accuracyClass = resultSet.getNullableString(table.ACCURACY_CLASS.queryIndex)
            nominalRatio = resultSet.getNullableRatio(table.NOMINAL_RATIO_NUMERATOR.queryIndex, table.NOMINAL_RATIO_DENOMINATOR.queryIndex)
            primaryRatio = resultSet.getNullableDouble(table.PRIMARY_RATIO.queryIndex)
            ptClass = resultSet.getNullableString(table.PT_CLASS.queryIndex)
            ratedVoltage = resultSet.getNullableInt(table.RATED_VOLTAGE.queryIndex)
            secondaryRatio = resultSet.getNullableDouble(table.SECONDARY_RATIO.queryIndex)
        }

        return readAssetInfo(potentialTransformerInfo, table, resultSet) && service.addOrThrow(potentialTransformerInfo)
    }

    // ##################################
    // # IEC61968 infIEC61968 InfAssets #
    // ##################################

    /**
     * Create a [Pole] and populate its fields from [TablePoles].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Pole] fields from.
     * @param resultSet The record in the database table containing the fields for this [Pole].
     * @param setIdentifier A callback to register the mRID of this [Pole] for logging purposes.
     *
     * @return true if the [Pole] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePoles, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val pole = Pole(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        pole.classification = resultSet.getNullableString(table.CLASSIFICATION.queryIndex)

        return readStructure(service, pole, table, resultSet) && service.addOrThrow(pole)
    }

    // #####################
    // # IEC61968 Metering #
    // #####################

    @Throws(SQLException::class)
    private fun readEndDevice(service: NetworkService, endDevice: EndDevice, table: TableEndDevices, resultSet: ResultSet): Boolean {
        endDevice.apply {
            customerMRID = resultSet.getNullableString(table.CUSTOMER_MRID.queryIndex)
            serviceLocation = service.ensureGet(
                resultSet.getNullableString(table.SERVICE_LOCATION_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readAssetContainer(service, endDevice, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readEndDeviceFunction(endDeviceFunction: EndDeviceFunction, table: TableEndDeviceFunctions, resultSet: ResultSet): Boolean {
        endDeviceFunction.apply {
            enabled = resultSet.getNullableBoolean(table.ENABLED.queryIndex)
        }

        return readAssetFunction(endDeviceFunction, table, resultSet)
    }

    /**
     * Create a [Meter] and populate its fields from [TableMeters].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Meter] fields from.
     * @param resultSet The record in the database table containing the fields for this [Meter].
     * @param setIdentifier A callback to register the mRID of this [Meter] for logging purposes.
     *
     * @return true if the [Meter] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableMeters, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val meter = Meter(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readEndDevice(service, meter, table, resultSet) && service.addOrThrow(meter)
    }

    /**
     * Create a [ContactDetails] and populate its fields from [TableUsagePointContactDetails].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [UsagePoint] fields from.
     * @param resultSet The record in the database table containing the fields for this [UsagePoint].
     * @param setIdentifier A callback to register the mRID of this [UsagePoint] for logging purposes.
     *
     * @return true if the [UsagePoint] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     *
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableUsagePointContactDetails, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val usagePointMrid = setIdentifier(resultSet.getString(table.USAGE_POINT_MRID.queryIndex))
        val contactDetailsId = resultSet.getString(table.ID.queryIndex)
        val id = setIdentifier("$usagePointMrid-to-${contactDetailsId}")

        val contactDetails = ContactDetails(contactDetailsId)
        val usagePoint = service.getOrThrow<UsagePoint>(usagePointMrid, "UsagePoint to ContactDetails association $id")

        usagePoint.addContact(contactDetails)

        return readContactDetails(contactDetails, table, resultSet, setIdentifier)
    }

    /**
     * Create a [UsagePoint] and populate its fields from [TableUsagePoints].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [UsagePoint] fields from.
     * @param resultSet The record in the database table containing the fields for this [UsagePoint].
     * @param setIdentifier A callback to register the mRID of this [UsagePoint] for logging purposes.
     *
     * @return true if the [UsagePoint] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableUsagePoints, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val usagePoint = UsagePoint(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            usagePointLocation = service.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
            isVirtual = resultSet.getNullableBoolean(table.IS_VIRTUAL.queryIndex)
            connectionCategory = resultSet.getNullableString(table.CONNECTION_CATEGORY.queryIndex)
            ratedPower = resultSet.getNullableInt(table.RATED_POWER.queryIndex)
            approvedInverterCapacity = resultSet.getNullableInt(table.APPROVED_INVERTER_CAPACITY.queryIndex)
            phaseCode = resultSet.getString(table.PHASE_CODE.queryIndex).let { PhaseCode.valueOf(it) }
        }

        return readIdentifiedObject(usagePoint, table, resultSet) && service.addOrThrow(usagePoint)
    }

    // #######################
    // # IEC61968 Operations #
    // #######################

    /**
     * Create an [OperationalRestriction] and populate its fields from [TableOperationalRestrictions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [OperationalRestriction] fields from.
     * @param resultSet The record in the database table containing the fields for this [OperationalRestriction].
     * @param setIdentifier A callback to register the mRID of this [OperationalRestriction] for logging purposes.
     *
     * @return true if the [OperationalRestriction] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableOperationalRestrictions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val operationalRestriction = OperationalRestriction(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readDocument(operationalRestriction, table, resultSet) && service.addOrThrow(operationalRestriction)
    }

    // #####################################
    // # IEC61970 Base Auxiliary Equipment #
    // #####################################

    @Throws(SQLException::class)
    private fun readAuxiliaryEquipment(
        service: NetworkService,
        auxiliaryEquipment: AuxiliaryEquipment,
        table: TableAuxiliaryEquipment,
        resultSet: ResultSet
    ): Boolean {
        auxiliaryEquipment.apply {
            terminal =
                service.ensureGet(resultSet.getNullableString(table.TERMINAL_MRID.queryIndex), typeNameAndMRID())
        }

        return readEquipment(service, auxiliaryEquipment, table, resultSet)
    }

    /**
     * Create a [CurrentTransformer] and populate its fields from [TableCurrentTransformers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [CurrentTransformer] fields from.
     * @param resultSet The record in the database table containing the fields for this [CurrentTransformer].
     * @param setIdentifier A callback to register the mRID of this [CurrentTransformer] for logging purposes.
     *
     * @return true if the [CurrentTransformer] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCurrentTransformers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val currentTransformer = CurrentTransformer(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            assetInfo = service.ensureGet(resultSet.getNullableString(table.CURRENT_TRANSFORMER_INFO_MRID.queryIndex), typeNameAndMRID())
            coreBurden = resultSet.getNullableInt(table.CORE_BURDEN.queryIndex)
        }

        return readSensor(service, currentTransformer, table, resultSet) && service.addOrThrow(currentTransformer)
    }

    /**
     * Create a [FaultIndicator] and populate its fields from [TableFaultIndicators].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [FaultIndicator] fields from.
     * @param resultSet The record in the database table containing the fields for this [FaultIndicator].
     * @param setIdentifier A callback to register the mRID of this [FaultIndicator] for logging purposes.
     *
     * @return true if the [FaultIndicator] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableFaultIndicators, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val faultIndicator = FaultIndicator(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readAuxiliaryEquipment(service, faultIndicator, table, resultSet) && service.addOrThrow(faultIndicator)
    }

    /**
     * Create a [PotentialTransformer] and populate its fields from [TablePotentialTransformers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PotentialTransformer] fields from.
     * @param resultSet The record in the database table containing the fields for this [PotentialTransformer].
     * @param setIdentifier A callback to register the mRID of this [PotentialTransformer] for logging purposes.
     *
     * @return true if the [PotentialTransformer] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePotentialTransformers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val potentialTransformer = PotentialTransformer(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            assetInfo = service.ensureGet(resultSet.getNullableString(table.POTENTIAL_TRANSFORMER_INFO_MRID.queryIndex), typeNameAndMRID())
            type = PotentialTransformerKind.valueOf(resultSet.getString(table.TYPE.queryIndex))
        }

        return readSensor(service, potentialTransformer, table, resultSet) && service.addOrThrow(potentialTransformer)
    }

    @Throws(SQLException::class)
    private fun readSensor(service: NetworkService, sensor: Sensor, table: TableSensors, resultSet: ResultSet): Boolean =
        readAuxiliaryEquipment(service, sensor, table, resultSet)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    @Throws(SQLException::class)
    private fun readAcDcTerminal(acDcTerminal: AcDcTerminal, table: TableAcDcTerminals, resultSet: ResultSet): Boolean =
        readIdentifiedObject(acDcTerminal, table, resultSet)

    /**
     * Create a [BaseVoltage] and populate its fields from [TableBaseVoltages].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [BaseVoltage] fields from.
     * @param resultSet The record in the database table containing the fields for this [BaseVoltage].
     * @param setIdentifier A callback to register the mRID of this [BaseVoltage] for logging purposes.
     *
     * @return true if the [BaseVoltage] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBaseVoltages, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val baseVoltage = BaseVoltage(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            nominalVoltage = resultSet.getInt(table.NOMINAL_VOLTAGE.queryIndex)
        }

        return readIdentifiedObject(baseVoltage, table, resultSet) && service.addOrThrow(baseVoltage)
    }

    @Throws(SQLException::class)
    private fun readConductingEquipment(
        service: NetworkService,
        conductingEquipment: ConductingEquipment,
        table: TableConductingEquipment,
        resultSet: ResultSet
    ): Boolean {
        conductingEquipment.apply {
            baseVoltage = service.ensureGet(
                resultSet.getNullableString(table.BASE_VOLTAGE_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readEquipment(service, conductingEquipment, table, resultSet)
    }

    /**
     * Create a [ConnectivityNode] and populate its fields from [TableConnectivityNodes].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ConnectivityNode] fields from.
     * @param resultSet The record in the database table containing the fields for this [ConnectivityNode].
     * @param setIdentifier A callback to register the mRID of this [ConnectivityNode] for logging purposes.
     *
     * @return true if the [ConnectivityNode] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableConnectivityNodes, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val connectivityNode = ConnectivityNode(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readIdentifiedObject(connectivityNode, table, resultSet) && service.addOrThrow(connectivityNode)
    }

    @Throws(SQLException::class)
    private fun readConnectivityNodeContainer(
        service: NetworkService,
        connectivityNodeContainer: ConnectivityNodeContainer,
        table: TableConnectivityNodeContainers,
        resultSet: ResultSet
    ): Boolean =
        readPowerSystemResource(service, connectivityNodeContainer, table, resultSet)

    @Throws(SQLException::class)
    private fun readCurve(curve: Curve, table: TableCurves, resultSet: ResultSet): Boolean =
        readIdentifiedObject(curve, table, resultSet)

    /**
     * Create a [CurveData] and populate its fields from [TableCurveData] then add it to associated [Curve].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [CurveData] fields from.
     * @param resultSet The record in the database table containing the fields for this [CurveData].
     * @param setIdentifier A callback to register the mRID of this [CurveData] and its associated [Curve] for logging purposes.
     *
     * @return true if the [CurveData] was successfully read from the database and added to associated [Curve].
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCurveData, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val curveMRID = setIdentifier(resultSet.getString(table.CURVE_MRID.queryIndex))
        val xValue = resultSet.getFloat(table.X_VALUE.queryIndex)
        val id = setIdentifier("$curveMRID-x-$xValue")

        val curve = service.getOrThrow<Curve>(curveMRID, "Curve to CurveData association $id")

        curve.addData(
            xValue,
            resultSet.getFloat(table.Y1_VALUE.queryIndex),
            resultSet.getNullableFloat(table.Y2_VALUE.queryIndex),
            resultSet.getNullableFloat(table.Y3_VALUE.queryIndex)
        )

        return true
    }

    @Throws(SQLException::class)
    private fun readEquipment(service: NetworkService, equipment: Equipment, table: TableEquipment, resultSet: ResultSet): Boolean {
        equipment.apply {
            normallyInService = resultSet.getBoolean(table.NORMALLY_IN_SERVICE.queryIndex)
            inService = resultSet.getBoolean(table.IN_SERVICE.queryIndex)
            commissionedDate = resultSet.getInstant(table.COMMISSIONED_DATE.queryIndex)
        }

        return readPowerSystemResource(service, equipment, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readEquipmentContainer(
        service: NetworkService,
        equipmentContainer: EquipmentContainer,
        table: TableEquipmentContainers,
        resultSet: ResultSet
    ): Boolean =
        readConnectivityNodeContainer(service, equipmentContainer, table, resultSet)

    /**
     * Create a [Feeder] and populate its fields from [TableFeeders].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Feeder] fields from.
     * @param resultSet The record in the database table containing the fields for this [Feeder].
     * @param setIdentifier A callback to register the mRID of this [Feeder] for logging purposes.
     *
     * @return true if the [Feeder] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableFeeders, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val feeder = Feeder(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            normalHeadTerminal = service.ensureGet(
                resultSet.getNullableString(table.NORMAL_HEAD_TERMINAL_MRID.queryIndex),
                typeNameAndMRID()
            )
            normalEnergizingSubstation =
                service.ensureGet(
                    resultSet.getNullableString(table.NORMAL_ENERGIZING_SUBSTATION_MRID.queryIndex),
                    typeNameAndMRID()
                )
            normalEnergizingSubstation?.addFeeder(this)
        }

        return readEquipmentContainer(service, feeder, table, resultSet) && service.addOrThrow(feeder)
    }

    /**
     * Create a [GeographicalRegion] and populate its fields from [TableGeographicalRegions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [GeographicalRegion] fields from.
     * @param resultSet The record in the database table containing the fields for this [GeographicalRegion].
     * @param setIdentifier A callback to register the mRID of this [GeographicalRegion] for logging purposes.
     *
     * @return true if the [GeographicalRegion] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableGeographicalRegions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val geographicalRegion = GeographicalRegion(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readIdentifiedObject(geographicalRegion, table, resultSet) && service.addOrThrow(geographicalRegion)
    }

    @Throws(SQLException::class)
    private fun readPowerSystemResource(
        service: NetworkService,
        powerSystemResource: PowerSystemResource,
        table: TablePowerSystemResources,
        resultSet: ResultSet
    ): Boolean {
        powerSystemResource.apply {
            location = service.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
            numControls = resultSet.getNullableInt(table.NUM_CONTROLS.queryIndex)
        }

        return readIdentifiedObject(powerSystemResource, table, resultSet)
    }

    /**
     * Create a [SubGeographicalRegion] and populate its fields from [TableSubGeographicalRegions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [SubGeographicalRegion] fields from.
     * @param resultSet The record in the database table containing the fields for this [SubGeographicalRegion].
     * @param setIdentifier A callback to register the mRID of this [SubGeographicalRegion] for logging purposes.
     *
     * @return true if the [SubGeographicalRegion] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSubGeographicalRegions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val subGeographicalRegion =
            SubGeographicalRegion(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
                geographicalRegion = service.ensureGet(
                    resultSet.getNullableString(table.GEOGRAPHICAL_REGION_MRID.queryIndex),
                    typeNameAndMRID()
                )
                geographicalRegion?.addSubGeographicalRegion(this)
            }

        return readIdentifiedObject(subGeographicalRegion, table, resultSet) && service.addOrThrow(subGeographicalRegion)
    }

    /**
     * Create a [Substation] and populate its fields from [TableSubstations].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Substation] fields from.
     * @param resultSet The record in the database table containing the fields for this [Substation].
     * @param setIdentifier A callback to register the mRID of this [Substation] for logging purposes.
     *
     * @return true if the [Substation] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSubstations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val substation = Substation(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            subGeographicalRegion = service.ensureGet(
                resultSet.getNullableString(table.SUB_GEOGRAPHICAL_REGION_MRID.queryIndex),
                typeNameAndMRID()
            )
            subGeographicalRegion?.addSubstation(this)
        }

        return readEquipmentContainer(service, substation, table, resultSet) && service.addOrThrow(substation)
    }

    /**
     * Create a [Terminal] and populate its fields from [TableTerminals].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Terminal] fields from.
     * @param resultSet The record in the database table containing the fields for this [Terminal].
     * @param setIdentifier A callback to register the mRID of this [Terminal] for logging purposes.
     *
     * @return true if the [Terminal] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableTerminals, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val terminal = Terminal(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)
            conductingEquipment = service.ensureGet(
                resultSet.getNullableString(table.CONDUCTING_EQUIPMENT_MRID.queryIndex),
                typeNameAndMRID()
            )
            conductingEquipment?.addTerminal(this)
            phases = PhaseCode.valueOf(resultSet.getString(table.PHASES.queryIndex))
        }

        service.connect(terminal, resultSet.getNullableString(table.CONNECTIVITY_NODE_MRID.queryIndex))

        return readAcDcTerminal(terminal, table, resultSet) && service.addOrThrow(terminal)
    }

    // #############################
    // # IEC61970 Base Equivalents #
    // #############################

    /**
     * Create an [EquivalentBranch] and populate its fields from [TableEquivalentBranches].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EquivalentBranch] fields from.
     * @param resultSet The record in the database table containing the fields for this [EquivalentBranch].
     * @param setIdentifier A callback to register the mRID of this [EquivalentBranch] for logging purposes.
     *
     * @return true if the [EquivalentBranch] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEquivalentBranches, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val equivalentBranch = EquivalentBranch(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            negativeR12 = resultSet.getNullableDouble(table.NEGATIVE_R12.queryIndex)
            negativeR21 = resultSet.getNullableDouble(table.NEGATIVE_R21.queryIndex)
            negativeX12 = resultSet.getNullableDouble(table.NEGATIVE_X12.queryIndex)
            negativeX21 = resultSet.getNullableDouble(table.NEGATIVE_X21.queryIndex)
            positiveR12 = resultSet.getNullableDouble(table.POSITIVE_R12.queryIndex)
            positiveR21 = resultSet.getNullableDouble(table.POSITIVE_R21.queryIndex)
            positiveX12 = resultSet.getNullableDouble(table.POSITIVE_X12.queryIndex)
            positiveX21 = resultSet.getNullableDouble(table.POSITIVE_X21.queryIndex)
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r21 = resultSet.getNullableDouble(table.R21.queryIndex)
            x = resultSet.getNullableDouble(table.X.queryIndex)
            x21 = resultSet.getNullableDouble(table.X21.queryIndex)
            zeroR12 = resultSet.getNullableDouble(table.ZERO_R12.queryIndex)
            zeroR21 = resultSet.getNullableDouble(table.ZERO_R21.queryIndex)
            zeroX12 = resultSet.getNullableDouble(table.ZERO_X12.queryIndex)
            zeroX21 = resultSet.getNullableDouble(table.ZERO_X21.queryIndex)
        }

        return readEquivalentEquipment(service, equivalentBranch, table, resultSet) && service.addOrThrow(equivalentBranch)
    }

    @Throws(SQLException::class)
    private fun readEquivalentEquipment(
        service: NetworkService,
        equivalentEquipment: EquivalentEquipment,
        table: TableEquivalentEquipment,
        resultSet: ResultSet
    ): Boolean =
        readConductingEquipment(service, equivalentEquipment, table, resultSet)

    // #######################################
    // # IEC61970 Base Generation Production #
    // #######################################

    /**
     * Create a [BatteryUnit] and populate its fields from [TableBatteryUnits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [BatteryUnit] fields from.
     * @param resultSet The record in the database table containing the fields for this [BatteryUnit].
     * @param setIdentifier A callback to register the mRID of this [BatteryUnit] for logging purposes.
     *
     * @return true if the [BatteryUnit] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBatteryUnits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val batteryUnit = BatteryUnit(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            batteryState = BatteryStateKind.valueOf(resultSet.getString(table.BATTERY_STATE.queryIndex))
            ratedE = resultSet.getNullableLong(table.RATED_E.queryIndex)
            storedE = resultSet.getNullableLong(table.STORED_E.queryIndex)
        }

        return readPowerElectronicsUnit(service, batteryUnit, table, resultSet) && service.addOrThrow(batteryUnit)
    }

    /**
     * Create a [PhotoVoltaicUnit] and populate its fields from [TablePhotoVoltaicUnits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PhotoVoltaicUnit] fields from.
     * @param resultSet The record in the database table containing the fields for this [PhotoVoltaicUnit].
     * @param setIdentifier A callback to register the mRID of this [PhotoVoltaicUnit] for logging purposes.
     *
     * @return true if the [PhotoVoltaicUnit] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePhotoVoltaicUnits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val photoVoltaicUnit = PhotoVoltaicUnit(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readPowerElectronicsUnit(service, photoVoltaicUnit, table, resultSet) && service.addOrThrow(photoVoltaicUnit)
    }

    @Throws(SQLException::class)
    private fun readPowerElectronicsUnit(
        service: NetworkService,
        powerElectronicsUnit: PowerElectronicsUnit,
        table: TablePowerElectronicsUnits,
        resultSet: ResultSet
    ): Boolean {
        powerElectronicsUnit.apply {
            powerElectronicsConnection = service.ensureGet(
                resultSet.getNullableString(table.POWER_ELECTRONICS_CONNECTION_MRID.queryIndex),
                typeNameAndMRID()
            )
            powerElectronicsConnection?.addUnit(this)

            maxP = resultSet.getNullableInt(table.MAX_P.queryIndex)
            minP = resultSet.getNullableInt(table.MIN_P.queryIndex)
        }

        return readEquipment(service, powerElectronicsUnit, table, resultSet)
    }

    /**
     * Create a [PowerElectronicsWindUnit] and populate its fields from [TablePowerElectronicsWindUnits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerElectronicsWindUnit] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerElectronicsWindUnit].
     * @param setIdentifier A callback to register the mRID of this [PowerElectronicsWindUnit] for logging purposes.
     *
     * @return true if the [PowerElectronicsWindUnit] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerElectronicsWindUnits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerElectronicsWindUnit = PowerElectronicsWindUnit(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readPowerElectronicsUnit(service, powerElectronicsWindUnit, table, resultSet) && service.addOrThrow(powerElectronicsWindUnit)
    }

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Create an [Accumulator] and populate its fields from [TableAccumulators].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Accumulator] fields from.
     * @param resultSet The record in the database table containing the fields for this [Accumulator].
     * @param setIdentifier A callback to register the mRID of this [Accumulator] for logging purposes.
     *
     * @return true if the [Accumulator] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAccumulators, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val meas = Accumulator(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readMeasurement(service, meas, table, resultSet) && service.addOrThrow(meas)
    }

    /**
     * Create an [Analog] and populate its fields from [TableAnalogs].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Analog] fields from.
     * @param resultSet The record in the database table containing the fields for this [Analog].
     * @param setIdentifier A callback to register the mRID of this [Analog] for logging purposes.
     *
     * @return true if the [Analog] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAnalogs, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val meas = Analog(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            positiveFlowIn = resultSet.getNullableBoolean(table.POSITIVE_FLOW_IN.queryIndex)
        }

        return readMeasurement(service, meas, table, resultSet) && service.addOrThrow(meas)
    }

    /**
     * Create a [Control] and populate its fields from [TableControls].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Control] fields from.
     * @param resultSet The record in the database table containing the fields for this [Control].
     * @param setIdentifier A callback to register the mRID of this [Control] for logging purposes.
     *
     * @return true if the [Control] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableControls, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val control = Control(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            powerSystemResourceMRID = resultSet.getNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex)
        }

        return readIoPoint(control, table, resultSet) && service.addOrThrow(control)
    }

    /**
     * Create a [Discrete] and populate its fields from [TableDiscretes].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Discrete] fields from.
     * @param resultSet The record in the database table containing the fields for this [Discrete].
     * @param setIdentifier A callback to register the mRID of this [Discrete] for logging purposes.
     *
     * @return true if the [Discrete] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableDiscretes, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val meas = Discrete(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readMeasurement(service, meas, table, resultSet) && service.addOrThrow(meas)
    }

    @Throws(SQLException::class)
    private fun readIoPoint(ioPoint: IoPoint, table: TableIoPoints, resultSet: ResultSet): Boolean =
        readIdentifiedObject(ioPoint, table, resultSet)

    @Throws(SQLException::class)
    private fun readMeasurement(service: NetworkService, measurement: Measurement, table: TableMeasurements, resultSet: ResultSet): Boolean {
        measurement.apply {
            powerSystemResourceMRID = resultSet.getNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex)
            remoteSource = service.ensureGet(
                resultSet.getNullableString(table.REMOTE_SOURCE_MRID.queryIndex),
                typeNameAndMRID()
            )
            remoteSource?.measurement = this
            terminalMRID = resultSet.getNullableString(table.TERMINAL_MRID.queryIndex)
            phases = PhaseCode.valueOf(resultSet.getString(table.PHASES.queryIndex))
            unitSymbol = UnitSymbol.valueOf(resultSet.getString(table.UNIT_SYMBOL.queryIndex))
        }
        return readIdentifiedObject(measurement, table, resultSet)
    }

    // ############################
    // # IEC61970 Base Protection #
    // ############################

    /**
     * Create a [CurrentRelay] and populate its fields from [TableCurrentRelays].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [CurrentRelay] fields from.
     * @param resultSet The record in the database table containing the fields for this [CurrentRelay].
     * @param setIdentifier A callback to register the mRID of this [CurrentRelay] for logging purposes.
     *
     * @return true if the [CurrentRelay] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCurrentRelays, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val currentRelay = CurrentRelay(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            currentLimit1 = resultSet.getNullableDouble(table.CURRENT_LIMIT_1.queryIndex)
            inverseTimeFlag = resultSet.getNullableBoolean(table.INVERSE_TIME_FLAG.queryIndex)
            timeDelay1 = resultSet.getNullableDouble(table.TIME_DELAY_1.queryIndex)
        }

        return readProtectionRelayFunction(service, currentRelay, table, resultSet) && service.addOrThrow(currentRelay)
    }

    // #######################
    // # IEC61970 Base Scada #
    // #######################

    /**
     * Create a [RemoteControl] and populate its fields from [TableRemoteControls].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [RemoteControl] fields from.
     * @param resultSet The record in the database table containing the fields for this [RemoteControl].
     * @param setIdentifier A callback to register the mRID of this [RemoteControl] for logging purposes.
     *
     * @return true if the [RemoteControl] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableRemoteControls, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val remoteControl = RemoteControl(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            control =
                service.ensureGet(resultSet.getNullableString(table.CONTROL_MRID.queryIndex), typeNameAndMRID())
            control?.remoteControl = this
        }

        return readRemotePoint(remoteControl, table, resultSet) && service.addOrThrow(remoteControl)
    }

    @Throws(SQLException::class)
    private fun readRemotePoint(remotePoint: RemotePoint, table: TableRemotePoints, resultSet: ResultSet): Boolean =
        readIdentifiedObject(remotePoint, table, resultSet)

    /**
     * Create a [RemoteSource] and populate its fields from [TableRemoteSources].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [RemoteSource] fields from.
     * @param resultSet The record in the database table containing the fields for this [RemoteSource].
     * @param setIdentifier A callback to register the mRID of this [RemoteSource] for logging purposes.
     *
     * @return true if the [RemoteSource] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableRemoteSources, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val remoteSource = RemoteSource(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readRemotePoint(remoteSource, table, resultSet) && service.addOrThrow(remoteSource)
    }

    // #######################
    // # IEC61970 Base Wires #
    // #######################

    /**
     * Create an [AcLineSegment] and populate its fields from [TableAcLineSegments].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [AcLineSegment] fields from.
     * @param resultSet The record in the database table containing the fields for this [AcLineSegment].
     * @param setIdentifier A callback to register the mRID of this [AcLineSegment] for logging purposes.
     *
     * @return true if the [AcLineSegment] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAcLineSegments, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val acLineSegment = AcLineSegment(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            perLengthImpedance = service.ensureGet(resultSet.getNullableString(table.PER_LENGTH_IMPEDANCE_MRID.queryIndex), typeNameAndMRID())
        }

        return readConductor(service, acLineSegment, table, resultSet) && service.addOrThrow(acLineSegment)
    }

    /**
     * Create a [Breaker] and populate its fields from [TableBreakers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Breaker] fields from.
     * @param resultSet The record in the database table containing the fields for this [Breaker].
     * @param setIdentifier A callback to register the mRID of this [Breaker] for logging purposes.
     *
     * @return true if the [Breaker] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBreakers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val breaker = Breaker(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            inTransitTime = resultSet.getNullableDouble(table.IN_TRANSIT_TIME.queryIndex)
        }

        return readProtectedSwitch(service, breaker, table, resultSet) && service.addOrThrow(breaker)
    }

    /**
     * Create a [BusbarSection] and populate its fields from [TableBusbarSections].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [BusbarSection] fields from.
     * @param resultSet The record in the database table containing the fields for this [BusbarSection].
     * @param setIdentifier A callback to register the mRID of this [BusbarSection] for logging purposes.
     *
     * @return true if the [BusbarSection] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBusbarSections, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val busbarSection = BusbarSection(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readConnector(service, busbarSection, table, resultSet) && service.addOrThrow(busbarSection)
    }

    /**
     * Create a [Clamp] and populate its fields from [TableClamps].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Clamp] fields from.
     * @param resultSet The record in the database table containing the fields for this [Clamp].
     * @param setIdentifier A callback to register the mRID of this [Clamp] for logging purposes.
     *
     * @return true if the [Clamp] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableClamps, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val clamp = Clamp(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            lengthFromTerminal1 = resultSet.getNullableDouble(table.LENGTH_FROM_TERMINAL_1.queryIndex)
            acLineSegment = service.ensureGet(resultSet.getString(table.AC_LINE_SEGMENT_MRID.queryIndex), typeNameAndMRID())
            acLineSegment?.addClamp(this)
        }

        return readConductingEquipment(service, clamp, table, resultSet) && service.addOrThrow(clamp)
    }

    @Throws(SQLException::class)
    private fun readConductor(service: NetworkService, conductor: Conductor, table: TableConductors, resultSet: ResultSet): Boolean {
        conductor.apply {
            length = resultSet.getNullableDouble(table.LENGTH.queryIndex)
            designTemperature = resultSet.getNullableInt(table.DESIGN_TEMPERATURE.queryIndex)
            designRating = resultSet.getNullableDouble(table.DESIGN_RATING.queryIndex)
            assetInfo = service.ensureGet(
                resultSet.getNullableString(table.WIRE_INFO_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readConductingEquipment(service, conductor, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readConnector(service: NetworkService, connector: Connector, table: TableConnectors, resultSet: ResultSet): Boolean =
        readConductingEquipment(service, connector, table, resultSet)

    /**
     * Create a [Cut] and populate its fields from [TableCuts].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Cut] fields from.
     * @param resultSet The record in the database table containing the fields for this [Cut].
     * @param setIdentifier A callback to register the mRID of this [Cut] for logging purposes.
     *
     * @return true if the [Cut] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCuts, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val cut = Cut(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            lengthFromTerminal1 = resultSet.getNullableDouble(table.LENGTH_FROM_TERMINAL_1.queryIndex)
            acLineSegment = service.ensureGet(resultSet.getString(table.AC_LINE_SEGMENT_MRID.queryIndex), typeNameAndMRID())
            acLineSegment?.addCut(this)
        }

        return readSwitch(service, cut, table, resultSet) && service.addOrThrow(cut)
    }

    /**
     * Create a [Disconnector] and populate its fields from [TableDisconnectors].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Disconnector] fields from.
     * @param resultSet The record in the database table containing the fields for this [Disconnector].
     * @param setIdentifier A callback to register the mRID of this [Disconnector] for logging purposes.
     *
     * @return true if the [Disconnector] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableDisconnectors, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val disconnector = Disconnector(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readSwitch(service, disconnector, table, resultSet) && service.addOrThrow(disconnector)
    }

    @Throws(SQLException::class)
    private fun readEarthFaultCompensator(
        service: NetworkService,
        earthFaultCompensator: EarthFaultCompensator,
        table: TableEarthFaultCompensators,
        resultSet: ResultSet
    ): Boolean {
        earthFaultCompensator.apply {
            r = resultSet.getNullableDouble(table.R.queryIndex)
        }

        return readConductingEquipment(service, earthFaultCompensator, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readEnergyConnection(
        service: NetworkService,
        energyConnection: EnergyConnection,
        table: TableEnergyConnections,
        resultSet: ResultSet
    ): Boolean =
        readConductingEquipment(service, energyConnection, table, resultSet)

    /**
     * Create an [EnergyConsumer] and populate its fields from [TableEnergyConsumers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EnergyConsumer] fields from.
     * @param resultSet The record in the database table containing the fields for this [EnergyConsumer].
     * @param setIdentifier A callback to register the mRID of this [EnergyConsumer] for logging purposes.
     *
     * @return true if the [EnergyConsumer] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEnergyConsumers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val energyConsumer = EnergyConsumer(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            customerCount = resultSet.getNullableInt(table.CUSTOMER_COUNT.queryIndex)
            grounded = resultSet.getNullableBoolean(table.GROUNDED.queryIndex)
            p = resultSet.getNullableDouble(table.P.queryIndex)
            q = resultSet.getNullableDouble(table.Q.queryIndex)
            pFixed = resultSet.getNullableDouble(table.P_FIXED.queryIndex)
            qFixed = resultSet.getNullableDouble(table.Q_FIXED.queryIndex)
            phaseConnection = PhaseShuntConnectionKind.valueOf(resultSet.getString(table.PHASE_CONNECTION.queryIndex))
        }

        return readEnergyConnection(service, energyConsumer, table, resultSet) && service.addOrThrow(energyConsumer)
    }

    /**
     * Create an [EnergyConsumerPhase] and populate its fields from [TableEnergyConsumerPhases].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EnergyConsumerPhase] fields from.
     * @param resultSet The record in the database table containing the fields for this [EnergyConsumerPhase].
     * @param setIdentifier A callback to register the mRID of this [EnergyConsumerPhase] for logging purposes.
     *
     * @return true if the [EnergyConsumerPhase] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEnergyConsumerPhases, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val energyConsumerPhase = EnergyConsumerPhase(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            energyConsumer =
                service.ensureGet(resultSet.getString(table.ENERGY_CONSUMER_MRID.queryIndex), typeNameAndMRID())
            energyConsumer?.addPhase(this)

            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
            p = resultSet.getNullableDouble(table.P.queryIndex)
            q = resultSet.getNullableDouble(table.Q.queryIndex)
            pFixed = resultSet.getNullableDouble(table.P_FIXED.queryIndex)
            qFixed = resultSet.getNullableDouble(table.Q_FIXED.queryIndex)
        }

        return readPowerSystemResource(service, energyConsumerPhase, table, resultSet) && service.addOrThrow(energyConsumerPhase)
    }

    /**
     * Create an [EnergySource] and populate its fields from [TableEnergySources].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EnergySource] fields from.
     * @param resultSet The record in the database table containing the fields for this [EnergySource].
     * @param setIdentifier A callback to register the mRID of this [EnergySource] for logging purposes.
     *
     * @return true if the [EnergySource] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEnergySources, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val energySource = EnergySource(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            activePower = resultSet.getNullableDouble(table.ACTIVE_POWER.queryIndex)
            reactivePower = resultSet.getNullableDouble(table.REACTIVE_POWER.queryIndex)
            voltageAngle = resultSet.getNullableDouble(table.VOLTAGE_ANGLE.queryIndex)
            voltageMagnitude = resultSet.getNullableDouble(table.VOLTAGE_MAGNITUDE.queryIndex)
            pMax = resultSet.getNullableDouble(table.P_MAX.queryIndex)
            pMin = resultSet.getNullableDouble(table.P_MIN.queryIndex)
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r0 = resultSet.getNullableDouble(table.R0.queryIndex)
            rn = resultSet.getNullableDouble(table.RN.queryIndex)
            x = resultSet.getNullableDouble(table.X.queryIndex)
            x0 = resultSet.getNullableDouble(table.X0.queryIndex)
            xn = resultSet.getNullableDouble(table.XN.queryIndex)
            isExternalGrid = resultSet.getNullableBoolean(table.IS_EXTERNAL_GRID.queryIndex)
            rMin = resultSet.getNullableDouble(table.R_MIN.queryIndex)
            rnMin = resultSet.getNullableDouble(table.RN_MIN.queryIndex)
            r0Min = resultSet.getNullableDouble(table.R0_MIN.queryIndex)
            xMin = resultSet.getNullableDouble(table.X_MIN.queryIndex)
            xnMin = resultSet.getNullableDouble(table.XN_MIN.queryIndex)
            x0Min = resultSet.getNullableDouble(table.X0_MIN.queryIndex)
            rMax = resultSet.getNullableDouble(table.R_MAX.queryIndex)
            rnMax = resultSet.getNullableDouble(table.RN_MAX.queryIndex)
            r0Max = resultSet.getNullableDouble(table.R0_MAX.queryIndex)
            xMax = resultSet.getNullableDouble(table.X_MAX.queryIndex)
            xnMax = resultSet.getNullableDouble(table.XN_MAX.queryIndex)
            x0Max = resultSet.getNullableDouble(table.X0_MAX.queryIndex)
        }

        return readEnergyConnection(service, energySource, table, resultSet) && service.addOrThrow(energySource)
    }

    /**
     * Create an [EnergySourcePhase] and populate its fields from [TableEnergySourcePhases].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [EnergySourcePhase] fields from.
     * @param resultSet The record in the database table containing the fields for this [EnergySourcePhase].
     * @param setIdentifier A callback to register the mRID of this [EnergySourcePhase] for logging purposes.
     *
     * @return true if the [EnergySourcePhase] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEnergySourcePhases, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val energySourcePhase = EnergySourcePhase(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            energySource =
                service.ensureGet(resultSet.getString(table.ENERGY_SOURCE_MRID.queryIndex), typeNameAndMRID())
            energySource?.addPhase(this)

            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
        }

        return readPowerSystemResource(service, energySourcePhase, table, resultSet) && service.addOrThrow(energySourcePhase)
    }

    /**
     * Create a [Fuse] and populate its fields from [TableFuses].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Fuse] fields from.
     * @param resultSet The record in the database table containing the fields for this [Fuse].
     * @param setIdentifier A callback to register the mRID of this [Fuse] for logging purposes.
     *
     * @return true if the [Fuse] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableFuses, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val fuse = Fuse(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            function = service.ensureGet(resultSet.getString(table.FUNCTION_MRID.queryIndex), typeNameAndMRID())
        }

        return readSwitch(service, fuse, table, resultSet) && service.addOrThrow(fuse)
    }

    /**
     * Create a [Ground] and populate its fields from [TableGrounds].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Ground] fields from.
     * @param resultSet The record in the database table containing the fields for this [Ground].
     * @param setIdentifier A callback to register the mRID of this [Ground] for logging purposes.
     *
     * @return true if the [Ground] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableGrounds, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val ground = Ground(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readConductingEquipment(service, ground, table, resultSet) && service.addOrThrow(ground)
    }

    /**
     * Create a [GroundDisconnector] and populate its fields from [TableGroundDisconnectors].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [GroundDisconnector] fields from.
     * @param resultSet The record in the database table containing the fields for this [GroundDisconnector].
     * @param setIdentifier A callback to register the mRID of this [GroundDisconnector] for logging purposes.
     *
     * @return true if the [GroundDisconnector] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableGroundDisconnectors, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val groundDisconnector = GroundDisconnector(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readSwitch(service, groundDisconnector, table, resultSet) && service.addOrThrow(groundDisconnector)
    }

    /**
     * Create a [GroundingImpedance] and populate its fields from [TableGroundingImpedances].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [GroundingImpedance] fields from.
     * @param resultSet The record in the database table containing the fields for this [GroundingImpedance].
     * @param setIdentifier A callback to register the mRID of this [GroundingImpedance] for logging purposes.
     *
     * @return true if the [GroundingImpedance] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableGroundingImpedances, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val groundingImpedance = GroundingImpedance(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            x = resultSet.getNullableDouble(table.X.queryIndex)
        }

        return readEarthFaultCompensator(service, groundingImpedance, table, resultSet) && service.addOrThrow(groundingImpedance)
    }

    /**
     * Create a [Jumper] and populate its fields from [TableJumpers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Jumper] fields from.
     * @param resultSet The record in the database table containing the fields for this [Jumper].
     * @param setIdentifier A callback to register the mRID of this [Jumper] for logging purposes.
     *
     * @return true if the [Jumper] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableJumpers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val jumper = Jumper(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readSwitch(service, jumper, table, resultSet) && service.addOrThrow(jumper)
    }

    /**
     * Create a [Junction] and populate its fields from [TableJunctions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Junction] fields from.
     * @param resultSet The record in the database table containing the fields for this [Junction].
     * @param setIdentifier A callback to register the mRID of this [Junction] for logging purposes.
     *
     * @return true if the [Junction] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableJunctions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val junction = Junction(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readConnector(service, junction, table, resultSet) && service.addOrThrow(junction)
    }

    @Throws(SQLException::class)
    private fun readLine(service: NetworkService, line: Line, table: TableLines, resultSet: ResultSet): Boolean =
        readEquipmentContainer(service, line, table, resultSet)

    /**
     * Create a [LinearShuntCompensator] and populate its fields from [TableLinearShuntCompensators].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [LinearShuntCompensator] fields from.
     * @param resultSet The record in the database table containing the fields for this [LinearShuntCompensator].
     * @param setIdentifier A callback to register the mRID of this [LinearShuntCompensator] for logging purposes.
     *
     * @return true if the [LinearShuntCompensator] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLinearShuntCompensators, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val linearShuntCompensator =
            LinearShuntCompensator(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
                b0PerSection = resultSet.getNullableDouble(table.B0_PER_SECTION.queryIndex)
                bPerSection = resultSet.getNullableDouble(table.B_PER_SECTION.queryIndex)
                g0PerSection = resultSet.getNullableDouble(table.G0_PER_SECTION.queryIndex)
                gPerSection = resultSet.getNullableDouble(table.G_PER_SECTION.queryIndex)
            }

        return readShuntCompensator(service, linearShuntCompensator, table, resultSet) && service.addOrThrow(linearShuntCompensator)
    }

    /**
     * Create a [LoadBreakSwitch] and populate its fields from [TableLoadBreakSwitches].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [LoadBreakSwitch] fields from.
     * @param resultSet The record in the database table containing the fields for this [LoadBreakSwitch].
     * @param setIdentifier A callback to register the mRID of this [LoadBreakSwitch] for logging purposes.
     *
     * @return true if the [LoadBreakSwitch] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLoadBreakSwitches, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val loadBreakSwitch = LoadBreakSwitch(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readProtectedSwitch(service, loadBreakSwitch, table, resultSet) && service.addOrThrow(loadBreakSwitch)
    }

    @Throws(SQLException::class)
    private fun readPerLengthImpedance(perLengthImpedance: PerLengthImpedance, table: TablePerLengthImpedances, resultSet: ResultSet): Boolean =
        readPerLengthLineParameter(perLengthImpedance, table, resultSet)

    @Throws(SQLException::class)
    private fun readPerLengthLineParameter(perLengthLineParameter: PerLengthLineParameter, table: TablePerLengthLineParameters, resultSet: ResultSet): Boolean =
        readIdentifiedObject(perLengthLineParameter, table, resultSet)

    /**
     * Create a [PerLengthPhaseImpedance] and populate its fields from [TablePerLengthPhaseImpedances].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PerLengthPhaseImpedance] fields from.
     * @param resultSet The record in the database table containing the fields for this [PerLengthPhaseImpedance].
     * @param setIdentifier A callback to register the mRID of this [PerLengthPhaseImpedance] for logging purposes.
     *
     * @return true if the [PerLengthPhaseImpedance] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePerLengthPhaseImpedances, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val perLengthPhaseImpedance = PerLengthPhaseImpedance(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readPerLengthImpedance(perLengthPhaseImpedance, table, resultSet) && service.addOrThrow(perLengthPhaseImpedance)
    }

    /**
     * Create a [PerLengthSequenceImpedance] and populate its fields from [TablePerLengthSequenceImpedances].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PerLengthSequenceImpedance] fields from.
     * @param resultSet The record in the database table containing the fields for this [PerLengthSequenceImpedance].
     * @param setIdentifier A callback to register the mRID of this [PerLengthSequenceImpedance] for logging purposes.
     *
     * @return true if the [PerLengthSequenceImpedance] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePerLengthSequenceImpedances, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val perLengthSequenceImpedance =
            PerLengthSequenceImpedance(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
                r = resultSet.getNullableDouble(table.R.queryIndex)
                x = resultSet.getNullableDouble(table.X.queryIndex)
                r0 = resultSet.getNullableDouble(table.R0.queryIndex)
                x0 = resultSet.getNullableDouble(table.X0.queryIndex)
                bch = resultSet.getNullableDouble(table.BCH.queryIndex)
                gch = resultSet.getNullableDouble(table.GCH.queryIndex)
                b0ch = resultSet.getNullableDouble(table.B0CH.queryIndex)
                g0ch = resultSet.getNullableDouble(table.G0CH.queryIndex)
            }

        return readPerLengthImpedance(perLengthSequenceImpedance, table, resultSet) && service.addOrThrow(perLengthSequenceImpedance)
    }

    /**
     * Create a [PetersenCoil] and populate its fields from [TablePetersenCoils].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PetersenCoil] fields from.
     * @param resultSet The record in the database table containing the fields for this [PetersenCoil].
     * @param setIdentifier A callback to register the mRID of this [PetersenCoil] for logging purposes.
     *
     * @return true if the [PetersenCoil] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePetersenCoils, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val petersenCoil = PetersenCoil(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            xGroundNominal = resultSet.getNullableDouble(table.X_GROUND_NOMINAL.queryIndex)
        }

        return readEarthFaultCompensator(service, petersenCoil, table, resultSet) && service.addOrThrow(petersenCoil)
    }

    /**
     * Create a [PhaseImpedanceData] and populate its fields from [TablePhaseImpedanceData] then add it to associated [PerLengthPhaseImpedance].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PhaseImpedanceData] fields from.
     * @param resultSet The record in the database table containing the fields for this [PhaseImpedanceData].
     * @param setIdentifier A callback to register the mRID of this [PhaseImpedanceData] and its associated [PerLengthPhaseImpedance] for logging purposes.
     *
     * @return true if the [PhaseImpedanceData] was successfully read from the database and added to associated [PerLengthPhaseImpedance].
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePhaseImpedanceData, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val perLengthPhaseImpedanceMRID = setIdentifier(resultSet.getString(table.PER_LENGTH_PHASE_IMPEDANCE_MRID.queryIndex))
        val id = setIdentifier(perLengthPhaseImpedanceMRID)

        val perLengthPhaseImpedance =
            service.getOrThrow<PerLengthPhaseImpedance>(perLengthPhaseImpedanceMRID, "PerLengthPhaseImpedance to PhaseImpedanceData association $id")

        perLengthPhaseImpedance.addData(
            PhaseImpedanceData(
                SinglePhaseKind.valueOf(resultSet.getString(table.FROM_PHASE.queryIndex)),
                SinglePhaseKind.valueOf(resultSet.getString(table.TO_PHASE.queryIndex)),
                resultSet.getNullableDouble(table.B.queryIndex),
                resultSet.getNullableDouble(table.G.queryIndex),
                resultSet.getNullableDouble(table.R.queryIndex),
                resultSet.getNullableDouble(table.X.queryIndex),
            )
        )

        return true
    }

    /**
     * Create a [PowerElectronicsConnection] and populate its fields from [TablePowerElectronicsConnections].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerElectronicsConnection] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerElectronicsConnection].
     * @param setIdentifier A callback to register the mRID of this [PowerElectronicsConnection] for logging purposes.
     *
     * @return true if the [PowerElectronicsConnection] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerElectronicsConnections, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerElectronicsConnection = PowerElectronicsConnection(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            maxIFault = resultSet.getNullableInt(table.MAX_I_FAULT.queryIndex)
            maxQ = resultSet.getNullableDouble(table.MAX_Q.queryIndex)
            minQ = resultSet.getNullableDouble(table.MIN_Q.queryIndex)
            p = resultSet.getNullableDouble(table.P.queryIndex)
            q = resultSet.getNullableDouble(table.Q.queryIndex)
            ratedU = resultSet.getNullableInt(table.RATED_U.queryIndex)
            ratedS = resultSet.getNullableInt(table.RATED_S.queryIndex)
            inverterStandard = resultSet.getNullableString(table.INVERTER_STANDARD.queryIndex)
            sustainOpOvervoltLimit = resultSet.getNullableInt(table.SUSTAIN_OP_OVERVOLT_LIMIT.queryIndex)
            stopAtOverFreq = resultSet.getNullableFloat(table.STOP_AT_OVER_FREQ.queryIndex)
            stopAtUnderFreq = resultSet.getNullableFloat(table.STOP_AT_UNDER_FREQ.queryIndex)
            invVoltWattRespMode = resultSet.getNullableBoolean(table.INV_VOLT_WATT_RESP_MODE.queryIndex)
            invWattRespV1 = resultSet.getNullableInt(table.INV_WATT_RESP_V1.queryIndex)
            invWattRespV2 = resultSet.getNullableInt(table.INV_WATT_RESP_V2.queryIndex)
            invWattRespV3 = resultSet.getNullableInt(table.INV_WATT_RESP_V3.queryIndex)
            invWattRespV4 = resultSet.getNullableInt(table.INV_WATT_RESP_V4.queryIndex)
            invWattRespPAtV1 = resultSet.getNullableFloat(table.INV_WATT_RESP_P_AT_V1.queryIndex)
            invWattRespPAtV2 = resultSet.getNullableFloat(table.INV_WATT_RESP_P_AT_V2.queryIndex)
            invWattRespPAtV3 = resultSet.getNullableFloat(table.INV_WATT_RESP_P_AT_V3.queryIndex)
            invWattRespPAtV4 = resultSet.getNullableFloat(table.INV_WATT_RESP_P_AT_V4.queryIndex)
            invVoltVarRespMode = resultSet.getNullableBoolean(table.INV_VOLT_VAR_RESP_MODE.queryIndex)
            invVarRespV1 = resultSet.getNullableInt(table.INV_VAR_RESP_V1.queryIndex)
            invVarRespV2 = resultSet.getNullableInt(table.INV_VAR_RESP_V2.queryIndex)
            invVarRespV3 = resultSet.getNullableInt(table.INV_VAR_RESP_V3.queryIndex)
            invVarRespV4 = resultSet.getNullableInt(table.INV_VAR_RESP_V4.queryIndex)
            invVarRespQAtV1 = resultSet.getNullableFloat(table.INV_VAR_RESP_Q_AT_V1.queryIndex)
            invVarRespQAtV2 = resultSet.getNullableFloat(table.INV_VAR_RESP_Q_AT_V2.queryIndex)
            invVarRespQAtV3 = resultSet.getNullableFloat(table.INV_VAR_RESP_Q_AT_V3.queryIndex)
            invVarRespQAtV4 = resultSet.getNullableFloat(table.INV_VAR_RESP_Q_AT_V4.queryIndex)
            invReactivePowerMode = resultSet.getNullableBoolean(table.INV_REACTIVE_POWER_MODE.queryIndex)
            invFixReactivePower = resultSet.getNullableFloat(table.INV_FIX_REACTIVE_POWER.queryIndex)
        }

        return readRegulatingCondEq(service, powerElectronicsConnection, table, resultSet) && service.addOrThrow(powerElectronicsConnection)
    }

    /**
     * Create a [PowerElectronicsConnectionPhase] and populate its fields from [TablePowerElectronicsConnectionPhases].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerElectronicsConnectionPhase] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerElectronicsConnectionPhase].
     * @param setIdentifier A callback to register the mRID of this [PowerElectronicsConnectionPhase] for logging purposes.
     *
     * @return true if the [PowerElectronicsConnectionPhase] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerElectronicsConnectionPhases, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerElectronicsConnectionPhase = PowerElectronicsConnectionPhase(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            powerElectronicsConnection =
                service.ensureGet(resultSet.getString(table.POWER_ELECTRONICS_CONNECTION_MRID.queryIndex), typeNameAndMRID())
            powerElectronicsConnection?.addPhase(this)

            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
            p = resultSet.getNullableDouble(table.P.queryIndex)
            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
            q = resultSet.getNullableDouble(table.Q.queryIndex)
        }

        return readPowerSystemResource(service, powerElectronicsConnectionPhase, table, resultSet) && service.addOrThrow(powerElectronicsConnectionPhase)
    }

    /**
     * Create a [PowerTransformer] and populate its fields from [TablePowerTransformers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerTransformer] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerTransformer].
     * @param setIdentifier A callback to register the mRID of this [PowerTransformer] for logging purposes.
     *
     * @return true if the [PowerTransformer] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerTransformers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerTransformer = PowerTransformer(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            vectorGroup = VectorGroup.valueOf(resultSet.getString(table.VECTOR_GROUP.queryIndex))
            transformerUtilisation = resultSet.getNullableDouble(table.TRANSFORMER_UTILISATION.queryIndex)
            constructionKind = TransformerConstructionKind.valueOf(resultSet.getString(table.CONSTRUCTION_KIND.queryIndex))
            function = TransformerFunctionKind.valueOf(resultSet.getString(table.FUNCTION.queryIndex))
            assetInfo = service.ensureGet(
                resultSet.getNullableString(table.POWER_TRANSFORMER_INFO_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readConductingEquipment(service, powerTransformer, table, resultSet) && service.addOrThrow(powerTransformer)
    }

    /**
     * Create a [PowerTransformerEnd] and populate its fields from [TablePowerTransformerEnds].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [PowerTransformerEnd] fields from.
     * @param resultSet The record in the database table containing the fields for this [PowerTransformerEnd].
     * @param setIdentifier A callback to register the mRID of this [PowerTransformerEnd] for logging purposes.
     *
     * @return true if the [PowerTransformerEnd] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TablePowerTransformerEnds, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val powerTransformerEnd = PowerTransformerEnd(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            endNumber = resultSet.getInt(table.END_NUMBER.queryIndex)
            powerTransformer = service.ensureGet(resultSet.getString(table.POWER_TRANSFORMER_MRID.queryIndex), typeNameAndMRID())
            powerTransformer?.addEnd(this)

            connectionKind = WindingConnection.valueOf(resultSet.getString(table.CONNECTION_KIND.queryIndex))
            phaseAngleClock = resultSet.getNullableInt(table.PHASE_ANGLE_CLOCK.queryIndex)
            b = resultSet.getNullableDouble(table.B.queryIndex)
            b0 = resultSet.getNullableDouble(table.B0.queryIndex)
            g = resultSet.getNullableDouble(table.G.queryIndex)
            g0 = resultSet.getNullableDouble(table.G0.queryIndex)
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r0 = resultSet.getNullableDouble(table.R0.queryIndex)
            ratedU = resultSet.getNullableInt(table.RATED_U.queryIndex)
            x = resultSet.getNullableDouble(table.X.queryIndex)
            x0 = resultSet.getNullableDouble(table.X0.queryIndex)
        }

        return readTransformerEnd(service, powerTransformerEnd, table, resultSet) && service.addOrThrow(powerTransformerEnd)
    }

    @Throws(SQLException::class)
    private fun readProtectedSwitch(service: NetworkService, protectedSwitch: ProtectedSwitch, table: TableProtectedSwitches, resultSet: ResultSet): Boolean {
        protectedSwitch.apply {
            breakingCapacity = resultSet.getNullableInt(table.BREAKING_CAPACITY.queryIndex)
        }

        return readSwitch(service, protectedSwitch, table, resultSet)
    }

    /**
     * Create a [RatioTapChanger] and populate its fields from [TableRatioTapChangers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [RatioTapChanger] fields from.
     * @param resultSet The record in the database table containing the fields for this [RatioTapChanger].
     * @param setIdentifier A callback to register the mRID of this [RatioTapChanger] for logging purposes.
     *
     * @return true if the [RatioTapChanger] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableRatioTapChangers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val ratioTapChanger = RatioTapChanger(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            transformerEnd = service.ensureGet(
                resultSet.getNullableString(table.TRANSFORMER_END_MRID.queryIndex),
                typeNameAndMRID()
            )
            transformerEnd?.ratioTapChanger = this

            stepVoltageIncrement = resultSet.getNullableDouble(table.STEP_VOLTAGE_INCREMENT.queryIndex)
        }

        return readTapChanger(service, ratioTapChanger, table, resultSet) && service.addOrThrow(ratioTapChanger)
    }

    /**
     * Create a [ReactiveCapabilityCurve] and populate its fields from [TableReactiveCapabilityCurves].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [ReactiveCapabilityCurve] fields from.
     * @param resultSet The record in the database table containing the fields for this [ReactiveCapabilityCurve].
     * @param setIdentifier A callback to register the mRID of this [ReactiveCapabilityCurve] for logging purposes.
     *
     * @return true if the [ReactiveCapabilityCurve] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableReactiveCapabilityCurves, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val reactiveCapabilityCurve = ReactiveCapabilityCurve(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readCurve(reactiveCapabilityCurve, table, resultSet) && service.addOrThrow(reactiveCapabilityCurve)
    }

    /**
     * Create a [Recloser] and populate its fields from [TableReclosers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Recloser] fields from.
     * @param resultSet The record in the database table containing the fields for this [Recloser].
     * @param setIdentifier A callback to register the mRID of this [Recloser] for logging purposes.
     *
     * @return true if the [Recloser] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableReclosers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val recloser = Recloser(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readProtectedSwitch(service, recloser, table, resultSet) && service.addOrThrow(recloser)
    }

    @Throws(SQLException::class)
    private fun readRegulatingCondEq(
        service: NetworkService,
        regulatingCondEq: RegulatingCondEq,
        table: TableRegulatingCondEq,
        resultSet: ResultSet
    ): Boolean {
        regulatingCondEq.apply {
            controlEnabled = resultSet.getNullableBoolean(table.CONTROL_ENABLED.queryIndex)
            // We use a resolver here because there is an ordering conflict between terminals, RegulatingCondEq, and RegulatingControls
            // We check this resolver has actually been resolved in the afterServiceRead of the database read and throw there if it hasn't.
            service.resolveOrDeferReference(Resolvers.regulatingControl(this), resultSet.getNullableString(table.REGULATING_CONTROL_MRID.queryIndex))
        }

        return readEnergyConnection(service, regulatingCondEq, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readRegulatingControl(
        service: NetworkService,
        regulatingControl: RegulatingControl,
        table: TableRegulatingControls,
        resultSet: ResultSet
    ): Boolean {
        regulatingControl.apply {
            discrete = resultSet.getNullableBoolean(table.DISCRETE.queryIndex)
            mode = RegulatingControlModeKind.valueOf(resultSet.getString(table.MODE.queryIndex))
            monitoredPhase = PhaseCode.valueOf(resultSet.getString(table.MONITORED_PHASE.queryIndex))
            targetDeadband = resultSet.getNullableFloat(table.TARGET_DEADBAND.queryIndex)
            targetValue = resultSet.getNullableDouble(table.TARGET_VALUE.queryIndex)
            enabled = resultSet.getNullableBoolean(table.ENABLED.queryIndex)
            maxAllowedTargetValue = resultSet.getNullableDouble(table.MAX_ALLOWED_TARGET_VALUE.queryIndex)
            minAllowedTargetValue = resultSet.getNullableDouble(table.MIN_ALLOWED_TARGET_VALUE.queryIndex)
            ratedCurrent = resultSet.getNullableDouble(table.RATED_CURRENT.queryIndex)
            terminal = service.ensureGet(resultSet.getNullableString(table.TERMINAL_MRID.queryIndex), typeNameAndMRID())
            ctPrimary = resultSet.getNullableDouble(table.CT_PRIMARY.queryIndex)
            minTargetDeadband = resultSet.getNullableDouble(table.MIN_TARGET_DEADBAND.queryIndex)
        }

        return readPowerSystemResource(service, regulatingControl, table, resultSet)
    }

    @Throws(SQLException::class)
    private fun readRotatingMachine(service: NetworkService, rotatingMachine: RotatingMachine, table: TableRotatingMachines, resultSet: ResultSet): Boolean {
        rotatingMachine.apply {
            ratedPowerFactor = resultSet.getNullableDouble(table.RATED_POWER_FACTOR.queryIndex)
            ratedS = resultSet.getNullableDouble(table.RATED_S.queryIndex)
            ratedU = resultSet.getNullableInt(table.RATED_U.queryIndex)
            p = resultSet.getNullableDouble(table.P.queryIndex)
            q = resultSet.getNullableDouble(table.Q.queryIndex)
        }
        return readRegulatingCondEq(service, rotatingMachine, table, resultSet)
    }

    /**
     * Create a [SeriesCompensator] and populate its fields from [TableSeriesCompensators].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [SeriesCompensator] fields from.
     * @param resultSet The record in the database table containing the fields for this [SeriesCompensator].
     * @param setIdentifier A callback to register the mRID of this [SeriesCompensator] for logging purposes.
     *
     * @return true if the [SeriesCompensator] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSeriesCompensators, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val seriesCompensator = SeriesCompensator(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r0 = resultSet.getNullableDouble(table.R0.queryIndex)
            x = resultSet.getNullableDouble(table.X.queryIndex)
            x0 = resultSet.getNullableDouble(table.X0.queryIndex)
            varistorRatedCurrent = resultSet.getNullableInt(table.VARISTOR_RATED_CURRENT.queryIndex)
            varistorVoltageThreshold = resultSet.getNullableInt(table.VARISTOR_VOLTAGE_THRESHOLD.queryIndex)
        }

        return readConductingEquipment(service, seriesCompensator, table, resultSet) && service.addOrThrow(seriesCompensator)
    }

    @Throws(SQLException::class)
    private fun readShuntCompensator(
        service: NetworkService,
        shuntCompensator: ShuntCompensator,
        table: TableShuntCompensators,
        resultSet: ResultSet
    ): Boolean {
        shuntCompensator.apply {
            assetInfo = service.ensureGet(
                resultSet.getNullableString(table.SHUNT_COMPENSATOR_INFO_MRID.queryIndex),
                typeNameAndMRID()
            )

            grounded = resultSet.getNullableBoolean(table.GROUNDED.queryIndex)
            nomU = resultSet.getNullableInt(table.NOM_U.queryIndex)
            phaseConnection = PhaseShuntConnectionKind.valueOf(resultSet.getString(table.PHASE_CONNECTION.queryIndex))
            sections = resultSet.getNullableDouble(table.SECTIONS.queryIndex)
        }

        return readRegulatingCondEq(service, shuntCompensator, table, resultSet)
    }

    /**
     * Create a [StaticVarCompensator] and populate its fields from [TableStaticVarCompensators].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [StaticVarCompensator] fields from.
     * @param resultSet The record in the database table containing the fields for this [StaticVarCompensator].
     * @param setIdentifier A callback to register the mRID of this [StaticVarCompensator] for logging purposes.
     *
     * @return true if the [StaticVarCompensator] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableStaticVarCompensators, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val staticVarCompensator = StaticVarCompensator(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            capacitiveRating = resultSet.getNullableDouble(table.CAPACITIVE_RATING.queryIndex)
            inductiveRating = resultSet.getNullableDouble(table.INDUCTIVE_RATING.queryIndex)
            q = resultSet.getNullableDouble(table.Q.queryIndex)
            svcControlMode = SVCControlMode.valueOf(resultSet.getString(table.SVC_CONTROL_MODE.queryIndex))
            voltageSetPoint = resultSet.getNullableInt(table.VOLTAGE_SET_POINT.queryIndex)
        }

        return readRegulatingCondEq(service, staticVarCompensator, table, resultSet) && service.addOrThrow(staticVarCompensator)
    }

    @Throws(SQLException::class)
    private fun readSwitch(service: NetworkService, switch: Switch, table: TableSwitches, resultSet: ResultSet): Boolean {
        switch.apply {
            assetInfo = service.ensureGet(resultSet.getNullableString(table.SWITCH_INFO_MRID.queryIndex), typeNameAndMRID())
            ratedCurrent = resultSet.getNullableDouble(table.RATED_CURRENT.queryIndex)
            normalOpen = resultSet.getInt(table.NORMAL_OPEN.queryIndex)
            open = resultSet.getInt(table.OPEN.queryIndex)
        }

        return readConductingEquipment(service, switch, table, resultSet)
    }

    /**
     * Create a [SynchronousMachine] and populate its fields from [TableSynchronousMachines].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [SynchronousMachine] fields from.
     * @param resultSet The record in the database table containing the fields for this [SynchronousMachine].
     * @param setIdentifier A callback to register the mRID of this [SynchronousMachine] for logging purposes.
     *
     * @return true if the [SynchronousMachine] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableSynchronousMachines, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val synchronousMachine = SynchronousMachine(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            baseQ = resultSet.getNullableDouble(table.BASE_Q.queryIndex)
            condenserP = resultSet.getNullableInt(table.CONDENSER_P.queryIndex)
            earthing = resultSet.getBoolean(table.EARTHING.queryIndex)
            earthingStarPointR = resultSet.getNullableDouble(table.EARTHING_STAR_POINT_R.queryIndex)
            earthingStarPointX = resultSet.getNullableDouble(table.EARTHING_STAR_POINT_X.queryIndex)
            ikk = resultSet.getNullableDouble(table.IKK.queryIndex)
            maxQ = resultSet.getNullableDouble(table.MAX_Q.queryIndex)
            maxU = resultSet.getNullableInt(table.MAX_U.queryIndex)
            minQ = resultSet.getNullableDouble(table.MIN_Q.queryIndex)
            minU = resultSet.getNullableInt(table.MIN_U.queryIndex)
            mu = resultSet.getNullableDouble(table.MU.queryIndex)
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r0 = resultSet.getNullableDouble(table.R0.queryIndex)
            r2 = resultSet.getNullableDouble(table.R2.queryIndex)
            satDirectSubtransX = resultSet.getNullableDouble(table.SAT_DIRECT_SUBTRANS_X.queryIndex)
            satDirectSyncX = resultSet.getNullableDouble(table.SAT_DIRECT_SYNC_X.queryIndex)
            satDirectTransX = resultSet.getNullableDouble(table.SAT_DIRECT_TRANS_X.queryIndex)
            x0 = resultSet.getNullableDouble(table.X0.queryIndex)
            x2 = resultSet.getNullableDouble(table.X2.queryIndex)
            type = SynchronousMachineKind.valueOf(resultSet.getString(table.TYPE.queryIndex))
            operatingMode = SynchronousMachineKind.valueOf(resultSet.getString(table.OPERATING_MODE.queryIndex))
        }

        return readRotatingMachine(service, synchronousMachine, table, resultSet) && service.addOrThrow(synchronousMachine)
    }

    @Throws(SQLException::class)
    private fun readTapChanger(service: NetworkService, tapChanger: TapChanger, table: TableTapChangers, resultSet: ResultSet): Boolean {
        tapChanger.apply {
            controlEnabled = resultSet.getNullableBoolean(table.CONTROL_ENABLED.queryIndex)
            highStep = resultSet.getNullableInt(table.HIGH_STEP.queryIndex)
            lowStep = resultSet.getNullableInt(table.LOW_STEP.queryIndex)
            neutralStep = resultSet.getNullableInt(table.NEUTRAL_STEP.queryIndex)
            neutralU = resultSet.getNullableInt(table.NEUTRAL_U.queryIndex)
            normalStep = resultSet.getNullableInt(table.NORMAL_STEP.queryIndex)
            step = resultSet.getNullableDouble(table.STEP.queryIndex)
            tapChangerControl = service.ensureGet(resultSet.getNullableString(table.TAP_CHANGER_CONTROL_MRID.queryIndex), typeNameAndMRID())
        }

        return readPowerSystemResource(service, tapChanger, table, resultSet)
    }

    /**
     * Create a [TapChangerControl] and populate its fields from [TableTapChangerControls].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [TapChangerControl] fields from.
     * @param resultSet The record in the database table containing the fields for this [TapChangerControl].
     * @param setIdentifier A callback to register the mRID of this [TapChangerControl] for logging purposes.
     *
     * @return true if the [TapChangerControl] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableTapChangerControls, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val tapChangerControl = TapChangerControl(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            limitVoltage = resultSet.getNullableInt(table.LIMIT_VOLTAGE.queryIndex)
            lineDropCompensation = resultSet.getNullableBoolean(table.LINE_DROP_COMPENSATION.queryIndex)
            lineDropR = resultSet.getNullableDouble(table.LINE_DROP_R.queryIndex)
            lineDropX = resultSet.getNullableDouble(table.LINE_DROP_X.queryIndex)
            reverseLineDropR = resultSet.getNullableDouble(table.REVERSE_LINE_DROP_R.queryIndex)
            reverseLineDropX = resultSet.getNullableDouble(table.REVERSE_LINE_DROP_X.queryIndex)
            forwardLDCBlocking = resultSet.getNullableBoolean(table.FORWARD_LDC_BLOCKING.queryIndex)
            timeDelay = resultSet.getNullableDouble(table.TIME_DELAY.queryIndex)
            coGenerationEnabled = resultSet.getNullableBoolean(table.CO_GENERATION_ENABLED.queryIndex)
        }

        return readRegulatingControl(service, tapChangerControl, table, resultSet) && service.addOrThrow(tapChangerControl)
    }

    @Throws(SQLException::class)
    private fun readTransformerEnd(service: NetworkService, transformerEnd: TransformerEnd, table: TableTransformerEnds, resultSet: ResultSet): Boolean {
        transformerEnd.apply {
            terminal = service.ensureGet(resultSet.getNullableString(table.TERMINAL_MRID.queryIndex), typeNameAndMRID())
            baseVoltage = service.ensureGet(resultSet.getNullableString(table.BASE_VOLTAGE_MRID.queryIndex), typeNameAndMRID())
            grounded = resultSet.getNullableBoolean(table.GROUNDED.queryIndex)
            rGround = resultSet.getNullableDouble(table.R_GROUND.queryIndex)
            xGround = resultSet.getNullableDouble(table.X_GROUND.queryIndex)
            starImpedance = service.ensureGet(resultSet.getNullableString(table.STAR_IMPEDANCE_MRID.queryIndex), typeNameAndMRID())
        }

        return readIdentifiedObject(transformerEnd, table, resultSet)
    }

    /**
     * Create a [TransformerStarImpedance] and populate its fields from [TableTransformerStarImpedances].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [TransformerStarImpedance] fields from.
     * @param resultSet The record in the database table containing the fields for this [TransformerStarImpedance].
     * @param setIdentifier A callback to register the mRID of this [TransformerStarImpedance] for logging purposes.
     *
     * @return true if the [TransformerStarImpedance] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableTransformerStarImpedances, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val transformerStarImpedance = TransformerStarImpedance(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            r = resultSet.getNullableDouble(table.R.queryIndex)
            r0 = resultSet.getNullableDouble(table.R0.queryIndex)
            x = resultSet.getNullableDouble(table.X.queryIndex)
            x0 = resultSet.getNullableDouble(table.X0.queryIndex)

            transformerEndInfo = service.ensureGet(resultSet.getNullableString(table.TRANSFORMER_END_INFO_MRID.queryIndex), typeNameAndMRID())
            transformerEndInfo?.transformerStarImpedance = this
        }

        return readIdentifiedObject(transformerStarImpedance, table, resultSet) && service.addOrThrow(transformerStarImpedance)
    }

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    /**
     * Create a [Circuit] and populate its fields from [TableCircuits].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the [Circuit] fields from.
     * @param resultSet The record in the database table containing the fields for this [Circuit].
     * @param setIdentifier A callback to register the mRID of this [Circuit] for logging purposes.
     *
     * @return true if the [Circuit] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCircuits, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val circuit = Circuit(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            loop = service.ensureGet(resultSet.getNullableString(table.LOOP_MRID.queryIndex), typeNameAndMRID())
            loop?.addCircuit(this)
        }

        return readLine(service, circuit, table, resultSet) && service.addOrThrow(circuit)
    }

    // ################
    // # Associations #
    // ################

    /**
     * Create a [AssetOrganisationRole] to [Asset] association from [TableAssetOrganisationRolesAssets].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAssetOrganisationRolesAssets, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val assetOrganisationRoleMRID = resultSet.getString(table.ASSET_ORGANISATION_ROLE_MRID.queryIndex)
        setIdentifier("${assetOrganisationRoleMRID}-to-UNKNOWN")

        val assetMRID = resultSet.getString(table.ASSET_MRID.queryIndex)
        val id = setIdentifier("${assetOrganisationRoleMRID}-to-${assetMRID}")

        val typeNameAndMRID = "AssetOrganisationRole to Asset association $id"
        val assetOrganisationRole = service.getOrThrow<AssetOrganisationRole>(assetOrganisationRoleMRID, typeNameAndMRID)
        val asset = service.getOrThrow<Asset>(assetMRID, typeNameAndMRID)

        asset.addOrganisationRole(assetOrganisationRole)

        return true
    }

    /**
     * Create a [Asset] to [PowerSystemResource] association from [TableAssetsPowerSystemResources].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableAssetsPowerSystemResources, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val assetMRID = resultSet.getString(table.ASSET_MRID.queryIndex)
        setIdentifier("${assetMRID}-to-UNKNOWN")

        val powerSystemResourceMRID = resultSet.getString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex)
        val id = setIdentifier("${assetMRID}-to-${powerSystemResourceMRID}")

        val typeNameAndMRID = "Asset to PowerSystemResource association $id"
        val asset = service.getOrThrow<Asset>(assetMRID, typeNameAndMRID)
        val powerSystemResource = service.getOrThrow<PowerSystemResource>(powerSystemResourceMRID, typeNameAndMRID)

        asset.addPowerSystemResource(powerSystemResource)
        powerSystemResource.addAsset(asset)

        return true
    }

    /**
     * Create a [BatteryUnit] to [BatteryControl] association from [TableBatteryUnitsBatteryControls].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableBatteryUnitsBatteryControls, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val batteryUnitMRID = resultSet.getString(table.BATTERY_UNIT_MRID.queryIndex)
        setIdentifier("${batteryUnitMRID}-to-UNKNOWN")

        val batteryControlMRID = resultSet.getString(table.BATTERY_CONTROL_MRID.queryIndex)
        val id = setIdentifier("${batteryUnitMRID}-to-${batteryControlMRID}")

        val typeNameAndMRID = "BatteryUnit to BatteryControl association $id"
        val batteryUnit = service.getOrThrow<BatteryUnit>(batteryUnitMRID, typeNameAndMRID)
        val batteryControl = service.getOrThrow<BatteryControl>(batteryControlMRID, typeNameAndMRID)

        batteryUnit.addControl(batteryControl)

        return true
    }

    /**
     * Create a [Circuit] to [Substation] association from [TableCircuitsSubstations].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCircuitsSubstations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val circuitMRID = resultSet.getString(table.CIRCUIT_MRID.queryIndex)
        setIdentifier("${circuitMRID}-to-UNKNOWN")

        val substationMRID = resultSet.getString(table.SUBSTATION_MRID.queryIndex)
        val id = setIdentifier("${circuitMRID}-to-${substationMRID}")

        val typeNameAndMRID = "Circuit to Substation association $id"
        val circuit = service.getOrThrow<Circuit>(circuitMRID, typeNameAndMRID)
        val substation = service.getOrThrow<Substation>(substationMRID, typeNameAndMRID)

        substation.addCircuit(circuit)
        circuit.addEndSubstation(substation)

        return true
    }

    /**
     * Create a [Circuit] to [Terminal] association from [TableCircuitsTerminals].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableCircuitsTerminals, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val circuitMRID = resultSet.getString(table.CIRCUIT_MRID.queryIndex)
        setIdentifier("${circuitMRID}-to-UNKNOWN")

        val terminalMRID = resultSet.getString(table.TERMINAL_MRID.queryIndex)
        val id = setIdentifier("${circuitMRID}-to-${terminalMRID}")

        val typeNameAndMRID = "Circuit to Terminal association $id"
        val circuit = service.getOrThrow<Circuit>(circuitMRID, typeNameAndMRID)
        val terminal = service.getOrThrow<Terminal>(terminalMRID, typeNameAndMRID)

        circuit.addEndTerminal(terminal)

        return true
    }

    /**
     * Create a [EndDevice] to [EndDeviceFunction] association from [TableEndDevicesEndDeviceFunctions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEndDevicesEndDeviceFunctions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val endDeviceMRID = resultSet.getString(table.END_DEVICE_MRID.queryIndex)
        setIdentifier("${endDeviceMRID}-to-UNKNOWN")

        val endDeviceFunctionMRID = resultSet.getString(table.END_DEVICE_FUNCTION_MRID.queryIndex)
        val id = setIdentifier("${endDeviceMRID}-to-${endDeviceFunctionMRID}")

        val typeNameAndMRID = "EndDevice to EndDeviceFunction association $id"
        val endDevice = service.getOrThrow<EndDevice>(endDeviceMRID, typeNameAndMRID)
        val endDeviceFunction = service.getOrThrow<EndDeviceFunction>(endDeviceFunctionMRID, typeNameAndMRID)

        endDevice.addFunction(endDeviceFunction)

        return true
    }

    /**
     * Create a [Equipment] to [EquipmentContainer] association from [TableEquipmentEquipmentContainers].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEquipmentEquipmentContainers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val equipmentMRID = resultSet.getString(table.EQUIPMENT_MRID.queryIndex)
        setIdentifier("${equipmentMRID}-to-UNKNOWN")

        val equipmentContainerMRID = resultSet.getString(table.EQUIPMENT_CONTAINER_MRID.queryIndex)
        val id = setIdentifier("${equipmentMRID}-to-${equipmentContainerMRID}")

        val typeNameAndMRID = "Equipment to EquipmentContainer association $id"
        val equipment = service.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val equipmentContainer = service.getOrThrow<EquipmentContainer>(equipmentContainerMRID, typeNameAndMRID)

        equipmentContainer.addEquipment(equipment)
        equipment.addContainer(equipmentContainer)

        return true
    }

    /**
     * Create a [Equipment] to [OperationalRestriction] association from [TableEquipmentOperationalRestrictions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEquipmentOperationalRestrictions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val equipmentMRID = resultSet.getString(table.EQUIPMENT_MRID.queryIndex)
        setIdentifier("${equipmentMRID}-to-UNKNOWN")

        val operationalRestrictionMRID = resultSet.getString(table.OPERATIONAL_RESTRICTION_MRID.queryIndex)
        val id = setIdentifier("${equipmentMRID}-to-${operationalRestrictionMRID}")

        val typeNameAndMRID = "Equipment to OperationalRestriction association $id"
        val equipment = service.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val operationalRestriction = service.getOrThrow<OperationalRestriction>(operationalRestrictionMRID, typeNameAndMRID)

        operationalRestriction.addEquipment(equipment)
        equipment.addOperationalRestriction(operationalRestriction)

        return true
    }

    /**
     * Create a [Equipment] to [UsagePoint] association from [TableEquipmentUsagePoints].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableEquipmentUsagePoints, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val equipmentMRID = resultSet.getString(table.EQUIPMENT_MRID.queryIndex)
        setIdentifier("${equipmentMRID}-to-UNKNOWN")

        val usagePointMRID = resultSet.getString(table.USAGE_POINT_MRID.queryIndex)
        val id = setIdentifier("${equipmentMRID}-to-${usagePointMRID}")

        val typeNameAndMRID = "Equipment to UsagePoint association $id"
        val equipment = service.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val usagePoint = service.getOrThrow<UsagePoint>(usagePointMRID, typeNameAndMRID)

        usagePoint.addEquipment(equipment)
        equipment.addUsagePoint(usagePoint)

        return true
    }

    /**
     * Create a [Loop] to [Substation] association from [TableLoopsSubstations].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableLoopsSubstations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val assetOrganisationRoleMRID = resultSet.getString(table.LOOP_MRID.queryIndex)
        setIdentifier("${assetOrganisationRoleMRID}-to-UNKNOWN")

        val assetMRID = resultSet.getString(table.SUBSTATION_MRID.queryIndex)
        val id = setIdentifier("${assetOrganisationRoleMRID}-to-${assetMRID}")

        val typeNameAndMRID = "Loop to Substation association $id"
        val loop = service.getOrThrow<Loop>(assetOrganisationRoleMRID, typeNameAndMRID)
        val substation = service.getOrThrow<Substation>(assetMRID, typeNameAndMRID)

        when (LoopSubstationRelationship.valueOf(resultSet.getString(table.RELATIONSHIP.queryIndex))) {
            LoopSubstationRelationship.LOOP_ENERGIZES_SUBSTATION -> {
                substation.addLoop(loop)
                loop.addSubstation(substation)
            }

            LoopSubstationRelationship.SUBSTATION_ENERGIZES_LOOP -> {
                substation.addEnergizedLoop(loop)
                loop.addEnergizingSubstation(substation)
            }
        }

        return true
    }

    /**
     * Create a [ProtectionRelayFunction] to [ProtectedSwitch] association from [TableProtectionRelayFunctionsProtectedSwitches].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelayFunctionsProtectedSwitches, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val protectionRelayFunctionMRID = resultSet.getString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex)
        setIdentifier("${protectionRelayFunctionMRID}-to-UNKNOWN")

        val protectedSwitchMRID = resultSet.getString(table.PROTECTED_SWITCH_MRID.queryIndex)
        val id = setIdentifier("${protectionRelayFunctionMRID}-to-${protectedSwitchMRID}")

        val typeNameAndMRID = "ProtectionRelayFunction to ProtectedSwitch association $id"
        val protectionRelayFunction = service.getOrThrow<ProtectionRelayFunction>(protectionRelayFunctionMRID, typeNameAndMRID)
        val protectedSwitch = service.getOrThrow<ProtectedSwitch>(protectedSwitchMRID, typeNameAndMRID)

        protectionRelayFunction.addProtectedSwitch(protectedSwitch)
        protectedSwitch.addRelayFunction(protectionRelayFunction)

        return true
    }

    /**
     * Create a [ProtectionRelayFunction] to [Sensor] association from [TableProtectionRelayFunctionsSensors].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableProtectionRelayFunctionsSensors, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val protectionRelayFunctionMRID = resultSet.getString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex)
        setIdentifier("${protectionRelayFunctionMRID}-to-UNKNOWN")

        val sensorMRID = resultSet.getString(table.SENSOR_MRID.queryIndex)
        val id = setIdentifier("${protectionRelayFunctionMRID}-to-${sensorMRID}")

        val typeNameAndMRID = "ProtectionRelayFunction to Sensor association $id"
        val protectionRelayFunction = service.getOrThrow<ProtectionRelayFunction>(protectionRelayFunctionMRID, typeNameAndMRID)
        val sensor = service.getOrThrow<Sensor>(sensorMRID, typeNameAndMRID)

        protectionRelayFunction.addSensor(sensor)
        sensor.addRelayFunction(protectionRelayFunction)

        return true
    }

    /**
     * Create a [ProtectionRelayScheme] to [ProtectionRelayFunction] association from [TableProtectionRelaySchemesProtectionRelayFunctions].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(
        service: NetworkService,
        table: TableProtectionRelaySchemesProtectionRelayFunctions,
        resultSet: ResultSet,
        setIdentifier: (String) -> String
    ): Boolean {
        val protectionRelaySchemeMRID = resultSet.getString(table.PROTECTION_RELAY_SCHEME_MRID.queryIndex)
        setIdentifier("${protectionRelaySchemeMRID}-to-UNKNOWN")

        val protectionRelayFunctionMRID = resultSet.getString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex)
        val id = setIdentifier("${protectionRelaySchemeMRID}-to-${protectionRelayFunctionMRID}")

        val typeNameAndMRID = "ProtectionRelayScheme to ProtectionRelayFunction association $id"
        val protectionRelayScheme = service.getOrThrow<ProtectionRelayScheme>(protectionRelaySchemeMRID, typeNameAndMRID)
        val protectionRelayFunction = service.getOrThrow<ProtectionRelayFunction>(protectionRelayFunctionMRID, typeNameAndMRID)

        protectionRelayScheme.addFunction(protectionRelayFunction)
        protectionRelayFunction.addScheme(protectionRelayScheme)

        return true
    }

    /**
     * Create a [SynchronousMachine] to [ReactiveCapabilityCurve] association from [TableSynchronousMachinesReactiveCapabilityCurves].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(
        service: NetworkService,
        table: TableSynchronousMachinesReactiveCapabilityCurves,
        resultSet: ResultSet,
        setIdentifier: (String) -> String
    ): Boolean {
        val synchronousMachineMRID = resultSet.getString(table.SYNCHRONOUS_MACHINE_MRID.queryIndex)
        setIdentifier("${synchronousMachineMRID}-to-UNKNOWN")

        val curveMRID = resultSet.getString(table.REACTIVE_CAPABILITY_CURVE_MRID.queryIndex)
        val id = setIdentifier("${synchronousMachineMRID}-to-${curveMRID}")

        val typeNameAndMRID = "SynchronousMachine to ReactiveCapabilityCurve association $id"
        val synchronousMachine = service.getOrThrow<SynchronousMachine>(synchronousMachineMRID, typeNameAndMRID)
        val curve = service.getOrThrow<ReactiveCapabilityCurve>(curveMRID, typeNameAndMRID)

        synchronousMachine.addCurve(curve)

        return true
    }

    /**
     * Create a [UsagePoint] to [EndDevice] association from [TableUsagePointsEndDevices].
     *
     * @param service The [NetworkService] used to store any items read from the database.
     * @param table The database table to read the association from.
     * @param resultSet The record in the database table containing the fields for this association.
     * @param setIdentifier A callback to register the identifier of this association for logging purposes.
     *
     * @return true if the association was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: NetworkService, table: TableUsagePointsEndDevices, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val usagePointMRID = resultSet.getString(table.USAGE_POINT_MRID.queryIndex)
        setIdentifier("${usagePointMRID}-to-UNKNOWN")

        val endDeviceMRID = resultSet.getString(table.END_DEVICE_MRID.queryIndex)
        val id = setIdentifier("${usagePointMRID}-to-${endDeviceMRID}")

        val typeNameAndMRID = "UsagePoint to EndDevice association $id"
        val usagePoint = service.getOrThrow<UsagePoint>(usagePointMRID, typeNameAndMRID)
        val endDevice = service.getOrThrow<EndDevice>(endDeviceMRID, typeNameAndMRID)

        endDevice.addUsagePoint(usagePoint)
        usagePoint.addEndDevice(endDevice)

        return true
    }

}
