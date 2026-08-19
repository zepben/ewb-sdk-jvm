/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.iec61970.base.core.ConnectivityNode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerTransformerEndListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `lookup helpers return matching ends or null`() {
        val otherNode = ConnectivityNode("other-node")
        val targetNode = ConnectivityNode("target-node")
        val otherTerminal = Terminal("other-terminal").apply { connectivityNode = otherNode }
        val targetTerminal = Terminal("target-terminal").apply { connectivityNode = targetNode }
        val withoutTerminal = PowerTransformerEnd("without-terminal").apply { endNumber = 1 }
        val other = PowerTransformerEnd("other").apply {
            endNumber = 2
            terminal = otherTerminal
        }
        val target = PowerTransformerEnd("target").apply {
            endNumber = 3
            terminal = targetTerminal
        }
        var backing: MutableList<PowerTransformerEnd>? = mutableListOf(withoutTerminal, other, target)
        val ends = PowerTransformerEndList(
            { backing },
            { backing = it },
            PowerTransformer("transformer"),
            "A PowerTransformerEnd",
        )

        assertThat(ends.getByEndNumber(3), sameInstance(target))
        assertThat(ends.getByEndNumber(4), nullValue())
        assertThat(ends.getByTerminal(targetTerminal), sameInstance(target))
        assertThat(ends.getByTerminal(Terminal("missing-terminal")), nullValue())
        assertThat(ends.getByNode(targetNode), sameInstance(target))
        assertThat(ends.getByNode(ConnectivityNode("missing-node")), nullValue())
    }

}
