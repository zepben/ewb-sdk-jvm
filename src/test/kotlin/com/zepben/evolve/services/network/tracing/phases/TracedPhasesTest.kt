/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.testutils.exception.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class TracedPhasesTest {

    @Test
    fun testGetPhase() {
        val tracedPhases = newTestPhaseObject()

        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.A))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.C), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.N), equalTo(SinglePhaseKind.N))

        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.N))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.C), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.N), equalTo(SinglePhaseKind.A))
    }

    @Test
    fun testGetDirection() {
        val tracedPhases = newTestPhaseObject()

        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.UPSTREAM))
    }

    @Test
    fun testSet() {
        val tracedPhases = newTestPhaseObject()

        /* -- Setting -- */
        assertThat(tracedPhases.setNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N), equalTo(true))

        assertThat(tracedPhases.setCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N), equalTo(true))

        // Returns false if no changes were done.
        assertThat(tracedPhases.setNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N), equalTo(false))

        assertThat(tracedPhases.setCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N), equalTo(false))

        /* -- Getting Phase-- */
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.N))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.C), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.N), equalTo(SinglePhaseKind.A))

        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.A))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.C), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.N), equalTo(SinglePhaseKind.N))

        /* -- Getting Direction-- */
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.UPSTREAM))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        /* -- Setting -- */
        assertThat(tracedPhases.setNormal(SinglePhaseKind.N, FeederDirection.UPSTREAM, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.C, FeederDirection.DOWNSTREAM, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.B, FeederDirection.BOTH, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.setNormal(SinglePhaseKind.A, FeederDirection.BOTH, SinglePhaseKind.N), equalTo(true))

        assertThat(tracedPhases.setCurrent(SinglePhaseKind.A, FeederDirection.BOTH, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.B, FeederDirection.BOTH, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.C, FeederDirection.DOWNSTREAM, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.N, FeederDirection.UPSTREAM, SinglePhaseKind.N), equalTo(true))

        /* -- Getting Phase-- */
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.N))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.C), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.N), equalTo(SinglePhaseKind.A))

        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.A))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.C), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.N), equalTo(SinglePhaseKind.N))

        /* -- Getting Direction-- */
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.UPSTREAM))

        // Setting NONE to the direction clears the whole phase
        tracedPhases.setNormal(SinglePhaseKind.N, FeederDirection.NONE, SinglePhaseKind.A)
        tracedPhases.setCurrent(SinglePhaseKind.A, FeederDirection.NONE, SinglePhaseKind.A)

        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.NONE))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.NONE))

        // Setting NONE to the phase clears the whole phase
        assertThat(tracedPhases.setNormal(SinglePhaseKind.NONE, FeederDirection.NONE, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.setCurrent(SinglePhaseKind.NONE, FeederDirection.NONE, SinglePhaseKind.B), equalTo(true))

        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.NONE))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.NONE))
    }

    @Test
    fun testAdd() {
        val tracedPhases = TracedPhases()

        /* -- Adding -- */
        assertThat(tracedPhases.addNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N), equalTo(true))

        assertThat(tracedPhases.addCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N), equalTo(true))

        // Returns false if no changes were done.
        assertThat(tracedPhases.addNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N), equalTo(false))

        assertThat(tracedPhases.addCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N), equalTo(false))

        /* -- Getting Phase-- */
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.N))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.C), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.N), equalTo(SinglePhaseKind.A))

        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.A))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.C), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.N), equalTo(SinglePhaseKind.N))

        /* -- Getting Direction-- */
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.UPSTREAM))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        /* -- Adding -- */
        assertThat(tracedPhases.addNormal(SinglePhaseKind.N, FeederDirection.NONE, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.C, FeederDirection.UPSTREAM, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.B, FeederDirection.UPSTREAM, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.addNormal(SinglePhaseKind.A, FeederDirection.DOWNSTREAM, SinglePhaseKind.N), equalTo(true))

        assertThat(tracedPhases.addCurrent(SinglePhaseKind.A, FeederDirection.DOWNSTREAM, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.B, FeederDirection.UPSTREAM, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.C, FeederDirection.NONE, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.addCurrent(SinglePhaseKind.N, FeederDirection.DOWNSTREAM, SinglePhaseKind.N), equalTo(false))

        /* -- Getting Phase-- */
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.A), equalTo(SinglePhaseKind.N))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.B), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.C), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseNormal(SinglePhaseKind.N), equalTo(SinglePhaseKind.A))

        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.A), equalTo(SinglePhaseKind.A))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.B), equalTo(SinglePhaseKind.B))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.C), equalTo(SinglePhaseKind.C))
        assertThat(tracedPhases.phaseCurrent(SinglePhaseKind.N), equalTo(SinglePhaseKind.N))

        /* -- Getting Direction-- */
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.BOTH))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.BOTH))

        expect { tracedPhases.addNormal(SinglePhaseKind.A, FeederDirection.BOTH, SinglePhaseKind.A) }
            .toThrow(UnsupportedOperationException::class.java)
    }

    @Test
    fun testRemoveDirection() {
        val tracedPhases = newTestPhaseObject()

        tracedPhases.addNormal(SinglePhaseKind.A, FeederDirection.DOWNSTREAM, SinglePhaseKind.A)
        tracedPhases.addNormal(SinglePhaseKind.B, FeederDirection.UPSTREAM, SinglePhaseKind.B)

        tracedPhases.addCurrent(SinglePhaseKind.B, FeederDirection.UPSTREAM, SinglePhaseKind.C)
        tracedPhases.addCurrent(SinglePhaseKind.A, FeederDirection.DOWNSTREAM, SinglePhaseKind.N)

        tracedPhases.removeNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A)
        tracedPhases.removeNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B)
        tracedPhases.removeNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C)
        tracedPhases.removeNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N)

        tracedPhases.removeCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A)
        tracedPhases.removeCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B)
        tracedPhases.removeCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C)
        tracedPhases.removeCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N)

        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.DOWNSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.NONE))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.UPSTREAM))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.DOWNSTREAM))

        assertThat(tracedPhases.removeNormal(SinglePhaseKind.A, SinglePhaseKind.A), equalTo(true))
        assertThat(tracedPhases.removeNormal(SinglePhaseKind.B, SinglePhaseKind.B), equalTo(true))
        assertThat(tracedPhases.removeNormal(SinglePhaseKind.C, SinglePhaseKind.C), equalTo(false))
        assertThat(tracedPhases.removeNormal(SinglePhaseKind.N, SinglePhaseKind.N), equalTo(false))

        assertThat(tracedPhases.removeCurrent(SinglePhaseKind.N, SinglePhaseKind.A), equalTo(false))
        assertThat(tracedPhases.removeCurrent(SinglePhaseKind.C, SinglePhaseKind.B), equalTo(false))
        assertThat(tracedPhases.removeCurrent(SinglePhaseKind.B, SinglePhaseKind.C), equalTo(true))
        assertThat(tracedPhases.removeCurrent(SinglePhaseKind.A, SinglePhaseKind.N), equalTo(true))

        assertThat(tracedPhases.directionNormal(SinglePhaseKind.A), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.B), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.C), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionNormal(SinglePhaseKind.N), equalTo(FeederDirection.NONE))

        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.A), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.B), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.C), equalTo(FeederDirection.NONE))
        assertThat(tracedPhases.directionCurrent(SinglePhaseKind.N), equalTo(FeederDirection.NONE))
    }

    @Test
    fun testInvalidCoreNormal() {
        expect { newTestPhaseObject().phaseNormal(SinglePhaseKind.INVALID) }
            .toThrow(IllegalArgumentException::class.java)
    }

    @Test
    fun testCrossingPhasesExceptionNormal() {
        expect { newTestPhaseObject().addNormal(SinglePhaseKind.B, FeederDirection.BOTH, SinglePhaseKind.A) }
            .toThrow(UnsupportedOperationException::class.java)
    }

    @Test
    fun testInvalidCoreCurrent() {
        expect { newTestPhaseObject().phaseCurrent(SinglePhaseKind.INVALID) }
            .toThrow(IllegalArgumentException::class.java)
    }

    @Test
    fun testCrossingPhasesExceptionCurrent() {
        expect { newTestPhaseObject().addCurrent(SinglePhaseKind.B, FeederDirection.BOTH, SinglePhaseKind.A) }
            .toThrow(UnsupportedOperationException::class.java)
    }

    @Test
    fun removingSomethingNotPresentNormal() {
        val tracedPhases = newTestPhaseObject()
        assertThat(tracedPhases.removeNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.A), equalTo(false))
    }

    @Test
    fun removingSomethingNotPresentCurrent() {
        val tracedPhases = newTestPhaseObject()
        assertThat(tracedPhases.removeCurrent(SinglePhaseKind.A, FeederDirection.DOWNSTREAM, SinglePhaseKind.N), equalTo(false))
    }

    private fun newTestPhaseObject() = TracedPhases().apply {
        setNormal(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.A)
        setNormal(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.B)
        setNormal(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.C)
        setNormal(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.N)

        setCurrent(SinglePhaseKind.N, FeederDirection.BOTH, SinglePhaseKind.A)
        setCurrent(SinglePhaseKind.C, FeederDirection.BOTH, SinglePhaseKind.B)
        setCurrent(SinglePhaseKind.B, FeederDirection.DOWNSTREAM, SinglePhaseKind.C)
        setCurrent(SinglePhaseKind.A, FeederDirection.UPSTREAM, SinglePhaseKind.N)
    }

}
