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
package com.zepben.cimbend.measurement

import com.zepben.cimbend.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.cimbend.cim.iec61970.base.meas.AnalogValue
import com.zepben.cimbend.cim.iec61970.base.meas.DiscreteValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class MeasurementServiceTest {

    private val service = MeasurementService()

    @Test
    internal fun supportsAnalogValue() {
        val measurement = AnalogValue()
        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.remove(measurement), equalTo(true))
    }

    @Test
    internal fun supportsAccumulatorValue() {
        val measurement = AccumulatorValue()
        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.remove(measurement), equalTo(true))
    }

    @Test
    internal fun supportsDiscreteValue() {
        val measurement = DiscreteValue()
        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.remove(measurement), equalTo(true))
    }

    @Test
    internal fun num() {
        service.add(AnalogValue())

        assertThat(service.num(), equalTo(1))
    }

    @Test
    internal fun listOf() {
        val av = AnalogValue()
        val acv = AccumulatorValue()
        val dv = DiscreteValue()
        service.add(av)
        service.add(acv)
        service.add(dv)

        assertThat(service.listOf(AnalogValue::class), contains(av))
        assertThat(service.listOf(AccumulatorValue::class), contains(acv))
        assertThat(service.listOf(DiscreteValue::class), contains(dv))
    }
}
