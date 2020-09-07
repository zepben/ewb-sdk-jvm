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

internal class ShuntCompensatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ShuntCompensator() {}.mRID, not(equalTo("")))
        assertThat(object : ShuntCompensator("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val shuntCompensator = object : ShuntCompensator() {}

        assertThat(shuntCompensator.grounded, equalTo(false))
        assertThat(shuntCompensator.nomU, equalTo(0))
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.UNKNOWN))
        assertThat(shuntCompensator.sections, equalTo(0.0))

        shuntCompensator.grounded = true
        shuntCompensator.nomU = 2
        shuntCompensator.phaseConnection = PhaseShuntConnectionKind.G
        shuntCompensator.sections = 5.6

        assertThat(shuntCompensator.grounded, equalTo(true))
        assertThat(shuntCompensator.nomU, equalTo(2))
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.G))
        assertThat(shuntCompensator.sections, equalTo(5.6))
    }
}
