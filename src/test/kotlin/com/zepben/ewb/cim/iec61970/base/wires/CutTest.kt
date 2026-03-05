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

internal class CutTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Cut("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val cut = Cut(generateId())
        val acls = AcLineSegment(generateId())

        assertThat(cut.lengthFromTerminal1, nullValue())
        assertThat(cut.acLineSegment, nullValue())

        cut.lengthFromTerminal1 = 1.1
        cut.acLineSegment = acls

        assertThat(cut.lengthFromTerminal1, equalTo(1.1))
        assertThat(cut.acLineSegment, sameInstance(acls))
    }

    @Test
    internal fun `can only have two terminals`() {
        val cut = Cut(generateId())
        val terminal = Terminal(generateId())

        cut.addTerminal(Terminal(generateId()))
        cut.addTerminal(Terminal(generateId()))
        expect { cut.addTerminal(terminal) }
            .toThrow<IllegalStateException>()
            .withMessage(
                "Unable to add ${terminal.typeNameAndMRID()} to ${cut.typeNameAndMRID()}. This conducting equipment already has the maximum number of terminals (2)."
            )
    }

}
