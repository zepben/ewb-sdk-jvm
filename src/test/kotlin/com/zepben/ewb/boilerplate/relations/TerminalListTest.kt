/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TerminalListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `getByNumber returns the matching terminal or null`() {
        val terminals = TerminalList(
            owner = Junction("junction"),
            elementDescription = "A Terminal",
        )
        val first = Terminal("first").apply { sequenceNumber = 1 }
        val second = Terminal("second").apply { sequenceNumber = 2 }
        terminals.add(first)
        terminals.add(second)

        assertThat(terminals.getByNumber(2), sameInstance(second))
        assertThat(terminals.getByNumber(3), nullValue())
    }

}
