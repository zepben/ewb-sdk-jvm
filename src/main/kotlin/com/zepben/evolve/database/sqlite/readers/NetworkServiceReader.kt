/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.readers

import com.zepben.evolve.database.sqlite.tables.associations.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetOwners
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TablePoles
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableStreetlights
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TablePositionPoints
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableMeters
import com.zepben.evolve.database.sqlite.tables.iec61968.metering.TableUsagePoints
import com.zepben.evolve.database.sqlite.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.equivalents.TableEquivalentBranches
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableAccumulators
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableAnalogs
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.meas.TableDiscretes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TableBatteryUnit
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
class NetworkServiceReader constructor(getStatement: () -> Statement) : BaseServiceReader(getStatement) {

    fun load(reader: NetworkCIMReader): Boolean {
        var status = loadNameTypes(reader)

        status = status and loadEach<TableCableInfo>("cable info", reader::load)
        status = status and loadEach<TableOverheadWireInfo>("overhead wire info", reader::load)
        status = status and loadEach<TablePowerTransformerInfo>("power transformer info", reader::load)
        status = status and loadEach<TableTransformerTankInfo>("transformer tank info", reader::load)
        status = status and loadEach<TableNoLoadTests>("no load tests", reader::load)
        status = status and loadEach<TableOpenCircuitTests>("open circuit tests", reader::load)
        status = status and loadEach<TableShortCircuitTests>("short circuit tests", reader::load)
        status = status and loadEach<TableShuntCompensatorInfo>("shunt compensator info", reader::load)
        status = status and loadEach<TableTransformerEndInfo>("transformer end info", reader::load)
        status = status and loadEach<TableLocations>("locations", reader::load)
        status = status and loadEach<TableOrganisations>("organisations", reader::load)
        status = status and loadEach<TableAssetOwners>("asset owners", reader::load)
        status = status and loadEach<TablePoles>("poles", reader::load)
        status = status and loadEach<TableStreetlights>("streetlights", reader::load)
        status = status and loadEach<TableMeters>("meters", reader::load)
        status = status and loadEach<TableUsagePoints>("usage points", reader::load)
        status = status and loadEach<TableOperationalRestrictions>("operational restrictions", reader::load)
        status = status and loadEach<TableBaseVoltages>("base voltages", reader::load)
        status = status and loadEach<TableConnectivityNodes>("connectivity nodes", reader::load)
        status = status and loadEach<TableGeographicalRegions>("geographical regions", reader::load)
        status = status and loadEach<TableSubGeographicalRegions>("sub-geographical regions", reader::load)
        status = status and loadEach<TableSubstations>("substations", reader::load)
        status = status and loadEach<TableSites>("sites", reader::load)
        status = status and loadEach<TablePerLengthSequenceImpedances>("per length sequence impedances", reader::load)
        status = status and loadEach<TableEquivalentBranches>("equivalent branches", reader::load)
        status = status and loadEach<TablePowerElectronicsConnection>("power electronics connection", reader::load)
        status = status and loadEach<TablePowerElectronicsConnectionPhases>("power electronics connection phases", reader::load)
        status = status and loadEach<TableBatteryUnit>("battery unit", reader::load)
        status = status and loadEach<TablePhotoVoltaicUnit>("photo voltaic unit", reader::load)
        status = status and loadEach<TablePowerElectronicsWindUnit>("power electronics wind unit", reader::load)
        status = status and loadEach<TableAcLineSegments>("AC line segments", reader::load)
        status = status and loadEach<TableBreakers>("breakers", reader::load)
        status = status and loadEach<TableLoadBreakSwitches>("load break switches", reader::load)
        status = status and loadEach<TableBusbarSections>("busbar sections", reader::load)
        status = status and loadEach<TableDisconnectors>("disconnectors", reader::load)
        status = status and loadEach<TableEnergyConsumers>("energy consumers", reader::load)
        status = status and loadEach<TableEnergyConsumerPhases>("energy consumer phases", reader::load)
        status = status and loadEach<TableEnergySources>("energy sources", reader::load)
        status = status and loadEach<TableEnergySourcePhases>("energy source phases", reader::load)
        status = status and loadEach<TableFuses>("fuses", reader::load)
        status = status and loadEach<TableJumpers>("jumpers", reader::load)
        status = status and loadEach<TableJunctions>("junctions", reader::load)
        status = status and loadEach<TableLinearShuntCompensators>("linear shunt compensators", reader::load)
        status = status and loadEach<TablePowerTransformers>("power transformers", reader::load)
        status = status and loadEach<TableReclosers>("reclosers", reader::load)
        status = status and loadEach<TableTerminals>("terminals", reader::load)
        status = status and loadEach<TableTransformerStarImpedance>("transformer star impedance", reader::load)
        status = status and loadEach<TablePowerTransformerEnds>("power transformer ends", reader::load)
        status = status and loadEach<TableRatioTapChangers>("ratio tap changers", reader::load)
        status = status and loadEach<TableFaultIndicators>("fault indicators", reader::load)
        status = status and loadEach<TableFeeders>("feeders", reader::load)
        status = status and loadEach<TableLoops>("loops", reader::load)
        status = status and loadEach<TableLvFeeders>("lv feeders", reader::load)
        status = status and loadEach<TableCircuits>("circuits", reader::load)
        status = status and loadEach<TablePositionPoints>("position points", reader::load)
        status = status and loadEach<TableLocationStreetAddresses>("location street addresses", reader::load)
        status = status and loadEach<TableAssetOrganisationRolesAssets>("asset organisation role to asset associations", reader::load)
        status = status and loadEach<TableUsagePointsEndDevices>("usage point to end device associations", reader::load)
        status = status and loadEach<TableEquipmentUsagePoints>("equipment to usage point associations", reader::load)
        status = status and loadEach<TableEquipmentOperationalRestrictions>("equipment to operational restriction associations", reader::load)
        status = status and loadEach<TableEquipmentEquipmentContainers>("equipment to equipment container associations", reader::load)
        status = status and loadEach<TableFeederLvFeeders>("feeder to lv feeder associations", reader::load)
        status = status and loadEach<TableCircuitsSubstations>("circuit to substation associations", reader::load)
        status = status and loadEach<TableCircuitsTerminals>("circuit to terminal associations", reader::load)
        status = status and loadEach<TableLoopsSubstations>("loop to substation associations", reader::load)
        status = status and loadEach<TableControls>("controls", reader::load)
        status = status and loadEach<TableRemoteControls>("remote controls", reader::load)
        status = status and loadEach<TableRemoteSources>("remote sources", reader::load)
        status = status and loadEach<TableAnalogs>("analogs", reader::load)
        status = status and loadEach<TableAccumulators>("accumulators", reader::load)
        status = status and loadEach<TableDiscretes>("discretes", reader::load)

        status = status and loadNames(reader)

        return status
    }

}
