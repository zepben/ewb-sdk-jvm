/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.domain

import com.zepben.test.util.junit.SystemLogExtension
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