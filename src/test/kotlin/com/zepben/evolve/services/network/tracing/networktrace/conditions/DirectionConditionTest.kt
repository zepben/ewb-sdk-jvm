/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class DirectionConditionTest {

    @Test
    fun shouldQueue() {
        NONE conditionInternallySteppingTo NONE terminalShouldQueue true
        NONE conditionInternallySteppingTo UPSTREAM terminalShouldQueue false
        NONE conditionInternallySteppingTo DOWNSTREAM terminalShouldQueue false
        NONE conditionInternallySteppingTo BOTH terminalShouldQueue false
        NONE conditionInternallySteppingTo CONNECTOR terminalShouldQueue false

        UPSTREAM conditionInternallySteppingTo NONE terminalShouldQueue false
        UPSTREAM conditionInternallySteppingTo UPSTREAM terminalShouldQueue true
        UPSTREAM conditionInternallySteppingTo DOWNSTREAM terminalShouldQueue false
        UPSTREAM conditionInternallySteppingTo BOTH terminalShouldQueue true
        UPSTREAM conditionInternallySteppingTo CONNECTOR terminalShouldQueue true

        DOWNSTREAM conditionInternallySteppingTo NONE terminalShouldQueue false
        DOWNSTREAM conditionInternallySteppingTo UPSTREAM terminalShouldQueue false
        DOWNSTREAM conditionInternallySteppingTo DOWNSTREAM terminalShouldQueue true
        DOWNSTREAM conditionInternallySteppingTo BOTH terminalShouldQueue true
        DOWNSTREAM conditionInternallySteppingTo CONNECTOR terminalShouldQueue true

        BOTH conditionInternallySteppingTo NONE terminalShouldQueue false
        BOTH conditionInternallySteppingTo UPSTREAM terminalShouldQueue false
        BOTH conditionInternallySteppingTo DOWNSTREAM terminalShouldQueue false
        BOTH conditionInternallySteppingTo BOTH terminalShouldQueue true
        BOTH conditionInternallySteppingTo CONNECTOR terminalShouldQueue true

        NONE conditionExternallySteppingTo UPSTREAM terminalShouldQueue false
        NONE conditionExternallySteppingTo DOWNSTREAM terminalShouldQueue false
        NONE conditionExternallySteppingTo BOTH terminalShouldQueue false
        NONE conditionExternallySteppingTo NONE terminalShouldQueue true
        NONE conditionExternallySteppingTo CONNECTOR terminalShouldQueue false

        UPSTREAM conditionExternallySteppingTo NONE terminalShouldQueue false
        UPSTREAM conditionExternallySteppingTo UPSTREAM terminalShouldQueue false
        UPSTREAM conditionExternallySteppingTo DOWNSTREAM terminalShouldQueue true
        UPSTREAM conditionExternallySteppingTo BOTH terminalShouldQueue true
        UPSTREAM conditionExternallySteppingTo CONNECTOR terminalShouldQueue true

        DOWNSTREAM conditionExternallySteppingTo NONE terminalShouldQueue false
        DOWNSTREAM conditionExternallySteppingTo UPSTREAM terminalShouldQueue true
        DOWNSTREAM conditionExternallySteppingTo DOWNSTREAM terminalShouldQueue false
        DOWNSTREAM conditionExternallySteppingTo BOTH terminalShouldQueue true
        DOWNSTREAM conditionExternallySteppingTo CONNECTOR terminalShouldQueue true

        BOTH conditionExternallySteppingTo UPSTREAM terminalShouldQueue false
        BOTH conditionExternallySteppingTo DOWNSTREAM terminalShouldQueue false
        BOTH conditionExternallySteppingTo BOTH terminalShouldQueue true
        BOTH conditionExternallySteppingTo NONE terminalShouldQueue false
        BOTH conditionExternallySteppingTo CONNECTOR terminalShouldQueue true
    }

    @Test
    fun shouldQueueStartItem() {
        NONE conditionWith NONE startTerminalShouldQueue true
        NONE conditionWith UPSTREAM startTerminalShouldQueue false
        NONE conditionWith DOWNSTREAM startTerminalShouldQueue false
        NONE conditionWith BOTH startTerminalShouldQueue false
        NONE conditionWith CONNECTOR startTerminalShouldQueue false

        UPSTREAM conditionWith NONE startTerminalShouldQueue false
        UPSTREAM conditionWith UPSTREAM startTerminalShouldQueue true
        UPSTREAM conditionWith DOWNSTREAM startTerminalShouldQueue false
        UPSTREAM conditionWith BOTH startTerminalShouldQueue true
        UPSTREAM conditionWith CONNECTOR startTerminalShouldQueue true

        DOWNSTREAM conditionWith NONE startTerminalShouldQueue false
        DOWNSTREAM conditionWith UPSTREAM startTerminalShouldQueue false
        DOWNSTREAM conditionWith DOWNSTREAM startTerminalShouldQueue true
        DOWNSTREAM conditionWith BOTH startTerminalShouldQueue true
        DOWNSTREAM conditionWith CONNECTOR startTerminalShouldQueue true

        BOTH conditionWith NONE startTerminalShouldQueue false
        BOTH conditionWith UPSTREAM startTerminalShouldQueue false
        BOTH conditionWith DOWNSTREAM startTerminalShouldQueue false
        BOTH conditionWith BOTH startTerminalShouldQueue true
        BOTH conditionWith CONNECTOR startTerminalShouldQueue true
    }

    private infix fun Triple<FeederDirection, FeederDirection, Boolean>.terminalShouldQueue(expected: Boolean) {
        val (direction, toDirection, tracedInternally) = this
        val nextPath = mockk<NetworkTraceStep.Path>()
        every { nextPath.tracedInternally } returns tracedInternally
        every { nextPath.toTerminal } returns Terminal()
        val nextItem = NetworkTraceStep(nextPath, 0, 0, Unit)

        val result = DirectionCondition<Unit>(direction) { toDirection }.shouldQueue(nextItem, mockk(), mockk(), mockk())

        assertThat(result, equalTo(expected))
    }

    private infix fun Pair<FeederDirection, FeederDirection>.startTerminalShouldQueue(expected: Boolean) {
        val (direction, toDirection) = this
        val nextPath = mockk<NetworkTraceStep.Path>()
        every { nextPath.toTerminal } returns Terminal()
        val nextItem = NetworkTraceStep(nextPath, 0, 0, Unit)

        val result = DirectionCondition<Unit>(direction) { toDirection }.shouldQueueStartItem(nextItem)

        assertThat(result, equalTo(expected))
    }

    private infix fun FeederDirection.conditionInternallySteppingTo(toDirection: FeederDirection): Triple<FeederDirection, FeederDirection, Boolean> =
        Triple(this, toDirection, true)

    private infix fun FeederDirection.conditionExternallySteppingTo(toDirection: FeederDirection): Triple<FeederDirection, FeederDirection, Boolean> =
        Triple(this, toDirection, false)

    private infix fun FeederDirection.conditionWith(toDirection: FeederDirection): Pair<FeederDirection, FeederDirection> = this to toDirection


}
