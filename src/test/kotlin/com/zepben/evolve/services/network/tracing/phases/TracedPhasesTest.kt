/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class TracedPhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val tracedPhases = TracedPhases(terminal = Terminal().apply { phases = PhaseCode.ABCN })

    @Test
    internal fun testSetAndGet() {
        /* -- Setting -- */
        assertThat("Should return true when setting normal traced phase of A to N", tracedPhases.setNormal(SPK.A, SPK.N))
        assertThat("Should return true when setting normal traced phase of B to C", tracedPhases.setNormal(SPK.B, SPK.C))
        assertThat("Should return true when setting normal traced phase of C to B", tracedPhases.setNormal(SPK.C, SPK.B))
        assertThat("Should return true when setting normal traced phase of N to A", tracedPhases.setNormal(SPK.N, SPK.A))

        assertThat("Should return true when setting current traced phase of A to A", tracedPhases.setCurrent(SPK.A, SPK.A))
        assertThat("Should return true when setting current traced phase of B to B", tracedPhases.setCurrent(SPK.B, SPK.B))
        assertThat("Should return true when setting current traced phase of C to C", tracedPhases.setCurrent(SPK.C, SPK.C))
        assertThat("Should return true when setting current traced phase of N to N", tracedPhases.setCurrent(SPK.N, SPK.N))

        /* -- Getting Phase-- */
        assertThat(tracedPhases.normal[SPK.A], equalTo(SPK.N))
        assertThat(tracedPhases.normal[SPK.B], equalTo(SPK.C))
        assertThat(tracedPhases.normal[SPK.C], equalTo(SPK.B))
        assertThat(tracedPhases.normal[SPK.N], equalTo(SPK.A))

        assertThat(tracedPhases.current[SPK.A], equalTo(SPK.A))
        assertThat(tracedPhases.current[SPK.B], equalTo(SPK.B))
        assertThat(tracedPhases.current[SPK.C], equalTo(SPK.C))
        assertThat(tracedPhases.current[SPK.N], equalTo(SPK.N))

        /* -- Setting Unchanged -- */
        assertThat("Should return false when attempting to set already-set normal traced phase of A", !tracedPhases.setNormal(SPK.A, SPK.N))
        assertThat("Should return false when attempting to set already-set normal traced phase of B", !tracedPhases.setNormal(SPK.B, SPK.C))
        assertThat("Should return false when attempting to set already-set normal traced phase of C", !tracedPhases.setNormal(SPK.C, SPK.B))
        assertThat("Should return false when attempting to set already-set normal traced phase of N", !tracedPhases.setNormal(SPK.N, SPK.A))

        assertThat("Should return false when attempting to set already-set current traced phase of A", !tracedPhases.setCurrent(SPK.A, SPK.A))
        assertThat("Should return false when attempting to set already-set current traced phase of B", !tracedPhases.setCurrent(SPK.B, SPK.B))
        assertThat("Should return false when attempting to set already-set current traced phase of C", !tracedPhases.setCurrent(SPK.C, SPK.C))
        assertThat("Should return false when attempting to set already-set current traced phase of N", !tracedPhases.setCurrent(SPK.N, SPK.N))
    }

    @Test
    internal fun testInvalidNominalPhaseNormal() {
        expect { tracedPhases.normal[SPK.INVALID] }
            .toThrow<IllegalArgumentException>()
            .withMessage("INTERNAL ERROR: Phase INVALID is invalid.")
    }

    @Test
    internal fun testCrossingPhasesExceptionNormal() {
        expect {
            tracedPhases.setNormal(SPK.A, SPK.A)
            tracedPhases.setNormal(SPK.A, SPK.B)
        }.toThrow<UnsupportedOperationException>()
            .withMessage("Crossing Phases.")
    }

    @Test
    internal fun testInvalidNominalPhaseCurrent() {
        expect { tracedPhases.current[SPK.INVALID] }
            .toThrow<IllegalArgumentException>()
    }

    @Test
    internal fun testCrossingPhasesExceptionCurrent() {
        expect {
            tracedPhases.setCurrent(SPK.A, SPK.A)
            tracedPhases.setCurrent(SPK.A, SPK.B)
        }.toThrow<UnsupportedOperationException>()
    }

}
