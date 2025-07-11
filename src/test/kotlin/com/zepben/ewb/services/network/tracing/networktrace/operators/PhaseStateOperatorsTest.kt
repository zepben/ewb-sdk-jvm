/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.operators

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.network.tracing.phases.PhaseStatus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

import kotlin.reflect.KProperty1

class PhaseStateOperatorsTest {

    private val normal = PhaseStateOperators.NORMAL
    private val current = PhaseStateOperators.CURRENT

    @Test
    fun phaseStatus() {
        fun test(operators: PhaseStateOperators, phasesProp: KProperty1<Terminal, PhaseStatus>) {
            val terminal = Terminal()
            val result = operators.phaseStatus(terminal)
            assertThat(result, sameInstance(phasesProp.get(terminal)))
        }

        test(normal, Terminal::normalPhases)
        test(current, Terminal::currentPhases)
    }
}
