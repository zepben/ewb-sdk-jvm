/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.domain

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class UnitSymbolTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun valueCoverage() {
        UnitSymbol.values().forEach {
            assertThat(UnitSymbol.valueOf(it.name).value(), `is`(it.value()))
        }
    }

    @Test
    internal fun toStringCoverage() {
        assertThat(UnitSymbol.METRES.toString(), `is`("m"))
        assertThat(UnitSymbol.SIEMENS.toString(), `is`("S"))
        assertThat(UnitSymbol.HENRYS.toString(), `is`("H"))
        assertThat(UnitSymbol.SECONDS.toString(), `is`("s"))
        assertThat(UnitSymbol.HOURS.toString(), `is`("h"))
        assertThat(UnitSymbol.MILES_NAUTICAL.toString(), `is`("M"))
    }

    @Test
    internal fun fromCimNameCoverage() {
        UnitSymbol.values().forEach {
            assertThat(UnitSymbol.fromCimName(it.toString()), `is`(it))
        }
    }
}
