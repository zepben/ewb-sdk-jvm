/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConnectivityResultTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val asset1: ConductingEquipment = Junction("asset1").apply {
        name = "asset 1"
        addTerminal(Terminal().apply { phases = PhaseCode.A })
        addTerminal(Terminal().apply { phases = PhaseCode.A })
    }

    private val asset2: ConductingEquipment = Junction("asset2").apply {
        name = "asset 2"
        addTerminal(Terminal().apply { phases = PhaseCode.A })
        addTerminal(Terminal().apply { phases = PhaseCode.A })
    }

    private val terminal11 = asset1.t1
    private val terminal12 = asset1.t2
    private val terminal21 = asset2.t1

    @Test
    internal fun accessors() {
        val expectedPhaseMap =
            setOf(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A), NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.X))
        val cr = ConnectivityResult.between(
            terminal11,
            terminal21,
            expectedPhaseMap
        )

        assertThat(cr.from, equalTo(asset1))
        assertThat(cr.fromTerminal, equalTo(terminal11))
        assertThat(cr.to, equalTo(asset2))
        assertThat(cr.toTerminal, equalTo(terminal21))
        assertThat(cr.fromNominalPhases, containsInAnyOrder(SinglePhaseKind.A, SinglePhaseKind.B))
        assertThat(cr.toNominalPhases, containsInAnyOrder(SinglePhaseKind.A, SinglePhaseKind.X))
        assertThat(cr.nominalPhasePaths, containsInAnyOrder(*expectedPhaseMap.toTypedArray()))
    }

    @Test
    internal fun coverage() {
        val cr1 = ConnectivityResult.between(terminal11, terminal21, listOf(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)))
        val cr1Dup = ConnectivityResult.between(terminal11, terminal21, listOf(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)))
        val cr2 = ConnectivityResult.between(terminal11, terminal21, listOf(NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.B)))
        val cr3 = ConnectivityResult.between(terminal11, terminal12, listOf(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)))
        val cr4 = ConnectivityResult.between(terminal21, terminal11, listOf(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)))

        assertThat(cr1, equalTo(cr1))
        assertThat(cr1, equalTo(cr1Dup))
        assertThat(cr1, not(equalTo<ConnectivityResult?>(null)))
        assertThat(cr1, not(equalTo(cr2)))
        assertThat(cr1, not(equalTo(cr3)))
        assertThat(cr1, not(equalTo(cr4)))
        assertThat(cr1.hashCode(), equalTo(cr1Dup.hashCode()))
        assertThat(cr1.toString(), not(emptyString()))
    }
}
