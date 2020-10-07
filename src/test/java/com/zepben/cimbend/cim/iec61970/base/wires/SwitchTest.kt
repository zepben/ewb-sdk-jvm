/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension

internal class SwitchTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Switch() {}.mRID, not(equalTo("")))
        assertThat(object : Switch("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun `defaults to closed`() {
        val switch = object : Switch() {}
        testOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = false)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = false)
    }

    @Test
    internal fun `sets all open with null phase`() {
        val switch = object : Switch() {}
        switch.setOpen(false, null)
        testOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = false)

        switch.setOpen(true, null)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = true)

        switch.setNormallyOpen(false, null)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = false)

        switch.setNormallyOpen(true, null)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = true)
    }

    @Test
    internal fun `set open state`() {
        val switch = object : Switch() {}

        // Test closing current
        switch.setOpen(true, null).setOpen(false, A)
        testOpen(switch, aOpen = false, bOpen = true, cOpen = true, nOpen = true)
        switch.setOpen(true, null).setOpen(false, B)
        testOpen(switch, aOpen = true, bOpen = false, cOpen = true, nOpen = true)
        switch.setOpen(true, null).setOpen(false, C)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
        switch.setOpen(true, null).setOpen(false, N)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = false)

        // Test opening current
        switch.setOpen(false, null).setOpen(true, A)
        testOpen(switch, aOpen = true, bOpen = false, cOpen = false, nOpen = false)
        switch.setOpen(false, null).setOpen(true, B)
        testOpen(switch, aOpen = false, bOpen = true, cOpen = false, nOpen = false)
        switch.setOpen(false, null).setOpen(true, C)
        testOpen(switch, aOpen = false, bOpen = false, cOpen = true, nOpen = false)
        switch.setOpen(false, null).setOpen(true, N)
        testOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = true)

        // Test closing normal
        switch.setNormallyOpen(true, null).setNormallyOpen(false, A)
        testNormallyOpen(switch, aOpen = false, bOpen = true, cOpen = true, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, B)
        testNormallyOpen(switch, aOpen = true, bOpen = false, cOpen = true, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, C)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, N)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = false)

        // Test opening normal
        switch.setNormallyOpen(false, null).setNormallyOpen(true, A)
        testNormallyOpen(switch, aOpen = true, bOpen = false, cOpen = false, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, B)
        testNormallyOpen(switch, aOpen = false, bOpen = true, cOpen = false, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, C)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = true, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, N)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = true)
    }

    @Test
    internal fun `any open phase returns open for null phase`() {
        val switch = object : Switch() {}
        switch.setOpen(false, null)
        assertThat(switch.isOpen(null), equalTo(false))

        switch.setOpen(true, A)
        assertThat(switch.isOpen(null), equalTo(true))

        switch.setNormallyOpen(false, null)
        assertThat(switch.isNormallyOpen(null), equalTo(false))

        switch.setNormallyOpen(true, A)
        assertThat(switch.isNormallyOpen(null), equalTo(true))
    }

    @Test
    internal fun `ignores terminal phases`() {
        val switch = object : Switch() {}.also {
            it.addTerminal(Terminal().apply { conductingEquipment = it; phases = PhaseCode.A })
            it.addTerminal(Terminal().apply { conductingEquipment = it; phases = PhaseCode.B })
        }

        switch.setOpen(true)
        switch.setOpen(false, C)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)

        switch.setNormallyOpen(true)
        switch.setNormallyOpen(false, C)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
    }

    @Test
    internal fun `throws on invalid phase`() {
        val switch = object : Switch() {}
        expect { switch.setOpen(true, INVALID) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.setOpen(true, NONE) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.isOpen(INVALID) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.isOpen(NONE) }.toThrow(IllegalArgumentException::class.java)

        expect { switch.setNormallyOpen(true, INVALID) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.setNormallyOpen(true, NONE) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.isNormallyOpen(INVALID) }.toThrow(IllegalArgumentException::class.java)
        expect { switch.isNormallyOpen(NONE) }.toThrow(IllegalArgumentException::class.java)
    }

    private fun testOpen(switch: Switch, aOpen: Boolean, bOpen: Boolean, cOpen: Boolean, nOpen: Boolean) {
        assertAll("open states",
            { assertThat("Phase A open", switch.isOpen(A), equalTo(aOpen)) },
            { assertThat("Phase B open", switch.isOpen(B), equalTo(bOpen)) },
            { assertThat("Phase C open", switch.isOpen(C), equalTo(cOpen)) },
            { assertThat("Phase N open", switch.isOpen(N), equalTo(nOpen)) })
    }

    private fun testNormallyOpen(switch: Switch, aOpen: Boolean, bOpen: Boolean, cOpen: Boolean, nOpen: Boolean) {
        assertAll("normally open states",
            { assertThat("Phase A normally open", switch.isNormallyOpen(A), equalTo(aOpen)) },
            { assertThat("Phase B normally open", switch.isNormallyOpen(B), equalTo(bOpen)) },
            { assertThat("Phase C normally open", switch.isNormallyOpen(C), equalTo(cOpen)) },
            { assertThat("Phase N normally open", switch.isNormallyOpen(N), equalTo(nOpen)) })
    }
}
