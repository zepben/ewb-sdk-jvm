/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.database.sqlite.common.BaseServiceReader
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.tables.associations.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetOwners
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TablePoles
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableStreetlights
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TablePositionPoints
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableRecloseDelays
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableRelayInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableMeters
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableUsagePoints
import com.zepben.evolve.database.sqlite.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableCurrentTransformers
import com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment.TablePotentialTransformers
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableAccumulators
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableAnalogs
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableDiscretes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.protection.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TableBatteryUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TableEvChargingUnits
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TablePhotoVoltaicUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TablePowerElectronicsWindUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLvFeeders
import com.zepben.evolve.services.network.NetworkService
import java.sql.Statement

/**
 * Class for reading a [NetworkService] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class NetworkServiceReader(
    databaseTables: DatabaseTables,
    reader: NetworkCIMReader,
    getStatement: () -> Statement,
) : BaseServiceReader<NetworkCIMReader>(databaseTables, getStatement, reader) {

    override fun load(): Boolean {
        var status = loadNameTypes(reader)

        status = status and loadEach("cable info", TableCableInfo(), reader::load)
        status = status and loadEach("overhead wire info", TableOverheadWireInfo(), reader::load)
        status = status and loadEach("power transformer info", TablePowerTransformerInfo(), reader::load)
        status = status and loadEach("transformer tank info", TableTransformerTankInfo(), reader::load)
        status = status and loadEach("no load tests", TableNoLoadTests(), reader::load)
        status = status and loadEach("open circuit tests", TableOpenCircuitTests(), reader::load)
        status = status and loadEach("short circuit tests", TableShortCircuitTests(), reader::load)
        status = status and loadEach("shunt compensator info", TableShuntCompensatorInfo(), reader::load)
        status = status and loadEach("switch info", TableSwitchInfo(), reader::load)
        status = status and loadEach("transformer end info", TableTransformerEndInfo(), reader::load)
        status = status and loadEach("current transformer info", TableCurrentTransformerInfo(), reader::load)
        status = status and loadEach("potential transformer info", TablePotentialTransformerInfo(), reader::load)
        status = status and loadEach("relay info", TableRelayInfo(), reader::load)
        status = status and loadEach("reclose delays", TableRecloseDelays(), reader::load)
        status = status and loadEach("locations", TableLocations(), reader::load)
        status = status and loadEach("organisations", TableOrganisations(), reader::load)
        status = status and loadEach("asset owners", TableAssetOwners(), reader::load)
        status = status and loadEach("poles", TablePoles(), reader::load)
        status = status and loadEach("streetlights", TableStreetlights(), reader::load)
        status = status and loadEach("meters", TableMeters(), reader::load)
        status = status and loadEach("usage points", TableUsagePoints(), reader::load)
        status = status and loadEach("operational restrictions", TableOperationalRestrictions(), reader::load)
        status = status and loadEach("base voltages", TableBaseVoltages(), reader::load)
        status = status and loadEach("connectivity nodes", TableConnectivityNodes(), reader::load)
        status = status and loadEach("geographical regions", TableGeographicalRegions(), reader::load)
        status = status and loadEach("sub-geographical regions", TableSubGeographicalRegions(), reader::load)
        status = status and loadEach("substations", TableSubstations(), reader::load)
        status = status and loadEach("sites", TableSites(), reader::load)
        status = status and loadEach("per length sequence impedances", TablePerLengthSequenceImpedances(), reader::load)
        status = status and loadEach("equivalent branches", TableEquivalentBranches(), reader::load)
        status = status and loadEach("AC line segments", TableAcLineSegments(), reader::load)
        status = status and loadEach("breakers", TableBreakers(), reader::load)
        status = status and loadEach("load break switches", TableLoadBreakSwitches(), reader::load)
        status = status and loadEach("busbar sections", TableBusbarSections(), reader::load)
        status = status and loadEach("current relays", TableCurrentRelays(), reader::load)
        status = status and loadEach("distance relays", TableDistanceRelays(), reader::load)
        status = status and loadEach("voltage relays", TableVoltageRelays(), reader::load)
        status = status and loadEach("protection relay function thresholds", TableProtectionRelayFunctionThresholds(), reader::load)
        status = status and loadEach("protection relay function time limits", TableProtectionRelayFunctionTimeLimits(), reader::load)
        status = status and loadEach("protection relay system", TableProtectionRelaySystems(), reader::load)
        status = status and loadEach("protection relay schemes", TableProtectionRelaySchemes(), reader::load)
        status = status and loadEach("disconnectors", TableDisconnectors(), reader::load)
        status = status and loadEach("energy consumers", TableEnergyConsumers(), reader::load)
        status = status and loadEach("energy consumer phases", TableEnergyConsumerPhases(), reader::load)
        status = status and loadEach("energy sources", TableEnergySources(), reader::load)
        status = status and loadEach("energy source phases", TableEnergySourcePhases(), reader::load)
        status = status and loadEach("fuses", TableFuses(), reader::load)
        status = status and loadEach("jumpers", TableJumpers(), reader::load)
        status = status and loadEach("junctions", TableJunctions(), reader::load)
        status = status and loadEach("grounds", TableGrounds(), reader::load)
        status = status and loadEach("ground disconnectors", TableGroundDisconnectors(), reader::load)
        status = status and loadEach("series compensators", TableSeriesCompensators(), reader::load)
        status = status and loadEach("linear shunt compensators", TableLinearShuntCompensators(), reader::load)
        status = status and loadEach("power transformers", TablePowerTransformers(), reader::load)
        status = status and loadEach("reclosers", TableReclosers(), reader::load)
        status = status and loadEach("power electronics connection", TablePowerElectronicsConnection(), reader::load)
        status = status and loadEach("terminals", TableTerminals(), reader::load)
        status = status and loadEach("tap changer controls", TableTapChangerControls(), reader::load)
        status = status and loadEach("power electronics connection phases", TablePowerElectronicsConnectionPhases(), reader::load)
        status = status and loadEach("battery unit", TableBatteryUnit(), reader::load)
        status = status and loadEach("photo voltaic unit", TablePhotoVoltaicUnit(), reader::load)
        status = status and loadEach("power electronics wind unit", TablePowerElectronicsWindUnit(), reader::load)
        status = status and loadEach("ev charging units", TableEvChargingUnits(), reader::load)
        status = status and loadEach("transformer star impedance", TableTransformerStarImpedance(), reader::load)
        status = status and loadEach("power transformer ends", TablePowerTransformerEnds(), reader::load)
        status = status and loadEach("power transformer end ratings", TablePowerTransformerEndRatings(), reader::load)
        status = status and loadEach("ratio tap changers", TableRatioTapChangers(), reader::load)
        status = status and loadEach("current transformers", TableCurrentTransformers(), reader::load)
        status = status and loadEach("fault indicators", TableFaultIndicators(), reader::load)
        status = status and loadEach("potential transformers", TablePotentialTransformers(), reader::load)
        status = status and loadEach("feeders", TableFeeders(), reader::load)
        status = status and loadEach("loops", TableLoops(), reader::load)
        status = status and loadEach("lv feeders", TableLvFeeders(), reader::load)
        status = status and loadEach("circuits", TableCircuits(), reader::load)
        status = status and loadEach("position points", TablePositionPoints(), reader::load)
        status = status and loadEach("location street addresses", TableLocationStreetAddresses(), reader::load)
        status = status and loadEach("asset organisation role to asset associations", TableAssetOrganisationRolesAssets(), reader::load)
        status = status and loadEach("usage point to end device associations", TableUsagePointsEndDevices(), reader::load)
        status = status and loadEach("equipment to usage point associations", TableEquipmentUsagePoints(), reader::load)
        status = status and loadEach("equipment to operational restriction associations", TableEquipmentOperationalRestrictions(), reader::load)
        status = status and loadEach("equipment to equipment container associations", TableEquipmentEquipmentContainers(), reader::load)
        status = status and loadEach("circuit to substation associations", TableCircuitsSubstations(), reader::load)
        status = status and loadEach("circuit to terminal associations", TableCircuitsTerminals(), reader::load)
        status = status and loadEach("loop to substation associations", TableLoopsSubstations(), reader::load)
        status = status and loadEach("protection relay function to protected switch associations", TableProtectionRelayFunctionsProtectedSwitches(), reader::load)
        status = status and loadEach("protection relay function to sensor associations", TableProtectionRelayFunctionsSensors(), reader::load)
        status = status and loadEach("protection relay scheme to protection relay function associations", TableProtectionRelaySchemesProtectionRelayFunctions(), reader::load)
        status = status and loadEach("controls", TableControls(), reader::load)
        status = status and loadEach("remote controls", TableRemoteControls(), reader::load)
        status = status and loadEach("remote sources", TableRemoteSources(), reader::load)
        status = status and loadEach("analogs", TableAnalogs(), reader::load)
        status = status and loadEach("accumulators", TableAccumulators(), reader::load)
        status = status and loadEach("discretes", TableDiscretes(), reader::load)

        status = status and loadNames(reader)

        return status
    }

}
