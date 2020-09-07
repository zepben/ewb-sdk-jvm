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
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LinearShuntCompensatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(LinearShuntCompensator().mRID, not(equalTo("")))
        assertThat(LinearShuntCompensator("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val linearShuntCompensator = LinearShuntCompensator()

        assertThat(linearShuntCompensator.b0PerSection, equalTo(0.0))
        assertThat(linearShuntCompensator.bPerSection, equalTo(0.0))
        assertThat(linearShuntCompensator.g0PerSection, equalTo(0.0))
        assertThat(linearShuntCompensator.gPerSection, equalTo(0.0))

        linearShuntCompensator.b0PerSection = 1.1
        linearShuntCompensator.bPerSection = 2.2
        linearShuntCompensator.g0PerSection = 3.3
        linearShuntCompensator.gPerSection = 4.4

        assertThat(linearShuntCompensator.b0PerSection, equalTo(1.1))
        assertThat(linearShuntCompensator.bPerSection, equalTo(2.2))
        assertThat(linearShuntCompensator.g0PerSection, equalTo(3.3))
        assertThat(linearShuntCompensator.gPerSection, equalTo(4.4))
    }
}
