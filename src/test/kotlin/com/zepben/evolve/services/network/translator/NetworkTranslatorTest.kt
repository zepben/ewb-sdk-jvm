/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.translator

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
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkServiceComparator
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.fail

internal class NetworkTranslatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val comparator = NetworkServiceComparator()

    // We need to create concrete types that are supported by the service so the references can be resolved below.
    private val abstractCreators = mapOf<Class<*>, (String) -> IdentifiedObject>(
        AssetOrganisationRole::class.java to { AssetOwner(it) },
        ConductingEquipment::class.java to { Junction(it) },
        Curve::class.java to { ReactiveCapabilityCurve(it) },
        EndDevice::class.java to { Meter(it) },
        Equipment::class.java to { Junction(it) },
        EquipmentContainer::class.java to { Site(it) },
        EarthFaultCompensator::class.java to { GroundingImpedance(it) },
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

    @Test
    internal fun convertsCorrectly() {
        val nsToPb = NetworkCimToProto()

        /************ IEC61968 ASSET INFO ************/
        validate({ CableInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ NoLoadTest() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ OpenCircuitTest() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ OverheadWireInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerTransformerInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ShortCircuitTest() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ShuntCompensatorInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ SwitchInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ TransformerEndInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ TransformerTankInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61968 ASSETS ************/
        validate({ AssetOwner() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Pole() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Streetlight() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61968 COMMON ************/
        validate({ Location() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Organisation() }, { ns, it -> it.fillFieldsCommon(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61968 infIEC61968 InfAssetInfo ************/
        validate({ RelayInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ CurrentTransformerInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PotentialTransformerInfo() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61968 METERING ************/
        validate({ Meter() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ UsagePoint() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61968 OPERATIONS ************/
        validate({ OperationalRestriction() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/
        validate({ CurrentTransformer() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ FaultIndicator() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PotentialTransformer() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE CORE ************/
        validate({ BaseVoltage() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ConnectivityNode() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Feeder() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ GeographicalRegion() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Site() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ SubGeographicalRegion() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Substation() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Terminal() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE EQUIVALENTS ************/
        validate({ EquivalentBranch() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE MEAS ************/
        validate({ Accumulator() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Analog() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Control() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Discrete() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 Base Protection ************/
        validate({ CurrentRelay() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ DistanceRelay() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ProtectionRelayScheme() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ProtectionRelaySystem() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ VoltageRelay() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE SCADA ************/
        validate({ RemoteControl() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ RemoteSource() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/
        validate({ BatteryUnit() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PhotoVoltaicUnit() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerElectronicsConnection() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerElectronicsConnectionPhase() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerElectronicsWindUnit() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 BASE WIRES ************/
        validate({ AcLineSegment() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Breaker() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ BusbarSection() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Disconnector() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ EnergyConsumer() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ EnergyConsumerPhase() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ EnergySource() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ EnergySourcePhase() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Fuse() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Ground() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ GroundDisconnector() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ GroundingImpedance() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Jumper() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Junction() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ LinearShuntCompensator() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ LoadBreakSwitch() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PerLengthSequenceImpedance() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PetersenCoil() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerTransformer() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ PowerTransformerEnd() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ RatioTapChanger() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ ReactiveCapabilityCurve() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Recloser() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ SeriesCompensator() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ SynchronousMachine() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ TransformerStarImpedance() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ TapChangerControl() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 InfIEC61970 BASE WIRES GENERATION PRODUCTION ************/
        validate({ EvChargingUnit() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })

        /************ IEC61970 InfIEC61970 Feeder ************/
        validate({ Circuit() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ Loop() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
        validate({ LvFeeder() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(nsToPb.toPb(it)) })
    }

    //
    // NOTE: NameType is not sent via any grpc messages at this stage, so test it separately
    //

    @Test
    internal fun createsNewNameType() {
        val pb = NameType("nt1 name").apply {
            description = "nt1 desc"
        }.toPb()

        val cim = NetworkService().addFromPb(pb)

        assertThat(cim.name, equalTo(pb.name))
        assertThat(cim.description, equalTo(pb.description))
    }

    @Test
    internal fun updatesExistingNameType() {
        val pb = NameType("nt1 name").apply {
            description = "nt1 desc"
        }.toPb()

        val nt = NameType("nt1 name")
        val cim = NetworkService().apply { addNameType(nt) }.addFromPb(pb)

        assertThat(cim, sameInstance(nt))
        assertThat(cim.description, equalTo(pb.description))
    }

    private inline fun <reified T : IdentifiedObject> validate(creator: () -> T, filler: (NetworkService, T) -> Unit, adder: (NetworkService, T) -> T?) {
        val cim = creator()
        val blankDifferences = comparator.compare(cim, adder(NetworkService(), cim)!!).differences
        assertThat("Failed to convert blank ${T::class.simpleName}:${blankDifferences}", blankDifferences, anEmptyMap())

        filler(NetworkService(), cim)
        removeUnsentReferences(cim)

        val populatedDifferences = comparator.compare(cim, addWithUnresolvedReferences(cim, adder)).differences
        assertThat("Failed to convert populated ${T::class.simpleName}:${populatedDifferences}", populatedDifferences, anEmptyMap())
    }

    private inline fun <reified T : IdentifiedObject> removeUnsentReferences(cim: T) {
        if (cim is EquipmentContainer)
            cim.clearEquipment()

        if (cim is OperationalRestriction)
            cim.clearEquipment()

        if (cim is Feeder)
            cim.clearCurrentEquipment()

        if (cim is ConnectivityNode)
            cim.clearTerminals()

        if (cim is LvFeeder)
            cim.clearCurrentEquipment()
    }

    private inline fun <reified T : IdentifiedObject> addWithUnresolvedReferences(cim: T, adder: (NetworkService, T) -> T?): T {
        // We need to convert the populated item before we check the differences, so we can complete the unresolved references.
        val service = NetworkService()
        val convertedCim = adder(service, cim)!!
        service.unresolvedReferences().toList().forEach { (_, toMrid, resolver, _) ->
            try {
                val io = abstractCreators[resolver.toClass]?.invoke(toMrid) ?: resolver.toClass.getDeclaredConstructor(String::class.java)
                    .newInstance(toMrid)
                io.also { service.tryAdd(it) }
            } catch (e: Exception) {
                // If this fails you need to add a concrete type mapping to the abstractCreators map at the top of this class.
                fail("Failed to create unresolved reference for ${resolver.toClass}.", e)
            }
        }
        return convertedCim
    }

}
