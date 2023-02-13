/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.evolve.database.sqlite.tables.associations.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetOwners
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TablePoles
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableStreetlights
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableLocations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TablePositionPoints
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableCurrentRelayInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TableCurrentTransformerInfo
import com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo.TablePotentialTransformerInfo
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
import com.zepben.evolve.database.sqlite.tables.iec61970.base.protection.TableCurrentRelays
import com.zepben.evolve.database.sqlite.tables.iec61970.base.protection.TableRecloseSequences
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.evolve.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TableBatteryUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TablePhotoVoltaicUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production.TablePowerElectronicsWindUnit
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLvFeeders

val networkDatabaseTables = object : DatabaseTables() {
    override val tables: Map<Class<out SqliteTable>, SqliteTable> = listOf(
        TableAcLineSegments(),
        TableAccumulators(),
        TableAnalogs(),
        TableAssetOrganisationRolesAssets(),
        TableAssetOwners(),
        TableBaseVoltages(),
        TableBatteryUnit(),
        TableBreakers(),
        TableBusbarSections(),
        TableCableInfo(),
        TableCircuits(),
        TableCircuitsSubstations(),
        TableCircuitsTerminals(),
        TableConnectivityNodes(),
        TableControls(),
        TableCurrentRelayInfo(),
        TableCurrentRelays(),
        TableCurrentTransformerInfo(),
        TableCurrentTransformers(),
        TableDisconnectors(),
        TableDiscretes(),
        TableEnergyConsumerPhases(),
        TableEnergyConsumers(),
        TableEnergySourcePhases(),
        TableEnergySources(),
        TableEquipmentEquipmentContainers(),
        TableEquipmentOperationalRestrictions(),
        TableEquipmentUsagePoints(),
        TableEquivalentBranches(),
        TableFaultIndicators(),
        TableFeeders(),
        TableFuses(),
        TableGeographicalRegions(),
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
        TableNameTypes(),
        TableNames(),
        TableNoLoadTests(),
        TableOpenCircuitTests(),
        TableOperationalRestrictions(),
        TableOrganisations(),
        TableOverheadWireInfo(),
        TablePerLengthSequenceImpedances(),
        TablePhotoVoltaicUnit(),
        TablePoles(),
        TablePositionPoints(),
        TablePotentialTransformerInfo(),
        TablePotentialTransformers(),
        TablePowerElectronicsConnection(),
        TablePowerElectronicsConnectionPhases(),
        TablePowerElectronicsWindUnit(),
        TablePowerTransformerEnds(),
        TablePowerTransformerInfo(),
        TablePowerTransformers(),
        TableProtectionEquipmentProtectedSwitches(),
        TableRatioTapChangers(),
        TableReclosers(),
        TableRecloseSequences(),
        TableRemoteControls(),
        TableRemoteSources(),
        TableShortCircuitTests(),
        TableShuntCompensatorInfo(),
        TableSites(),
        TableStreetlights(),
        TableSubGeographicalRegions(),
        TableSubstations(),
        TableSwitchInfo(),
        TableTerminals(),
        TableTransformerEndInfo(),
        TableTransformerStarImpedance(),
        TableTransformerTankInfo(),
        TableUsagePoints(),
        TableUsagePointsEndDevices(),
    ).associateBy { it::class.java }
}
