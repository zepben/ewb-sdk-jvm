/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseValidator.validatePhases
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class RemovePhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val removePhases = RemovePhases(debugLogger = null)

    //
    // s0 --c1-- --c2--
    //           \-c3--
    //
    // s4 --c5--
    //
    private var n = TestNetworkBuilder()
        .fromSource(PhaseCode.ABCN) // s0
        .toAcls(PhaseCode.ABCN) // c1
        .toAcls(PhaseCode.ABCN) // c2
        .branchFrom("c1")
        .toAcls(PhaseCode.AB) // c3
        .fromSource(PhaseCode.ABCN) // s4
        .toAcls(PhaseCode.ABCN) // c5
        .build()

    @AfterEach
    internal fun afterEach() {
        PhaseLogger.trace(n.listOf<EnergySource>())
    }

    @Test
    internal fun `validate base network`() {
        // This is probably a pointless test, but it used to be rolled into a BeforeEach block and is now only executed once.
        validatePhases(n, "s0", PhaseCode.ABCN)
        validatePhases(n, "c1", PhaseCode.ABCN, PhaseCode.ABCN)
        validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
        validatePhases(n, "c3", PhaseCode.AB, PhaseCode.AB)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun removesAllCoreByDefault() {
        removePhases.run(n.getT("c1", 2), NetworkStateOperators.NORMAL)
        removePhases.run(n.getT("c1", 2), NetworkStateOperators.CURRENT)

        validatePhases(n, "s0", PhaseCode.ABCN)
        validatePhases(n, "c1", PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n, "c2", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun canRemoveSpecificPhases() {
        removePhases.run(n.getT("s0", 1), PhaseCode.AB, NetworkStateOperators.NORMAL)
        removePhases.run(n.getT("s0", 1), PhaseCode.AB, NetworkStateOperators.CURRENT)

        validatePhases(n, "s0", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c1", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c2", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun canRemoveFromEntireNetwork() {
        removePhases.run(n, NetworkStateOperators.CURRENT)

        validatePhases(n.getT("s0", 1), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c1", 1), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c1", 2), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c2", 1), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c2", 2), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c3", 1), PhaseCode.AB, PhaseCode.NONE)
        validatePhases(n.getT("c3", 2), PhaseCode.AB, PhaseCode.NONE)
        validatePhases(n.getT("s4", 1), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c5", 1), PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n.getT("c5", 2), PhaseCode.ABCN, PhaseCode.NONE)
    }

    @Test
    internal fun `stops at open points`() {
        //
        // s0 11--c1--21 b2 21--c3--21 s4
        //
        n = TestNetworkBuilder()
            .fromSource() // s0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .toSource() // s4
            .build()

        RemovePhases(debugLogger = null).run(n["s0-t1"]!!, NetworkStateOperators.NORMAL)
        RemovePhases(debugLogger = null).run(n["s0-t1"]!!, NetworkStateOperators.CURRENT)

        validatePhases(n, "s0", PhaseCode.NONE)
        validatePhases(n, "c1", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "b2", PhaseCode.NONE, PhaseCode.ABC)
        validatePhases(n, "c3", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(n, "s4", PhaseCode.ABC)
    }

    @Test
    internal fun `revisits equipment when ebbing different phases`() {
        // This test was added when we stopped using a branching trace to ensure it still revisits items when removing different phases.
        //
        //               1------c7-----2
        //               3             3
        // s0 11--c1--21 j2 41--c3--21 j4 41--c5--
        //               2             2
        //               1------c6-----2
        //
        // If c5 isn't revisited it will have phases still as c3, c6 and c7 are only single phase lines,
        //
        n = TestNetworkBuilder()
            .fromSource() // s0
            .toAcls() // c1
            .toJunction(numTerminals = 4) // j2
            .toAcls(nominalPhases = PhaseCode.A) // c3
            .toJunction(numTerminals = 4) // j4
            .toAcls() // c5
            .fromAcls(nominalPhases = PhaseCode.B) // c6
            .connect("j2", "c6", 2, 1)
            .connect("j4", "c6", 2, 2)
            .fromAcls(nominalPhases = PhaseCode.C) // c7
            .connect("j2", "c6", 2, 1)
            .connect("j4", "c6", 2, 2)
            .build()

        RemovePhases(debugLogger = null).run(n["s0-t1"]!!, NetworkStateOperators.NORMAL)
        RemovePhases(debugLogger = null).run(n["s0-t1"]!!, NetworkStateOperators.CURRENT)

        validatePhases(n, "s0", PhaseCode.NONE)
        validatePhases(n, "c1", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "j2", PhaseCode.NONE, PhaseCode.NONE, PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "j4", PhaseCode.NONE, PhaseCode.NONE, PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c5", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c6", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c7", PhaseCode.NONE, PhaseCode.NONE)
    }

    private fun NetworkService.getT(ce: String, t: Int): Terminal =
        get<ConductingEquipment>(ce)!!.getTerminal(t)!!

}
