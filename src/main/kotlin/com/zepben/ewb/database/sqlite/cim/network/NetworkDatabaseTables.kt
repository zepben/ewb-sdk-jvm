/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.zepben.ewb.database.sqlite.cim.CimDatabaseTables
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
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetOwners
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableStreetlights
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableLocations
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableOrganisations
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TablePositionPoints
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassets.TablePoles
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableMeters
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableUsagePointContactDetails
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering.TableUsagePoints
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TableCurrentTransformers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TablePotentialTransformers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TableBatteryUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePhotoVoltaicUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePowerElectronicsWindUnits
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas.TableAccumulators
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas.TableAnalogs
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas.TableControls
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas.TableDiscretes
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.protection.TableCurrentRelays
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires.*
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * The collection of tables for our network databases.
 */
class NetworkDatabaseTables : CimDatabaseTables() {

    override val includedTables: Sequence<SqliteTable> =
        super.includedTables + sequenceOf(
            TableAcLineSegments(),
            TableAccumulators(),
            TableAnalogs(),
            TableAssetOrganisationRolesAssets(),
            TableAssetsPowerSystemResources(),
            TableAssetOwners(),
            TableBaseVoltages(),
            TableBatteryControls(),
            TableBatteryUnits(),
            TableBatteryUnitsBatteryControls(),
            TableBreakers(),
            TableBusbarSections(),
            TableCableInfo(),
            TableCircuits(),
            TableCircuitsSubstations(),
            TableCircuitsTerminals(),
            TableClamps(),
            TableConnectivityNodes(),
            TableContactDetailsElectronicAddresses(),
            TableContactDetailsStreetAddresses(),
            TableContactDetailsTelephoneNumbers(),
            TableControls(),
            TableCurrentRelays(),
            TableCurrentTransformerInfo(),
            TableCurrentTransformers(),
            TableCurveData(),
            TableCuts(),
            TableDirectionalCurrentRelays(),
            TableDisconnectors(),
            TableDiscretes(),
            TableDistanceRelays(),
            TableEndDevicesEndDeviceFunctions(),
            TableEnergyConsumerPhases(),
            TableEnergyConsumers(),
            TableEnergySourcePhases(),
            TableEnergySources(),
            TableEquipmentEquipmentContainers(),
            TableEquipmentOperationalRestrictions(),
            TableEquipmentUsagePoints(),
            TableEquivalentBranches(),
            TableEvChargingUnits(),
            TableFaultIndicators(),
            TableFeeders(),
            TableFuses(),
            TableGeographicalRegions(),
            TableGrounds(),
            TableGroundDisconnectors(),
            TableGroundingImpedances(),
            TableJumpers(),
            TableJunctions(),
            TableLinearShuntCompensators(),
            TableLoadBreakSwitches(),
            TableLocationStreetAddresses(),
            TableLocations(),
            TableLoops(),
            TableLoopsSubstations(),
            TableLvFeeders(),
            TableMeters(),
            TableNoLoadTests(),
            TableOpenCircuitTests(),
            TableOperationalRestrictions(),
            TableOrganisations(),
            TableOverheadWireInfo(),
            TablePanDemandResponseFunctions(),
            TablePerLengthPhaseImpedances(),
            TablePerLengthSequenceImpedances(),
            TablePhaseImpedanceData(),
            TablePetersenCoils(),
            TablePhotoVoltaicUnits(),
            TablePoles(),
            TablePositionPoints(),
            TablePotentialTransformerInfo(),
            TablePotentialTransformers(),
            TablePowerElectronicsConnections(),
            TablePowerElectronicsConnectionPhases(),
            TablePowerElectronicsWindUnits(),
            TablePowerTransformerEnds(),
            TablePowerTransformerEndRatings(),
            TablePowerTransformerInfo(),
            TablePowerTransformers(),
            TableProtectionRelayFunctionThresholds(),
            TableProtectionRelayFunctionTimeLimits(),
            TableProtectionRelayFunctionsProtectedSwitches(),
            TableProtectionRelayFunctionsSensors(),
            TableProtectionRelaySchemes(),
            TableProtectionRelaySchemesProtectionRelayFunctions(),
            TableProtectionRelaySystems(),
            TableRatioTapChangers(),
            TableReactiveCapabilityCurves(),
            TableReclosers(),
            TableRecloseDelays(),
            TableRelayInfo(),
            TableRemoteControls(),
            TableRemoteSources(),
            TableSeriesCompensators(),
            TableShortCircuitTests(),
            TableShuntCompensatorInfo(),
            TableSites(),
            TableStreetlights(),
            TableSubGeographicalRegions(),
            TableSubstations(),
            TableStaticVarCompensators(),
            TableSwitchInfo(),
            TableSynchronousMachines(),
            TableSynchronousMachinesReactiveCapabilityCurves(),
            TableTapChangerControls(),
            TableTerminals(),
            TableTransformerEndInfo(),
            TableTransformerStarImpedances(),
            TableTransformerTankInfo(),
            TableUsagePoints(),
            TableUsagePointContactDetails(),
            TableUsagePointsEndDevices(),
            TableVoltageRelays()
        )

}
