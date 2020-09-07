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
import com.zepben.cimbend.common.translator.BaseCimToProto
import com.zepben.cimbend.common.translator.toTimestamp
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

/************ IEC61970 MEAS ************/
fun toPb(cim: AnalogValue, pb: PBAnalogValue.Builder): PBAnalogValue.Builder =
    pb.apply {
        cim.analogMRID?.let { analogMRID = it } ?: clearAnalogMRID()
        value = cim.value
        toPb(cim, mvBuilder)
    }

fun toPb(cim: AccumulatorValue, pb: PBAccumulatorValue.Builder): PBAccumulatorValue.Builder =
    pb.apply {
        cim.accumulatorMRID?.let { accumulatorMRID = it } ?: clearAccumulatorMRID()
        value = cim.value.toInt()
        toPb(cim, mvBuilder)
    }

fun toPb(cim: DiscreteValue, pb: PBDiscreteValue.Builder): PBDiscreteValue.Builder =
    pb.apply {
        cim.discreteMRID?.let { discreteMRID = it } ?: clearDiscreteMRID()
        value = cim.value
        toPb(cim, mvBuilder)
    }

fun toPb(cim: MeasurementValue, pb: PBMeasurementValue.Builder): PBMeasurementValue.Builder =
    pb.apply {
        cim.timeStamp?.let { timeStamp = it.toTimestamp() } ?: clearTimeStamp()
    }

/************ Extension ************/

fun AnalogValue.toPb(): PBAnalogValue = toPb(this, PBAnalogValue.newBuilder()).build()
fun AccumulatorValue.toPb(): PBAccumulatorValue = toPb(this, PBAccumulatorValue.newBuilder()).build()
fun DiscreteValue.toPb(): PBDiscreteValue = toPb(this, PBDiscreteValue.newBuilder()).build()

/************ Class for Java friendly usage ************/

class MeasurementCimToProto : BaseCimToProto() {
    fun toPb(analogValue: AnalogValue): PBAnalogValue = analogValue.toPb()
    fun toPb(accumulatorValue: AccumulatorValue): PBAccumulatorValue = accumulatorValue.toPb()
    fun toPb(discreteValue: DiscreteValue): PBDiscreteValue = discreteValue.toPb()
}


