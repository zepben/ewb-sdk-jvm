/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ClampTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Clamp("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val clamp = Clamp(generateId())
        val acls = AcLineSegment(generateId())

        assertThat(clamp.lengthFromTerminal1, nullValue())
        assertThat(clamp.acLineSegment, nullValue())

        clamp.lengthFromTerminal1 = 1.1
        clamp.acLineSegment = acls

        assertThat(clamp.lengthFromTerminal1, equalTo(1.1))
        assertThat(clamp.acLineSegment, sameInstance(acls))
    }

    @Test
    internal fun `can only have a single terminal`() {
        val clamp = Clamp(generateId())
        val terminal = Terminal(generateId())

        clamp.addTerminal(Terminal(generateId()))
        expect { clamp.addTerminal(terminal) }
            .toThrow<IllegalStateException>()
            .withMessage(
                "Unable to add ${terminal.typeNameAndMRID()} to ${clamp.typeNameAndMRID()}. This conducting equipment already has the maximum number of terminals (1)."
            )
    }

}
