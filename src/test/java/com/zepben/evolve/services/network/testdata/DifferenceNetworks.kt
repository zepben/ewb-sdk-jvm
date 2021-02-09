/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.phases.PhaseDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseStatus

object DifferenceNetworks {
    @JvmStatic
    fun createSourceNetwork(): NetworkService {
        val network = NetworkService()
        createAcLineSegmentForConnecting(network, "4", PhaseCode.A)
        createNodeForConnecting(network, "5", 1)
        createAcLineSegmentForConnecting(network, "6", PhaseCode.A)
        createNodeForConnecting(network, "7", 1)
        createAcLineSegmentForConnecting(network, "8", PhaseCode.A)
        createNodeForConnecting(network, "9", 1)
        createAcLineSegmentForConnecting(network, "10", PhaseCode.A)
        createNodeForConnecting(network, "11", 1)
        val n12 = createNodeForConnecting(network, "12", 1)
        val n13 = createNodeForConnecting(network, "13", 1)
        createNodeForConnecting(network, "14", 1)
        val n15 = createNodeForConnecting(network, "15", 1)
        val n16 = createNodeForConnecting(network, "16", 1)
        val n17 = createNodeForConnecting(network, "17", 1)
        val n18 = createNodeForConnecting(network, "18", 1)
        val n19 = createNodeForConnecting(network, "19", 1)
        val n20 = createNodeForConnecting(network, "20", 1)
        createFeeder(network, "f001", n12, listOf(n13), listOf(n13))
        createFeeder(network, "f002", n12, listOf(n13), listOf(n13))
        createFeeder(network, "f003", n12, listOf(n13), listOf(n13))
        createFeeder(network, "f004", n12, emptyList(), emptyList())
        createFeeder(network, "f006", n12, listOf(n13), listOf(n13))
        createFeeder(network, "f007", n12, listOf(n13), listOf(n13))
        setPhases(n15, { obj, nominalPhase: SinglePhaseKind -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n16, { obj, nominalPhase: SinglePhaseKind -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n17, { obj, nominalPhase: SinglePhaseKind -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n18, { obj, nominalPhase: SinglePhaseKind -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n19, { obj, nominalPhase: SinglePhaseKind -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n20, { obj, nominalPhase: SinglePhaseKind -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        return network
    }

    private fun setPhases(
        node: Junction,
        phaseExtractor: (Terminal, SinglePhaseKind) -> PhaseStatus,
        singlePhaseKind: SinglePhaseKind,
        direction: PhaseDirection
    ) {
        phaseExtractor.invoke(node.getTerminal(1)!!, SinglePhaseKind.A).set(singlePhaseKind, direction)
    }

    @JvmStatic
    fun createTargetNetwork(): NetworkService {
        val network = NetworkService()
        createNodeForConnecting(network, "1", 1)
        createAcLineSegmentForConnecting(network, "2", PhaseCode.A)
        createNodeForConnecting(network, "3", 1)
        createAcLineSegmentForConnecting(network, "6", PhaseCode.AB)
        createNodeForConnecting(network, "7", 2)
        createAcLineSegmentForConnecting(network, "8", PhaseCode.AB)
        createNodeForConnecting(network, "9", 2)
        createAcLineSegmentForConnecting(network, "10", PhaseCode.A)
        createNodeForConnecting(network, "11", 1)
        val n12 = createNodeForConnecting(network, "12", 1)
        val n13 = createNodeForConnecting(network, "13", 1)
        val n14 = createNodeForConnecting(network, "14", 1)
        val n15 = createNodeForConnecting(network, "15", 1)
        val n16 = createNodeForConnecting(network, "16", 1)
        val n17 = createNodeForConnecting(network, "17", 1)
        val n18 = createNodeForConnecting(network, "18", 1)
        val n19 = createNodeForConnecting(network, "19", 1)
        val n20 = createNodeForConnecting(network, "20", 1)
        createFeeder(network, "f001", n12, listOf(n13), listOf(n13))
        createFeeder(network, "f002", n12, emptyList(), listOf(n13))
        createFeeder(network, "f003", n12, listOf(n13), emptyList())
        createFeeder(network, "f005", n12, emptyList(), emptyList())
        createFeeder(network, "f006", n12, listOf(n14), listOf(n13))
        createFeeder(network, "f007", n12, listOf(n13), listOf(n14))
        setPhases(n15, { obj, nominalPhase -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n16, { obj, nominalPhase -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.B, PhaseDirection.IN)
        setPhases(n17, { obj, nominalPhase -> obj.normalPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.OUT)
        setPhases(n18, { obj, nominalPhase -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.IN)
        setPhases(n19, { obj, nominalPhase -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.B, PhaseDirection.IN)
        setPhases(n20, { obj, nominalPhase -> obj.currentPhases(nominalPhase) }, SinglePhaseKind.A, PhaseDirection.OUT)
        return network
    }

    private fun createFeeder(
        network: NetworkService,
        feederName: String,
        startAsset: ConductingEquipment,
        normalAssets: List<ConductingEquipment>,
        currentAssets: List<ConductingEquipment>
    ) {
        val feeder = Feeder(feederName)
        feeder.normalHeadTerminal = startAsset.getTerminal(1)
        feeder.name = feederName
        normalAssets.forEach { equipment -> feeder.addEquipment(equipment) }
        currentAssets.forEach { equipment -> feeder.addCurrentEquipment(equipment) }
        network.add(feeder)
    }
}