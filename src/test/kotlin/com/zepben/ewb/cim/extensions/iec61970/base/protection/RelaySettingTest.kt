/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class RelaySettingTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(RelaySetting(UnitSymbol.NONE, 0.0), notNullValue())
        assertThat(RelaySetting(UnitSymbol.W, 1.5, "setting"), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val (unit, value, name) = RelaySetting(UnitSymbol.W, 1.5, "setting")

        assertThat(unit, equalTo(UnitSymbol.W))
        assertThat(value, equalTo(1.5))
        assertThat(name, equalTo("setting"))
    }

    @Test
    internal fun equals() {
        val relaySetting1 = RelaySetting(UnitSymbol.W, 1.5, "setting")
        val relaySetting2 = RelaySetting(UnitSymbol.W, 1.5, "other")
        val relaySetting3 = RelaySetting(UnitSymbol.NONE, 0.0)
        val relaySetting1Dup = RelaySetting(UnitSymbol.W, 1.5, "setting")
        val relaySetting2Dup = RelaySetting(UnitSymbol.W, 1.5, "other")
        val relaySetting3Dup = RelaySetting(UnitSymbol.NONE, 0.0)

        assertThat(relaySetting1, equalTo(relaySetting1Dup))
        assertThat(relaySetting2, equalTo(relaySetting2Dup))
        assertThat(relaySetting3, equalTo(relaySetting3Dup))
        assertThat(relaySetting1, not(equalTo(relaySetting2)))
        assertThat(relaySetting1, not(equalTo(relaySetting3)))
        assertThat(relaySetting2, not(equalTo(relaySetting3)))
    }
}
