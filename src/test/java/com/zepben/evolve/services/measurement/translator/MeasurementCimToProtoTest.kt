/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.measurement.translator

import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
import com.zepben.evolve.cim.iec61970.base.meas.MeasurementValue
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.services.measurement.MeasurementCimToProto
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import java.time.Instant
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

class MeasurementCimToProtoTest {

    private val cim2proto = MeasurementCimToProto()
    private fun validate(cim: MeasurementValue, pb: PBMeasurementValue) {
        assertThat(pb.timeStamp, `is`(cim.timeStamp?.toTimestamp()))
    }

    @Test
    internal fun coversAnalogValue() {
        val cim = AnalogValue().apply {
            analogMRID = "id"
            value = 1.0
            timeStamp = Instant.now()
        }
        val pb = cim2proto.toPb(cim)

        validate(cim, pb.mv)
        assertThat(pb.analogMRID, `is`(cim.analogMRID))
        assertThat(pb.value, `is`(cim.value))
    }

    @Test
    internal fun coversAccumulatorValue() {
        val cim = AccumulatorValue().apply {
            accumulatorMRID = "id"
            value = 10u
            timeStamp = Instant.now()
        }
        val pb = cim2proto.toPb(cim)

        validate(cim, pb.mv)
        assertThat(pb.accumulatorMRID, `is`(cim.accumulatorMRID))
        assertThat(pb.value, `is`(cim.value.toInt()))
    }

    @Test
    internal fun coversDiscreteValue() {
        val cim = DiscreteValue().apply {
            discreteMRID = "id"
            value = 1
            timeStamp = Instant.now()
        }
        val pb = cim2proto.toPb(cim)

        validate(cim, pb.mv)
        assertThat(pb.discreteMRID, `is`(cim.discreteMRID))
        assertThat(pb.value, `is`(cim.value))
    }
}
