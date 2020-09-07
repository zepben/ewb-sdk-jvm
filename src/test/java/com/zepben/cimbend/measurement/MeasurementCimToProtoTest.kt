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
import com.zepben.cimbend.cim.iec61970.base.meas.MeasurementValue
import com.zepben.cimbend.common.translator.toTimestamp
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