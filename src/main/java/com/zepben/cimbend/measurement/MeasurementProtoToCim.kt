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

import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.common.translator.toInstant
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

/************ IEC61970 MEAS ************/
fun toCim(pb: PBMeasurementValue, cim: MeasurementValue): MeasurementValue =
    cim.apply { timeStamp = pb.timeStamp.toInstant() }

fun toCim(pb: PBAnalogValue): AnalogValue =
    AnalogValue().apply {
        analogMRID = pb.analogMRID
        value = pb.value
        toCim(pb.mv, this)
    }

fun toCim(pb: PBAccumulatorValue): AccumulatorValue =
    AccumulatorValue().apply {
        accumulatorMRID = pb.accumulatorMRID
        value = pb.value.toUInt()
        toCim(pb.mv, this)
    }

fun toCim(pb: PBDiscreteValue): DiscreteValue =
    DiscreteValue().apply {
        discreteMRID = pb.discreteMRID
        value = pb.value
        toCim(pb.mv, this)
    }


/************ Extensions ************/

fun MeasurementService.addFromPb(pb: PBAnalogValue): AnalogValue = toCim(pb).also { add(it) }
fun MeasurementService.addFromPb(pb: PBAccumulatorValue): AccumulatorValue = toCim(pb).also { add(it) }
fun MeasurementService.addFromPb(pb: PBDiscreteValue): DiscreteValue = toCim(pb).also { add(it) }

/************ Class for Java friendly usage ************/

class MeasurementProtoToCim(private val measurementService: MeasurementService) {
    fun addFromPb(pb: PBAnalogValue): AnalogValue = measurementService.addFromPb(pb)
    fun addFromPb(pb: PBAccumulatorValue): AccumulatorValue = measurementService.addFromPb(pb)
    fun addFromPb(pb: PBDiscreteValue): DiscreteValue = measurementService.addFromPb(pb)
}
