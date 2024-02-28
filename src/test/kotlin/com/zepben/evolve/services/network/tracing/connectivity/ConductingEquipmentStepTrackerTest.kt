/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConductingEquipmentStepTrackerTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val tracker = ConductingEquipmentStepTracker()

    @Test
    internal fun `visited step is reported as visited`() {
        val step = ConductingEquipmentStep(Junction())

        assertThat("hasVisited returns false for unvisited equipment", !tracker.hasVisited(step))
        assertThat("Visiting unvisited equipment returns true", tracker.visit(step))
        assertThat("hasVisited returns true for visited equipment", tracker.hasVisited(step))
        assertThat("Revisiting visited equipment returns false", !tracker.visit(step))
    }

    @Test
    internal fun `smaller step for same equipment is reported as unvisited`() {
        val ce = Junction()
        val step1 = ConductingEquipmentStep(ce, 1)
        val step2 = ConductingEquipmentStep(ce)

        tracker.visit(step1)

        assertThat("hasVisited returns false for smaller step of visited", !tracker.hasVisited(step2))
        assertThat("Visiting smaller step of visited returns true", tracker.visit(step2))
    }

    @Test
    internal fun `larger step for same equipment is reported as visited`() {
        val ce = Junction()
        val step1 = ConductingEquipmentStep(ce)
        val step2 = ConductingEquipmentStep(ce, 1)

        tracker.visit(step1)

        assertThat("hasVisited returns true for larger step of visited", tracker.hasVisited(step2))
        assertThat("Visiting larger step of visited returns false", !tracker.visit(step2))
    }

    @Test
    internal fun `steps of different equipment are tracked separately`() {
        val step1 = ConductingEquipmentStep(Junction())
        val step2 = ConductingEquipmentStep(Junction())

        tracker.visit(step1)

        assertThat("hasVisited returns false for same step on different equipment", !tracker.hasVisited(step2))
        assertThat("Visiting same step on different equipment returns true", tracker.visit(step2))
    }

}
