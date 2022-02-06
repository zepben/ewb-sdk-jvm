/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.collectionutils.CollectionUtils
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.tracing.phases.FeederDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseStep
import com.zepben.evolve.services.network.tracing.traversals.Traversal
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CoreTraceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun traceCores() {
        val n = getNetwork()

        // Trace all cores, we should visit everything
        var start = n.get<ConductingEquipment>("node0")!!
        var visited = currentNonDirectionalTrace(start, SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N)
        Assert.assertEquals(22, visited.size.toLong())

        // Trace core 0 from single core asset
        start = n["node9"]!!
        visited = currentNonDirectionalTrace(start, SinglePhaseKind.Y)
        Assert.assertEquals(21, visited.size.toLong())
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment11", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node5", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node0", SinglePhaseKind.B)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node6", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node6", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node7", SinglePhaseKind.B)))
    }

    @Test
    internal fun traceSingleCoresDownstream() {
        val n = getNetwork()

        // Test from the "source" of the network downstream
        var start = n.get<ConductingEquipment>("node0")!!
        var visited = currentDownstreamTrace(start, SinglePhaseKind.A)

        // node7, node9, acLineSegment8, acLineSegment9 and acLineSegment11 should not be traced.
        assertThat(
            visited, Matchers.containsInAnyOrder(
                createResultItem(n, "node0", SinglePhaseKind.A),
                createResultItem(n, "node1", SinglePhaseKind.A),
                createResultItem(n, "node2", SinglePhaseKind.A),
                createResultItem(n, "node3", SinglePhaseKind.A),
                createResultItem(n, "node4", SinglePhaseKind.X),
                createResultItem(n, "node5", SinglePhaseKind.X),
                createResultItem(n, "node6", SinglePhaseKind.X),
                createResultItem(n, "node8", SinglePhaseKind.X),
                createResultItem(n, "acLineSegment0", SinglePhaseKind.A),
                createResultItem(n, "acLineSegment1", SinglePhaseKind.A),
                createResultItem(n, "acLineSegment2", SinglePhaseKind.A),
                createResultItem(n, "acLineSegment3", SinglePhaseKind.A),
                createResultItem(n, "acLineSegment4", SinglePhaseKind.A),
                createResultItem(n, "acLineSegment5", SinglePhaseKind.X),
                createResultItem(n, "acLineSegment6", SinglePhaseKind.X),
                createResultItem(n, "acLineSegment7", SinglePhaseKind.X),
                createResultItem(n, "acLineSegment10", SinglePhaseKind.X)
            )
        )

        // Test from partway downstream to make sure we don't go upstream
        start = n["node1"]!!
        visited = normalDownstreamTrace(start, SinglePhaseKind.A)
        Assert.assertEquals(4, visited.size.toLong())
        Assert.assertTrue(visited.contains(createResultItem(n, "node1", SinglePhaseKind.A)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node2", SinglePhaseKind.A)))
        Assert.assertTrue(visited.stream().noneMatch { i: PhaseStep -> i.conductingEquipment.mRID == "acLineSegment1" })
        Assert.assertTrue(visited.stream().noneMatch { i: PhaseStep -> i.conductingEquipment.mRID == "acLineSegment9" })

        // Test on a core that splits onto different cores on different branches
        start = n["node0"]!!
        visited = normalDownstreamTrace(start, SinglePhaseKind.C)
        Assert.assertEquals(11, visited.size.toLong())
        Assert.assertTrue(visited.contains(createResultItem(n, "node1", SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node2", SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node7", SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node6", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.stream().noneMatch { i: PhaseStep -> i.conductingEquipment.mRID == "node3" })
    }

    @Test
    internal fun traceMultipleCoresDownstream() {
        val n = getNetwork()

        // Test from the "source" of the network downstream
        val start = n.get<ConductingEquipment>("node0")!!
        val visited = normalDownstreamTrace(start, SinglePhaseKind.B, SinglePhaseKind.C)

        // node8 and acLineSegment10 should not be traced.
        assertThat(
            visited, Matchers.containsInAnyOrder(
                createResultItem(n, "node0", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "node1", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "node2", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "node3", SinglePhaseKind.B),
                createResultItem(n, "node4", SinglePhaseKind.Y),
                createResultItem(n, "node5", SinglePhaseKind.Y),
                createResultItem(n, "node6", SinglePhaseKind.X, SinglePhaseKind.Y),
                createResultItem(n, "node7", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "node9", SinglePhaseKind.Y),
                createResultItem(n, "acLineSegment0", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "acLineSegment1", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "acLineSegment2", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "acLineSegment3", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "acLineSegment4", SinglePhaseKind.B),
                createResultItem(n, "acLineSegment5", SinglePhaseKind.Y),
                createResultItem(n, "acLineSegment6", SinglePhaseKind.Y),
                createResultItem(n, "acLineSegment7", SinglePhaseKind.Y),
                createResultItem(n, "acLineSegment8", SinglePhaseKind.X, SinglePhaseKind.Y),
                createResultItem(n, "acLineSegment9", SinglePhaseKind.B, SinglePhaseKind.C),
                createResultItem(n, "acLineSegment11", SinglePhaseKind.Y)
            )
        )
    }

    @Test
    internal fun traceSingleCoresUpstream() {
        val n = getNetwork()
        val start = n.get<ConductingEquipment>("acLineSegment11")!!
        val visited = normalUpstreamTrace(start, SinglePhaseKind.Y)

        Assert.assertEquals(9, visited.size.toLong())
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment11", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node5", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment6", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node4", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment5", SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node3", SinglePhaseKind.B)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment4", SinglePhaseKind.B)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment0", SinglePhaseKind.B)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node0", SinglePhaseKind.B)))
    }

    @Test
    internal fun traceMultipleCoresUpstream() {
        val n = getNetwork()
        val start = n.get<ConductingEquipment>("acLineSegment8")!!
        val visited = normalUpstreamTrace(start, SinglePhaseKind.X, SinglePhaseKind.Y)

        Assert.assertEquals(8, visited.size.toLong())
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment8", SinglePhaseKind.X, SinglePhaseKind.Y)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node7", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment9", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment2", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node1", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment1", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "acLineSegment0", SinglePhaseKind.B, SinglePhaseKind.C)))
        Assert.assertTrue(visited.contains(createResultItem(n, "node0", SinglePhaseKind.B, SinglePhaseKind.C)))
    }

    private fun getNetwork() = PhaseSwapLoopNetwork.create().also {
        setPhases(it)
    }

    // Set the phases so we can do a directional trace
    private fun setPhases(n: NetworkService) {
        val start = n.get<ConductingEquipment>("node0")!!

        start.terminals.forEach { t ->
            for (phase in t.phases.singlePhases) {
                t.normalPhases(phase).add(phase, FeederDirection.DOWNSTREAM)
                t.currentPhases(phase).add(phase, FeederDirection.DOWNSTREAM)
            }
        }

        start.terminals.forEach { t ->
            Tracing.setPhases().run(t, emptyList())
        }
    }

    private fun currentNonDirectionalTrace(start: ConductingEquipment, vararg phases: SinglePhaseKind): Set<PhaseStep> {
        return runTrace(Tracing.currentPhaseTrace(), start, *phases)
    }

    private fun normalDownstreamTrace(start: ConductingEquipment, vararg phases: SinglePhaseKind): Set<PhaseStep> {
        return runTrace(Tracing.normalDownstreamTrace(), start, *phases)
    }

    private fun currentDownstreamTrace(start: ConductingEquipment, vararg phases: SinglePhaseKind): Set<PhaseStep> {
        return runTrace(Tracing.currentDownstreamTrace(), start, *phases)
    }

    private fun normalUpstreamTrace(start: ConductingEquipment, vararg phases: SinglePhaseKind): Set<PhaseStep> {
        return runTrace(Tracing.normalUpstreamTrace(), start, *phases)
    }

    private fun runTrace(trace: Traversal<PhaseStep>, start: ConductingEquipment, vararg phases: SinglePhaseKind): Set<PhaseStep> {
        val visited = mutableSetOf<PhaseStep>()

        trace.addStepAction { phaseStep, _ -> System.err.println(phaseStep) }
        trace.addStepAction { phaseStep, _ -> visited.add(phaseStep) }
        trace.run(PhaseStep.startAt(start, CollectionUtils.setOf(*phases)))

        return visited
    }

    private fun createResultItem(n: NetworkService, id: String, vararg phases: SinglePhaseKind): PhaseStep {
        return PhaseStep.startAt(n[id]!!, CollectionUtils.setOf(*phases))
    }

}
