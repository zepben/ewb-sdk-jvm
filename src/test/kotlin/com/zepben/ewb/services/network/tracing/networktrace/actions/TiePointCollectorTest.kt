/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.actions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.network.tracing.networktrace.Tracing
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.limitEquipmentSteps
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.networktrace.operators.OpenStateOperators
import com.zepben.ewb.services.network.tracing.networktrace.run
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TiePointCollectorTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    fun `collects every piece of equipment stepped on by the trace`() {
        //
        // j0 -- c1 -- b2 -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker() // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2", "c3", "j4"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch on the boundary of the trace is a tie point`() {
        //
        // j0 -- c1 -- b2(open) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2"))
        assertThat(collector.calculateTiePoints().mRIDs(), contains("b2"))
    }

    @Test
    fun `closed switch on the boundary of the trace is not a tie point`() {
        //
        // j0 -- c1 -- b2 -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker() // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addStopCondition(limitEquipmentSteps(2))
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch with nothing connected on the far side is not a tie point`() {
        //
        // j0 -- c1 -- b2(open) -- (nothing)
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), hasItem("b2"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch inside the traced area is not a tie point`() {
        //
        //             c3 -- j4 -- c5 -- b6(open) -- c7 -- j8 -- c9
        //            /                                           \
        // j0 -- c1 -- j2 -------------------------------------------+
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toJunction() // j4
            .toAcls() // c5
            .toBreaker(isNormallyOpen = true) // b6
            .toAcls() // c7
            .toJunction() // j8
            .toAcls() // c9
            .connect("c9", "j2", 2, 3)
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), hasItems("c5", "b6", "c7"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `switch with only some phases open is a tie point`() {
        //
        // j0 -- c1 -- b2(phase A open) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker { setNormallyOpen(true, SinglePhaseKind.A) } // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.calculateTiePoints().mRIDs(), contains("b2"))
    }

    @Test
    fun `stop conditions define the trace results used for tie points`() {
        //
        //             c3 -- j4 -- c5 -- b6(open) -- c7 -- j8 -- c9(stop)
        //            /                                           \
        // j0 -- c1 -- j2 -------------------------------------------+
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toJunction() // j4
            .toAcls() // c5
            .toBreaker(isNormallyOpen = true) // b6
            .toAcls() // c7
            .toJunction() // j8
            .toAcls() // c9
            .connect("c9", "j2", 2, 3)
            .network

        // Stopping at c9 means c7 is never reached, so b6 is now on the boundary of the trace.
        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStopCondition { step, _ -> step.path.toEquipment.mRID == "c9" }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), not(hasItem("c7")))
        assertThat(collector.calculateTiePoints().mRIDs(), contains("b6"))
    }

    @Test
    fun `reports every tie point on the boundary of the trace`() {
        //
        //             c3 -- b4(open) -- c5
        //            /
        // j0 -- c1 -- j2
        //            \
        //             c6 -- b7(open) -- c8
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toBreaker(isNormallyOpen = true) // b4
            .toAcls() // c5
            .branchFrom("j2", 3)
            .toAcls() // c6
            .toBreaker(isNormallyOpen = true) // b7
            .toAcls() // c8
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.calculateTiePoints().mRIDs(), containsInAnyOrder("b4", "b7"))
    }

    @Test
    fun `uses the open state operators it was created with`() {
        //
        // j0 -- c1 -- b2(normally open, currently closed) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true, isOpen = false) // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val defaultCollector = TiePointCollector()
        val normalCollector = TiePointCollector(OpenStateOperators.NORMAL)
        val currentCollector = TiePointCollector(OpenStateOperators.CURRENT)

        // Only trace to b2, so it is on the boundary regardless of the open state being checked.
        Tracing.networkTrace()
            .addStopCondition(limitEquipmentSteps(2))
            .addStepAction(defaultCollector)
            .addStepAction(normalCollector)
            .addStepAction(currentCollector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(defaultCollector.calculateTiePoints().mRIDs(), contains("b2"))
        assertThat(normalCollector.calculateTiePoints().mRIDs(), contains("b2"))
        assertThat(currentCollector.calculateTiePoints(), empty())
    }

    @Test
    fun `supports traces of the current network state`() {
        //
        // j0 -- c1 -- b2(normally closed, currently open) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = false, isOpen = true) // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector(OpenStateOperators.CURRENT)
        Tracing.networkTrace(NetworkStateOperators.CURRENT)
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2"))
        assertThat(collector.calculateTiePoints().mRIDs(), contains("b2"))
    }

    @Test
    fun `open switch the trace starts on is not a tie point when both sides are traced`() {
        //
        // c0 -- b1(open) -- c2
        //
        val n = TestNetworkBuilder()
            .fromAcls() // c0
            .toBreaker(isNormallyOpen = true) // b1
            .toAcls() // c2
            .network

        // Starting on the equipment queues all of its terminals, so both sides of the open switch are traced.
        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("b1")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("c0", "b1", "c2"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch the trace starts on with nothing connected on the far side is not a tie point`() {
        //
        // c0 -- b1(open) -- (nothing)
        //
        val n = TestNetworkBuilder()
            .fromAcls() // c0
            .toBreaker(isNormallyOpen = true) // b1
            .network

        // The open condition only blocks stepping onto an open switch, not off the start item, so c0 is still traced.
        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("b1")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("c0", "b1"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch the trace starts on is a tie point when started from only one of its terminals`() {
        //
        // c0 -- b1(open) -- c2
        //
        val n = TestNetworkBuilder()
            .fromAcls() // c0
            .toBreaker(isNormallyOpen = true) // b1
            .toAcls() // c2
            .network

        // Starting on a single terminal only steps externally from it, so the far side of the switch is never traced.
        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<Terminal>("b1-t1")!!)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("c0", "b1"))
        assertThat(collector.calculateTiePoints().mRIDs(), contains("b1"))
    }

    @Test
    fun `switch open on a phase that is not traced is not a tie point when the trace continues through it`() {
        //
        // j0 -- c1 -- b2(phase B open) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker { setNormallyOpen(true, SinglePhaseKind.B) } // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        // Only phase A is traced and checked for open, so the trace continues through b2 to the far side.
        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen(SinglePhaseKind.A) }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!, PhaseCode.A)

        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2", "c3", "j4"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `clear removes all collected equipment`() {
        //
        // j0 -- c1 -- b2(open) -- c3
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .network

        val collector = TiePointCollector()
        Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)
            .run(n.get<ConductingEquipment>("j0")!!)

        assertThat(collector.calculateTiePoints(), not(empty<Switch>()))

        collector.clear()

        assertThat(collector.visited, empty())
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `collects across multiple traces until cleared`() {
        //
        // j0 -- c1 -- b2(open) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        val collector = TiePointCollector()
        val trace = Tracing.networkTrace()
            .addCondition { stopAtOpen() }
            .addStepAction(collector)

        trace.run(n.get<ConductingEquipment>("j0")!!)
        assertThat(collector.calculateTiePoints().mRIDs(), contains("b2"))

        // Tracing from the other side adds to the results, so both sides of b2 are now traced.
        trace.run(n.get<ConductingEquipment>("j4")!!)
        assertThat(collector.visited.mRIDs(), containsInAnyOrder("j0", "c1", "b2", "c3", "j4"))
        assertThat(collector.calculateTiePoints(), empty())
    }

    @Test
    fun `open switch is a tie point when only some of its connected equipment is outside the trace results`() {
        //
        //                         c3
        //                        /
        // j0 -- c1 -- b2(open) --
        //                        \
        //                         c4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .branchFrom("b2", 2)
            .toAcls() // c4
            .network

        fun results(vararg mRIDs: String) = mRIDs.map { n.get<ConductingEquipment>(it)!! }.toSet()

        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2", "c3")).mRIDs(), contains("b2"))
        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2", "c4")).mRIDs(), contains("b2"))
        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2", "c3", "c4")), empty())
    }

    @Test
    fun `calculates tie points from existing trace results`() {
        //
        // j0 -- c1 -- b2(normally open, currently closed) -- c3 -- j4
        //
        val n = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true, isOpen = false) // b2
            .toAcls() // c3
            .toJunction() // j4
            .network

        fun results(vararg mRIDs: String) = mRIDs.map { n.get<ConductingEquipment>(it)!! }.toSet()

        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2")).mRIDs(), contains("b2"))
        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2"), OpenStateOperators.CURRENT), empty())
        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1", "b2", "c3", "j4")), empty())
        assertThat(TiePointCollector.calculateTiePoints(results("j0", "c1")), empty())
        assertThat(TiePointCollector.calculateTiePoints(emptySet()), empty())
    }

    private fun Iterable<IdentifiedObject>.mRIDs(): List<String> = map { it.mRID }

}
