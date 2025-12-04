/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class PhaseStatusTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun setAndGetNominalMatchesTraced() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        /* -- Setting -- */
        assertThat("Should return true when setting traced phase of A to A", phaseStatus.set(SPK.A, SPK.A))
        assertThat("Should return true when setting traced phase of B to B", phaseStatus.set(SPK.B, SPK.B))
        assertThat("Should return true when setting traced phase of C to C", phaseStatus.set(SPK.C, SPK.C))
        assertThat("Should return true when setting traced phase of N to N", phaseStatus.set(SPK.N, SPK.N))

        /* -- Getting -- */
        assertThat(phaseStatus[SPK.A], equalTo(SPK.A))
        assertThat(phaseStatus[SPK.B], equalTo(SPK.B))
        assertThat(phaseStatus[SPK.C], equalTo(SPK.C))
        assertThat(phaseStatus[SPK.N], equalTo(SPK.N))

        /* -- Setting Unchanged -- */
        assertThat("Should return false when attempting to set already-set current traced phase of A", !phaseStatus.set(SPK.A, SPK.A))
        assertThat("Should return false when attempting to set already-set current traced phase of B", !phaseStatus.set(SPK.B, SPK.B))
        assertThat("Should return false when attempting to set already-set current traced phase of C", !phaseStatus.set(SPK.C, SPK.C))
        assertThat("Should return false when attempting to set already-set current traced phase of N", !phaseStatus.set(SPK.N, SPK.N))
    }

    @Test
    internal fun setAndGetNominalDoesNotMatchTraced() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        /* -- Setting -- */
        assertThat("Should return true when setting traced phase of A to N", phaseStatus.set(SPK.A, SPK.N))
        assertThat("Should return true when setting traced phase of B to C", phaseStatus.set(SPK.B, SPK.C))
        assertThat("Should return true when setting traced phase of C to B", phaseStatus.set(SPK.C, SPK.B))
        assertThat("Should return true when setting traced phase of N to A", phaseStatus.set(SPK.N, SPK.A))

        /* -- Getting -- */
        assertThat(phaseStatus[SPK.A], equalTo(SPK.N))
        assertThat(phaseStatus[SPK.B], equalTo(SPK.C))
        assertThat(phaseStatus[SPK.C], equalTo(SPK.B))
        assertThat(phaseStatus[SPK.N], equalTo(SPK.A))

        /* -- Setting Unchanged -- */
        assertThat("Should return false when attempting to set already-set normal traced phase of A", !phaseStatus.set(SPK.A, SPK.N))
        assertThat("Should return false when attempting to set already-set normal traced phase of B", !phaseStatus.set(SPK.B, SPK.C))
        assertThat("Should return false when attempting to set already-set normal traced phase of C", !phaseStatus.set(SPK.C, SPK.B))
        assertThat("Should return false when attempting to set already-set normal traced phase of N", !phaseStatus.set(SPK.N, SPK.A))
    }

    @Test
    internal fun phaseCodeThree() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.NONE))

        phaseStatus[SPK.A] = SPK.A
        assertThat(phaseStatus.asPhaseCode(), nullValue())

        phaseStatus[SPK.B] = SPK.B
        phaseStatus[SPK.C] = SPK.C
        phaseStatus[SPK.N] = SPK.N
        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.ABCN))
    }

    @Test
    internal fun phaseCodeSingle() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.BC }
        val phaseStatus = PhaseStatus(terminal)

        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.NONE))

        phaseStatus[SPK.B] = SPK.B
        assertThat(phaseStatus.asPhaseCode(), nullValue())

        phaseStatus[SPK.C] = SPK.C
        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.BC))
    }

    @Test
    internal fun phaseCodeNone() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.NONE))

        phaseStatus[SPK.A] = SPK.A
        assertThat(phaseStatus.asPhaseCode(), nullValue())

        phaseStatus[SPK.B] = SPK.B
        assertThat(phaseStatus.asPhaseCode(), nullValue())

        phaseStatus[SPK.C] = SPK.C
        assertThat(phaseStatus.asPhaseCode(), nullValue())

        phaseStatus[SPK.N] = SPK.N
        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.ABCN))
    }

    @Test
    internal fun asPhaseCodeHandlesChangingTerminalPhases() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABN }
        val phaseStatus = PhaseStatus(terminal)

        phaseStatus[SPK.A] = SPK.A
        phaseStatus[SPK.B] = SPK.B
        phaseStatus[SPK.C] = SPK.C
        phaseStatus[SPK.N] = SPK.N

        assertThat(phaseStatus[SinglePhaseKind.A], equalTo(SPK.A))
        assertThat(phaseStatus[SinglePhaseKind.B], equalTo(SPK.B))
        assertThat(phaseStatus[SinglePhaseKind.C], equalTo(SPK.C))
        assertThat(phaseStatus[SinglePhaseKind.N], equalTo(SPK.N))
        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.ABN))

        terminal.phases = PhaseCode.AC

        assertThat(phaseStatus[SinglePhaseKind.A], equalTo(SPK.A))
        assertThat(phaseStatus[SinglePhaseKind.B], equalTo(SPK.B))
        assertThat(phaseStatus[SinglePhaseKind.C], equalTo(SPK.C))
        assertThat(phaseStatus[SinglePhaseKind.N], equalTo(SPK.N))
        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.AC))

        terminal.phases = PhaseCode.ABN

        assertThat(phaseStatus.asPhaseCode(), equalTo(PhaseCode.ABN))
    }

    @Test
    internal fun asPhaseCodeDoesNotDropPhases() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        phaseStatus[SPK.B] = SPK.A
        phaseStatus[SPK.C] = SPK.A

        assertThat(phaseStatus.asPhaseCode(), nullValue())
    }

    @Test
    internal fun testInvalidNominalPhase() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        ExpectException.expect { phaseStatus[SPK.INVALID] }
            .toThrow<IllegalArgumentException>()
            .withMessage("INTERNAL ERROR: Phase INVALID is invalid.")
    }

    @Test
    internal fun testCrossingPhasesException() {
        val terminal = Terminal(generateId()).apply { phases = PhaseCode.ABCN }
        val phaseStatus = PhaseStatus(terminal)

        ExpectException.expect {
            phaseStatus[SPK.A] = SPK.A
            phaseStatus[SPK.A] = SPK.B
        }.toThrow<UnsupportedOperationException>()
            .withMessage("Crossing Phases.")
    }

}
