/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class PhaseSelectorTest {

    private val normal = mock<PhaseStatus>()
    private val current = mock<PhaseStatus>()
    private val tracedPhases = mock<TracedPhases>().also {
        doReturn(normal).`when`(it).normal
        doReturn(current).`when`(it).current
    }
    private val terminal = mock<Terminal>().also { doReturn(tracedPhases).`when`(it).tracedPhases }

    @Test
    fun testNormalPhaseSelectorGet() {
        val ps = PhaseSelector.NORMAL_PHASES.phases(terminal)

        ps[SinglePhaseKind.A]

        verify(terminal).tracedPhases
        verify(tracedPhases).normal
        verify(normal)[SinglePhaseKind.A]

        verifyDone()
    }

    @Test
    fun testNormalPhaseSelectorSet() {
        val ps = PhaseSelector.NORMAL_PHASES.phases(terminal)

        ps[SinglePhaseKind.B] = SinglePhaseKind.C

        verify(terminal).tracedPhases
        verify(tracedPhases).normal
        verify(normal)[SinglePhaseKind.B] = SinglePhaseKind.C

        verifyDone()
    }

    @Test
    fun testCurrentPhaseSelectorGet() {
        val ps = PhaseSelector.CURRENT_PHASES.phases(terminal)

        ps[SinglePhaseKind.X]

        verify(terminal).tracedPhases
        verify(tracedPhases).current
        verify(current)[SinglePhaseKind.X]

        verifyDone()
    }

    @Test
    fun testCurrentPhaseSelectorSet() {
        val ps = PhaseSelector.CURRENT_PHASES.phases(terminal)

        ps[SinglePhaseKind.Y] = SinglePhaseKind.N

        verify(terminal).tracedPhases
        verify(tracedPhases).current
        verify(current)[SinglePhaseKind.Y] = SinglePhaseKind.N

        verifyDone()
    }

    @Test
    fun testAsPhaseCodesThree() {
        val terminal = Terminal().apply { phases = PhaseCode.ABCN }
        val normalPhases = terminal.normalPhases
        val currentPhases = terminal.currentPhases

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.NONE))
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.NONE))

        normalPhases[SinglePhaseKind.A] = SinglePhaseKind.A

        assertThat(normalPhases.asPhaseCode(), nullValue())
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.NONE))

        currentPhases[SinglePhaseKind.A] = SinglePhaseKind.A

        assertThat(normalPhases.asPhaseCode(), nullValue())
        assertThat(currentPhases.asPhaseCode(), nullValue())

        normalPhases[SinglePhaseKind.B] = SinglePhaseKind.B
        normalPhases[SinglePhaseKind.C] = SinglePhaseKind.C
        normalPhases[SinglePhaseKind.N] = SinglePhaseKind.N

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.ABCN))
        assertThat(currentPhases.asPhaseCode(), nullValue())

        currentPhases[SinglePhaseKind.B] = SinglePhaseKind.B
        currentPhases[SinglePhaseKind.C] = SinglePhaseKind.C
        currentPhases[SinglePhaseKind.N] = SinglePhaseKind.N

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.ABCN))
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.ABCN))
    }

    @Test
    fun testAsPhaseCodesSingle() {
        val terminal = Terminal().apply { phases = PhaseCode.BC }
        val normalPhases = terminal.normalPhases
        val currentPhases = terminal.currentPhases

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.NONE))
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.NONE))

        normalPhases[SinglePhaseKind.A] = SinglePhaseKind.A
        currentPhases[SinglePhaseKind.A] = SinglePhaseKind.A

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.NONE))
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.NONE))

        normalPhases[SinglePhaseKind.B] = SinglePhaseKind.B
        currentPhases[SinglePhaseKind.B] = SinglePhaseKind.B

        assertThat(normalPhases.asPhaseCode(), nullValue())
        assertThat(currentPhases.asPhaseCode(), nullValue())

        normalPhases[SinglePhaseKind.C] = SinglePhaseKind.C
        currentPhases[SinglePhaseKind.C] = SinglePhaseKind.C

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.BC))
        assertThat(currentPhases.asPhaseCode(), equalTo(PhaseCode.BC))
    }

    @Test
    fun testAsPhaseCodesChangingTerminalPhases() {
        val terminal = Terminal().apply { phases = PhaseCode.BC }
        val normalPhases = terminal.normalPhases

        normalPhases[SinglePhaseKind.A] = SinglePhaseKind.A
        normalPhases[SinglePhaseKind.B] = SinglePhaseKind.B
        normalPhases[SinglePhaseKind.C] = SinglePhaseKind.C

        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.BC))

        terminal.phases = PhaseCode.AC
        assertThat(normalPhases.asPhaseCode(), equalTo(PhaseCode.AC))
    }

    private fun verifyDone() {
        verifyNoMoreInteractions(terminal)
        verifyNoMoreInteractions(tracedPhases)
        verifyNoMoreInteractions(normal)
        verifyNoMoreInteractions(current)
    }

}
