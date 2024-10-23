/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.DirectionValidator.validateDirections
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RemoveDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    //
    // j0 --c1-- --c2-- j3
    //
    private val nb = TestNetworkBuilder()
        .fromJunction(PhaseCode.A, 1) // j0
        .toAcls(PhaseCode.A) // c1
        .toAcls(PhaseCode.A) // c2
        .toJunction(PhaseCode.A, 1) // j3

    @Test
    internal fun removesAllDirectionsPresentByDefaultDown() {
        val n = nb
            .addFeeder("j0")
            .buildAndLog()

        n.validateDirections(DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM)

        removeDirections(n.getT("c1", 2))

        DirectionLogger.trace(n["j0"])
        n.validateDirections(DOWNSTREAM, UPSTREAM, NONE, NONE, NONE, NONE)
    }

    @Test
    internal fun removesAllDirectionsPresentByDefaultUp() {
        val n = nb
            .addFeeder("j0")
            .buildAndLog()

        n.validateDirections(DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM)

        removeDirections(n.getT("c2", 1))

        DirectionLogger.trace(n["j0"])
        n.validateDirections(NONE, NONE, NONE, NONE, DOWNSTREAM, UPSTREAM)
    }

    @Test
    internal fun removesAllDirectionsPresentByDefaultBoth() {
        val n = nb
            .addFeeder("j0")
            .addFeeder("j3")
            .buildAndLog()

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)

        removeDirections(n.getT("c1", 2))

        DirectionLogger.trace(n["j0"])
        n.validateDirections(BOTH, BOTH, NONE, NONE, NONE, NONE)
    }

    @Test
    internal fun canRemoveOnlySelectedDirectionsDown() {
        val n = nb
            .addFeeder("j0")
            .addFeeder("j3")
            .buildAndLog()

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)

        removeDirections(n.getT("j0", 1), DOWNSTREAM)

        DirectionLogger.trace(n["j0"])
        n.validateDirections(UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM)
    }

    @Test
    internal fun canRemoveOnlySelectedDirectionsUp() {
        val n = nb
            .addFeeder("j0")
            .addFeeder("j3")
            .buildAndLog()

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)

        removeDirections(n.getT("j0", 1), UPSTREAM)

        DirectionLogger.trace(n["j0"])
        n.validateDirections(DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM)
    }

    @Test
    internal fun respectsMultiFeedsUp() {
        //
        // j0 --c1-- --c2-- j3
        //          |
        //          c4
        //          |
        //           --c5--
        //
        val n = nb
            .branchFrom("c1")
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .addFeeder("j0")
            .addFeeder("j3")
            .buildAndLog()

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)
        n.getT("c4", 1).validateDirections(UPSTREAM)
        n.getT("c4", 2).validateDirections(DOWNSTREAM)
        n.getT("c5", 1).validateDirections(UPSTREAM)
        n.getT("c5", 2).validateDirections(DOWNSTREAM)

        removeDirections(n.getT("c5", 1))
        DirectionLogger.trace(n["j0"])

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)
        n.getT("c4", 1).validateDirections(NONE)
        n.getT("c4", 2).validateDirections(NONE)
        n.getT("c5", 1).validateDirections(NONE)
        n.getT("c5", 2).validateDirections(DOWNSTREAM)
    }

    @Test
    internal fun respectsMultiFeedsDown() {
        //
        // j0 --c1-- --c2-- j3
        //          |
        //          c4
        //          |
        //           --c5--
        //
        val n = nb
            .branchFrom("c1")
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .addFeeder("j0")
            .addFeeder("j3")
            .buildAndLog()

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)
        n.getT("c4", 1).validateDirections(UPSTREAM)
        n.getT("c4", 2).validateDirections(DOWNSTREAM)
        n.getT("c5", 1).validateDirections(UPSTREAM)
        n.getT("c5", 2).validateDirections(DOWNSTREAM)

        removeDirections(n.getT("j0", 1), DOWNSTREAM)
        DirectionLogger.trace(n["j0"])

        n.validateDirections(UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM, UPSTREAM, DOWNSTREAM)
        n.getT("c4", 1).validateDirections(UPSTREAM)
        n.getT("c4", 2).validateDirections(DOWNSTREAM)
        n.getT("c5", 1).validateDirections(UPSTREAM)
        n.getT("c5", 2).validateDirections(DOWNSTREAM)
    }

    @Test
    internal fun respectsMultiFeedsBoth() {
        //
        // j0 --c1-- --c2-- j3
        //          |
        //          c4
        //          |
        //           --c5--
        //
        // j6 --c7--
        //
        val n = nb
            .branchFrom("c1")
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .fromJunction(PhaseCode.A, 1) // j6
            .toAcls(PhaseCode.A) // c7
            .addFeeder("j0")
            .addFeeder("j3")
            .addFeeder("j6")
            .buildAndLog("j0", "j6")

        n.validateDirections(BOTH, BOTH, BOTH, BOTH, BOTH, BOTH)
        n.getT("c4", 1).validateDirections(UPSTREAM)
        n.getT("c4", 2).validateDirections(DOWNSTREAM)
        n.getT("c5", 1).validateDirections(UPSTREAM)
        n.getT("c5", 2).validateDirections(DOWNSTREAM)
        n.getT("j6", 1).validateDirections(DOWNSTREAM)
        n.getT("c7", 1).validateDirections(UPSTREAM)
        n.getT("c7", 2).validateDirections(DOWNSTREAM)

        removeDirections(n.getT("j0", 1), BOTH)
        DirectionLogger.trace(listOf(n["j0"], n["j6"]))

        n.validateDirections(NONE, NONE, NONE, NONE, NONE, NONE)
        n.getT("c4", 1).validateDirections(NONE)
        n.getT("c4", 2).validateDirections(NONE)
        n.getT("c5", 1).validateDirections(NONE)
        n.getT("c5", 2).validateDirections(NONE)
        n.getT("j6", 1).validateDirections(DOWNSTREAM)
        n.getT("c7", 1).validateDirections(UPSTREAM)
        n.getT("c7", 2).validateDirections(DOWNSTREAM)
    }

    @Test
    internal fun respectsMultiFeedsJunction() {
        //
        // j0 12--c1--21 j2 31--c3--21 j4
        //               2
        //               1
        //               |
        //               c5
        //               |
        //               2
        //               1
        //               j6
        //
        val n = TestNetworkBuilder()
            .fromJunction(PhaseCode.A, 1)
            .toAcls(PhaseCode.A) // c1
            .toJunction(PhaseCode.A, 3) // j2
            .toAcls(PhaseCode.A) // c3
            .toJunction(PhaseCode.A, 1) // j4
            .fromAcls(PhaseCode.A) // c5
            .connect("j2", "c5", 2, 1)
            .toJunction(PhaseCode.A, 1) // j6
            .addFeeder("j0")
            .addFeeder("j4")
            .addFeeder("j6")
            .buildAndLog()

        n.getT("j0", 1).validateDirections(BOTH)
        n.getT("j2", 1).validateDirections(BOTH)
        n.getT("j2", 2).validateDirections(BOTH)
        n.getT("j2", 3).validateDirections(BOTH)
        n.getT("j4", 1).validateDirections(BOTH)
        n.getT("j6", 1).validateDirections(BOTH)

        removeDirections(n.getT("j0", 1), DOWNSTREAM)
        DirectionLogger.trace(n["j0"])

        n.getT("j0", 1).validateDirections(UPSTREAM)
        n.getT("j2", 1).validateDirections(DOWNSTREAM)
        n.getT("j2", 2).validateDirections(BOTH)
        n.getT("j2", 3).validateDirections(BOTH)
        n.getT("j4", 1).validateDirections(BOTH)
        n.getT("j6", 1).validateDirections(BOTH)
    }

    private fun removeDirections(terminal: Terminal, feederDirection: FeederDirection = NONE) {
        RemoveDirection(NetworkStateOperators.NORMAL).run(terminal, feederDirection)
        RemoveDirection(NetworkStateOperators.CURRENT).run(terminal, feederDirection)
    }

    private fun NetworkService.validateDirections(
        j0: FeederDirection,
        c1t1: FeederDirection,
        c1t2: FeederDirection,
        c2t1: FeederDirection,
        c2t2: FeederDirection,
        j3: FeederDirection
    ) {
        getT("j0", 1).validateDirections(j0)
        getT("c1", 1).validateDirections(c1t1)
        getT("c1", 2).validateDirections(c1t2)
        getT("c2", 1).validateDirections(c2t1)
        getT("c2", 2).validateDirections(c2t2)
        getT("j3", 1).validateDirections(j3)
    }

    private fun NetworkService.getT(ce: String, t: Int): Terminal =
        get<ConductingEquipment>(ce)!!.getTerminal(t)!!

    private fun TestNetworkBuilder.buildAndLog(vararg logFrom: String) = build().apply {
        if (logFrom.isEmpty())
            DirectionLogger.trace(get("j0"))
        else {
            DirectionLogger.trace(logFrom.map { get(it) })
        }
    }

}
