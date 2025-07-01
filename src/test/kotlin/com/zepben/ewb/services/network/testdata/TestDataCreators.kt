/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.ewb.cim.iec61968.assets.AssetOwner
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.networktrace.Tracing
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import org.hamcrest.MatcherAssert.assertThat

fun createSourceForConnecting(network: NetworkService, id: String, numTerminals: Int, phaseCode: PhaseCode = PhaseCode.A): EnergySource =
    EnergySource(id).apply {
        phaseCode.singlePhases.forEach { phase ->
            EnergySourcePhase().also {
                it.phase = phase
                addPhase(it)
                network.add(it)
            }
        }

        createTerminals(network, this, numTerminals, phaseCode)
        network.add(this)
    }

fun createJunctionForConnecting(network: NetworkService, id: String, numTerminals: Int, nominalPhases: PhaseCode = PhaseCode.A): Junction =
    Junction(id).apply {
        name = "test name"
        createTerminals(network, this, numTerminals, nominalPhases)
        network.add(this)
    }

fun createSwitchForConnecting(
    network: NetworkService,
    id: String,
    numTerminals: Int,
    vararg openStatus: Boolean,
    nominalPhases: PhaseCode = PhaseCode.A
): Breaker =
    Breaker(id).apply {
        name = "test name"
        createTerminals(network, this, numTerminals, nominalPhases)

        for (index in openStatus.indices) {
            setNormallyOpen(openStatus[index], nominalPhases.singlePhases[index])
            setOpen(openStatus[index], nominalPhases.singlePhases[index])
        }

        network.add(this)
    }

fun createPowerTransformerForConnecting(
    network: NetworkService,
    id: String,
    numTerminals: Int,
    numUsagePoints: Int,
    numMeters: Int,
    nominalPhases: PhaseCode = PhaseCode.A
): PowerTransformer =
    PowerTransformer(id).apply {
        name = "$id name"
        createTerminals(network, this, numTerminals, nominalPhases)

        network.add(this)

        for (i in 1..numUsagePoints) {
            val usagePoint = UsagePoint("$id-up$i")

            addUsagePoint(usagePoint)
            usagePoint.addEquipment(this)

            for (j in 1..numMeters) {
                val meter = createMeter(network, "$id-up$i-m$j")

                usagePoint.addEndDevice(meter)
                meter.addUsagePoint(usagePoint)
            }

            network.add(usagePoint)
        }
    }

@JvmOverloads
fun createAcLineSegmentForConnecting(
    network: NetworkService,
    id: String,
    nominalPhases: PhaseCode = PhaseCode.A,
    length: Double = 0.0,
    perLengthSequenceImpedanceId: String = "perLengthSequenceImpedanceId",
    wireInfoId: String = "wireInfo"
): AcLineSegment =
    AcLineSegment(id).apply {
        perLengthImpedance = network[perLengthSequenceImpedanceId]
            ?: PerLengthSequenceImpedance(perLengthSequenceImpedanceId).also { network.add(it) }

        assetInfo = network[wireInfoId] ?: OverheadWireInfo(wireInfoId).also { network.add(it) }
        name = "$id name"
        this.length = length

        createTerminals(network, this, 2, nominalPhases)

        network.add(this)
    }

fun createMeter(network: NetworkService, id: String): Meter =
    Meter(id).apply {
        name = "companyMeterId$id"
        addOrganisationRole(createAssetOwner(network, null, "company$id"))

        network.add(this)
    }

fun createAssetOwner(network: NetworkService, customerService: CustomerService?, company: String): AssetOwner =
    AssetOwner("$company-owner-role").apply {
        val org = Organisation(company)
        org.name = company
        organisation = org

        network.add(org)
        network.add(this)

        customerService?.add(org)
    }

fun createTerminals(network: NetworkService, condEq: ConductingEquipment, numTerminals: Int, nominalPhases: PhaseCode = PhaseCode.A) {
    for (i in 1..numTerminals)
        createTerminal(network, condEq, nominalPhases, i)
}

fun createTerminal(network: NetworkService, conductingEquipment: ConductingEquipment?, phases: PhaseCode = PhaseCode.A, sequenceNumber: Int): Terminal =
    conductingEquipment?.getTerminal(sequenceNumber) ?: Terminal(conductingEquipment?.mRID?.let { "$it-t$sequenceNumber" } ?: "").apply {
        this.conductingEquipment = conductingEquipment
        this.phases = phases
        this.sequenceNumber = sequenceNumber
        conductingEquipment?.addTerminal(this)

        assertThat("Initial add should return true", network.add(this))
    }

fun createSubstation(networkService: NetworkService, mRID: String, name: String, subGeographicalRegion: SubGeographicalRegion? = null): Substation =
    Substation(mRID).also {
        it.name = name

        if (subGeographicalRegion != null) {
            it.subGeographicalRegion = subGeographicalRegion
            subGeographicalRegion.addSubstation(it)
        }

        networkService.add(it)
    }

fun createFeeder(
    networkService: NetworkService,
    mRID: String,
    name: String,
    substation: Substation?,
    feederStartPoint: ConductingEquipment?,
    headTerminal: Terminal? = null
): Feeder =
    Feeder(mRID).apply {
        this.name = name

        if (feederStartPoint != null)
            normalHeadTerminal = headTerminal ?: feederStartPoint.t1

        if (substation != null) {
            normalEnergizingSubstation = substation
            substation.addFeeder(this)
        }

        networkService.add(this)
    }

fun createFeeder(networkService: NetworkService, mRID: String, name: String, substation: Substation, vararg equipmentMRIDs: String?): Feeder =
    createFeeder(networkService, mRID, name, substation, networkService.get(ConductingEquipment::class, equipmentMRIDs[0]))
        .apply {
            for (equipmentMRID in equipmentMRIDs) {
                val conductingEquipment = networkService.get(ConductingEquipment::class, equipmentMRID)!!
                conductingEquipment.addContainer(this)

                this.addEquipment(conductingEquipment)
            }
        }

fun createEnd(networkService: NetworkService, tx: PowerTransformer, ratedU: Int? = null, endNumber: Int = 0): PowerTransformerEnd =
    PowerTransformerEnd().also {
        it.ratedU = ratedU
        it.endNumber = endNumber

        tx.addEnd(it)
        networkService.add(it)
    }

fun createOperationalRestriction(networkService: NetworkService, mRID: String, name: String, vararg equipmentMRIDs: String): OperationalRestriction =
    OperationalRestriction(mRID).apply {
        this.name = name

        networkService.add(this)

        equipmentMRIDs.forEach { mRID ->
            networkService.get<Equipment>(mRID)!!.also {
                it.addOperationalRestriction(this)
                this.addEquipment(it)
            }
        }
    }

inline fun <reified T : ConductingEquipment> T.addFeederDirections(terminal: Int = 1): T {
    getTerminal(terminal)?.also { Tracing.setDirection().run(it, NetworkStateOperators.NORMAL) }
    getTerminal(terminal)?.also { Tracing.setDirection().run(it, NetworkStateOperators.CURRENT) }
    return this
}

fun NetworkService.setPhases() {
    Tracing.setPhases().run(this, NetworkStateOperators.NORMAL)
    Tracing.setPhases().run(this, NetworkStateOperators.CURRENT)
}

fun NetworkService.setFeederDirections() {
    Tracing.setDirection().run(this, NetworkStateOperators.NORMAL)
    Tracing.setDirection().run(this, NetworkStateOperators.CURRENT)
}

fun NetworkService.assignEquipmentToFeeders() {
    Tracing.assignEquipmentToFeeders().run(this, NetworkStateOperators.NORMAL)
    Tracing.assignEquipmentToFeeders().run(this, NetworkStateOperators.CURRENT)
}

fun NetworkService.assignEquipmentToLvFeeders() {
    Tracing.assignEquipmentToLvFeeders().run(this, NetworkStateOperators.NORMAL)
    Tracing.assignEquipmentToLvFeeders().run(this, NetworkStateOperators.CURRENT)
}
