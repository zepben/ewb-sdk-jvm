/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

class TracedPhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val tracedPhases = TracedPhases()

    @Test
    fun testSetAndGet() {
        /* -- Setting -- */
        assertThat(tracedPhases.setNormal(SPK.A, SPK.N), equalTo(true))
        assertThat(tracedPhases.setNormal(SPK.B, SPK.C), equalTo(true))
        assertThat(tracedPhases.setNormal(SPK.C, SPK.B), equalTo(true))
        assertThat(tracedPhases.setNormal(SPK.N, SPK.A), equalTo(true))

        assertThat(tracedPhases.setCurrent(SPK.A, SPK.A), equalTo(true))
        assertThat(tracedPhases.setCurrent(SPK.B, SPK.B), equalTo(true))
        assertThat(tracedPhases.setCurrent(SPK.C, SPK.C), equalTo(true))
        assertThat(tracedPhases.setCurrent(SPK.N, SPK.N), equalTo(true))

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
        assertThat(tracedPhases.setNormal(SPK.A, SPK.N), equalTo(false))
        assertThat(tracedPhases.setNormal(SPK.B, SPK.C), equalTo(false))
        assertThat(tracedPhases.setNormal(SPK.C, SPK.B), equalTo(false))
        assertThat(tracedPhases.setNormal(SPK.N, SPK.A), equalTo(false))

        assertThat(tracedPhases.setCurrent(SPK.A, SPK.A), equalTo(false))
        assertThat(tracedPhases.setCurrent(SPK.B, SPK.B), equalTo(false))
        assertThat(tracedPhases.setCurrent(SPK.C, SPK.C), equalTo(false))
        assertThat(tracedPhases.setCurrent(SPK.N, SPK.N), equalTo(false))
    }

    @Test
    fun testInvalidNominalPhaseNormal() {
        expect { tracedPhases.normal[SPK.INVALID] }
            .toThrow(IllegalArgumentException::class.java)
            .withMessage("INTERNAL ERROR: Phase INVALID is invalid.")
    }

    @Test
    fun testCrossingPhasesExceptionNormal() {
        expect {
            tracedPhases.setNormal(SPK.A, SPK.A)
            tracedPhases.setNormal(SPK.A, SPK.B)
        }.toThrow(UnsupportedOperationException::class.java)
            .withMessage("Crossing Phases.")
    }

    @Test
    fun testInvalidNominalPhaseCurrent() {
        expect { tracedPhases.current[SPK.INVALID] }
            .toThrow(IllegalArgumentException::class.java)
    }

    @Test
    fun testCrossingPhasesExceptionCurrent() {
        expect {
            tracedPhases.setCurrent(SPK.A, SPK.A)
            tracedPhases.setCurrent(SPK.A, SPK.B)
        }.toThrow(UnsupportedOperationException::class.java)
    }

}
