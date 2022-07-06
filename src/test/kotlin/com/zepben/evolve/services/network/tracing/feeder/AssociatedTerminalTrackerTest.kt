/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import org.hamcrest.Matchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class AssociatedTerminalTrackerTest {

    /**
     * Verify that terminal tracking is linked to its conducting equipment
     */
    @Test
    fun testVisit() {
        val junction1 = Junction()
        val junction2 = Junction()
        val terminal11 = Terminal().also { junction1.addTerminal(it) }
        val terminal12 = Terminal().also { junction1.addTerminal(it) }
        val terminal21 = Terminal().also { junction2.addTerminal(it) }
        val terminal22 = Terminal().also { junction2.addTerminal(it) }

        val tracker = AssociatedTerminalTracker()

        assertThat("has not visited terminal11", not(tracker.hasVisited(terminal11)))
        assertThat("has not visited terminal12", not(tracker.hasVisited(terminal12)))
        assertThat("has not visited terminal21", not(tracker.hasVisited(terminal21)))
        assertThat("has not visited terminal22", not(tracker.hasVisited(terminal22)))

        assertThat("can visit terminal11", tracker.visit(terminal11))

        assertThat("has visited terminal11", tracker.hasVisited(terminal11))
        assertThat("has visited terminal12", tracker.hasVisited(terminal12))
        assertThat("has not visited terminal21", not(tracker.hasVisited(terminal21)))
        assertThat("has not visited terminal22", not(tracker.hasVisited(terminal22)))

        assertThat("can't visit terminal11 twice", not(tracker.visit(terminal11)))
        assertThat("can't visit terminal12 after terminal11", not(tracker.visit(terminal12)))

        assertThat("can visit terminal22", tracker.visit(terminal22))

        assertThat("has visited terminal21", tracker.hasVisited(terminal21))
        assertThat("has visited terminal22", tracker.hasVisited(terminal22))
    }

    /**
     * Verify that a terminal that has no conducting equipment is considered visited even without being visited.
     */
    @Test
    fun testTerminalsWithoutConductingEquipmentAreConsideredVisited() {
        val terminal = Terminal()

        val tracker = AssociatedTerminalTracker()

        assertThat("terminal is considered visited", tracker.hasVisited(terminal))
    }

    /**
     * Verify that a terminal that has no conducting equipment can't be visited.
     */
    @Test
    fun testCantVisitTerminalsWithoutConductingEquipment() {
        val terminal = Terminal()

        val tracker = AssociatedTerminalTracker()

        assertThat("can't visit terminal", not(tracker.visit(terminal)))
    }
}