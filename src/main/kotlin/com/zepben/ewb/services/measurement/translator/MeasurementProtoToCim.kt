/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.measurement.translator

import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.services.common.translator.toInstant
import com.zepben.ewb.services.measurement.MeasurementService
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

// ######################
// # IEC61970 Base Meas #
// ######################

/**
 * Convert the protobuf [PBAccumulatorValue] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAccumulatorValue] to convert.
 * @return The converted [pb] as a CIM [AccumulatorValue].
 */
fun toCim(pb: PBAccumulatorValue): AccumulatorValue =
    AccumulatorValue().apply {
        accumulatorMRID = pb.accumulatorMRID.takeIf { it.isNotBlank() }
        value = pb.value.toUInt()
        toCim(pb.mv, this)
    }

/**
 * Convert the protobuf [PBAnalogValue] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAnalogValue] to convert.
 * @return The converted [pb] as a CIM [AnalogValue].
 */
fun toCim(pb: PBAnalogValue): AnalogValue =
    AnalogValue().apply {
        analogMRID = pb.analogMRID.takeIf { it.isNotBlank() }
        value = pb.value
        toCim(pb.mv, this)
    }

/**
 * Convert the protobuf [PBDiscreteValue] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDiscreteValue] to convert.
 * @return The converted [pb] as a CIM [DiscreteValue].
 */
fun toCim(pb: PBDiscreteValue): DiscreteValue =
    DiscreteValue().apply {
        discreteMRID = pb.discreteMRID.takeIf { it.isNotBlank() }
        value = pb.value
        toCim(pb.mv, this)
    }

/**
 * Convert the protobuf [PBMeasurementValue] into its CIM counterpart.
 *
 * @param pb The protobuf [PBMeasurementValue] to convert.
 * @param cim The CIM [MeasurementValue] to populate.
 * @return The converted [pb] as a CIM [MeasurementValue].
 */
fun toCim(pb: PBMeasurementValue, cim: MeasurementValue): MeasurementValue =
    cim.apply {
        timeStamp = pb.timeStampSet.takeUnless { pb.hasTimeStampNull() }?.toInstant()
    }


/**
 * An extension to add a converted copy of the protobuf [PBAccumulatorValue] to the [MeasurementService].
 */
fun MeasurementService.addFromPb(pb: PBAccumulatorValue): AccumulatorValue = toCim(pb).also { add(it) }

/**
 * An extension to add a converted copy of the protobuf [PBAnalogValue] to the [MeasurementService].
 */
fun MeasurementService.addFromPb(pb: PBAnalogValue): AnalogValue = toCim(pb).also { add(it) }

/**
 * An extension to add a converted copy of the protobuf [PBDiscreteValue] to the [MeasurementService].
 */
fun MeasurementService.addFromPb(pb: PBDiscreteValue): DiscreteValue = toCim(pb).also { add(it) }

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 *
 * @property measurementService The [MeasurementService] all converted objects should be added to.
 */
//
// NOTE: This will be left unused until we have a measurement consumer client.
//
@Suppress("Unused")
class MeasurementProtoToCim(private val measurementService: MeasurementService) {

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBAnalogValue] to the [MeasurementService].
     *
     * @param pb The [PBAnalogValue] to convert.
     * @return The converted [AnalogValue]
     */
    fun addFromPb(pb: PBAnalogValue): AnalogValue = measurementService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBAccumulatorValue] to the [MeasurementService].
     *
     * @param pb The [PBAccumulatorValue] to convert.
     * @return The converted [AccumulatorValue]
     */
    fun addFromPb(pb: PBAccumulatorValue): AccumulatorValue = measurementService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBDiscreteValue] to the [MeasurementService].
     *
     * @param pb The [PBDiscreteValue] to convert.
     * @return The converted [DiscreteValue]
     */
    fun addFromPb(pb: PBDiscreteValue): DiscreteValue = measurementService.addFromPb(pb)

}
