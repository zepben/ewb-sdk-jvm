/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.measurement

import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
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
