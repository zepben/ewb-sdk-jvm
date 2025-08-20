/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.Clamp
import com.zepben.ewb.cim.iec61970.base.wires.Cut
import com.zepben.ewb.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.getT
import com.zepben.ewb.services.network.tracing.traversal.StepActionWithContextValue
import com.zepben.ewb.services.network.tracing.traversal.StepContext
import com.zepben.ewb.services.network.tracing.traversal.TraversalQueue
import com.zepben.ewb.testing.TestNetworkBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration
import kotlin.test.assertContentEquals

class NetworkTraceTest {

    @Test
    internal fun `adds start clamp terminal as traversed segment path`() {
        val trace = Tracing.networkTrace()
        val segment = AcLineSegment()
        val clamp = Clamp().apply { addTerminal(Terminal()) }
        segment.addClamp(clamp)

        trace.addStartItem(clamp.t1)
        assertThat(trace.startItems().first().path, equalTo(NetworkTraceStep.Path(clamp.t1, clamp.t1, segment)))
    }

    @Test
    internal fun `adds start whole clamp as not traversed segment path`() {
        val trace = Tracing.networkTrace()
        val segment = AcLineSegment()
        val clamp = Clamp().apply {
            addTerminal(Terminal())
            segment.addClamp(this)
        }

        trace.addStartItem(clamp)
        assertThat(trace.startItems().first().path, equalTo(NetworkTraceStep.Path(clamp.t1, clamp.t1, null)))
    }

    @Test
    internal fun `adds start AcLineSegment terminals, cut terminals and clamp terminals as traversed segment`() {
        val trace = Tracing.networkTrace()
        val segment = AcLineSegment().apply {
            addTerminal(Terminal())
            addTerminal(Terminal())
        }
        val clamp1 = Clamp().apply {
            addTerminal(Terminal())
            segment.addClamp(this)
        }
        val clamp2 = Clamp().apply {
            addTerminal(Terminal())
            segment.addClamp(this)
        }
        val cut1 = Cut().apply {
            addTerminal(Terminal())
            addTerminal(Terminal())
            segment.addCut(this)
        }
        val cut2 = Cut().apply {
            addTerminal(Terminal())
            addTerminal(Terminal())
            segment.addCut(this)
        }

        trace.addStartItem(segment)
        assertThat(
            trace.startItems().map { it.path },
            containsInAnyOrder(
                NetworkTraceStep.Path(segment.t1, segment.t1, segment),
                NetworkTraceStep.Path(segment.t2, segment.t2, segment),
                NetworkTraceStep.Path(clamp1.t1, clamp1.t1, segment),
                NetworkTraceStep.Path(clamp2.t1, clamp2.t1, segment),
                NetworkTraceStep.Path(cut1.t1, cut1.t1, segment),
                NetworkTraceStep.Path(cut1.t2, cut1.t2, segment),
                NetworkTraceStep.Path(cut2.t1, cut2.t1, segment),
                NetworkTraceStep.Path(cut2.t2, cut2.t2, segment),
            )
        )
    }

    @Test
    internal fun `doesn't bypass stop conditions with multiple branches in equipment traces loop`() {
        //
        // /--21--c1--21
        // c0          j2 21--c3--2
        // \--12--c4--13
        //
        val ns = TestNetworkBuilder()
            .fromAcls() // c0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .branchFrom("j2", 2)
            .toAcls() // c3
            .branchFrom("j2", 3)
            .toAcls() // c4
            .connect("c4", "c0", 2, 1)
            .network

        val steppedOn = mutableSetOf<String>()
        Tracing.networkTrace()
            .addStopCondition { step, _ -> step.path.toEquipment.mRID == "j2" }
            .addStepAction { step, _ -> steppedOn.add(step.path.toEquipment.mRID) }
            .run(ns.get<ConductingEquipment>("c0")!!)

        assertThat(steppedOn, containsInAnyOrder("c0", "c1", "j2", "c4"))
    }

    @Test
    internal fun `breadth first queue supports multiple start items`() {
        //
        // 1--c1--21--c2--2
        // 2              1
        // j0             j3
        // 1              2
        // 2--c5--12--c4--1
        //
        val ns = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toAcls() // c2
            .toJunction() // j3
            .toAcls() // c4
            .toAcls() // c5
            .connect("c5", "j0", 2, 1)
            .network

        val steps = mutableSetOf<NetworkTraceStep<Unit>>()
        Tracing.networkTrace(queue = TraversalQueue.breadthFirst())
            .addStepAction { step, _ -> steps.add(step) }
            .run(ns.get<Junction>("j0")!!)

        assertThat(
            steps.map { it.numEquipmentSteps to it.path.toEquipment.mRID }.toSet(),
            containsInAnyOrder(
                0 to "j0",
                1 to "c1",
                1 to "c5",
                2 to "c2",
                2 to "c4",
                3 to "j3"
            )
        )
    }

    @Test
    internal fun `can stop on start item when running from conducting equipment`() {
        //
        // 1 b0 21--c1--2
        //
        val ns = TestNetworkBuilder()
            .fromBreaker() // j0
            .toAcls() // c1
            .network

        val steps = mutableSetOf<NetworkTraceStep<Unit>>()
        Tracing.networkTrace()
            .addStepAction { step, _ -> steps.add(step) }
            .addStopCondition { _, _ -> true }
            .run(ns.get<ConductingEquipment>("b0")!!)

        assertThat(steps.map { it.numEquipmentSteps to it.path.toEquipment.mRID }.toSet(), containsInAnyOrder(0 to "b0"))
    }

    @Test
    internal fun `can stop on start item when running from conducting equipment branching`() {
        //
        // 1 b0 21--c1--2
        //      1
        //       \--c2--2
        //
        val ns = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .branchFrom("b0")
            .toAcls() // c2
            .network

        val steps = mutableSetOf<NetworkTraceStep<Unit>>()
        Tracing.networkTraceBranching()
            .addStepAction { step, _ -> steps.add(step) }
            .addStopCondition { _, _ -> true }
            .run(ns.get<ConductingEquipment>("b0")!!)

        assertThat(steps.map { it.numEquipmentSteps to it.path.toEquipment.mRID }.toSet(), containsInAnyOrder(0 to "b0"))
    }

    @Test
    internal fun `Can run large branching traces`() {
        //
        // NOTE: This test was added when investigating an exponential run time on real world networks. It revealed an issue in the
        //       tracker for recursive branch traces which wasn't noticed in the other tests due to not being large/deep enough.
        //
        val builder = TestNetworkBuilder()
        val network = builder.network

        builder.fromJunction(numTerminals = 1)
            .toAcls()

        for (i in 1..1000) {
            builder.toJunction(mRID = "junc-$i", numTerminals = 3)
                .toAcls(mRID = "acls-$i-top")
                .fromAcls(mRID = "acls-$i-bottom")
                .connect("junc-$i", "acls-$i-bottom", 2, 1)
        }

        assertTimeoutPreemptively(
            Duration.ofSeconds(5),
            message = "If this test times out, you have managed to break things as described in the test note. Go fix it."
        ) {
            Tracing.networkTraceBranching().run(network.getT("j0", 1))
        }
    }


    @Test
    internal fun `multiple start items canStopOnStart doesn't prevent stop checks when visiting via loop`() {
        val ns = TestNetworkBuilder()
            .fromAcls() // c0
            .toAcls() // c1
            .toAcls() // c2
            .connectTo("c0")
            .network

        val stopChecks = mutableListOf<String>()
        val steps = mutableListOf<String>()

        Tracing.networkTrace(actionStepType = NetworkTraceActionType.ALL_STEPS)
            .addStopCondition { (path), _ ->
                stopChecks.add(path.toTerminal.mRID)
                path.toEquipment.mRID == "c1"
            }
            .ifNotStopping { (path), _ -> steps.add(path.toTerminal.mRID) }
            .run(ns.get<ConductingEquipment>("c1")!!, canStopOnStartItem = false)

        // Should loop out of t2 back to t1 which should be checked (depth first trace).
        // * c1-t2 shouldn't be checked as the start item, but should be actioned.
        // * c1-t1 on the trace loop should be checked, but not actioned if it stopped.
        // * c1-t1 as the start item should be ignored as it has already been visited.
        assertThat(stopChecks, contains("c2-t1", "c2-t2", "c0-t1", "c0-t2", "c1-t1"))
        assertThat(steps, contains("c1-t2", "c2-t1", "c2-t2", "c0-t1", "c0-t2"))
    }

    @Test
    internal fun `can provide a path to force the trace to traverse in a given direction`() {
        //
        // 1--c0--21--c1-*-21--c2--2
        //               1
        //               1--c3--2
        //
        val ns = TestNetworkBuilder()
            .fromAcls() // c0
            .toAcls() // c1
            .withClamp() // c1-clamp1
            .toAcls() // c2
            .branchFrom("c1-clamp1")
            .toAcls() // c3
            .network

        fun validate(start: Pair<String, String>, actionStepType: NetworkTraceActionType, vararg expected: String) {
            val steppedOn = mutableListOf<NetworkTraceStep<*>>()

            Tracing.networkTrace(actionStepType = actionStepType)
                .addStepAction { item, _ -> steppedOn.add(item) }
                .run(ns.createStartPath(start))

            assertThat(steppedOn.map { it.path.toTerminal.mRID }, contains(*expected))
        }

        validate("c0-t1" to "c0-t2", NetworkTraceActionType.ALL_STEPS, "c0-t2", "c1-t1", "c1-t2", "c2-t1", "c2-t2", "c1-clamp1-t1", "c3-t1", "c3-t2")
        validate("c0-t2" to "c0-t1", NetworkTraceActionType.ALL_STEPS, "c0-t1")
        validate("c1-t2" to "c2-t1", NetworkTraceActionType.ALL_STEPS, "c2-t1", "c2-t2")
        validate("c1-t1" to "c1-clamp1-t1", NetworkTraceActionType.ALL_STEPS, "c1-clamp1-t1", "c3-t1", "c3-t2")
        validate("c1-clamp1-t1" to "c1-t2", NetworkTraceActionType.ALL_STEPS, "c1-t2", "c2-t1", "c2-t2")

        validate("c0-t1" to "c0-t2", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c0-t2", "c1-t1", "c2-t1", "c1-clamp1-t1", "c3-t1")
        validate("c0-t2" to "c0-t1", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c0-t1")
        validate("c1-t2" to "c2-t1", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c2-t1")
        validate("c1-t1" to "c1-clamp1-t1", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c1-clamp1-t1", "c3-t1")
        validate("c1-clamp1-t1" to "c1-t2", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c1-t2", "c2-t1")

        // Can even use bizarre paths, they are just the same as any other external path.
        validate("c0-t1" to "c2-t1", NetworkTraceActionType.ALL_STEPS, "c2-t1", "c2-t2")
        validate("c0-t1" to "c2-t1", NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT, "c2-t1")
    }

    private fun NetworkService.createStartPath(start: Pair<String, String>): NetworkTraceStep.Path {
        val from = get<Terminal>(start.first)!!
        val to = get<Terminal>(start.second)!!

        val fromCe = from.conductingEquipment
        val toCe = to.conductingEquipment

        val traversed = when {
            toCe == fromCe -> toCe as? AcLineSegment
            (toCe is Clamp) && (toCe.acLineSegment == fromCe) -> toCe.acLineSegment
            (fromCe is Clamp) && (fromCe.acLineSegment == toCe) -> fromCe.acLineSegment
            else -> null
        }

        return NetworkTraceStep.Path(from, to, traversed)
    }

    @Test
    internal fun `test context is not shared between branches`() {
        //
        // 1--c0--21--c1-*-21--c2--21--ec3
        //               1
        //               1--c4--21--ec5
        //
        val ns = TestNetworkBuilder()
            .fromAcls() // c0
            .toAcls() // c1
            .withClamp() // c1-clamp1
            .toAcls() // c2
            .toEnergyConsumer() // ec3
            .branchFrom("c1-clamp1")
            .toAcls() // c4
            .toEnergyConsumer() // ec5
            .network

        val dataCapture = mutableMapOf<String, List<String>>()
        class StepActionWithContext : StepActionWithContextValue<NetworkTraceStep<*>, List<String>> {
            override val key: String get() = "testing"

            override fun apply( item: NetworkTraceStep<*>, context: StepContext ) {
                item.path.toEquipment.let {
                    if (it is EnergyConsumer) dataCapture[it.mRID] = context.value
                }
            }

            override fun computeInitialValue(item: NetworkTraceStep<*>): List<String> {
                return listOf(item.path.fromEquipment.mRID)
            }

            override fun computeNextValueTyped(
                nextItem: NetworkTraceStep<*>,
                currentItem: NetworkTraceStep<*>,
                currentValue: List<String>
            ): List<String> {
                return currentValue.toMutableList().also {
                    it.add(nextItem.path.fromEquipment.mRID)
                }
            }
        }
        Tracing.networkTrace()
            .addStepAction(StepActionWithContext())
            .run(ns.get<ConductingEquipment>("c0")!!)

        assert(dataCapture.size == 2)
        assertContentEquals(dataCapture.getValue("ec3"), listOf("c0", "c0", "c1", "c1", "c2", "c2"))
        assertContentEquals(dataCapture.getValue("ec5"),  listOf("c0", "c0", "c1", "c1-clamp1", "c4", "c4"))
    }
}
