/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.network.NetworkService
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import java.util.function.Consumer

fun createSourceForConnecting(network: NetworkService, id: String, numTerminals: Int, phaseCode: PhaseCode): EnergySource =
    EnergySource(id).apply {
        phaseCode.singlePhases().forEach(Consumer { phase: SinglePhaseKind? ->
            EnergySourcePhase().also {
                it.energySource = this
                it.phase = phase!!
                addPhase(it)
                network.add(it)
            }
        })

        createTerminals(network, this, numTerminals, phaseCode)
        network.add(this)
    }

fun createNodeForConnecting(network: NetworkService, id: String, numTerminals: Int, nominalPhases: PhaseCode = PhaseCode.ABCN): Junction =
    Junction(id).apply {
        name = "test name"
        createTerminals(network, this, numTerminals, nominalPhases)
        network.add(this)
    }

fun createSwitchForConnecting(
    network: NetworkService,
    id: String,
    numTerminals: Int,
    nominalPhases: PhaseCode = PhaseCode.ABCN,
    vararg openStatus: Boolean
): Breaker =
    Breaker(id).apply {
        name = "test name"
        createTerminals(network, this, numTerminals, nominalPhases)

        for (index in openStatus.indices) {
            setNormallyOpen(openStatus[index], nominalPhases.singlePhases()[index])
            setOpen(openStatus[index], nominalPhases.singlePhases()[index])
        }

        network.add(this)
    }

fun createPowerTransformerForConnecting(
    network: NetworkService,
    id: String,
    numTerminals: Int,
    nominalPhases: PhaseCode,
    numUsagePoints: Int,
    numMeters: Int
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
    nominalPhases: PhaseCode,
    length: Double = 0.0,
    perLengthSequenceImpedanceId: String = "perLengthSequenceImpedanceId",
    wireInfoId: String = "wireInfo"
): AcLineSegment =
    AcLineSegment(id).apply {
        perLengthSequenceImpedance = network.get(PerLengthSequenceImpedance::class, perLengthSequenceImpedanceId)
            ?: PerLengthSequenceImpedance(perLengthSequenceImpedanceId).also { network.add(it) }

        assetInfo = network.get(WireInfo::class, wireInfoId) ?: OverheadWireInfo(wireInfoId).also { network.add(it) }
        name = "$id name"
        this.length = length

        createTerminals(network, this, 2, nominalPhases)

        network.add(this)
    }

fun createEnergyConsumer(network: NetworkService, id: String, numTerminals: Int, nominalPhases: PhaseCode): EnergyConsumer =
    EnergyConsumer(id).apply {
        name = id
        createTerminals(network, this, numTerminals, nominalPhases)

        network.add(this)
    }

fun createHvMeterNode(network: NetworkService, id: String, nominalPhases: PhaseCode): Junction =
    Junction(id).apply {
        name = id
        createTerminals(network, this, 1, nominalPhases)

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

fun createTerminals(network: NetworkService, condEq: ConductingEquipment, numTerminals: Int, nominalPhases: PhaseCode) {
    for (i in 1..numTerminals)
        createTerminal(network, condEq, nominalPhases, i)
}

fun createTerminal(network: NetworkService, conductingEquipment: ConductingEquipment, phases: PhaseCode): Terminal =
    Terminal().apply {
        this.conductingEquipment = conductingEquipment
        this.phases = phases
        conductingEquipment.addTerminal(this)

        MatcherAssert.assertThat(network.add(this), Matchers.equalTo(true))
    }

fun createTerminal(network: NetworkService, conductingEquipment: ConductingEquipment?, phases: PhaseCode, sequenceNumber: Int) =
    conductingEquipment?.getTerminal(sequenceNumber) ?: Terminal().apply {
        this.conductingEquipment = conductingEquipment
        this.phases = phases
        this.sequenceNumber = sequenceNumber
        conductingEquipment?.addTerminal(this)

        MatcherAssert.assertThat(network.add(this), Matchers.equalTo(true))
    }

fun createGeographicalRegion(networkService: NetworkService, mRID: String, name: String): GeographicalRegion =
    GeographicalRegion(mRID).apply {
        this.name = name

        networkService.add(this)
    }

fun createSubGeographicalRegion(networkService: NetworkService, mRID: String, name: String, geographicalRegion: GeographicalRegion?) =
    SubGeographicalRegion(mRID).apply {
        this.name = name

        if (geographicalRegion != null) {
            this.geographicalRegion = geographicalRegion
            geographicalRegion.addSubGeographicalRegion(this)
        }

        networkService.add(this)
    }

fun createSubstation(networkService: NetworkService, mRID: String, name: String, subGeographicalRegion: SubGeographicalRegion? = null): Substation =
    Substation(mRID).apply {
        this.name = name

        if (subGeographicalRegion != null) {
            this.subGeographicalRegion = subGeographicalRegion
            subGeographicalRegion.addSubstation(this)
        }

        networkService.add(this)
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
            normalHeadTerminal = headTerminal ?: feederStartPoint.getTerminal(1)!!

        if (substation != null) {
            normalEnergizingSubstation = substation
            substation.addFeeder(this)
        }

        networkService.add(this)
    }

fun createFeeder(networkService: NetworkService, mRID: String, name: String, substation: Substation, vararg equipmentMRIDs: String?) =
    createFeeder(networkService, mRID, name, substation, networkService.get(ConductingEquipment::class, equipmentMRIDs[0]))
        .apply {
            for (equipmentMRID in equipmentMRIDs) {
                val conductingEquipment = networkService.get(ConductingEquipment::class, equipmentMRID)!!
                conductingEquipment.addContainer(this)

                this.addEquipment(conductingEquipment)
            }
        }

fun createEnd(networkService: NetworkService, tx: PowerTransformer, bv: BaseVoltage?, endNumber: Int) =
    PowerTransformerEnd().apply {
        baseVoltage = bv
        this.endNumber = endNumber

        powerTransformer = tx
        tx.addEnd(this)

        networkService.add(this)
    }

fun createEnd(networkService: NetworkService, tx: PowerTransformer, ratedU: Int, endNumber: Int) =
    PowerTransformerEnd().apply {
        this.ratedU = ratedU
        this.endNumber = endNumber

        powerTransformer = tx
        tx.addEnd(this)

        networkService.add(this)
    }

fun createOperationalRestriction(networkService: NetworkService, mRID: String, name: String, vararg equipmentMRIDs: String) =
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
