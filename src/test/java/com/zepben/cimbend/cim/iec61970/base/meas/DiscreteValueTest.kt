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
package com.zepben.cimbend.cim.iec61970.base.meas

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiscreteValueTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun accessorCoverage() {
        val mv = DiscreteValue()
        val measValue = 23
        val measMRID = "measurement-mrid"

        MatcherAssert.assertThat(mv.value, Matchers.`is`(0))
        MatcherAssert.assertThat(mv.discreteMRID, Matchers.nullValue())

        mv.apply {
            value = measValue
            discreteMRID = measMRID
        }
        MatcherAssert.assertThat(mv.value, Matchers.`is`(measValue))
        MatcherAssert.assertThat(mv.discreteMRID, Matchers.`is`(measMRID))
    }
}