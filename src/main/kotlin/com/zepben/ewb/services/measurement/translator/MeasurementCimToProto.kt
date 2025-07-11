/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.measurement.translator

import com.zepben.ewb.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.ewb.cim.iec61970.base.meas.AnalogValue
import com.zepben.ewb.cim.iec61970.base.meas.DiscreteValue
import com.zepben.ewb.cim.iec61970.base.meas.MeasurementValue
import com.zepben.ewb.services.common.translator.BaseCimToProto
import com.zepben.ewb.services.common.translator.toTimestamp
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

// ######################
// # IEC61970 Base Meas #
// ######################

fun toPb(cim: AccumulatorValue, pb: PBAccumulatorValue.Builder): PBAccumulatorValue.Builder =
    pb.apply {
        cim.accumulatorMRID?.let { accumulatorMRID = it } ?: clearAccumulatorMRID()
        value = cim.value.toInt()
        toPb(cim, mvBuilder)
    }

fun toPb(cim: AnalogValue, pb: PBAnalogValue.Builder): PBAnalogValue.Builder =
    pb.apply {
        cim.analogMRID?.let { analogMRID = it } ?: clearAnalogMRID()
        value = cim.value
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

fun AccumulatorValue.toPb(): PBAccumulatorValue = toPb(this, PBAccumulatorValue.newBuilder()).build()
fun AnalogValue.toPb(): PBAnalogValue = toPb(this, PBAnalogValue.newBuilder()).build()
fun DiscreteValue.toPb(): PBDiscreteValue = toPb(this, PBDiscreteValue.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

class MeasurementCimToProto : BaseCimToProto() {

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    fun toPb(accumulatorValue: AccumulatorValue): PBAccumulatorValue = accumulatorValue.toPb()
    fun toPb(analogValue: AnalogValue): PBAnalogValue = analogValue.toPb()
    fun toPb(discreteValue: DiscreteValue): PBDiscreteValue = discreteValue.toPb()

}
