/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.measurement.testdata

import com.zepben.ewb.cim.iec61970.base.meas.*
import java.time.Instant

// ######################
// # IEC61970 Base Meas #
// ######################

fun AccumulatorValue.fillFields(): AccumulatorValue {
    value = 23u
    accumulatorMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

fun AnalogValue.fillFields(): AnalogValue {
    value = 2.3
    analogMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

fun DiscreteValue.fillFields(): DiscreteValue {
    value = 23
    discreteMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

private fun MeasurementValue.fillFields() {
    timeStamp = Instant.now()
}
