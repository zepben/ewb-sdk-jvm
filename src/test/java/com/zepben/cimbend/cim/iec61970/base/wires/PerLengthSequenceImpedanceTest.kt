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

internal class PerLengthSequenceImpedanceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PerLengthSequenceImpedance().mRID, not(equalTo("")))
        assertThat(PerLengthSequenceImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val perLengthSequenceImpedance = PerLengthSequenceImpedance()

        assertThat(perLengthSequenceImpedance.r, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.x, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.bch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.gch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.r0, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.x0, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.b0ch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.g0ch, equalTo(0.0))

        perLengthSequenceImpedance.r = 1.0
        perLengthSequenceImpedance.x = 2.0
        perLengthSequenceImpedance.bch = 3.0
        perLengthSequenceImpedance.gch = 4.0
        perLengthSequenceImpedance.r0 = 5.0
        perLengthSequenceImpedance.x0 = 6.0
        perLengthSequenceImpedance.b0ch = 7.0
        perLengthSequenceImpedance.g0ch = 8.0

        assertThat(perLengthSequenceImpedance.r, equalTo(1.0))
        assertThat(perLengthSequenceImpedance.x, equalTo(2.0))
        assertThat(perLengthSequenceImpedance.bch, equalTo(3.0))
        assertThat(perLengthSequenceImpedance.gch, equalTo(4.0))
        assertThat(perLengthSequenceImpedance.r0, equalTo(5.0))
        assertThat(perLengthSequenceImpedance.x0, equalTo(6.0))
        assertThat(perLengthSequenceImpedance.b0ch, equalTo(7.0))
        assertThat(perLengthSequenceImpedance.g0ch, equalTo(8.0))
    }
}
