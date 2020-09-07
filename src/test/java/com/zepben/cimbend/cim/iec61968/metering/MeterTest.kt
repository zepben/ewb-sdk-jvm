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
package com.zepben.cimbend.cim.iec61968.metering

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeterTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Meter().mRID, not(equalTo("")))
        assertThat(Meter("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val meter = Meter()

        assertThat(meter.companyMeterId, equalTo(""))
        assertThat(meter.name, equalTo(""))

        meter.name = "name"

        assertThat(meter.companyMeterId, equalTo("name"))
        assertThat(meter.name, equalTo("name"))

        meter.companyMeterId = "companyMeterId"

        assertThat(meter.companyMeterId, equalTo("companyMeterId"))
        assertThat(meter.name, equalTo("companyMeterId"))
    }
}
