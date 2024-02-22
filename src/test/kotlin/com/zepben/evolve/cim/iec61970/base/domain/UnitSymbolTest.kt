/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.domain

import com.zepben.evolve.cim.validateEnum
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.protobuf.cim.iec61970.base.domain.UnitSymbol as PBUnitSymbol

internal class UnitSymbolTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun validateVsPb() {
        validateEnum(UnitSymbol.entries, PBUnitSymbol.entries)
    }

    @Test
    internal fun toStringCoverage() {
        assertThat(UnitSymbol.METRES.toString(), equalTo("m"))
        assertThat(UnitSymbol.SIEMENS.toString(), equalTo("S"))
        assertThat(UnitSymbol.HENRYS.toString(), equalTo("H"))
        assertThat(UnitSymbol.SECONDS.toString(), equalTo("s"))
        assertThat(UnitSymbol.HOURS.toString(), equalTo("h"))
        assertThat(UnitSymbol.MILES_NAUTICAL.toString(), equalTo("M"))
    }

    @Test
    internal fun fromCimNameCoverage() {
        UnitSymbol.entries.forEach {
            assertThat(UnitSymbol.fromCimName(it.toString()), equalTo(it))
        }
    }
}
