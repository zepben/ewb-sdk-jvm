/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

internal class PhaseSelectorTest {

    private val terminal = Terminal()

    @Test
    internal fun testNormal() {
        val ps = PhaseSelector.NORMAL_PHASES.phases(terminal)
        assertThat(ps, sameInstance(terminal.normalPhases))
    }

    @Test
    internal fun testCurrent() {
        val ps = PhaseSelector.CURRENT_PHASES.phases(terminal)
        assertThat(ps, sameInstance(terminal.currentPhases))
    }

}
