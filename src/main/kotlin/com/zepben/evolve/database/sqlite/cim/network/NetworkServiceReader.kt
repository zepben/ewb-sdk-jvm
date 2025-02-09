/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.network

import com.zepben.evolve.database.sqlite.cim.BaseServiceReader
import com.zepben.evolve.database.sqlite.cim.CimReader
import com.zepben.evolve.database.sqlite.cim.tables.associations.*
import com.zepben.evolve.database.sqlite.cim.tables.extensions.iec61968.metering.TablePanDemandResponseFunctions
import com.zepben.evolve.database.sqlite.cim.tables.extensions.iec61970.base.wires.TableBatteryControls
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.assets.TableAssetOwners
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.assets.TablePoles
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.assets.TableStreetlights
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableLocations
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TablePositionPoints
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableRecloseDelays
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableRelayInfo
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.metering.TableMeters
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.metering.TableUsagePoints
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TableCurrentTransformers
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment.TablePotentialTransformers
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.*
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.meas.TableAccumulators
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.meas.TableAnalogs
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.meas.TableControls
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.meas.TableDiscretes
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.protection.*
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.*
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.generation.production.TableBatteryUnits
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.generation.production.TablePhotoVoltaicUnits
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.generation.production.TablePowerElectronicsWindUnits
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.infiec61970.feeder.TableLvFeeders
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.infiec61970.wires.generation.production.TableEvChargingUnits
import com.zepben.evolve.services.network.NetworkService
import java.sql.Connection

/**
 * A class for reading a [NetworkService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 *
 * @property reader The [CimReader] used to read the objects from the database.
 */
internal class NetworkServiceReader(
    databaseTables: NetworkDatabaseTables,
    connection: Connection,
    override val reader: NetworkCimReader = NetworkCimReader()
) : BaseServiceReader<NetworkService>(databaseTables, connection, reader) {

    override fun readService(service: NetworkService): Boolean =
        readEach<TableCableInfo>(service, reader::read) and
            readEach<TableOverheadWireInfo>(service, reader::read) and
            readEach<TablePowerTransformerInfo>(service, reader::read) and
            readEach<TableTransformerTankInfo>(service, reader::read) and
            readEach<TableNoLoadTests>(service, reader::read) and
            readEach<TableOpenCircuitTests>(service, reader::read) and
            readEach<TableShortCircuitTests>(service, reader::read) and
            readEach<TableShuntCompensatorInfo>(service, reader::read) and
            readEach<TableSwitchInfo>(service, reader::read) and
            readEach<TableTransformerEndInfo>(service, reader::read) and
            readEach<TableCurrentTransformerInfo>(service, reader::read) and
            readEach<TablePotentialTransformerInfo>(service, reader::read) and
            readEach<TableRelayInfo>(service, reader::read) and
            readEach<TableRecloseDelays>(service, reader::read) and
            readEach<TableLocations>(service, reader::read) and
            readEach<TableOrganisations>(service, reader::read) and
            readEach<TableAssetOwners>(service, reader::read) and
            readEach<TablePoles>(service, reader::read) and
            readEach<TableStreetlights>(service, reader::read) and
            readEach<TablePanDemandResponseFunctions>(service, reader::read) and
            readEach<TableMeters>(service, reader::read) and
            readEach<TableEndDevicesEndDeviceFunctions>(service, reader::read) and
            readEach<TableUsagePoints>(service, reader::read) and
            readEach<TableOperationalRestrictions>(service, reader::read) and
            readEach<TableBaseVoltages>(service, reader::read) and
            readEach<TableConnectivityNodes>(service, reader::read) and
            readEach<TableGeographicalRegions>(service, reader::read) and
            readEach<TableSubGeographicalRegions>(service, reader::read) and
            readEach<TableSubstations>(service, reader::read) and
            readEach<TableSites>(service, reader::read) and
            readEach<TablePerLengthPhaseImpedances>(service, reader::read) and
            readEach<TablePhaseImpedanceData>(service, reader::read) and
            readEach<TablePerLengthSequenceImpedances>(service, reader::read) and
            readEach<TableEquivalentBranches>(service, reader::read) and
            readEach<TableAcLineSegments>(service, reader::read) and
            readEach<TableBreakers>(service, reader::read) and
            readEach<TableLoadBreakSwitches>(service, reader::read) and
            readEach<TableBusbarSections>(service, reader::read) and
            readEach<TableClamps>(service, reader::read) and
            readEach<TableCuts>(service, reader::read) and
            readEach<TableCurrentRelays>(service, reader::read) and
            readEach<TableDistanceRelays>(service, reader::read) and
            readEach<TableVoltageRelays>(service, reader::read) and
            readEach<TableProtectionRelayFunctionThresholds>(service, reader::read) and
            readEach<TableProtectionRelayFunctionTimeLimits>(service, reader::read) and
            readEach<TableProtectionRelaySystems>(service, reader::read) and
            readEach<TableProtectionRelaySchemes>(service, reader::read) and
            readEach<TableDisconnectors>(service, reader::read) and
            readEach<TableEnergyConsumers>(service, reader::read) and
            readEach<TableEnergyConsumerPhases>(service, reader::read) and
            readEach<TableEnergySources>(service, reader::read) and
            readEach<TableEnergySourcePhases>(service, reader::read) and
            readEach<TableFuses>(service, reader::read) and
            readEach<TableJumpers>(service, reader::read) and
            readEach<TableJunctions>(service, reader::read) and
            readEach<TableGrounds>(service, reader::read) and
            readEach<TableGroundDisconnectors>(service, reader::read) and
            readEach<TableSeriesCompensators>(service, reader::read) and
            readEach<TableStaticVarCompensators>(service, reader::read) and
            readEach<TableLinearShuntCompensators>(service, reader::read) and
            readEach<TablePowerTransformers>(service, reader::read) and
            readEach<TableReclosers>(service, reader::read) and
            readEach<TablePowerElectronicsConnections>(service, reader::read) and
            readEach<TableReactiveCapabilityCurves>(service, reader::read) and
            readEach<TableCurveData>(service, reader::read) and
            readEach<TablePetersenCoils>(service, reader::read) and
            readEach<TableGroundingImpedances>(service, reader::read) and
            readEach<TableSynchronousMachines>(service, reader::read) and
            readEach<TableTerminals>(service, reader::read) and
            readEach<TableTapChangerControls>(service, reader::read) and
            readEach<TablePowerElectronicsConnectionPhases>(service, reader::read) and
            readEach<TableBatteryControls>(service, reader::read) and
            readEach<TableBatteryUnits>(service, reader::read) and
            readEach<TableBatteryUnitsBatteryControls>(service, reader::read) and
            readEach<TablePhotoVoltaicUnits>(service, reader::read) and
            readEach<TablePowerElectronicsWindUnits>(service, reader::read) and
            readEach<TableEvChargingUnits>(service, reader::read) and
            readEach<TableTransformerStarImpedances>(service, reader::read) and
            readEach<TablePowerTransformerEnds>(service, reader::read) and
            readEach<TablePowerTransformerEndRatings>(service, reader::read) and
            readEach<TableRatioTapChangers>(service, reader::read) and
            readEach<TableCurrentTransformers>(service, reader::read) and
            readEach<TableFaultIndicators>(service, reader::read) and
            readEach<TablePotentialTransformers>(service, reader::read) and
            readEach<TableFeeders>(service, reader::read) and
            readEach<TableLoops>(service, reader::read) and
            readEach<TableLvFeeders>(service, reader::read) and
            readEach<TableCircuits>(service, reader::read) and
            readEach<TablePositionPoints>(service, reader::read) and
            readEach<TableLocationStreetAddresses>(service, reader::read) and
            readEach<TableAssetOrganisationRolesAssets>(service, reader::read) and
            readEach<TableUsagePointsEndDevices>(service, reader::read) and
            readEach<TableEquipmentUsagePoints>(service, reader::read) and
            readEach<TableEquipmentOperationalRestrictions>(service, reader::read) and
            readEach<TableEquipmentEquipmentContainers>(service, reader::read) and
            readEach<TableCircuitsSubstations>(service, reader::read) and
            readEach<TableCircuitsTerminals>(service, reader::read) and
            readEach<TableLoopsSubstations>(service, reader::read) and
            readEach<TableProtectionRelayFunctionsProtectedSwitches>(service, reader::read) and
            readEach<TableProtectionRelayFunctionsSensors>(service, reader::read) and
            readEach<TableProtectionRelaySchemesProtectionRelayFunctions>(service, reader::read) and
            readEach<TableSynchronousMachinesReactiveCapabilityCurves>(service, reader::read) and
            readEach<TableControls>(service, reader::read) and
            readEach<TableRemoteControls>(service, reader::read) and
            readEach<TableRemoteSources>(service, reader::read) and
            readEach<TableAnalogs>(service, reader::read) and
            readEach<TableAccumulators>(service, reader::read) and
            readEach<TableDiscretes>(service, reader::read)

}
