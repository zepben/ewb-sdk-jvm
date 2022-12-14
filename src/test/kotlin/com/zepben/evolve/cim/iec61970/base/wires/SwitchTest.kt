/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.SwitchInfo
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

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
    internal fun accessorCoverage() {
        val switch = object : Switch() {}

        assertThat(switch.assetInfo, nullValue())
        assertThat(switch.ratedCurrent, nullValue())

        switch.fillFields(NetworkService())

        assertThat(switch.assetInfo, notNullValue())
        assertThat(switch.assetInfo, instanceOf(SwitchInfo::class.java))
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
        switch.setOpen(true, null).setOpen(false, SPK.A)
        testOpen(switch, aOpen = false, bOpen = true, cOpen = true, nOpen = true)
        switch.setOpen(true, null).setOpen(false, SPK.B)
        testOpen(switch, aOpen = true, bOpen = false, cOpen = true, nOpen = true)
        switch.setOpen(true, null).setOpen(false, SPK.C)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
        switch.setOpen(true, null).setOpen(false, SPK.N)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = false)

        // Test opening current
        switch.setOpen(false, null).setOpen(true, SPK.A)
        testOpen(switch, aOpen = true, bOpen = false, cOpen = false, nOpen = false)
        switch.setOpen(false, null).setOpen(true, SPK.B)
        testOpen(switch, aOpen = false, bOpen = true, cOpen = false, nOpen = false)
        switch.setOpen(false, null).setOpen(true, SPK.C)
        testOpen(switch, aOpen = false, bOpen = false, cOpen = true, nOpen = false)
        switch.setOpen(false, null).setOpen(true, SPK.N)
        testOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = true)

        // Test closing normal
        switch.setNormallyOpen(true, null).setNormallyOpen(false, SPK.A)
        testNormallyOpen(switch, aOpen = false, bOpen = true, cOpen = true, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, SPK.B)
        testNormallyOpen(switch, aOpen = true, bOpen = false, cOpen = true, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, SPK.C)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
        switch.setNormallyOpen(true, null).setNormallyOpen(false, SPK.N)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = true, nOpen = false)

        // Test opening normal
        switch.setNormallyOpen(false, null).setNormallyOpen(true, SPK.A)
        testNormallyOpen(switch, aOpen = true, bOpen = false, cOpen = false, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, SPK.B)
        testNormallyOpen(switch, aOpen = false, bOpen = true, cOpen = false, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, SPK.C)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = true, nOpen = false)
        switch.setNormallyOpen(false, null).setNormallyOpen(true, SPK.N)
        testNormallyOpen(switch, aOpen = false, bOpen = false, cOpen = false, nOpen = true)
    }

    @Test
    internal fun `any open phase returns open for null phase`() {
        val switch = object : Switch() {}
        switch.setOpen(false, null)
        assertThat(switch.isOpen(null), equalTo(false))

        switch.setOpen(true, SPK.A)
        assertThat(switch.isOpen(null), equalTo(true))

        switch.setNormallyOpen(false, null)
        assertThat(switch.isNormallyOpen(null), equalTo(false))

        switch.setNormallyOpen(true, SPK.A)
        assertThat(switch.isNormallyOpen(null), equalTo(true))
    }

    @Test
    internal fun `ignores terminal phases`() {
        val switch = object : Switch() {}.also {
            it.addTerminal(Terminal().apply { phases = PhaseCode.A })
            it.addTerminal(Terminal().apply { phases = PhaseCode.B })
        }

        switch.setOpen(true)
        switch.setOpen(false, SPK.C)
        testOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)

        switch.setNormallyOpen(true)
        switch.setNormallyOpen(false, SPK.C)
        testNormallyOpen(switch, aOpen = true, bOpen = true, cOpen = false, nOpen = true)
    }

    @Test
    internal fun `throws on invalid phase`() {
        val switch = object : Switch() {}
        expect { switch.setOpen(true, SPK.INVALID) }.toThrow<IllegalArgumentException>()
        expect { switch.setOpen(true, SPK.NONE) }.toThrow<IllegalArgumentException>()
        expect { switch.isOpen(SPK.INVALID) }.toThrow<IllegalArgumentException>()
        expect { switch.isOpen(SPK.NONE) }.toThrow<IllegalArgumentException>()

        expect { switch.setNormallyOpen(true, SPK.INVALID) }.toThrow<IllegalArgumentException>()
        expect { switch.setNormallyOpen(true, SPK.NONE) }.toThrow<IllegalArgumentException>()
        expect { switch.isNormallyOpen(SPK.INVALID) }.toThrow<IllegalArgumentException>()
        expect { switch.isNormallyOpen(SPK.NONE) }.toThrow<IllegalArgumentException>()
    }

    private fun testOpen(switch: Switch, aOpen: Boolean, bOpen: Boolean, cOpen: Boolean, nOpen: Boolean) {
        assertAll("open states",
            { assertThat("Phase A open", switch.isOpen(SPK.A), equalTo(aOpen)) },
            { assertThat("Phase B open", switch.isOpen(SPK.B), equalTo(bOpen)) },
            { assertThat("Phase C open", switch.isOpen(SPK.C), equalTo(cOpen)) },
            { assertThat("Phase N open", switch.isOpen(SPK.N), equalTo(nOpen)) })
    }

    private fun testNormallyOpen(switch: Switch, aOpen: Boolean, bOpen: Boolean, cOpen: Boolean, nOpen: Boolean) {
        assertAll("normally open states",
            { assertThat("Phase A normally open", switch.isNormallyOpen(SPK.A), equalTo(aOpen)) },
            { assertThat("Phase B normally open", switch.isNormallyOpen(SPK.B), equalTo(bOpen)) },
            { assertThat("Phase C normally open", switch.isNormallyOpen(SPK.C), equalTo(cOpen)) },
            { assertThat("Phase N normally open", switch.isNormallyOpen(SPK.N), equalTo(nOpen)) })
    }
}
