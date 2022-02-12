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
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.phases.PhaseValidator.validatePhases
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

class RemovePhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    //
    // s0 --c1-- --c2--
    //           \-c3--
    //
    // s4 --c5--
    //
    val n = TestNetworkBuilder
        .startWithSource(PhaseCode.ABCN) // s0
        .toAcls(PhaseCode.ABCN) // c1
        .toAcls(PhaseCode.ABCN) // c2
        .splitFrom("c1")
        .toAcls(PhaseCode.AB) // c3
        .fromSource(PhaseCode.ABCN) // s4
        .toAcls(PhaseCode.ABCN) // c5
        .build()

    @BeforeEach
    internal fun beforeEach() {
        PhaseLogger.trace(listOf(n["s0"], n["s4"]))
        validatePhases(n, "s0", PhaseCode.ABCN)
        validatePhases(n, "c1", PhaseCode.ABCN, PhaseCode.ABCN)
        validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
        validatePhases(n, "c3", PhaseCode.AB, PhaseCode.AB)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @AfterEach
    internal fun afterEach() {
        PhaseLogger.trace(listOf(n["s0"], n["s4"]))
    }

    @Test
    internal fun removesAllCoreByDefault() {
        RemovePhases().run(n.getT("c1", 2))

        validatePhases(n, "s0", PhaseCode.ABCN)
        validatePhases(n, "c1", PhaseCode.ABCN, PhaseCode.NONE)
        validatePhases(n, "c2", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun canRemoveSpecificPhases() {
        RemovePhases().run(n.getT("s0", 1), PhaseCode.AB)

        validatePhases(n, "s0", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c1", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c2", listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.NONE, SPK.C, SPK.N))
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "s4", PhaseCode.ABCN)
        validatePhases(n, "c5", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun canRemoveFromEntireNetwork() {
        RemovePhases().run(n)

        validatePhases(n, "s0", PhaseCode.NONE)
        validatePhases(n, "c1", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c2", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "c3", PhaseCode.NONE, PhaseCode.NONE)
        validatePhases(n, "s4", PhaseCode.NONE)
        validatePhases(n, "c5", PhaseCode.NONE, PhaseCode.NONE)
    }

    private fun NetworkService.getT(ce: String, t: Int): Terminal =
        get<ConductingEquipment>(ce)!!.getTerminal(t)!!

}
