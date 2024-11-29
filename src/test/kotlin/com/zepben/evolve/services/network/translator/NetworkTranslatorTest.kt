/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.translator

import com.zepben.evolve.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.evolve.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOrganisationRole
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunction
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.database.sqlite.cim.network.NetworkDatabaseTables
import com.zepben.evolve.database.sqlite.cim.tables.associations.*
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableLocationStreetAddresses
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TablePositionPoints
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo.TableRecloseDelays
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableCurveData
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.protection.TableProtectionRelayFunctionThresholds
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.protection.TableProtectionRelayFunctionTimeLimits
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.TablePowerTransformerEndRatings
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.common.translator.TranslatorTestBase
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkServiceComparator
import com.zepben.evolve.services.network.testdata.fillFields

internal class NetworkTranslatorTest : TranslatorTestBase<NetworkService>(
    ::NetworkService,
    NetworkServiceComparator(),
    NetworkDatabaseTables(),
    NetworkService::addFromPb
) {

    private val nsToPb = NetworkCimToProto()

    override val validationInfo = listOf(
        /************ EXTENSIONS IEC61968 METERING ************/
        ValidationInfo(PanDemandResponseFunction(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ EXTENSIONS IEC61970 BASE WIRES ************/
        ValidationInfo(BatteryControl(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 ASSET INFO ************/
        ValidationInfo(CableInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(NoLoadTest(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(OpenCircuitTest(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(OverheadWireInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerTransformerInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ShortCircuitTest(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ShuntCompensatorInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(SwitchInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(TransformerEndInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(TransformerTankInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 ASSETS ************/
        ValidationInfo(AssetOwner(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Pole(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Streetlight(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 COMMON ************/
        ValidationInfo(Location(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Organisation(), { fillFieldsCommon(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 infIEC61968 InfAssetInfo ************/
        ValidationInfo(RelayInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(CurrentTransformerInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PotentialTransformerInfo(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 METERING ************/
        ValidationInfo(Meter(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(UsagePoint(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61968 OPERATIONS ************/
        ValidationInfo(OperationalRestriction(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/
        ValidationInfo(CurrentTransformer(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(FaultIndicator(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PotentialTransformer(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE CORE ************/
        ValidationInfo(BaseVoltage(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ConnectivityNode(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Feeder(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(GeographicalRegion(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Site(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(SubGeographicalRegion(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Substation(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Terminal(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE EQUIVALENTS ************/
        ValidationInfo(EquivalentBranch(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE MEAS ************/
        ValidationInfo(Accumulator(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Analog(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Control(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Discrete(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 Base Protection ************/
        ValidationInfo(CurrentRelay(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(DistanceRelay(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ProtectionRelayScheme(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ProtectionRelaySystem(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(VoltageRelay(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE SCADA ************/
        ValidationInfo(RemoteControl(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(RemoteSource(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/
        ValidationInfo(BatteryUnit(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PhotoVoltaicUnit(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerElectronicsConnection(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerElectronicsConnectionPhase(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerElectronicsWindUnit(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 BASE WIRES ************/
        ValidationInfo(AcLineSegment(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Breaker(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(BusbarSection(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Disconnector(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(EnergyConsumer(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(EnergyConsumerPhase(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(EnergySource(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(EnergySourcePhase(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Fuse(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Ground(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(GroundDisconnector(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(GroundingImpedance(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Jumper(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Junction(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(LinearShuntCompensator(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(LoadBreakSwitch(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PerLengthSequenceImpedance(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PetersenCoil(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerTransformer(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(PowerTransformerEnd(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(RatioTapChanger(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(ReactiveCapabilityCurve(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Recloser(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(SeriesCompensator(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(StaticVarCompensator(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(SynchronousMachine(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(TransformerStarImpedance(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(TapChangerControl(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 InfIEC61970 BASE WIRES GENERATION PRODUCTION ************/
        ValidationInfo(EvChargingUnit(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),

        /************ IEC61970 InfIEC61970 Feeder ************/
        ValidationInfo(Circuit(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(Loop(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) }),
        ValidationInfo(LvFeeder(), { fillFields(it) }, { addFromPb(nsToPb.toPb(it)) })
    )

    override val abstractCreators = mapOf<Class<*>, (String) -> IdentifiedObject>(
        AssetOrganisationRole::class.java to { AssetOwner(it) },
        ConductingEquipment::class.java to { Junction(it) },
        Curve::class.java to { ReactiveCapabilityCurve(it) },
        EarthFaultCompensator::class.java to { GroundingImpedance(it) },
        EndDevice::class.java to { Meter(it) },
        EndDeviceFunction::class.java to { PanDemandResponseFunction(it) },
        Equipment::class.java to { Junction(it) },
        EquipmentContainer::class.java to { Site(it) },
        Measurement::class.java to { Discrete(it) },
        ProtectionRelayFunction::class.java to { CurrentRelay(it) },
        ProtectedSwitch::class.java to { Breaker(it) },
        RegulatingControl::class.java to { TapChangerControl(it) },
        RegulatingCondEq::class.java to { PowerElectronicsConnection(it) },
        RotatingMachine::class.java to { SynchronousMachine(it) },
        Sensor::class.java to { CurrentTransformer(it) },
        TransformerEnd::class.java to { PowerTransformerEnd(it) },
        WireInfo::class.java to { OverheadWireInfo(it) },
    )

    override val excludedTables =
        super.excludedTables + setOf(
            // Excluded associations
            TableAssetOrganisationRolesAssets::class,
            TableCircuitsSubstations::class,
            TableCircuitsTerminals::class,
            TableEquipmentEquipmentContainers::class,
            TableEquipmentOperationalRestrictions::class,
            TableEquipmentUsagePoints::class,
            TableLoopsSubstations::class,
            TableProtectionRelayFunctionsProtectedSwitches::class,
            TableProtectionRelaySchemesProtectionRelayFunctions::class,
            TableSynchronousMachinesReactiveCapabilityCurves::class,
            TableUsagePointsEndDevices::class,

            // Excluded array data
            TableCurveData::class,
            TableLocationStreetAddresses::class,
            TablePositionPoints::class,
            TablePowerTransformerEndRatings::class,
            TableProtectionRelayFunctionThresholds::class,
            TableProtectionRelayFunctionTimeLimits::class,
            TableProtectionRelayFunctionsSensors::class,
            TableRecloseDelays::class,
        )

}
