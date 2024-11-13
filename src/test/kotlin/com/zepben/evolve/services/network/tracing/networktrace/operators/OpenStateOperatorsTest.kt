/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class OpenStateOperatorsTest {

    private val normal = OpenStateOperators.NORMAL
    private val current = OpenStateOperators.CURRENT

    @Test
    fun isOpenChecksSwitchOpenState() {
        fun test(operators: OpenStateOperators, isOpen: (Switch, SinglePhaseKind?) -> Boolean) {
            val switch = mockk<Switch>()
            every { isOpen(switch, SinglePhaseKind.A) } returns false andThen true

            assertThat(operators.isOpen(switch, SinglePhaseKind.A), equalTo(false))
            assertThat(operators.isOpen(switch, SinglePhaseKind.A), equalTo(true))
            verify(exactly = 2) { isOpen(switch, SinglePhaseKind.A) }
        }

        test(normal, Switch::isNormallyOpen)
        test(current, Switch::isOpen)
    }

    @Test
    fun setOpen() {
        fun test(operators: OpenStateOperators, setOpen: (Switch, Boolean, SinglePhaseKind?) -> Switch) {
            val switch = mockk<Switch>()
            every { setOpen(switch, any(), SinglePhaseKind.A) } returns switch

            operators.setOpen(switch, true, SinglePhaseKind.A)
            verify { setOpen(switch, true, SinglePhaseKind.A) }
        }

        test(normal, Switch::setNormallyOpen)
        test(current, Switch::setOpen)
    }
}
