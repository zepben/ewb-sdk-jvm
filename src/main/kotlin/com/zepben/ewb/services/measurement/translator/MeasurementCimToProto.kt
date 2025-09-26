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

/**
 * Convert the [AccumulatorValue] into its protobuf counterpart.
 *
 * @param cim The [AccumulatorValue] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AccumulatorValue, pb: PBAccumulatorValue.Builder): PBAccumulatorValue.Builder =
    pb.apply {
        cim.accumulatorMRID?.let { accumulatorMRID = it } ?: clearAccumulatorMRID()
        value = cim.value.toInt()
        toPb(cim, mvBuilder)
    }

/**
 * Convert the [AnalogValue] into its protobuf counterpart.
 *
 * @param cim The [AnalogValue] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AnalogValue, pb: PBAnalogValue.Builder): PBAnalogValue.Builder =
    pb.apply {
        cim.analogMRID?.let { analogMRID = it } ?: clearAnalogMRID()
        value = cim.value
        toPb(cim, mvBuilder)
    }

/**
 * Convert the [DiscreteValue] into its protobuf counterpart.
 *
 * @param cim The [DiscreteValue] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: DiscreteValue, pb: PBDiscreteValue.Builder): PBDiscreteValue.Builder =
    pb.apply {
        cim.discreteMRID?.let { discreteMRID = it } ?: clearDiscreteMRID()
        value = cim.value
        toPb(cim, mvBuilder)
    }

/**
 * Convert the [MeasurementValue] into its protobuf counterpart.
 *
 * @param cim The [MeasurementValue] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: MeasurementValue, pb: PBMeasurementValue.Builder): PBMeasurementValue.Builder =
    pb.apply {
        cim.timeStamp?.let { timeStamp = it.toTimestamp() } ?: clearTimeStamp()
    }

/**
 * An extension for converting any [AccumulatorValue] into its protobuf counterpart.
 */
fun AccumulatorValue.toPb(): PBAccumulatorValue = toPb(this, PBAccumulatorValue.newBuilder()).build()

/**
 * An extension for converting any [AnalogValue] into its protobuf counterpart.
 */
fun AnalogValue.toPb(): PBAnalogValue = toPb(this, PBAnalogValue.newBuilder()).build()

/**
 * An extension for converting any [DiscreteValue] into its protobuf counterpart.
 */
fun DiscreteValue.toPb(): PBDiscreteValue = toPb(this, PBDiscreteValue.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from CIM objects to their protobuf counterparts.
 */
class MeasurementCimToProto : BaseCimToProto() {

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Convert the [AccumulatorValue] into its protobuf counterpart.
     *
     * @param accumulatorValue The [AccumulatorValue] to convert.
     * @return The protobuf form of [accumulatorValue].
     */
    fun toPb(accumulatorValue: AccumulatorValue): PBAccumulatorValue = accumulatorValue.toPb()

    /**
     * Convert the [AnalogValue] into its protobuf counterpart.
     *
     * @param analogValue The [AnalogValue] to convert.
     * @return The protobuf form of [analogValue].
     */
    fun toPb(analogValue: AnalogValue): PBAnalogValue = analogValue.toPb()

    /**
     * Convert the [DiscreteValue] into its protobuf counterpart.
     *
     * @param discreteValue The [DiscreteValue] to convert.
     * @return The protobuf form of [discreteValue].
     */
    fun toPb(discreteValue: DiscreteValue): PBDiscreteValue = discreteValue.toPb()

}
