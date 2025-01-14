/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:Suppress("PropertyName")

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.getT
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.tracing.feeder.DirectionValidator.validateDirection
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

internal class SetDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun setDirectionTest() {
        val n = PhaseSwapLoopNetwork.create()

        doSetDirectionTrace(n)

        n.getT("acLineSegment0", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment0", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment4", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j4", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j4", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j4", 3).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j8", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j5", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j5", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j5", 3).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j9", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j6", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j6", 2).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment2", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment3", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("acLineSegment9", 2).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun stopsAtOpenPoints() {
        //
        // 1--c0--21 b1 21--c2--2
        //         1 b3 21--c4--2
        //
        val n = TestNetworkBuilder()
            .fromAcls() // c0
            .toBreaker(isNormallyOpen = true, isOpen = false) // b1
            .toAcls() // c2
            .branchFrom("c0")
            .toBreaker(isOpen = true) // b3
            .toAcls() // c4
            .network

        doSetDirectionTrace(n.getT("c0", 2))

        n.getT("c0", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c0", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b1", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("b1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.CURRENT)
        n.getT("c2", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c2", 1).validateDirection(UPSTREAM, NetworkStateOperators.CURRENT)
        n.getT("c2", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c2", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.CURRENT)
        n.getT("b3", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b3", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b3", 2).validateDirection(NONE, NetworkStateOperators.CURRENT)
        n.getT("c4", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c4", 1).validateDirection(NONE, NetworkStateOperators.CURRENT)
        n.getT("c4", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c4", 2).validateDirection(NONE, NetworkStateOperators.CURRENT)
    }

    @Test
    internal fun doesNotTraceThroughFeederHeads() {
        //
        // 1--c0--21 j1*21--c2--21*j3 21--c4--2
        //
        // * = feeder start
        //
        val n = TestNetworkBuilder()
            .fromAcls() // c0
            .toJunction() // j1
            .toAcls() // c2
            .toJunction() // j3
            .toAcls() // c4
            .addFeeder("j1", 2)
            .addFeeder("j3", 1)
            .build()

        DirectionLogger.trace(n["c0"])

        n.getT("c0", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c0", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("j1", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("j1", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c2", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c2", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j3", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c4", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c4", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun `stops at zone transformers in case feeder heads are missing`() {
        //
        // 1 b0*21--c1--21 tx2 21--c3--2
        //
        // * = feeder start
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toPowerTransformer { addContainer(Substation()) } // tx2
            .toAcls() // c3
            .addFeeder("b0", 2)
            .build()

        DirectionLogger.trace(n["b0"])

        n.getT("b0", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("b0", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("tx2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("tx2", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun setDirectionInClosedLoopTest() {
        //
        // s0 11----21 j2 21----21 j4 21----21 j6 21----21 j8 2
        //       c1       1  c3          c5  2       c7
        //                |__________________|
        //                         c9
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.A) // s0
            .toAcls(PhaseCode.A) // c1
            .toJunction(PhaseCode.A) // j2
            .toAcls(PhaseCode.A) // c3
            .toJunction(PhaseCode.A) // j4
            .toAcls(PhaseCode.A) // c5
            .toJunction(PhaseCode.A) // j6
            .toAcls(PhaseCode.A) // c7
            .toJunction(PhaseCode.A, 1) // j8
            .branchFrom("j2")
            .toAcls(PhaseCode.A) // c9
            .connect("c9", "j6", 2, 1)
            .addFeeder("s0")
            .network // Do not call build as we do not want to trace the directions yet.

        doSetDirectionTrace(n)

        n.getT("s0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j6", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j8", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c9", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c9", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun testDualPathLoopTop() {
        //
        // NOTE: This test is for checking the both setting around the loop when a completed loop via c9 and j4 is
        //       performed before the backwards path through j11.
        //

        //
        //               /-c10-21 j11 21-c12-\
        //               |                   |
        //               1                   2
        //               2                   2
        // j0 11--c1--21 j2                  j6 31--c7--21 j8
        //               32-------c9--------11
        //               1                   2
        //               |                   |
        //               \-c3--21 j4 21---c5-/
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toJunction() // j4
            .toAcls() // c5
            .toJunction(numTerminals = 3) // j6
            .toAcls() // c7
            .toJunction(numTerminals = 1) // j8
            .fromAcls() // c9
            .fromAcls() // c10
            .toJunction() // j11
            .toAcls() // c12
            .connect("c9", "j6", 1, 1)
            .connect("c9", "j2", 2, 3)
            .connect("c10", "j2", 1, 2)
            .connect("c12", "j6", 2, 2)
            .network

        doSetDirectionTrace(n.getT("j0", 1))

        n.getT("j0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j2", 3).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 3).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j8", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c9", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c9", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c10", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c10", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j11", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j11", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c12", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c12", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun testDualPathLoopBottom() {
        //
        // NOTE: This test is for checking the both setting around the loop when a completed loop via c9 and j11 is
        //       performed after the backwards path through j4.
        //

        //
        //               /-c3--21 j4 21---c5-\
        //               |                   |
        //               1                   2
        //               3                   1
        // j0 11--c1--21 j2                  j6 31--c7--21 j8
        //               22-------c9--------12
        //               1                   2
        //               |                   |
        //               \-c10-21 j11 21-c12-/
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toJunction() // j4
            .toAcls() // c5
            .toJunction(numTerminals = 3) // j6
            .toAcls() // c7
            .toJunction(numTerminals = 1) // j8
            .fromAcls() // c9
            .fromAcls() // c10
            .toJunction() // j11
            .toAcls() // c12
            .connect("c9", "j6", 1, 2)
            .connect("c9", "j2", 2, 2)
            .connect("c10", "j2", 1, 2)
            .connect("c12", "j6", 2, 2)
            .network

        doSetDirectionTrace(n.getT("j0", 1))

        // To avoid reprocessing all BOTH loops in larger networks we do not process anything with a direction already set. This means this test will apply
        // a UP/DOWN path through j2-t2 directly into a BOTH loop around the c9/j11 loop which will stop the reverse UP/DOWN path
        // ever being processed from j6-t2 via j2-t3.

        n.getT("j0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j2", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j2", 3).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j4", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j6", 3).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c7", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("j8", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c9", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c9", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c10", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c10", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j11", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j11", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c12", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c12", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun ignoresPhasePathing() {
        //
        // j0 11--c1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1, nominalPhases = PhaseCode.AB) // j0
            .toAcls(nominalPhases = PhaseCode.B) // c1
            .toAcls(nominalPhases = PhaseCode.A) // c2
            .network

        doSetDirectionTrace(n.getT("j0", 1))

        n.getT("j0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c2", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun worksWithoutPhase() {
        //
        // j0 11--c1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromJunction(numTerminals = 1, nominalPhases = PhaseCode.NONE) // j0
            .toAcls(nominalPhases = PhaseCode.NONE) // c1
            .toBreaker(PhaseCode.NONE, isNormallyOpen = true)
            .toAcls(nominalPhases = PhaseCode.NONE) // c2
            .network

        doSetDirectionTrace(n.getT("j0", 1))

        n.getT("j0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b2", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("b2", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(NONE, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(NONE, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun handlesMultiFeeds() {
        //
        // j0 --c1-- --c2-- j3
        //          |
        //          c4
        //          |
        //           --c5--
        //
        val n = TestNetworkBuilder()
            .fromJunction(PhaseCode.A, 1) // j0
            .toAcls(PhaseCode.A) // c1
            .toAcls(PhaseCode.A) // c2
            .toJunction(PhaseCode.A, 1) // j3
            .branchFrom("c1")
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .addFeeder("j0")
            .addFeeder("j3")
            .build()

        n.getT("j0", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c2", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c2", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("j3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c4", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c4", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c5", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c5", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun `ensure there are no exponential blowouts in the loop processing`() {
        //
        // NOTE: This test was added to ensure the loop processing wasn't blowing out when there are many nested loops. The original
        //       fix was to prevent reprocessing of the loop, but this caused parts of the loop to be "damaged", so now we allow a
        //       single re-pass which fixes this, without the exponential complexity increase of the original version.
        //
        val builder = TestNetworkBuilder()
            .fromSource() // s0
            .toJunction(numTerminals = 3) // j1
            .addFeeder("s0")

        // A loop of 10 solved quickly, 11 was slower but still acceptable, 20 was unworkable, so if 20 passes in the timeout we are happy.
        for (i in 3..20) {
            // Create the most simple loop of all back onto the junction over and over.
            builder.fromAcls()
                .connect("j1", "c$i", 2, 1)
                .connect("j1", "c$i", 3, 2)
        }

        assertTimeoutPreemptively(
            Duration.ofMillis(100),
            message = "If this test times out, you have managed to break things as described in the test note. Go fix it."
        ) {
            Tracing.setDirection().run(builder.network, NetworkStateOperators.NORMAL)
        }
    }

    @Test
    internal fun setsDirectionThroughBusbars() {
        //
        // s0 11--c1--2  1  1--c3--2
        //               o2
        val n = TestNetworkBuilder()
            .fromSource() // s0
            .toAcls() // c1
            .toOther<BusbarSection>(numTerminals = 1) // o2
            .toAcls() // c3
            .network

        doSetDirectionTrace(n.getT("s0", 1))

        n.getT("s0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("o2", 1).validateDirection(CONNECTOR, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun setsDirectionThroughBusbarsDualSource() {
        //
        // s0 11--c1--2  1  1--c3--21 s4
        //               o2
        val n = TestNetworkBuilder()
            .fromSource() // s0
            .toAcls() // c1
            .toOther<BusbarSection>(numTerminals = 1) // o2
            .toAcls() // c3
            .toSource() // s4
            .network

        doSetDirectionTrace(n.getT("s0", 1))
        doSetDirectionTrace(n.getT("s4", 1))

        n.getT("s0", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("o2", 1).validateDirection(CONNECTOR, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("s4", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
    }

    @Test
    internal fun setsDirectionThroughBusbarsWithLoops() {
        //
        //               1----c3---21
        // s0 11--c1--2  1 o2       b4
        //               2----c5---12
        val n = TestNetworkBuilder()
            .fromSource() // s0
            .toAcls() // c1
            .toOther<BusbarSection>(numTerminals = 1) // o2
            .toAcls() // c3
            .toBreaker() // b4
            .toAcls() // c5
            .connect("c5", "o2", 2, 1)
            .network

        doSetDirectionTrace(n.getT("s0", 1))

        n.getT("s0", 1).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(UPSTREAM, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(DOWNSTREAM, NetworkStateOperators.NORMAL)
        n.getT("o2", 1).validateDirection(CONNECTOR, NetworkStateOperators.NORMAL)
        n.getT("c3", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c3", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("b4", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("b4", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 1).validateDirection(BOTH, NetworkStateOperators.NORMAL)
        n.getT("c5", 2).validateDirection(BOTH, NetworkStateOperators.NORMAL)
    }

    private fun doSetDirectionTrace(terminal: Terminal) {
        SetDirection().apply {
            run(terminal, NetworkStateOperators.NORMAL)
            run(terminal, NetworkStateOperators.CURRENT)
        }
        DirectionLogger.trace(terminal.conductingEquipment!!)
    }

    private fun doSetDirectionTrace(n: NetworkService) {
        val setDirection = SetDirection()
        n.sequenceOf<Feeder>().forEach {
            setDirection.run(it.normalHeadTerminal!!, NetworkStateOperators.NORMAL)
            setDirection.run(it.normalHeadTerminal!!, NetworkStateOperators.CURRENT)
            DirectionLogger.trace(it.normalHeadTerminal!!.conductingEquipment!!)
        }
    }

}
