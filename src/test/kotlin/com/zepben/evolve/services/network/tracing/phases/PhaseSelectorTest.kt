/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
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

    private fun verifyDone() {
        verifyNoMoreInteractions(terminal)
        verifyNoMoreInteractions(tracedPhases)
        verifyNoMoreInteractions(normal)
        verifyNoMoreInteractions(current)
    }

}
