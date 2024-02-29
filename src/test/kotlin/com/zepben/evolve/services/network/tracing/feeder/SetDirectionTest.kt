/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:Suppress("PropertyName")

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.tracing.feeder.DirectionValidator.validateDirections
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SetDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun setDirectionTest() {
        val n = PhaseSwapLoopNetwork.create()

        doSetDirectionTrace(n)

        checkExpectedDirection(n.getT("acLineSegment0", 1), UPSTREAM)
        checkExpectedDirection(n.getT("acLineSegment0", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("acLineSegment1", 1), UPSTREAM)
        checkExpectedDirection(n.getT("acLineSegment4", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j4", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j4", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("j4", 3), DOWNSTREAM)
        checkExpectedDirection(n.getT("j8", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j5", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j5", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("j5", 3), DOWNSTREAM)
        checkExpectedDirection(n.getT("j9", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j6", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j6", 2), UPSTREAM)
        checkExpectedDirection(n.getT("acLineSegment2", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("acLineSegment3", 1), UPSTREAM)
        checkExpectedDirection(n.getT("acLineSegment9", 2), UPSTREAM)
        checkExpectedDirection(n.getT("j2", 1), UPSTREAM)
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

        SetDirection().run(n.getT("c0", 2))
        DirectionLogger.trace(n["c0"])

        n.getT("c0", 1).validateDirections(NONE)
        n.getT("c0", 2).validateDirections(DOWNSTREAM)
        n.getT("b1", 1).validateDirections(UPSTREAM)
        n.getT("b1", 2).validateDirections(NONE, DOWNSTREAM)
        n.getT("c2", 1).validateDirections(NONE, UPSTREAM)
        n.getT("c2", 2).validateDirections(NONE, DOWNSTREAM)
        n.getT("b3", 1).validateDirections(UPSTREAM)
        n.getT("b3", 2).validateDirections(DOWNSTREAM, NONE)
        n.getT("c4", 1).validateDirections(UPSTREAM, NONE)
        n.getT("c4", 2).validateDirections(DOWNSTREAM, NONE)
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

        n.getT("c0", 1).validateDirections(NONE)
        n.getT("c0", 2).validateDirections(NONE)
        n.getT("j1", 1).validateDirections(NONE)
        n.getT("j1", 2).validateDirections(BOTH)
        n.getT("c2", 1).validateDirections(BOTH)
        n.getT("c2", 2).validateDirections(BOTH)
        n.getT("j3", 1).validateDirections(BOTH)
        n.getT("j3", 2).validateDirections(NONE)
        n.getT("c4", 1).validateDirections(NONE)
        n.getT("c4", 2).validateDirections(NONE)
    }

    @Test
    internal fun `doesn't trace from open feeder heads`() {
        //
        // 1 b0 21--c1--21--c2--21 b3 2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toAcls() // c2
            .toBreaker(isNormallyOpen = true) // b3
            .addFeeder("b0", 2)
            .addFeeder("b3", 1)
            .network

        SetDirection().run(n)
        DirectionLogger.trace(n["b0"])

        n.getT("b0", 1).validateDirections(NONE)
        n.getT("b0", 2).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("c2", 1).validateDirections(UPSTREAM)
        n.getT("c2", 2).validateDirections(DOWNSTREAM)
        n.getT("b3", 1).validateDirections(UPSTREAM)
        n.getT("b3", 2).validateDirections(NONE)
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

        n.getT("b0", 1).validateDirections(NONE)
        n.getT("b0", 2).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("tx2", 1).validateDirections(UPSTREAM)
        n.getT("tx2", 2).validateDirections(NONE)
        n.getT("c3", 1).validateDirections(NONE)
        n.getT("c3", 2).validateDirections(NONE)
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

        checkExpectedDirection(n.getT("s0", 1), DOWNSTREAM)
        checkExpectedDirection(n.getT("c1", 1), UPSTREAM)
        checkExpectedDirection(n.getT("c1", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("j2", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j2", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("c3", 1), BOTH)
        checkExpectedDirection(n.getT("c3", 2), BOTH)
        checkExpectedDirection(n.getT("j4", 1), BOTH)
        checkExpectedDirection(n.getT("j4", 2), BOTH)
        checkExpectedDirection(n.getT("c5", 1), BOTH)
        checkExpectedDirection(n.getT("c5", 2), BOTH)
        checkExpectedDirection(n.getT("j6", 1), UPSTREAM)
        checkExpectedDirection(n.getT("j6", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("c7", 1), UPSTREAM)
        checkExpectedDirection(n.getT("c7", 2), DOWNSTREAM)
        checkExpectedDirection(n.getT("j8", 1), UPSTREAM)
        checkExpectedDirection(n.getT("c9", 1), BOTH)
        checkExpectedDirection(n.getT("c9", 2), BOTH)
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

        SetDirection().run(n.getT("j0", 1))
        DirectionLogger.trace(n["j0"])

        // To avoid reprocessing all BOTH loops in larger networks we do not process anything with a direction already set. This means this test will apply
        // a standard UP/DOWN path through j2-t2 through to j6-t2 and then a BOTH loop around the c9/j4 loop which will stop the reverse UP/DOWN path
        // ever being processed from j6-t2 via j2-t3.

        n.getT("j0", 1).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("j2", 1).validateDirections(UPSTREAM)
        n.getT("j2", 2).validateDirections(DOWNSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j2", 3).validateDirections(BOTH)
        n.getT("c3", 1).validateDirections(BOTH)
        n.getT("c3", 2).validateDirections(BOTH)
        n.getT("j4", 1).validateDirections(BOTH)
        n.getT("j4", 2).validateDirections(BOTH)
        n.getT("c5", 1).validateDirections(BOTH)
        n.getT("c5", 2).validateDirections(BOTH)
        n.getT("j6", 1).validateDirections(DOWNSTREAM)  // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j6", 2).validateDirections(UPSTREAM)  // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j6", 3).validateDirections(DOWNSTREAM)
        n.getT("c7", 1).validateDirections(UPSTREAM)
        n.getT("c7", 2).validateDirections(DOWNSTREAM)
        n.getT("j8", 1).validateDirections(UPSTREAM)
        n.getT("c9", 1).validateDirections(BOTH)
        n.getT("c9", 2).validateDirections(BOTH)
        n.getT("c10", 1).validateDirections(UPSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("c10", 2).validateDirections(DOWNSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j11", 1).validateDirections(UPSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j11", 2).validateDirections(DOWNSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("c12", 1).validateDirections(UPSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("c12", 2).validateDirections(DOWNSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
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

        SetDirection().run(n.getT("j0", 1))
        DirectionLogger.trace(n["j0"])

        // To avoid reprocessing all BOTH loops in larger networks we do not process anything with a direction already set. This means this test will apply
        // a UP/DOWN path through j2-t2 directly into a BOTH loop around the c9/j11 loop which will stop the reverse UP/DOWN path
        // ever being processed from j6-t2 via j2-t3.

        n.getT("j0", 1).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("j2", 1).validateDirections(UPSTREAM)
        n.getT("j2", 2).validateDirections(DOWNSTREAM) // Would have been BOTH if the intermediate loop was reprocessed.
        n.getT("j2", 3).validateDirections(BOTH)
        n.getT("c3", 1).validateDirections(BOTH)
        n.getT("c3", 2).validateDirections(BOTH)
        n.getT("j4", 1).validateDirections(BOTH)
        n.getT("j4", 2).validateDirections(BOTH)
        n.getT("c5", 1).validateDirections(BOTH)
        n.getT("c5", 2).validateDirections(BOTH)
        n.getT("j6", 1).validateDirections(BOTH)
        n.getT("j6", 2).validateDirections(BOTH)
        n.getT("j6", 3).validateDirections(DOWNSTREAM)
        n.getT("c7", 1).validateDirections(UPSTREAM)
        n.getT("c7", 2).validateDirections(DOWNSTREAM)
        n.getT("j8", 1).validateDirections(UPSTREAM)
        n.getT("c9", 1).validateDirections(BOTH)
        n.getT("c9", 2).validateDirections(BOTH)
        n.getT("c10", 1).validateDirections(BOTH)
        n.getT("c10", 2).validateDirections(BOTH)
        n.getT("j11", 1).validateDirections(BOTH)
        n.getT("j11", 2).validateDirections(BOTH)
        n.getT("c12", 1).validateDirections(BOTH)
        n.getT("c12", 2).validateDirections(BOTH)
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

        SetDirection().run(n.getT("j0", 1))
        DirectionLogger.trace(n["j0"])

        n.getT("j0", 1).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("c2", 1).validateDirections(UPSTREAM)
        n.getT("c2", 2).validateDirections(DOWNSTREAM)
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

        SetDirection().run(n.getT("j0", 1))
        DirectionLogger.trace(n["j0"])

        n.getT("j0", 1).validateDirections(DOWNSTREAM)
        n.getT("c1", 1).validateDirections(UPSTREAM)
        n.getT("c1", 2).validateDirections(DOWNSTREAM)
        n.getT("b2", 1).validateDirections(UPSTREAM)
        n.getT("b2", 2).validateDirections(NONE)
        n.getT("c3", 1).validateDirections(NONE)
        n.getT("c3", 2).validateDirections(NONE)
    }

    private fun doSetDirectionTrace(n: NetworkService) {
        SetDirection().run(n)
        n.sequenceOf<Feeder>().forEach { DirectionLogger.trace(it.normalHeadTerminal!!.conductingEquipment!!) }
    }

    private fun NetworkService.getT(id: String, terminalId: Int) =
        get<ConductingEquipment>(id)!!.getTerminal(terminalId)!!

    private fun checkExpectedDirection(t: Terminal, normalDirection: FeederDirection, currentDirection: FeederDirection = normalDirection) {
        checkExpectedDirection(t, normalDirection, DirectionSelector.NORMAL_DIRECTION)
        checkExpectedDirection(t, currentDirection, DirectionSelector.CURRENT_DIRECTION)
    }

    private fun checkExpectedDirection(t: Terminal, direction: FeederDirection, directionSelector: DirectionSelector) {
        assertThat(directionSelector.select(t).value, equalTo(direction))
    }

}
