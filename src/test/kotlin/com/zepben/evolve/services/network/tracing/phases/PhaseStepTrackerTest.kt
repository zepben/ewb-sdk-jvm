/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

internal class PhaseStepTrackerTest {

    @Test
    internal fun `visited set of phases is reported as visited`() {
        val tracker = PhaseStepTracker()
        val ce = Junction()
        val phaseStep = PhaseStep.startAt(ce, PhaseCode.AB)

        assertThat("hasVisited returns false for unvisited equipment", not(tracker.hasVisited(phaseStep)))
        assertThat("Visiting phases on unvisited equipment returns true", tracker.visit(phaseStep))
        assertThat("hasVisited returns true for visited phase set", tracker.hasVisited(phaseStep))
        assertThat("Revisiting visited phases returns false", not(tracker.visit(phaseStep)))
    }

    @Test
    internal fun `set of phases disjoint from visited phases is reported as unvisited`() {
        val tracker = PhaseStepTracker()
        val ce = Junction()
        val phaseStep1 = PhaseStep.startAt(ce, PhaseCode.AB)
        val phaseStep2 = PhaseStep.startAt(ce, PhaseCode.CN)

        tracker.visit(phaseStep1)

        assertThat("hasVisited returns false for phase set disjoint from visited phases", not(tracker.hasVisited(phaseStep2)))
        assertThat("Visiting phase set disjoint from visited phases returns true", tracker.visit(phaseStep2))
    }

    @Test
    internal fun `set of phases partially overlapping with visited phases is reported as unvisited`() {
        val tracker = PhaseStepTracker()
        val ce = Junction()
        val phaseStep1 = PhaseStep.startAt(ce, PhaseCode.AB)
        val phaseStep2 = PhaseStep.startAt(ce, PhaseCode.BC)

        tracker.visit(phaseStep1)

        assertThat("hasVisited returns false for phase set partially overlapping visited phases", not(tracker.hasVisited(phaseStep2)))
        assertThat("Visiting phase set partially overlapping visited phases returns true", tracker.visit(phaseStep2))
    }

    @Test
    internal fun `strict subset of visited phases is reported as visited`() {
        val tracker = PhaseStepTracker()
        val ce = Junction()
        val phaseStep1 = PhaseStep.startAt(ce, PhaseCode.ABC)
        val phaseStep2 = PhaseStep.startAt(ce, PhaseCode.BC)

        tracker.visit(phaseStep1)

        assertThat("hasVisited returns true for strict subset of visited phases", tracker.hasVisited(phaseStep2))
        assertThat("Visiting strict subset of visited phases returns false", not(tracker.visit(phaseStep2)))
    }

    @Test
    internal fun `phases of different equipment are tracked separately`() {
        val tracker = PhaseStepTracker()
        val ce1 = Junction()
        val ce2 = Junction()
        val phaseStep1 = PhaseStep.startAt(ce1, PhaseCode.AB)
        val phaseStep2 = PhaseStep.continueAt(ce2, PhaseCode.AB, ce1)

        tracker.visit(phaseStep1)

        assertThat("hasVisited returns false for same phases on different equipment", not(tracker.hasVisited(phaseStep2)))
        assertThat("Visiting same phases on different equipment returns true", tracker.visit(phaseStep2))
    }

}
