/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.services.network.getT
import com.zepben.evolve.testing.TestNetworkBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration

class NetworkTraceTest {

    @Test
    fun `adds start clamp terminal as traversed segment path`() {
        val trace = Tracing.networkTrace()
        val segment = AcLineSegment()
        val clamp = Clamp().apply { addTerminal(Terminal()) }
        segment.addClamp(clamp)

        trace.addStartItem(clamp.t1)
        assertThat(trace.startItems().first().path, equalTo(NetworkTraceStep.Path(clamp.t1, clamp.t1, segment)))
    }

    @Test
    fun `adds start whole clamp as not traversed segment path`() {
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
    fun `adds start AcLineSegment terminals, cut terminals and clamp terminals as traversed segment`() {
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

}
