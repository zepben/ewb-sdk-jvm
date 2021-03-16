/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.measurement.translator

import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
import com.zepben.evolve.cim.iec61970.base.meas.MeasurementValue
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.utils.validateMRID
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue

internal class MeasurementCimToProtoTestValidator {

    private fun validate(cim: MeasurementValue, pb: PBMeasurementValue) {
        cim.timeStamp?.let { assertThat(pb.timeStamp, equalTo(it.toTimestamp())) } ?: assertThat(pb.timeStamp, equalTo(Timestamp.getDefaultInstance()))
    }

    fun validate(cim: AnalogValue, pb: PBAnalogValue) {
        validate(cim as MeasurementValue, pb.mv)

        validateMRID(pb.analogMRID, cim.analogMRID)
        assertThat(pb.value, equalTo(cim.value))
    }

    fun validate(cim: AccumulatorValue, pb: PBAccumulatorValue) {
        validate(cim as MeasurementValue, pb.mv)

        validateMRID(pb.accumulatorMRID, cim.accumulatorMRID)
        assertThat(pb.value, equalTo(cim.value.toInt()))
    }

    fun validate(cim: DiscreteValue, pb: PBDiscreteValue) {
        validate(cim as MeasurementValue, pb.mv)

        validateMRID(pb.discreteMRID, cim.discreteMRID)
        assertThat(pb.value, equalTo(cim.value))
    }

}
