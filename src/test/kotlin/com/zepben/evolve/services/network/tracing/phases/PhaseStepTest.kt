/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.testutils.junit.SystemLogExtension
import com.zepben.testutils.mockito.DefaultAnswer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class PhaseStepTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun accessors() {
        val asset = mock<ConductingEquipment>()
        val ps = PhaseStep.startAt(asset, listOf(SPK.A, SPK.B, SPK.C, SPK.N, SPK.B, SPK.C, SPK.N))

        assertThat(ps.conductingEquipment, equalTo(asset))
        assertThat(ps.phases, containsInAnyOrder(SPK.A, SPK.B, SPK.C, SPK.N))
    }

    @Test
    internal fun coverage() {
        val asset1 = mock<ConductingEquipment>(defaultAnswer = DefaultAnswer.of(String::class.java, "asset1"))
        val asset2 = mock<ConductingEquipment>(defaultAnswer = DefaultAnswer.of(String::class.java, "asset2"))

        val ps1 = PhaseStep.startAt(asset1, listOf(SPK.A, SPK.B, SPK.C, SPK.N, SPK.B, SPK.C, SPK.N))
        val ps1Dup = PhaseStep.startAt(asset1, listOf(SPK.A, SPK.B, SPK.C, SPK.N, SPK.B, SPK.C, SPK.N))
        val ps2 = PhaseStep.startAt(asset2, listOf(SPK.A, SPK.B, SPK.C, SPK.N, SPK.B, SPK.C, SPK.N))
        val ps3 = PhaseStep.startAt(asset1, listOf(SPK.A, SPK.B))

        assertThat(ps1, equalTo(ps1))
        assertThat(ps1, equalTo(ps1Dup))
        assertThat(ps1, not(equalTo(null)))
        assertThat(ps1, not(equalTo(ps2)))
        assertThat(ps1, not(equalTo(ps3)))

        assertThat(ps1.hashCode(), equalTo(ps1Dup.hashCode()))

        assertThat(ps1.toString(), not(emptyString()))
    }

}
