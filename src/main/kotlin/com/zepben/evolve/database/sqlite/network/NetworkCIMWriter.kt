/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.*
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.database.sqlite.common.BaseCIMWriter
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.extensions.*
import com.zepben.evolve.database.sqlite.tables.associations.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.*
import com.zepben.evolve.database.sqlite.tables.iec61968.common.*
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableRecloseDelays
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableRelayInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableEndDevices
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableMeters
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableUsagePoints
import com.zepben.evolve.database.sqlite.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.evolve.database.sqlite.tables.iec61970.base.equivalents.TableEquivalentEquipment
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.protection.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemotePoints
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.*
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLvFeeders
import java.sql.PreparedStatement


@Suppress("SameParameterValue")
class NetworkCIMWriter(databaseTables: DatabaseTables) : BaseCIMWriter(databaseTables) {

    /************ IEC61968 ASSET INFO ************/

    fun save(cableInfo: CableInfo): Boolean {
        val table = databaseTables.getTable<TableCableInfo>()
        val insert = databaseTables.getInsert<TableCableInfo>()

        return saveWireInfo(table, insert, cableInfo, "cable info")
    }

    fun save(noLoadTest: NoLoadTest): Boolean {
        val table = databaseTables.getTable<TableNoLoadTests>()
        val insert = databaseTables.getInsert<TableNoLoadTests>()

        insert.setNullableInt(table.ENERGISED_END_VOLTAGE.queryIndex, noLoadTest.energisedEndVoltage)
        insert.setNullableDouble(table.EXCITING_CURRENT.queryIndex, noLoadTest.excitingCurrent)
        insert.setNullableDouble(table.EXCITING_CURRENT_ZERO.queryIndex, noLoadTest.excitingCurrentZero)
        insert.setNullableInt(table.LOSS.queryIndex, noLoadTest.loss)
        insert.setNullableInt(table.LOSS_ZERO.queryIndex, noLoadTest.lossZero)

        return saveTransformerTest(table, insert, noLoadTest, "no load test")
    }

    fun save(openCircuitTest: OpenCircuitTest): Boolean {
        val table = databaseTables.getTable<TableOpenCircuitTests>()
        val insert = databaseTables.getInsert<TableOpenCircuitTests>()

        insert.setNullableInt(table.ENERGISED_END_STEP.queryIndex, openCircuitTest.energisedEndStep)
        insert.setNullableInt(table.ENERGISED_END_VOLTAGE.queryIndex, openCircuitTest.energisedEndVoltage)
        insert.setNullableInt(table.OPEN_END_STEP.queryIndex, openCircuitTest.openEndStep)
        insert.setNullableInt(table.OPEN_END_VOLTAGE.queryIndex, openCircuitTest.openEndVoltage)
        insert.setNullableDouble(table.PHASE_SHIFT.queryIndex, openCircuitTest.phaseShift)

        return saveTransformerTest(table, insert, openCircuitTest, "open circuit test")
    }

    fun save(overheadWireInfo: OverheadWireInfo): Boolean {
        val table = databaseTables.getTable<TableOverheadWireInfo>()
        val insert = databaseTables.getInsert<TableOverheadWireInfo>()

        return saveWireInfo(table, insert, overheadWireInfo, "overhead wire info")
    }

    fun save(powerTransformerInfo: PowerTransformerInfo): Boolean {
        val table = databaseTables.getTable<TablePowerTransformerInfo>()
        val insert = databaseTables.getInsert<TablePowerTransformerInfo>()

        return saveAssetInfo(table, insert, powerTransformerInfo, "power transformer info")
    }

    fun save(shortCircuitTest: ShortCircuitTest): Boolean {
        val table = databaseTables.getTable<TableShortCircuitTests>()
        val insert = databaseTables.getInsert<TableShortCircuitTests>()

        insert.setNullableDouble(table.CURRENT.queryIndex, shortCircuitTest.current)
        insert.setNullableInt(table.ENERGISED_END_STEP.queryIndex, shortCircuitTest.energisedEndStep)
        insert.setNullableInt(table.GROUNDED_END_STEP.queryIndex, shortCircuitTest.groundedEndStep)
        insert.setNullableDouble(table.LEAKAGE_IMPEDANCE.queryIndex, shortCircuitTest.leakageImpedance)
        insert.setNullableDouble(table.LEAKAGE_IMPEDANCE_ZERO.queryIndex, shortCircuitTest.leakageImpedanceZero)
        insert.setNullableInt(table.LOSS.queryIndex, shortCircuitTest.loss)
        insert.setNullableInt(table.LOSS_ZERO.queryIndex, shortCircuitTest.lossZero)
        insert.setNullableInt(table.POWER.queryIndex, shortCircuitTest.power)
        insert.setNullableDouble(table.VOLTAGE.queryIndex, shortCircuitTest.voltage)
        insert.setNullableDouble(table.VOLTAGE_OHMIC_PART.queryIndex, shortCircuitTest.voltageOhmicPart)

        return saveTransformerTest(table, insert, shortCircuitTest, "short circuit test")
    }

    fun save(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean {
        val table = databaseTables.getTable<TableShuntCompensatorInfo>()
        val insert = databaseTables.getInsert<TableShuntCompensatorInfo>()

        insert.setNullableInt(table.MAX_POWER_LOSS.queryIndex, shuntCompensatorInfo.maxPowerLoss)
        insert.setNullableInt(table.RATED_CURRENT.queryIndex, shuntCompensatorInfo.ratedCurrent)
        insert.setNullableInt(table.RATED_REACTIVE_POWER.queryIndex, shuntCompensatorInfo.ratedReactivePower)
        insert.setNullableInt(table.RATED_VOLTAGE.queryIndex, shuntCompensatorInfo.ratedVoltage)

        return saveAssetInfo(table, insert, shuntCompensatorInfo, "shunt compensator info")
    }

    fun save(switchInfo: SwitchInfo): Boolean {
        val table = databaseTables.getTable<TableSwitchInfo>()
        val insert = databaseTables.getInsert<TableSwitchInfo>()

        insert.setNullableDouble(table.RATED_INTERRUPTING_TIME.queryIndex, switchInfo.ratedInterruptingTime)

        return saveAssetInfo(table, insert, switchInfo, "switch info")
    }

    fun save(transformerEndInfo: TransformerEndInfo): Boolean {
        val table = databaseTables.getTable<TableTransformerEndInfo>()
        val insert = databaseTables.getInsert<TableTransformerEndInfo>()

        insert.setString(table.CONNECTION_KIND.queryIndex, transformerEndInfo.connectionKind.name)
        insert.setNullableInt(table.EMERGENCY_S.queryIndex, transformerEndInfo.emergencyS)
        insert.setInt(table.END_NUMBER.queryIndex, transformerEndInfo.endNumber)
        insert.setNullableInt(table.INSULATION_U.queryIndex, transformerEndInfo.insulationU)
        insert.setNullableInt(table.PHASE_ANGLE_CLOCK.queryIndex, transformerEndInfo.phaseAngleClock)
        insert.setNullableDouble(table.R.queryIndex, transformerEndInfo.r)
        insert.setNullableInt(table.RATED_S.queryIndex, transformerEndInfo.ratedS)
        insert.setNullableInt(table.RATED_U.queryIndex, transformerEndInfo.ratedU)
        insert.setNullableInt(table.SHORT_TERM_S.queryIndex, transformerEndInfo.shortTermS)
        insert.setNullableString(table.TRANSFORMER_TANK_INFO_MRID.queryIndex, transformerEndInfo.transformerTankInfo?.mRID)
        insert.setNullableString(table.ENERGISED_END_NO_LOAD_TESTS.queryIndex, transformerEndInfo.energisedEndNoLoadTests?.mRID)
        insert.setNullableString(table.ENERGISED_END_SHORT_CIRCUIT_TESTS.queryIndex, transformerEndInfo.energisedEndShortCircuitTests?.mRID)
        insert.setNullableString(table.GROUNDED_END_SHORT_CIRCUIT_TESTS.queryIndex, transformerEndInfo.groundedEndShortCircuitTests?.mRID)
        insert.setNullableString(table.OPEN_END_OPEN_CIRCUIT_TESTS.queryIndex, transformerEndInfo.openEndOpenCircuitTests?.mRID)
        insert.setNullableString(table.ENERGISED_END_OPEN_CIRCUIT_TESTS.queryIndex, transformerEndInfo.energisedEndOpenCircuitTests?.mRID)

        return saveAssetInfo(table, insert, transformerEndInfo, "transformer end info")
    }

    fun save(transformerTankInfo: TransformerTankInfo): Boolean {
        val table = databaseTables.getTable<TableTransformerTankInfo>()
        val insert = databaseTables.getInsert<TableTransformerTankInfo>()

        insert.setNullableString(table.POWER_TRANSFORMER_INFO_MRID.queryIndex, transformerTankInfo.powerTransformerInfo?.mRID)

        return saveAssetInfo(table, insert, transformerTankInfo, "transformer tank info")
    }

    private fun saveTransformerTest(table: TableTransformerTest, insert: PreparedStatement, transformerTest: TransformerTest, description: String): Boolean {
        insert.setNullableInt(table.BASE_POWER.queryIndex, transformerTest.basePower)
        insert.setNullableDouble(table.TEMPERATURE.queryIndex, transformerTest.temperature)

        return saveIdentifiedObject(table, insert, transformerTest, description)
    }

    private fun saveWireInfo(table: TableWireInfo, insert: PreparedStatement, wireInfo: WireInfo, description: String): Boolean {
        insert.setNullableInt(table.RATED_CURRENT.queryIndex, wireInfo.ratedCurrent)
        insert.setNullableString(table.MATERIAL.queryIndex, wireInfo.material.name)

        return saveAssetInfo(table, insert, wireInfo, description)
    }

    /************ IEC61968 ASSETS ************/

    private fun saveAsset(table: TableAssets, insert: PreparedStatement, asset: Asset, description: String): Boolean {
        var status = true

        insert.setNullableString(table.LOCATION_MRID.queryIndex, asset.location?.mRID)
        asset.organisationRoles.forEach { status = status and saveAssociation(it, asset) }

        return status and saveIdentifiedObject(table, insert, asset, description)
    }

    private fun saveAssetContainer(table: TableAssetContainers, insert: PreparedStatement, assetContainer: AssetContainer, description: String): Boolean {
        return saveAsset(table, insert, assetContainer, description)
    }

    private fun saveAssetInfo(table: TableAssetInfo, insert: PreparedStatement, assetInfo: AssetInfo, description: String): Boolean {
        return saveIdentifiedObject(table, insert, assetInfo, description)
    }

    private fun saveAssetOrganisationRole(
        table: TableAssetOrganisationRoles,
        insert: PreparedStatement,
        assetOrganisationRole: AssetOrganisationRole,
        description: String
    ): Boolean {
        return saveOrganisationRole(table, insert, assetOrganisationRole, description)
    }

    fun save(assetOwner: AssetOwner): Boolean {
        val table = databaseTables.getTable<TableAssetOwners>()
        val insert = databaseTables.getInsert<TableAssetOwners>()

        return saveAssetOrganisationRole(table, insert, assetOwner, "asset owner")
    }

    private fun saveStructure(table: TableStructures, insert: PreparedStatement, structure: Structure, description: String): Boolean {
        return saveAssetContainer(table, insert, structure, description)
    }

    fun save(pole: Pole): Boolean {
        val table = databaseTables.getTable<TablePoles>()
        val insert = databaseTables.getInsert<TablePoles>()

        insert.setString(table.CLASSIFICATION.queryIndex, pole.classification)

        return saveStructure(table, insert, pole, "pole")
    }

    fun save(streetlight: Streetlight): Boolean {
        val table = databaseTables.getTable<TableStreetlights>()
        val insert = databaseTables.getInsert<TableStreetlights>()

        insert.setNullableString(table.POLE_MRID.queryIndex, streetlight.pole?.mRID)
        insert.setNullableInt(table.LIGHT_RATING.queryIndex, streetlight.lightRating)
        insert.setString(table.LAMP_KIND.queryIndex, streetlight.lampKind.name)
        return saveAsset(table, insert, streetlight, "streetlight")
    }

    /************ IEC61968 COMMON ************/

    private fun insertStreetDetail(table: TableStreetAddresses, insert: PreparedStatement, streetDetail: StreetDetail?) {
        insert.setNullableString(table.BUILDING_NAME.queryIndex, streetDetail?.buildingName)
        insert.setNullableString(table.FLOOR_IDENTIFICATION.queryIndex, streetDetail?.floorIdentification)
        insert.setNullableString(table.STREET_NAME.queryIndex, streetDetail?.name)
        insert.setNullableString(table.NUMBER.queryIndex, streetDetail?.number)
        insert.setNullableString(table.SUITE_NUMBER.queryIndex, streetDetail?.suiteNumber)
        insert.setNullableString(table.TYPE.queryIndex, streetDetail?.type)
        insert.setNullableString(table.DISPLAY_ADDRESS.queryIndex, streetDetail?.displayAddress)
    }

    private fun insertTownDetail(table: TableTownDetails, insert: PreparedStatement, townDetail: TownDetail?) {
        insert.setNullableString(table.TOWN_NAME.queryIndex, townDetail?.name)
        insert.setNullableString(table.STATE_OR_PROVINCE.queryIndex, townDetail?.stateOrProvince)
    }

    fun save(location: Location): Boolean {
        val table = databaseTables.getTable<TableLocations>()
        val insert = databaseTables.getInsert<TableLocations>()

        var status = saveLocationStreetAddress(location, TableLocationStreetAddressField.mainAddress, location.mainAddress, "location main address")
        location.points.forEachIndexed { sequenceNumber, point -> status = status and savePositionPoint(location, sequenceNumber, point) }

        return status and saveIdentifiedObject(table, insert, location, "location")
    }

    private fun saveLocationStreetAddress(
        location: Location,
        field: TableLocationStreetAddressField,
        streetAddress: StreetAddress?,
        description: String
    ): Boolean {
        if (streetAddress == null)
            return true

        val table = databaseTables.getTable<TableLocationStreetAddresses>()
        val insert = databaseTables.getInsert<TableLocationStreetAddresses>()

        insert.setNullableString(table.LOCATION_MRID.queryIndex, location.mRID)
        insert.setNullableString(table.ADDRESS_FIELD.queryIndex, field.name)

        return saveStreetAddress(
            table,
            insert,
            streetAddress,
            "${location.mRID}-$field",
            description
        )
    }

    private fun savePositionPoint(location: Location, sequenceNumber: Int, positionPoint: PositionPoint): Boolean {
        val table = databaseTables.getTable<TablePositionPoints>()
        val insert = databaseTables.getInsert<TablePositionPoints>()

        insert.setNullableString(table.LOCATION_MRID.queryIndex, location.mRID)
        insert.setNullableInt(table.SEQUENCE_NUMBER.queryIndex, sequenceNumber)
        insert.setNullableDouble(table.X_POSITION.queryIndex, positionPoint.xPosition)
        insert.setNullableDouble(table.Y_POSITION.queryIndex, positionPoint.yPosition)

        return tryExecuteSingleUpdate(
            insert,
            "${location.mRID}-point$sequenceNumber",
            "position point"
        )
    }

    private fun saveStreetAddress(
        table: TableStreetAddresses,
        insert: PreparedStatement,
        streetAddress: StreetAddress,
        id: String,
        description: String
    ): Boolean {
        insert.setString(table.POSTAL_CODE.queryIndex, streetAddress.postalCode)
        insert.setString(table.PO_BOX.queryIndex, streetAddress.poBox)

        insertTownDetail(table, insert, streetAddress.townDetail)
        insertStreetDetail(table, insert, streetAddress.streetDetail)

        return tryExecuteSingleUpdate(insert, id, description)
    }

    /************ IEC61968 infIEC61968 InfAssetInfo ************/

    fun save(relayInfo: RelayInfo): Boolean {
        val table = databaseTables.getTable<TableRelayInfo>()
        val insert = databaseTables.getInsert<TableRelayInfo>()

        val recloseDelayTable = databaseTables.getTable<TableRecloseDelays>()
        val recloseDelayInsert = databaseTables.getInsert<TableRecloseDelays>()
        relayInfo.recloseDelays.forEachIndexed { idx, delay ->
            recloseDelayInsert.setString(recloseDelayTable.RELAY_INFO_MRID.queryIndex, relayInfo.mRID)
            recloseDelayInsert.setInt(recloseDelayTable.SEQUENCE_NUMBER.queryIndex, idx)
            recloseDelayInsert.setDouble(recloseDelayTable.RECLOSE_DELAY.queryIndex, delay)
            tryExecuteSingleUpdate(recloseDelayInsert, "${relayInfo.mRID}-rd-${idx}", "reclose delay")
        }

        insert.setNullableString(table.CURVE_SETTING.queryIndex, relayInfo.curveSetting)
        insert.setNullableBoolean(table.RECLOSE_FAST.queryIndex, relayInfo.recloseFast)

        return saveAssetInfo(table, insert, relayInfo, "relay info")
    }

    fun save(currentTransformerInfo: CurrentTransformerInfo): Boolean {
        val table = databaseTables.getTable<TableCurrentTransformerInfo>()
        val insert = databaseTables.getInsert<TableCurrentTransformerInfo>()

        insert.setNullableString(table.ACCURACY_CLASS.queryIndex, currentTransformerInfo.accuracyClass)
        insert.setNullableDouble(table.ACCURACY_LIMIT.queryIndex, currentTransformerInfo.accuracyLimit)
        insert.setNullableInt(table.CORE_COUNT.queryIndex, currentTransformerInfo.coreCount)
        insert.setNullableString(table.CT_CLASS.queryIndex, currentTransformerInfo.ctClass)
        insert.setNullableInt(table.KNEE_POINT_VOLTAGE.queryIndex, currentTransformerInfo.kneePointVoltage)
        insert.setNullableRatio(table.MAX_RATIO_NUMERATOR.queryIndex, table.MAX_RATIO_DENOMINATOR.queryIndex, currentTransformerInfo.maxRatio)
        insert.setNullableRatio(table.NOMINAL_RATIO_NUMERATOR.queryIndex, table.NOMINAL_RATIO_DENOMINATOR.queryIndex, currentTransformerInfo.nominalRatio)
        insert.setNullableDouble(table.PRIMARY_RATIO.queryIndex, currentTransformerInfo.primaryRatio)
        insert.setNullableInt(table.RATED_CURRENT.queryIndex, currentTransformerInfo.ratedCurrent)
        insert.setNullableInt(table.SECONDARY_FLS_RATING.queryIndex, currentTransformerInfo.secondaryFlsRating)
        insert.setNullableDouble(table.SECONDARY_RATIO.queryIndex, currentTransformerInfo.secondaryRatio)
        insert.setNullableString(table.USAGE.queryIndex, currentTransformerInfo.usage)

        return saveAssetInfo(table, insert, currentTransformerInfo, "current transformer info")
    }

    fun save(potentialTransformerInfo: PotentialTransformerInfo): Boolean {
        val table = databaseTables.getTable<TablePotentialTransformerInfo>()
        val insert = databaseTables.getInsert<TablePotentialTransformerInfo>()

        insert.setNullableString(table.ACCURACY_CLASS.queryIndex, potentialTransformerInfo.accuracyClass)
        insert.setNullableRatio(table.NOMINAL_RATIO_NUMERATOR.queryIndex, table.NOMINAL_RATIO_DENOMINATOR.queryIndex, potentialTransformerInfo.nominalRatio)
        insert.setNullableDouble(table.PRIMARY_RATIO.queryIndex, potentialTransformerInfo.primaryRatio)
        insert.setNullableString(table.PT_CLASS.queryIndex, potentialTransformerInfo.ptClass)
        insert.setNullableInt(table.RATED_VOLTAGE.queryIndex, potentialTransformerInfo.ratedVoltage)
        insert.setNullableDouble(table.SECONDARY_RATIO.queryIndex, potentialTransformerInfo.secondaryRatio)

        return saveAssetInfo(table, insert, potentialTransformerInfo, "potential transformer info")
    }

    /************ IEC61968 METERING ************/

    private fun saveEndDevice(table: TableEndDevices, insert: PreparedStatement, endDevice: EndDevice, description: String): Boolean {
        insert.setNullableString(table.CUSTOMER_MRID.queryIndex, endDevice.customerMRID)
        insert.setNullableString(table.SERVICE_LOCATION_MRID.queryIndex, endDevice.serviceLocation?.mRID)

        var status = true
        endDevice.usagePoints.forEach { status = status and saveAssociation(it, endDevice) }

        return status and saveAssetContainer(table, insert, endDevice, description)
    }

    fun save(meter: Meter): Boolean {
        val table = databaseTables.getTable<TableMeters>()
        val insert = databaseTables.getInsert<TableMeters>()

        return saveEndDevice(table, insert, meter, "meter")
    }

    fun save(usagePoint: UsagePoint): Boolean {
        val table = databaseTables.getTable<TableUsagePoints>()
        val insert = databaseTables.getInsert<TableUsagePoints>()

        insert.setNullableString(table.LOCATION_MRID.queryIndex, usagePoint.usagePointLocation?.mRID)
        insert.setBoolean(table.IS_VIRTUAL.queryIndex, usagePoint.isVirtual)
        insert.setNullableString(table.CONNECTION_CATEGORY.queryIndex, usagePoint.connectionCategory)
        insert.setNullableInt(table.RATED_POWER.queryIndex, usagePoint.ratedPower)
        insert.setNullableInt(table.APPROVED_INVERTER_CAPACITY.queryIndex, usagePoint.approvedInverterCapacity)

        var status = true
        usagePoint.equipment.forEach { status = status and saveAssociation(it, usagePoint) }

        return status and saveIdentifiedObject(table, insert, usagePoint, "usage point")
    }

    /************ IEC61968 OPERATIONS ************/

    fun save(operationalRestriction: OperationalRestriction): Boolean {
        val table = databaseTables.getTable<TableOperationalRestrictions>()
        val insert = databaseTables.getInsert<TableOperationalRestrictions>()

        var status = true
        operationalRestriction.equipment.forEach { status = status and saveAssociation(it, operationalRestriction) }

        return status and saveDocument(table, insert, operationalRestriction, "operational restriction")
    }

    /************ IEC61970 AUXILIARY EQUIPMENT ************/

    private fun saveAuxiliaryEquipment(
        table: TableAuxiliaryEquipment,
        insert: PreparedStatement,
        auxiliaryEquipment: AuxiliaryEquipment,
        description: String
    ): Boolean {
        insert.setNullableString(table.TERMINAL_MRID.queryIndex, auxiliaryEquipment.terminal?.mRID)

        return saveEquipment(table, insert, auxiliaryEquipment, description)
    }

    fun save(currentTransformer: CurrentTransformer): Boolean {
        val table = databaseTables.getTable<TableCurrentTransformers>()
        val insert = databaseTables.getInsert<TableCurrentTransformers>()

        insert.setNullableString(table.CURRENT_TRANSFORMER_INFO_MRID.queryIndex, currentTransformer.assetInfo?.mRID)
        insert.setNullableInt(table.CORE_BURDEN.queryIndex, currentTransformer.coreBurden)

        return saveSensor(table, insert, currentTransformer, "current transformer")
    }

    fun save(faultIndicator: FaultIndicator): Boolean {
        val table = databaseTables.getTable<TableFaultIndicators>()
        val insert = databaseTables.getInsert<TableFaultIndicators>()

        return saveAuxiliaryEquipment(table, insert, faultIndicator, "fault indicator")
    }

    fun save(potentialTransformer: PotentialTransformer): Boolean {
        val table = databaseTables.getTable<TablePotentialTransformers>()
        val insert = databaseTables.getInsert<TablePotentialTransformers>()

        insert.setNullableString(table.POTENTIAL_TRANSFORMER_INFO_MRID.queryIndex, potentialTransformer.assetInfo?.mRID)
        insert.setString(table.TYPE.queryIndex, potentialTransformer.type.name)

        return saveSensor(table, insert, potentialTransformer, "potential transformer")
    }

    private fun saveSensor(table: TableSensors, insert: PreparedStatement, sensor: Sensor, description: String): Boolean {
        return saveAuxiliaryEquipment(table, insert, sensor, description)
    }

    /************ IEC61970 CORE ************/

    private fun saveAcDcTerminal(table: TableAcDcTerminals, insert: PreparedStatement, acDcTerminal: AcDcTerminal, description: String): Boolean {
        return saveIdentifiedObject(table, insert, acDcTerminal, description)
    }

    fun save(baseVoltage: BaseVoltage): Boolean {
        val table = databaseTables.getTable<TableBaseVoltages>()
        val insert = databaseTables.getInsert<TableBaseVoltages>()

        insert.setInt(table.NOMINAL_VOLTAGE.queryIndex, baseVoltage.nominalVoltage)

        return saveIdentifiedObject(table, insert, baseVoltage, "base voltage")
    }

    private fun saveConductingEquipment(
        table: TableConductingEquipment,
        insert: PreparedStatement,
        conductingEquipment: ConductingEquipment,
        description: String
    ): Boolean {
        insert.setNullableString(table.BASE_VOLTAGE_MRID.queryIndex, conductingEquipment.baseVoltage?.mRID)

        return saveEquipment(table, insert, conductingEquipment, description)
    }

    fun save(connectivityNode: ConnectivityNode): Boolean {
        val table = databaseTables.getTable<TableConnectivityNodes>()
        val insert = databaseTables.getInsert<TableConnectivityNodes>()

        return saveIdentifiedObject(table, insert, connectivityNode, "connectivity node")
    }

    private fun saveConnectivityNodeContainer(
        table: TableConnectivityNodeContainers,
        insert: PreparedStatement,
        connectivityNodeContainer: ConnectivityNodeContainer,
        description: String
    ): Boolean {
        return savePowerSystemResource(table, insert, connectivityNodeContainer, description)
    }

    private fun saveEquipment(table: TableEquipment, insert: PreparedStatement, equipment: Equipment, description: String): Boolean {
        insert.setBoolean(table.NORMALLY_IN_SERVICE.queryIndex, equipment.normallyInService)
        insert.setBoolean(table.IN_SERVICE.queryIndex, equipment.inService)
        insert.setInstant(table.COMMISSIONED_DATE.queryIndex, equipment.commissionedDate)

        var status = true
        equipment.containers.forEach {
            if (it !is Feeder)
                status = status and saveAssociation(equipment, it)
        }

        return status and savePowerSystemResource(table, insert, equipment, description)
    }

    private fun saveEquipmentContainer(
        table: TableEquipmentContainers,
        insert: PreparedStatement,
        equipmentContainer: EquipmentContainer,
        description: String
    ): Boolean {
        return saveConnectivityNodeContainer(table, insert, equipmentContainer, description)
    }

    fun save(feeder: Feeder): Boolean {
        val table = databaseTables.getTable<TableFeeders>()
        val insert = databaseTables.getInsert<TableFeeders>()

        insert.setNullableString(table.NORMAL_HEAD_TERMINAL_MRID.queryIndex, feeder.normalHeadTerminal?.mRID)
        insert.setNullableString(
            table.NORMAL_ENERGIZING_SUBSTATION_MRID.queryIndex,
            feeder.normalEnergizingSubstation?.mRID
        )

        return saveEquipmentContainer(table, insert, feeder, "feeder")
    }

    fun save(geographicalRegion: GeographicalRegion): Boolean {
        val table = databaseTables.getTable<TableGeographicalRegions>()
        val insert = databaseTables.getInsert<TableGeographicalRegions>()

        return saveIdentifiedObject(table, insert, geographicalRegion, "geographical region")
    }

    private fun savePowerSystemResource(
        table: TablePowerSystemResources,
        insert: PreparedStatement,
        powerSystemResource: PowerSystemResource,
        description: String
    ): Boolean {
        insert.setNullableString(table.LOCATION_MRID.queryIndex, powerSystemResource.location?.mRID)
        insert.setInt(table.NUM_CONTROLS.queryIndex, powerSystemResource.numControls)

        return saveIdentifiedObject(table, insert, powerSystemResource, description)
    }

    fun save(site: Site): Boolean {
        val table = databaseTables.getTable<TableSites>()
        val insert = databaseTables.getInsert<TableSites>()

        return saveEquipmentContainer(table, insert, site, "site")
    }

    fun save(subGeographicalRegion: SubGeographicalRegion): Boolean {
        val table = databaseTables.getTable<TableSubGeographicalRegions>()
        val insert = databaseTables.getInsert<TableSubGeographicalRegions>()

        insert.setNullableString(
            table.GEOGRAPHICAL_REGION_MRID.queryIndex,
            subGeographicalRegion.geographicalRegion?.mRID
        )

        return saveIdentifiedObject(table, insert, subGeographicalRegion, "sub-geographical region")
    }

    fun save(substation: Substation): Boolean {
        val table = databaseTables.getTable<TableSubstations>()
        val insert = databaseTables.getInsert<TableSubstations>()

        insert.setNullableString(table.SUB_GEOGRAPHICAL_REGION_MRID.queryIndex, substation.subGeographicalRegion?.mRID)

        return saveEquipmentContainer(table, insert, substation, "substation")
    }

    fun save(terminal: Terminal): Boolean {
        val table = databaseTables.getTable<TableTerminals>()
        val insert = databaseTables.getInsert<TableTerminals>()

        insert.setNullableString(table.CONDUCTING_EQUIPMENT_MRID.queryIndex, terminal.conductingEquipment?.mRID)
        insert.setInt(table.SEQUENCE_NUMBER.queryIndex, terminal.sequenceNumber)
        insert.setNullableString(table.CONNECTIVITY_NODE_MRID.queryIndex, terminal.connectivityNodeId)
        insert.setNullableString(table.PHASES.queryIndex, terminal.phases.name)

        return saveAcDcTerminal(table, insert, terminal, "terminal")
    }

    /************ IEC61970 WIRES ************/

    fun save(equivalentBranch: EquivalentBranch): Boolean {
        val table = databaseTables.getTable<TableEquivalentBranches>()
        val insert = databaseTables.getInsert<TableEquivalentBranches>()

        insert.setNullableDouble(table.NEGATIVE_R12.queryIndex, equivalentBranch.negativeR12)
        insert.setNullableDouble(table.NEGATIVE_R21.queryIndex, equivalentBranch.negativeR21)
        insert.setNullableDouble(table.NEGATIVE_X12.queryIndex, equivalentBranch.negativeX12)
        insert.setNullableDouble(table.NEGATIVE_X21.queryIndex, equivalentBranch.negativeX21)
        insert.setNullableDouble(table.POSITIVE_R12.queryIndex, equivalentBranch.positiveR12)
        insert.setNullableDouble(table.POSITIVE_R21.queryIndex, equivalentBranch.positiveR21)
        insert.setNullableDouble(table.POSITIVE_X12.queryIndex, equivalentBranch.positiveX12)
        insert.setNullableDouble(table.POSITIVE_X21.queryIndex, equivalentBranch.positiveX21)
        insert.setNullableDouble(table.R.queryIndex, equivalentBranch.r)
        insert.setNullableDouble(table.R21.queryIndex, equivalentBranch.r21)
        insert.setNullableDouble(table.X.queryIndex, equivalentBranch.x)
        insert.setNullableDouble(table.X21.queryIndex, equivalentBranch.x21)
        insert.setNullableDouble(table.ZERO_R12.queryIndex, equivalentBranch.zeroR12)
        insert.setNullableDouble(table.ZERO_R21.queryIndex, equivalentBranch.zeroR21)
        insert.setNullableDouble(table.ZERO_X12.queryIndex, equivalentBranch.zeroX12)
        insert.setNullableDouble(table.ZERO_X21.queryIndex, equivalentBranch.zeroX21)

        return saveEquivalentEquipment(table, insert, equivalentBranch, "equivalent branch")
    }

    private fun saveEquivalentEquipment(
        table: TableEquivalentEquipment,
        insert: PreparedStatement,
        equivalentEquipment: EquivalentEquipment,
        description: String
    ): Boolean =
        saveConductingEquipment(table, insert, equivalentEquipment, description)

    /************ IEC61970 WIRES GENERATION PRODUCTION************/
    fun save(evChargingUnit: EvChargingUnit): Boolean {
        val table = databaseTables.getTable<TableEvChargingUnits>()
        val insert = databaseTables.getInsert<TableEvChargingUnits>()

        return savePowerElectronicsUnit(table, insert, evChargingUnit, "ev charging unit")
    }

    fun save(batteryUnit: BatteryUnit): Boolean {
        val table = databaseTables.getTable<TableBatteryUnit>()
        val insert = databaseTables.getInsert<TableBatteryUnit>()

        insert.setString(table.BATTERY_STATE.queryIndex, batteryUnit.batteryState.name)
        insert.setNullableLong(table.RATED_E.queryIndex, batteryUnit.ratedE)
        insert.setNullableLong(table.STORED_E.queryIndex, batteryUnit.storedE)

        return savePowerElectronicsUnit(table, insert, batteryUnit, "battery unit")
    }

    fun save(photoVoltaicUnit: PhotoVoltaicUnit): Boolean {
        val table = databaseTables.getTable<TablePhotoVoltaicUnit>()
        val insert = databaseTables.getInsert<TablePhotoVoltaicUnit>()

        return savePowerElectronicsUnit(table, insert, photoVoltaicUnit, "photo voltaic unit")
    }

    private fun savePowerElectronicsUnit(
        table: TablePowerElectronicsUnit,
        insert: PreparedStatement,
        powerElectronicsUnit: PowerElectronicsUnit,
        description: String
    ): Boolean {
        insert.setNullableString(table.POWER_ELECTRONICS_CONNECTION_MRID.queryIndex, powerElectronicsUnit.powerElectronicsConnection?.mRID)
        insert.setNullableInt(table.MAX_P.queryIndex, powerElectronicsUnit.maxP)
        insert.setNullableInt(table.MIN_P.queryIndex, powerElectronicsUnit.minP)

        return saveEquipment(table, insert, powerElectronicsUnit, description)
    }

    fun save(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean {
        val table = databaseTables.getTable<TablePowerElectronicsWindUnit>()
        val insert = databaseTables.getInsert<TablePowerElectronicsWindUnit>()

        return savePowerElectronicsUnit(table, insert, powerElectronicsWindUnit, "power electronics wind unit")
    }

    /************ IEC61970 WIRES ************/

    fun save(acLineSegment: AcLineSegment): Boolean {
        val table = databaseTables.getTable<TableAcLineSegments>()
        val insert = databaseTables.getInsert<TableAcLineSegments>()

        insert.setNullableString(
            table.PER_LENGTH_SEQUENCE_IMPEDANCE_MRID.queryIndex,
            acLineSegment.perLengthSequenceImpedance?.mRID
        )

        return saveConductor(table, insert, acLineSegment, "AC line segment")
    }

    fun save(breaker: Breaker): Boolean {
        val table = databaseTables.getTable<TableBreakers>()
        val insert = databaseTables.getInsert<TableBreakers>()

        insert.setNullableDouble(table.IN_TRANSIT_TIME.queryIndex, breaker.inTransitTime)

        return saveProtectedSwitch(table, insert, breaker, "breaker")
    }

    fun save(loadBreakSwitch: LoadBreakSwitch): Boolean {
        val table = databaseTables.getTable<TableLoadBreakSwitches>()
        val insert = databaseTables.getInsert<TableLoadBreakSwitches>()

        return saveProtectedSwitch(table, insert, loadBreakSwitch, "load break switch")
    }

    fun save(busbarSection: BusbarSection): Boolean {
        val table = databaseTables.getTable<TableBusbarSections>()
        val insert = databaseTables.getInsert<TableBusbarSections>()

        return saveConnector(table, insert, busbarSection, "busbar section")
    }

    private fun saveConductor(table: TableConductors, insert: PreparedStatement, conductor: Conductor, description: String): Boolean {
        insert.setNullableDouble(table.LENGTH.queryIndex, conductor.length)
        insert.setNullableString(table.WIRE_INFO_MRID.queryIndex, conductor.assetInfo?.mRID)

        return saveConductingEquipment(table, insert, conductor, description)
    }

    private fun saveConnector(table: TableConnectors, insert: PreparedStatement, connector: Connector, description: String): Boolean {
        return saveConductingEquipment(table, insert, connector, description)
    }

    fun save(disconnector: Disconnector): Boolean {
        val table = databaseTables.getTable<TableDisconnectors>()
        val insert = databaseTables.getInsert<TableDisconnectors>()

        return saveSwitch(table, insert, disconnector, "disconnector")
    }

    private fun saveEnergyConnection(
        table: TableEnergyConnections,
        insert: PreparedStatement,
        energyConnection: EnergyConnection,
        description: String
    ): Boolean {
        return saveConductingEquipment(table, insert, energyConnection, description)
    }

    fun save(energyConsumer: EnergyConsumer): Boolean {
        val table = databaseTables.getTable<TableEnergyConsumers>()
        val insert = databaseTables.getInsert<TableEnergyConsumers>()

        insert.setNullableInt(table.CUSTOMER_COUNT.queryIndex, energyConsumer.customerCount)
        insert.setBoolean(table.GROUNDED.queryIndex, energyConsumer.grounded)
        insert.setNullableDouble(table.P.queryIndex, energyConsumer.p)
        insert.setNullableDouble(table.Q.queryIndex, energyConsumer.q)
        insert.setNullableDouble(table.P_FIXED.queryIndex, energyConsumer.pFixed)
        insert.setNullableDouble(table.Q_FIXED.queryIndex, energyConsumer.qFixed)
        insert.setNullableString(table.PHASE_CONNECTION.queryIndex, energyConsumer.phaseConnection.name)

        return saveEnergyConnection(table, insert, energyConsumer, "energy consumer")
    }

    fun save(energyConsumerPhase: EnergyConsumerPhase): Boolean {
        val table = databaseTables.getTable<TableEnergyConsumerPhases>()
        val insert = databaseTables.getInsert<TableEnergyConsumerPhases>()

        insert.setNullableString(table.ENERGY_CONSUMER_MRID.queryIndex, energyConsumerPhase.energyConsumer?.mRID)
        insert.setNullableString(table.PHASE.queryIndex, energyConsumerPhase.phase.name)
        insert.setNullableDouble(table.P.queryIndex, energyConsumerPhase.p)
        insert.setNullableDouble(table.Q.queryIndex, energyConsumerPhase.q)
        insert.setNullableDouble(table.P_FIXED.queryIndex, energyConsumerPhase.pFixed)
        insert.setNullableDouble(table.Q_FIXED.queryIndex, energyConsumerPhase.qFixed)

        return savePowerSystemResource(table, insert, energyConsumerPhase, "energy consumer phase")
    }

    fun save(energySource: EnergySource): Boolean {
        val table = databaseTables.getTable<TableEnergySources>()
        val insert = databaseTables.getInsert<TableEnergySources>()

        insert.setNullableDouble(table.ACTIVE_POWER.queryIndex, energySource.activePower)
        insert.setNullableDouble(table.REACTIVE_POWER.queryIndex, energySource.reactivePower)
        insert.setNullableDouble(table.VOLTAGE_ANGLE.queryIndex, energySource.voltageAngle)
        insert.setNullableDouble(table.VOLTAGE_MAGNITUDE.queryIndex, energySource.voltageMagnitude)
        insert.setNullableDouble(table.P_MAX.queryIndex, energySource.pMax)
        insert.setNullableDouble(table.P_MIN.queryIndex, energySource.pMin)
        insert.setNullableDouble(table.R.queryIndex, energySource.r)
        insert.setNullableDouble(table.R0.queryIndex, energySource.r0)
        insert.setNullableDouble(table.RN.queryIndex, energySource.rn)
        insert.setNullableDouble(table.X.queryIndex, energySource.x)
        insert.setNullableDouble(table.X0.queryIndex, energySource.x0)
        insert.setNullableDouble(table.XN.queryIndex, energySource.xn)
        insert.setBoolean(table.IS_EXTERNAL_GRID.queryIndex, energySource.isExternalGrid)
        insert.setNullableDouble(table.R_MIN.queryIndex, energySource.rMin)
        insert.setNullableDouble(table.RN_MIN.queryIndex, energySource.rnMin)
        insert.setNullableDouble(table.R0_MIN.queryIndex, energySource.r0Min)
        insert.setNullableDouble(table.X_MIN.queryIndex, energySource.xMin)
        insert.setNullableDouble(table.XN_MIN.queryIndex, energySource.xnMin)
        insert.setNullableDouble(table.X0_MIN.queryIndex, energySource.x0Min)
        insert.setNullableDouble(table.R_MAX.queryIndex, energySource.rMax)
        insert.setNullableDouble(table.RN_MAX.queryIndex, energySource.rnMax)
        insert.setNullableDouble(table.R0_MAX.queryIndex, energySource.r0Max)
        insert.setNullableDouble(table.X_MAX.queryIndex, energySource.xMax)
        insert.setNullableDouble(table.XN_MAX.queryIndex, energySource.xnMax)
        insert.setNullableDouble(table.X0_MAX.queryIndex, energySource.x0Max)

        return saveEnergyConnection(table, insert, energySource, "energy source")
    }

    fun save(energySourcePhase: EnergySourcePhase): Boolean {
        val table = databaseTables.getTable<TableEnergySourcePhases>()
        val insert = databaseTables.getInsert<TableEnergySourcePhases>()

        insert.setNullableString(table.ENERGY_SOURCE_MRID.queryIndex, energySourcePhase.energySource?.mRID)
        insert.setNullableString(table.PHASE.queryIndex, energySourcePhase.phase.name)

        return savePowerSystemResource(table, insert, energySourcePhase, "energy source phase")
    }

    fun save(fuse: Fuse): Boolean {
        val table = databaseTables.getTable<TableFuses>()
        val insert = databaseTables.getInsert<TableFuses>()

        insert.setNullableString(table.FUNCTION_MRID.queryIndex, fuse.function?.mRID)

        return saveSwitch(table, insert, fuse, "fuse")
    }

    fun save(ground: Ground): Boolean {
        val table = databaseTables.getTable<TableGrounds>()
        val insert = databaseTables.getInsert<TableGrounds>()

        return saveConductingEquipment(table, insert, ground, "ground")
    }

    fun save(groundDisconnector: GroundDisconnector): Boolean {
        val table = databaseTables.getTable<TableGroundDisconnectors>()
        val insert = databaseTables.getInsert<TableGroundDisconnectors>()

        return saveSwitch(table, insert, groundDisconnector, "ground disconnector")
    }

    fun save(jumper: Jumper): Boolean {
        val table = databaseTables.getTable<TableJumpers>()
        val insert = databaseTables.getInsert<TableJumpers>()

        return saveSwitch(table, insert, jumper, "jumper")
    }

    fun save(junction: Junction): Boolean {
        val table = databaseTables.getTable<TableJunctions>()
        val insert = databaseTables.getInsert<TableJunctions>()

        return saveConnector(table, insert, junction, "junction")
    }

    private fun saveLine(table: TableLines, insert: PreparedStatement, line: Line, description: String): Boolean {
        return saveEquipmentContainer(table, insert, line, description)
    }

    fun save(linearShuntCompensator: LinearShuntCompensator): Boolean {
        val table = databaseTables.getTable<TableLinearShuntCompensators>()
        val insert = databaseTables.getInsert<TableLinearShuntCompensators>()

        insert.setNullableDouble(table.B0_PER_SECTION.queryIndex, linearShuntCompensator.b0PerSection)
        insert.setNullableDouble(table.B_PER_SECTION.queryIndex, linearShuntCompensator.bPerSection)
        insert.setNullableDouble(table.G0_PER_SECTION.queryIndex, linearShuntCompensator.g0PerSection)
        insert.setNullableDouble(table.G_PER_SECTION.queryIndex, linearShuntCompensator.gPerSection)

        return saveShuntCompensator(table, insert, linearShuntCompensator, "linear shunt compensator")
    }

    private fun savePerLengthImpedance(
        table: TablePerLengthImpedances,
        insert: PreparedStatement,
        perLengthImpedance: PerLengthImpedance,
        description: String
    ): Boolean {
        return savePerLengthLineParameter(table, insert, perLengthImpedance, description)
    }

    private fun savePerLengthLineParameter(
        table: TablePerLengthLineParameters,
        insert: PreparedStatement,
        perLengthLineParameter: PerLengthLineParameter,
        description: String
    ): Boolean {
        return saveIdentifiedObject(table, insert, perLengthLineParameter, description)
    }

    fun save(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean {
        val table = databaseTables.getTable<TablePerLengthSequenceImpedances>()
        val insert = databaseTables.getInsert<TablePerLengthSequenceImpedances>()

        insert.setNullableDouble(table.R.queryIndex, perLengthSequenceImpedance.r)
        insert.setNullableDouble(table.X.queryIndex, perLengthSequenceImpedance.x)
        insert.setNullableDouble(table.R0.queryIndex, perLengthSequenceImpedance.r0)
        insert.setNullableDouble(table.X0.queryIndex, perLengthSequenceImpedance.x0)
        insert.setNullableDouble(table.BCH.queryIndex, perLengthSequenceImpedance.bch)
        insert.setNullableDouble(table.GCH.queryIndex, perLengthSequenceImpedance.gch)
        insert.setNullableDouble(table.B0CH.queryIndex, perLengthSequenceImpedance.b0ch)
        insert.setNullableDouble(table.G0CH.queryIndex, perLengthSequenceImpedance.g0ch)

        return savePerLengthImpedance(table, insert, perLengthSequenceImpedance, "per length sequence impedance")
    }

    fun save(powerElectronicsConnection: PowerElectronicsConnection): Boolean {
        val table = databaseTables.getTable<TablePowerElectronicsConnection>()
        val insert = databaseTables.getInsert<TablePowerElectronicsConnection>()

        insert.setNullableInt(table.MAX_I_FAULT.queryIndex, powerElectronicsConnection.maxIFault)
        insert.setNullableDouble(table.MAX_Q.queryIndex, powerElectronicsConnection.maxQ)
        insert.setNullableDouble(table.MIN_Q.queryIndex, powerElectronicsConnection.minQ)
        insert.setNullableDouble(table.P.queryIndex, powerElectronicsConnection.p)
        insert.setNullableDouble(table.Q.queryIndex, powerElectronicsConnection.q)
        insert.setNullableInt(table.RATED_S.queryIndex, powerElectronicsConnection.ratedS)
        insert.setNullableInt(table.RATED_U.queryIndex, powerElectronicsConnection.ratedU)
        insert.setNullableString(table.INVERTER_STANDARD.queryIndex, powerElectronicsConnection.inverterStandard)
        insert.setNullableInt(table.SUSTAIN_OP_OVERVOLT_LIMIT.queryIndex, powerElectronicsConnection.sustainOpOvervoltLimit)
        insert.setNullableFloat(table.STOP_AT_OVER_FREQ.queryIndex, powerElectronicsConnection.stopAtOverFreq)
        insert.setNullableFloat(table.STOP_AT_UNDER_FREQ.queryIndex, powerElectronicsConnection.stopAtUnderFreq)
        insert.setNullableBoolean(table.INV_VOLT_WATT_RESP_MODE.queryIndex, powerElectronicsConnection.invVoltWattRespMode)
        insert.setNullableInt(table.INV_WATT_RESP_V1.queryIndex, powerElectronicsConnection.invWattRespV1)
        insert.setNullableInt(table.INV_WATT_RESP_V2.queryIndex, powerElectronicsConnection.invWattRespV2)
        insert.setNullableInt(table.INV_WATT_RESP_V3.queryIndex, powerElectronicsConnection.invWattRespV3)
        insert.setNullableInt(table.INV_WATT_RESP_V4.queryIndex, powerElectronicsConnection.invWattRespV4)
        insert.setNullableFloat(table.INV_WATT_RESP_P_AT_V1.queryIndex, powerElectronicsConnection.invWattRespPAtV1)
        insert.setNullableFloat(table.INV_WATT_RESP_P_AT_V2.queryIndex, powerElectronicsConnection.invWattRespPAtV2)
        insert.setNullableFloat(table.INV_WATT_RESP_P_AT_V3.queryIndex, powerElectronicsConnection.invWattRespPAtV3)
        insert.setNullableFloat(table.INV_WATT_RESP_P_AT_V4.queryIndex, powerElectronicsConnection.invWattRespPAtV4)
        insert.setNullableBoolean(table.INV_VOLT_VAR_RESP_MODE.queryIndex, powerElectronicsConnection.invVoltVarRespMode)
        insert.setNullableInt(table.INV_VAR_RESP_V1.queryIndex, powerElectronicsConnection.invVarRespV1)
        insert.setNullableInt(table.INV_VAR_RESP_V2.queryIndex, powerElectronicsConnection.invVarRespV2)
        insert.setNullableInt(table.INV_VAR_RESP_V3.queryIndex, powerElectronicsConnection.invVarRespV3)
        insert.setNullableInt(table.INV_VAR_RESP_V4.queryIndex, powerElectronicsConnection.invVarRespV4)
        insert.setNullableFloat(table.INV_VAR_RESP_Q_AT_V1.queryIndex, powerElectronicsConnection.invVarRespQAtV1)
        insert.setNullableFloat(table.INV_VAR_RESP_Q_AT_V2.queryIndex, powerElectronicsConnection.invVarRespQAtV2)
        insert.setNullableFloat(table.INV_VAR_RESP_Q_AT_V3.queryIndex, powerElectronicsConnection.invVarRespQAtV3)
        insert.setNullableFloat(table.INV_VAR_RESP_Q_AT_V4.queryIndex, powerElectronicsConnection.invVarRespQAtV4)
        insert.setNullableBoolean(table.INV_REACTIVE_POWER_MODE.queryIndex, powerElectronicsConnection.invReactivePowerMode)
        insert.setNullableFloat(table.INV_FIX_REACTIVE_POWER.queryIndex, powerElectronicsConnection.invFixReactivePower)

        return saveRegulatingCondEq(table, insert, powerElectronicsConnection, "power electronics connection")
    }

    fun save(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean {
        val table = databaseTables.getTable<TablePowerElectronicsConnectionPhases>()
        val insert = databaseTables.getInsert<TablePowerElectronicsConnectionPhases>()

        insert.setNullableString(table.POWER_ELECTRONICS_CONNECTION_MRID.queryIndex, powerElectronicsConnectionPhase.powerElectronicsConnection?.mRID)
        insert.setNullableDouble(table.P.queryIndex, powerElectronicsConnectionPhase.p)
        insert.setString(table.PHASE.queryIndex, powerElectronicsConnectionPhase.phase.name)
        insert.setNullableDouble(table.Q.queryIndex, powerElectronicsConnectionPhase.q)

        return savePowerSystemResource(table, insert, powerElectronicsConnectionPhase, "power electronics connection phase")
    }

    fun save(powerTransformer: PowerTransformer): Boolean {
        val table = databaseTables.getTable<TablePowerTransformers>()
        val insert = databaseTables.getInsert<TablePowerTransformers>()

        insert.setString(table.VECTOR_GROUP.queryIndex, powerTransformer.vectorGroup.name)
        insert.setNullableDouble(table.TRANSFORMER_UTILISATION.queryIndex, powerTransformer.transformerUtilisation)
        insert.setString(table.CONSTRUCTION_KIND.queryIndex, powerTransformer.constructionKind.name)
        insert.setString(table.FUNCTION.queryIndex, powerTransformer.function.name)
        insert.setNullableString(table.POWER_TRANSFORMER_INFO_MRID.queryIndex, powerTransformer.assetInfo?.mRID)

        return saveConductingEquipment(table, insert, powerTransformer, "power transformer")
    }

    fun save(powerTransformerEnd: PowerTransformerEnd): Boolean {
        val table = databaseTables.getTable<TablePowerTransformerEnds>()
        val insert = databaseTables.getInsert<TablePowerTransformerEnds>()

        insert.setNullableString(table.POWER_TRANSFORMER_MRID.queryIndex, powerTransformerEnd.powerTransformer?.mRID)
        insert.setNullableString(table.CONNECTION_KIND.queryIndex, powerTransformerEnd.connectionKind.name)
        insert.setNullableInt(table.PHASE_ANGLE_CLOCK.queryIndex, powerTransformerEnd.phaseAngleClock)
        insert.setNullableDouble(table.B.queryIndex, powerTransformerEnd.b)
        insert.setNullableDouble(table.B0.queryIndex, powerTransformerEnd.b0)
        insert.setNullableDouble(table.G.queryIndex, powerTransformerEnd.g)
        insert.setNullableDouble(table.G0.queryIndex, powerTransformerEnd.g0)
        insert.setNullableDouble(table.R.queryIndex, powerTransformerEnd.r)
        insert.setNullableDouble(table.R0.queryIndex, powerTransformerEnd.r0)
        insert.setNullableInt(table.RATED_U.queryIndex, powerTransformerEnd.ratedU)
        insert.setNullableDouble(table.X.queryIndex, powerTransformerEnd.x)
        insert.setNullableDouble(table.X0.queryIndex, powerTransformerEnd.x0)

        val ratingsTable = databaseTables.getTable<TablePowerTransformerEndRatings>()
        val ratingsInsert = databaseTables.getInsert<TablePowerTransformerEndRatings>()
        powerTransformerEnd.sRatings.forEach {
            ratingsInsert.setString(ratingsTable.POWER_TRANSFORMER_END_MRID.queryIndex, powerTransformerEnd.mRID)
            ratingsInsert.setString(ratingsTable.COOLING_TYPE.queryIndex, it.coolingType.name)
            ratingsInsert.setInt(ratingsTable.RATED_S.queryIndex, it.ratedS)
            tryExecuteSingleUpdate(ratingsInsert, "${powerTransformerEnd.mRID}-${it.coolingType.name}-${it.ratedS}", "transformer end ratedS")
        }

        return saveTransformerEnd(table, insert, powerTransformerEnd, "power transformer end")
    }

    private fun saveProtectedSwitch(table: TableProtectedSwitches, insert: PreparedStatement, protectedSwitch: ProtectedSwitch, description: String): Boolean {
        insert.setNullableInt(table.BREAKING_CAPACITY.queryIndex, protectedSwitch.breakingCapacity)

        return saveSwitch(table, insert, protectedSwitch, description)
    }

    fun save(ratioTapChanger: RatioTapChanger): Boolean {
        val table = databaseTables.getTable<TableRatioTapChangers>()
        val insert = databaseTables.getInsert<TableRatioTapChangers>()

        insert.setNullableString(table.TRANSFORMER_END_MRID.queryIndex, ratioTapChanger.transformerEnd?.mRID)
        insert.setNullableDouble(table.STEP_VOLTAGE_INCREMENT.queryIndex, ratioTapChanger.stepVoltageIncrement)

        return saveTapChanger(table, insert, ratioTapChanger, "ratio tap changer")
    }

    fun save(recloser: Recloser): Boolean {
        val table = databaseTables.getTable<TableReclosers>()
        val insert = databaseTables.getInsert<TableReclosers>()

        return saveProtectedSwitch(table, insert, recloser, "recloser")
    }

    private fun saveRegulatingCondEq(
        table: TableRegulatingCondEq,
        insert: PreparedStatement,
        regulatingCondEq: RegulatingCondEq,
        description: String
    ): Boolean {
        insert.setBoolean(table.CONTROL_ENABLED.queryIndex, regulatingCondEq.controlEnabled)
        insert.setNullableString(table.REGULATING_CONTROL_MRID.queryIndex, regulatingCondEq.regulatingControl?.mRID)

        return saveEnergyConnection(table, insert, regulatingCondEq, description)
    }

    private fun saveRegulatingControl(
        table: TableRegulatingControls,
        insert: PreparedStatement,
        regulatingControl: RegulatingControl,
        description: String
    ): Boolean {
        insert.setNullableBoolean(table.DISCRETE.queryIndex, regulatingControl.discrete)
        insert.setString(table.MODE.queryIndex, regulatingControl.mode.name)
        insert.setString(table.MONITORED_PHASE.queryIndex, regulatingControl.monitoredPhase.name)
        insert.setNullableFloat(table.TARGET_DEADBAND.queryIndex, regulatingControl.targetDeadband)
        insert.setNullableDouble(table.TARGET_VALUE.queryIndex, regulatingControl.targetValue)
        insert.setNullableBoolean(table.ENABLED.queryIndex, regulatingControl.enabled)
        insert.setNullableDouble(table.MAX_ALLOWED_TARGET_VALUE.queryIndex, regulatingControl.maxAllowedTargetValue)
        insert.setNullableDouble(table.MIN_ALLOWED_TARGET_VALUE.queryIndex, regulatingControl.minAllowedTargetValue)
        insert.setNullableDouble(table.RATED_CURRENT.queryIndex, regulatingControl.ratedCurrent)
        insert.setNullableString(table.TERMINAL_MRID.queryIndex, regulatingControl.terminal?.mRID)

        return savePowerSystemResource(table, insert, regulatingControl, description)
    }

    fun save(seriesCompensator: SeriesCompensator): Boolean {
        val table = databaseTables.getTable<TableSeriesCompensators>()
        val insert = databaseTables.getInsert<TableSeriesCompensators>()

        insert.setNullableDouble(table.R.queryIndex, seriesCompensator.r)
        insert.setNullableDouble(table.R0.queryIndex, seriesCompensator.r0)
        insert.setNullableDouble(table.X.queryIndex, seriesCompensator.x)
        insert.setNullableDouble(table.X0.queryIndex, seriesCompensator.x0)
        insert.setNullableInt(table.VARISTOR_RATED_CURRENT.queryIndex, seriesCompensator.varistorRatedCurrent)
        insert.setNullableInt(table.VARISTOR_VOLTAGE_THRESHOLD.queryIndex, seriesCompensator.varistorVoltageThreshold)

        return saveConductingEquipment(table, insert, seriesCompensator, "series compensator")
    }

    private fun saveShuntCompensator(
        table: TableShuntCompensators,
        insert: PreparedStatement,
        shuntCompensator: ShuntCompensator,
        description: String
    ): Boolean {
        insert.setNullableString(table.SHUNT_COMPENSATOR_INFO_MRID.queryIndex, shuntCompensator.assetInfo?.mRID)
        insert.setBoolean(table.GROUNDED.queryIndex, shuntCompensator.grounded)
        insert.setNullableInt(table.NOM_U.queryIndex, shuntCompensator.nomU)
        insert.setNullableString(table.PHASE_CONNECTION.queryIndex, shuntCompensator.phaseConnection.name)
        insert.setNullableDouble(table.SECTIONS.queryIndex, shuntCompensator.sections)

        return saveRegulatingCondEq(table, insert, shuntCompensator, description)
    }

    private fun saveSwitch(table: TableSwitches, insert: PreparedStatement, switch: Switch, description: String): Boolean {
        insert.setInt(table.NORMAL_OPEN.queryIndex, switch.normalOpen)
        insert.setInt(table.OPEN.queryIndex, switch.open)
        insert.setNullableInt(table.RATED_CURRENT.queryIndex, switch.ratedCurrent)
        insert.setNullableString(table.SWITCH_INFO_MRID.queryIndex, switch.assetInfo?.mRID)

        return saveConductingEquipment(table, insert, switch, description)
    }

    private fun saveTapChanger(table: TableTapChangers, insert: PreparedStatement, tapChanger: TapChanger, description: String): Boolean {
        insert.setBoolean(table.CONTROL_ENABLED.queryIndex, tapChanger.controlEnabled)
        insert.setNullableInt(table.HIGH_STEP.queryIndex, tapChanger.highStep)
        insert.setNullableInt(table.LOW_STEP.queryIndex, tapChanger.lowStep)
        insert.setNullableInt(table.NEUTRAL_STEP.queryIndex, tapChanger.neutralStep)
        insert.setNullableInt(table.NEUTRAL_U.queryIndex, tapChanger.neutralU)
        insert.setNullableInt(table.NORMAL_STEP.queryIndex, tapChanger.normalStep)
        insert.setNullableDouble(table.STEP.queryIndex, tapChanger.step)
        insert.setNullableString(table.TAP_CHANGER_CONTROL_MRID.queryIndex, tapChanger.tapChangerControl?.mRID)

        return savePowerSystemResource(table, insert, tapChanger, description)
    }

    fun save(tapChangerControl: TapChangerControl): Boolean {
        val table = databaseTables.getTable<TableTapChangerControls>()
        val insert = databaseTables.getInsert<TableTapChangerControls>()

        insert.setNullableInt(table.LIMIT_VOLTAGE.queryIndex, tapChangerControl.limitVoltage)
        insert.setNullableBoolean(table.LINE_DROP_COMPENSATION.queryIndex, tapChangerControl.lineDropCompensation)
        insert.setNullableDouble(table.LINE_DROP_R.queryIndex, tapChangerControl.lineDropR)
        insert.setNullableDouble(table.LINE_DROP_X.queryIndex, tapChangerControl.lineDropX)
        insert.setNullableDouble(table.REVERSE_LINE_DROP_R.queryIndex, tapChangerControl.reverseLineDropR)
        insert.setNullableDouble(table.REVERSE_LINE_DROP_X.queryIndex, tapChangerControl.reverseLineDropX)
        insert.setNullableBoolean(table.FORWARD_LDC_BLOCKING.queryIndex, tapChangerControl.forwardLDCBlocking)
        insert.setNullableDouble(table.TIME_DELAY.queryIndex, tapChangerControl.timeDelay)
        insert.setNullableBoolean(table.CO_GENERATION_ENABLED.queryIndex, tapChangerControl.coGenerationEnabled)

        return saveRegulatingControl(table, insert, tapChangerControl, "tap changer control")
    }

    private fun saveTransformerEnd(
        table: TableTransformerEnds,
        insert: PreparedStatement,
        transformerEnd: TransformerEnd,
        description: String
    ): Boolean {
        insert.setInt(table.END_NUMBER.queryIndex, transformerEnd.endNumber)
        insert.setNullableString(table.TERMINAL_MRID.queryIndex, transformerEnd.terminal?.mRID)
        insert.setNullableString(table.BASE_VOLTAGE_MRID.queryIndex, transformerEnd.baseVoltage?.mRID)
        insert.setBoolean(table.GROUNDED.queryIndex, transformerEnd.grounded)
        insert.setNullableDouble(table.R_GROUND.queryIndex, transformerEnd.rGround)
        insert.setNullableDouble(table.X_GROUND.queryIndex, transformerEnd.xGround)
        insert.setNullableString(table.STAR_IMPEDANCE_MRID.queryIndex, transformerEnd.starImpedance?.mRID)

        return saveIdentifiedObject(table, insert, transformerEnd, description)
    }

    fun save(transformerStarImpedance: TransformerStarImpedance): Boolean {
        val table = databaseTables.getTable<TableTransformerStarImpedance>()
        val insert = databaseTables.getInsert<TableTransformerStarImpedance>()

        insert.setNullableDouble(table.R.queryIndex, transformerStarImpedance.r)
        insert.setNullableDouble(table.R0.queryIndex, transformerStarImpedance.r0)
        insert.setNullableDouble(table.X.queryIndex, transformerStarImpedance.x)
        insert.setNullableDouble(table.X0.queryIndex, transformerStarImpedance.x0)
        insert.setNullableString(table.TRANSFORMER_END_INFO_MRID.queryIndex, transformerStarImpedance.transformerEndInfo?.mRID)

        return saveIdentifiedObject(table, insert, transformerStarImpedance, "transformer star impedance")
    }

    /************ IEC61970 InfIEC61970 Feeder ************/

    fun save(circuit: Circuit): Boolean {
        val table = databaseTables.getTable<TableCircuits>()
        val insert = databaseTables.getInsert<TableCircuits>()

        insert.setNullableString(table.LOOP_MRID.queryIndex, circuit.loop?.mRID)

        var status = true
        circuit.endSubstations.forEach { status = status and saveAssociation(circuit, it) }
        circuit.endTerminals.forEach { status = status and saveAssociation(circuit, it) }

        return status and saveLine(table, insert, circuit, "circuit")
    }

    fun save(loop: Loop): Boolean {
        val table = databaseTables.getTable<TableLoops>()
        val insert = databaseTables.getInsert<TableLoops>()

        var status = true
        loop.energizingSubstations.forEach { status = status and saveAssociation(loop, it, LoopSubstationRelationship.SUBSTATION_ENERGIZES_LOOP) }
        loop.substations.forEach { status = status and saveAssociation(loop, it, LoopSubstationRelationship.LOOP_ENERGIZES_SUBSTATION) }

        return status and saveIdentifiedObject(table, insert, loop, "loop")
    }

    fun save(lvFeeder: LvFeeder): Boolean {
        val table = databaseTables.getTable<TableLvFeeders>()
        val insert = databaseTables.getInsert<TableLvFeeders>()

        insert.setNullableString(table.NORMAL_HEAD_TERMINAL_MRID.queryIndex, lvFeeder.normalHeadTerminal?.mRID)

        return saveEquipmentContainer(table, insert, lvFeeder, "lv feeder")
    }

    /************ IEC61970 MEAS ************/
    private fun saveMeasurement(
        table: TableMeasurements,
        insert: PreparedStatement,
        measurement: Measurement,
        description: String
    ): Boolean {
        insert.setNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex, measurement.powerSystemResourceMRID)
        insert.setNullableString(table.REMOTE_SOURCE_MRID.queryIndex, measurement.remoteSource?.mRID)
        insert.setNullableString(table.TERMINAL_MRID.queryIndex, measurement.terminalMRID)
        insert.setString(table.PHASES.queryIndex, measurement.phases.name)
        insert.setString(table.UNIT_SYMBOL.queryIndex, measurement.unitSymbol.name)
        return saveIdentifiedObject(table, insert, measurement, description)
    }

    fun save(analog: Analog): Boolean {
        val table = databaseTables.getTable<TableAnalogs>()
        val insert = databaseTables.getInsert<TableAnalogs>()

        insert.setBoolean(table.POSITIVE_FLOW_IN.queryIndex, analog.positiveFlowIn)

        return saveMeasurement(table, insert, analog, "analog")
    }

    fun save(accumulator: Accumulator): Boolean {
        val table = databaseTables.getTable<TableAccumulators>()
        val insert = databaseTables.getInsert<TableAccumulators>()

        return saveMeasurement(table, insert, accumulator, "accumulator")
    }

    fun save(discrete: Discrete): Boolean {
        val table = databaseTables.getTable<TableDiscretes>()
        val insert = databaseTables.getInsert<TableDiscretes>()

        return saveMeasurement(table, insert, discrete, "discrete")
    }

    fun save(control: Control): Boolean {
        val table = databaseTables.getTable<TableControls>()
        val insert = databaseTables.getInsert<TableControls>()

        insert.setNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex, control.powerSystemResourceMRID)

        return saveIoPoint(table, insert, control, "control")
    }

    private fun saveIoPoint(table: TableIoPoints, insert: PreparedStatement, ioPoint: IoPoint, description: String): Boolean {
        return saveIdentifiedObject(table, insert, ioPoint, description)
    }

    /************ IEC61970 Base Protection ************/

    fun save(currentRelay: CurrentRelay): Boolean {
        val table = databaseTables.getTable<TableCurrentRelays>()
        val insert = databaseTables.getInsert<TableCurrentRelays>()

        insert.setNullableDouble(table.CURRENT_LIMIT_1.queryIndex, currentRelay.currentLimit1)
        insert.setNullableBoolean(table.INVERSE_TIME_FLAG.queryIndex, currentRelay.inverseTimeFlag)
        insert.setNullableDouble(table.TIME_DELAY_1.queryIndex, currentRelay.timeDelay1)

        return saveProtectionRelayFunction(table, insert, currentRelay, "current relay")
    }

    fun save(distanceRelay: DistanceRelay): Boolean {
        val table = databaseTables.getTable<TableDistanceRelays>()
        val insert = databaseTables.getInsert<TableDistanceRelays>()

        insert.setNullableDouble(table.BACKWARD_BLIND.queryIndex, distanceRelay.backwardBlind)
        insert.setNullableDouble(table.BACKWARD_REACH.queryIndex, distanceRelay.backwardReach)
        insert.setNullableDouble(table.BACKWARD_REACTANCE.queryIndex, distanceRelay.backwardReactance)
        insert.setNullableDouble(table.FORWARD_BLIND.queryIndex, distanceRelay.forwardBlind)
        insert.setNullableDouble(table.FORWARD_REACH.queryIndex, distanceRelay.forwardReach)
        insert.setNullableDouble(table.FORWARD_REACTANCE.queryIndex, distanceRelay.forwardReactance)
        insert.setNullableDouble(table.OPERATION_PHASE_ANGLE1.queryIndex, distanceRelay.operationPhaseAngle1)
        insert.setNullableDouble(table.OPERATION_PHASE_ANGLE2.queryIndex, distanceRelay.operationPhaseAngle2)
        insert.setNullableDouble(table.OPERATION_PHASE_ANGLE3.queryIndex, distanceRelay.operationPhaseAngle3)

        return saveProtectionRelayFunction(table, insert, distanceRelay, "distance relay")
    }

    private fun saveProtectionRelayFunction(
        table: TableProtectionRelayFunctions,
        insert: PreparedStatement,
        protectionRelayFunction: ProtectionRelayFunction,
        description: String
    ): Boolean {
        insert.setNullableString(table.MODEL.queryIndex, protectionRelayFunction.model)
        insert.setNullableBoolean(table.RECLOSING.queryIndex, protectionRelayFunction.reclosing)
        insert.setNullableDouble(table.RELAY_DELAY_TIME.queryIndex, protectionRelayFunction.relayDelayTime)
        insert.setString(table.PROTECTION_KIND.queryIndex, protectionRelayFunction.protectionKind.name)
        insert.setNullableBoolean(table.DIRECTABLE.queryIndex, protectionRelayFunction.directable)
        insert.setString(table.POWER_DIRECTION.queryIndex, protectionRelayFunction.powerDirection.name)
        insert.setNullableString(table.RELAY_INFO_MRID.queryIndex, protectionRelayFunction.assetInfo?.mRID)

        var status = true
        protectionRelayFunction.protectedSwitches.forEach { status = status and saveAssociation(protectionRelayFunction, it) }
        protectionRelayFunction.sensors.forEach { status = status and saveAssociation(protectionRelayFunction, it) }
        protectionRelayFunction.thresholds.forEachIndexed { sequenceNumber, threshold ->
            status = status and saveProtectionRelayFunctionThreshold(protectionRelayFunction, sequenceNumber, threshold)
        }
        protectionRelayFunction.timeLimits.forEachIndexed { sequenceNumber, timeLimit ->
            status = status and saveProtectionRelayFunctionTimeLimit(protectionRelayFunction, sequenceNumber, timeLimit)
        }

        return status and savePowerSystemResource(table, insert, protectionRelayFunction, description)
    }

    private fun saveProtectionRelayFunctionThreshold(protectionRelayFunction: ProtectionRelayFunction, sequenceNumber: Int, threshold: RelaySetting): Boolean {
        val table = databaseTables.getTable<TableProtectionRelayFunctionThresholds>()
        val insert = databaseTables.getInsert<TableProtectionRelayFunctionThresholds>()

        insert.setString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex, protectionRelayFunction.mRID)
        insert.setInt(table.SEQUENCE_NUMBER.queryIndex, sequenceNumber)
        insert.setString(table.UNIT_SYMBOL.queryIndex, threshold.unitSymbol.name)
        insert.setDouble(table.VALUE.queryIndex, threshold.value)
        insert.setNullableString(table.NAME.queryIndex, threshold.name)

        return tryExecuteSingleUpdate(
            insert,
            "${protectionRelayFunction.mRID}-threshold$sequenceNumber",
            "protection relay function threshold"
        )
    }

    private fun saveProtectionRelayFunctionTimeLimit(protectionRelayFunction: ProtectionRelayFunction, sequenceNumber: Int, timeLimit: Double): Boolean {
        val table = databaseTables.getTable<TableProtectionRelayFunctionTimeLimits>()
        val insert = databaseTables.getInsert<TableProtectionRelayFunctionTimeLimits>()

        insert.setString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex, protectionRelayFunction.mRID)
        insert.setInt(table.SEQUENCE_NUMBER.queryIndex, sequenceNumber)
        insert.setDouble(table.TIME_LIMIT.queryIndex, timeLimit)

        return tryExecuteSingleUpdate(
            insert,
            "${protectionRelayFunction.mRID}-timeLimit$sequenceNumber",
            "protection relay function time limit"
        )
    }

    fun save(protectionRelayScheme: ProtectionRelayScheme): Boolean {
        val table = databaseTables.getTable<TableProtectionRelaySchemes>()
        val insert = databaseTables.getInsert<TableProtectionRelaySchemes>()

        insert.setNullableString(table.SYSTEM_MRID.queryIndex, protectionRelayScheme.system?.mRID)

        var status = true
        protectionRelayScheme.functions.forEach { status = status and saveAssociation(protectionRelayScheme, it) }

        return status and saveIdentifiedObject(table, insert, protectionRelayScheme, "protection relay scheme")
    }

    fun save(protectionRelaySystem: ProtectionRelaySystem): Boolean {
        val table = databaseTables.getTable<TableProtectionRelaySystems>()
        val insert = databaseTables.getInsert<TableProtectionRelaySystems>()

        insert.setString(table.PROTECTION_KIND.queryIndex, protectionRelaySystem.protectionKind.name)

        return saveEquipment(table, insert, protectionRelaySystem, "protection relay system")
    }

    fun save(voltageRelay: VoltageRelay): Boolean {
        val table = databaseTables.getTable<TableVoltageRelays>()
        val insert = databaseTables.getInsert<TableVoltageRelays>()

        return saveProtectionRelayFunction(table, insert, voltageRelay, "voltage relay")
    }

    /************ IEC61970 SCADA ************/
    fun save(remoteControl: RemoteControl): Boolean {
        val table = databaseTables.getTable<TableRemoteControls>()
        val insert = databaseTables.getInsert<TableRemoteControls>()

        insert.setNullableString(table.CONTROL_MRID.queryIndex, remoteControl.control?.mRID)

        return saveRemotePoint(table, insert, remoteControl, "remote control")
    }

    private fun saveRemotePoint(table: TableRemotePoints, insert: PreparedStatement, remotePoint: RemotePoint, description: String): Boolean {
        return saveIdentifiedObject(table, insert, remotePoint, description)
    }

    fun save(remoteSource: RemoteSource): Boolean {
        val table = databaseTables.getTable<TableRemoteSources>()
        val insert = databaseTables.getInsert<TableRemoteSources>()

        insert.setNullableString(table.MEASUREMENT_MRID.queryIndex, remoteSource.measurement?.mRID)

        return saveRemotePoint(table, insert, remoteSource, "remote source")
    }

    /************ ASSOCIATIONS ************/
    private fun saveAssociation(assetOrganisationRole: AssetOrganisationRole, asset: Asset): Boolean {
        val table = databaseTables.getTable<TableAssetOrganisationRolesAssets>()
        val insert = databaseTables.getInsert<TableAssetOrganisationRolesAssets>()

        insert.setString(table.ASSET_ORGANISATION_ROLE_MRID.queryIndex, assetOrganisationRole.mRID)
        insert.setString(table.ASSET_MRID.queryIndex, asset.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${assetOrganisationRole.mRID}-to-${asset.mRID}",
            "asset organisation role to asset association"
        )
    }

    private fun saveAssociation(usagePoint: UsagePoint, endDevice: EndDevice): Boolean {
        val table = databaseTables.getTable<TableUsagePointsEndDevices>()
        val insert = databaseTables.getInsert<TableUsagePointsEndDevices>()

        insert.setString(table.USAGE_POINT_MRID.queryIndex, usagePoint.mRID)
        insert.setString(table.END_DEVICE_MRID.queryIndex, endDevice.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${usagePoint.mRID}-to-${endDevice.mRID}",
            "usage point to end device association"
        )
    }

    private fun saveAssociation(equipment: Equipment, usagePoint: UsagePoint): Boolean {
        val table = databaseTables.getTable<TableEquipmentUsagePoints>()
        val insert = databaseTables.getInsert<TableEquipmentUsagePoints>()

        insert.setString(table.EQUIPMENT_MRID.queryIndex, equipment.mRID)
        insert.setString(table.USAGE_POINT_MRID.queryIndex, usagePoint.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${equipment.mRID}-to-${usagePoint.mRID}",
            "equipment to usage point association"
        )
    }

    private fun saveAssociation(equipment: Equipment, operationalRestriction: OperationalRestriction): Boolean {
        val table = databaseTables.getTable<TableEquipmentOperationalRestrictions>()
        val insert = databaseTables.getInsert<TableEquipmentOperationalRestrictions>()

        insert.setString(table.EQUIPMENT_MRID.queryIndex, equipment.mRID)
        insert.setString(table.OPERATIONAL_RESTRICTION_MRID.queryIndex, operationalRestriction.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${equipment.mRID}-to-${operationalRestriction.mRID}",
            "equipment to operational restriction association"
        )
    }

    private fun saveAssociation(equipment: Equipment, equipmentContainer: EquipmentContainer): Boolean {
        val table = databaseTables.getTable<TableEquipmentEquipmentContainers>()
        val insert = databaseTables.getInsert<TableEquipmentEquipmentContainers>()

        insert.setString(table.EQUIPMENT_MRID.queryIndex, equipment.mRID)
        insert.setString(table.EQUIPMENT_CONTAINER_MRID.queryIndex, equipmentContainer.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${equipment.mRID}-to-${equipmentContainer.mRID}",
            "equipment to equipment container association"
        )
    }

    private fun saveAssociation(circuit: Circuit, substation: Substation): Boolean {
        val table = databaseTables.getTable<TableCircuitsSubstations>()
        val insert = databaseTables.getInsert<TableCircuitsSubstations>()

        insert.setString(table.CIRCUIT_MRID.queryIndex, circuit.mRID)
        insert.setString(table.SUBSTATION_MRID.queryIndex, substation.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${circuit.mRID}-to-${substation.mRID}",
            "circuit to substation association"
        )
    }

    private fun saveAssociation(circuit: Circuit, terminal: Terminal): Boolean {
        val table = databaseTables.getTable<TableCircuitsTerminals>()
        val insert = databaseTables.getInsert<TableCircuitsTerminals>()

        insert.setString(table.CIRCUIT_MRID.queryIndex, circuit.mRID)
        insert.setString(table.TERMINAL_MRID.queryIndex, terminal.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${circuit.mRID}-to-${terminal.mRID}",
            "circuit to terminal association"
        )
    }

    private fun saveAssociation(loop: Loop, substation: Substation, relationship: LoopSubstationRelationship): Boolean {
        val table = databaseTables.getTable<TableLoopsSubstations>()
        val insert = databaseTables.getInsert<TableLoopsSubstations>()

        insert.setString(table.LOOP_MRID.queryIndex, loop.mRID)
        insert.setString(table.SUBSTATION_MRID.queryIndex, substation.mRID)
        insert.setString(table.RELATIONSHIP.queryIndex, relationship.name)

        return tryExecuteSingleUpdate(
            insert,
            "${loop.mRID}-to-${substation.mRID}",
            "loop to substation association"
        )
    }

    private fun saveAssociation(protectionRelayFunction: ProtectionRelayFunction, protectedSwitch: ProtectedSwitch): Boolean {
        val table = databaseTables.getTable<TableProtectionRelayFunctionsProtectedSwitches>()
        val insert = databaseTables.getInsert<TableProtectionRelayFunctionsProtectedSwitches>()

        insert.setString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex, protectionRelayFunction.mRID)
        insert.setString(table.PROTECTED_SWITCH_MRID.queryIndex, protectedSwitch.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${protectionRelayFunction.mRID}-to-${protectedSwitch.mRID}",
            "protection relay function to protected switch association"
        )
    }

    private fun saveAssociation(protectionRelayFunction: ProtectionRelayFunction, sensor: Sensor): Boolean {
        val table = databaseTables.getTable<TableProtectionRelayFunctionsSensors>()
        val insert = databaseTables.getInsert<TableProtectionRelayFunctionsSensors>()

        insert.setString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex, protectionRelayFunction.mRID)
        insert.setString(table.SENSOR_MRID.queryIndex, sensor.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${protectionRelayFunction.mRID}-to-${sensor.mRID}",
            "protection relay function to sensor association"
        )
    }

    private fun saveAssociation(protectionRelayScheme: ProtectionRelayScheme, protectionRelayFunction: ProtectionRelayFunction): Boolean {
        val table = databaseTables.getTable<TableProtectionRelaySchemesProtectionRelayFunctions>()
        val insert = databaseTables.getInsert<TableProtectionRelaySchemesProtectionRelayFunctions>()

        insert.setString(table.PROTECTION_RELAY_SCHEME_MRID.queryIndex, protectionRelayScheme.mRID)
        insert.setString(table.PROTECTION_RELAY_FUNCTION_MRID.queryIndex, protectionRelayFunction.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${protectionRelayScheme.mRID}-to-${protectionRelayFunction.mRID}",
            "protection relay function to protection relay function association"
        )
    }

}
