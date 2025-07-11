/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.meas

/**
 * AccumulatorValue represents an accumulated (counted) MeasurementValue.
 *
 * @property value The value to supervise
 * @property accumulatorMRID The [Accumulator] MRID of this [AccumulatorValue]
 */
class AccumulatorValue : MeasurementValue() {
    var value: UInt = 0u
    var accumulatorMRID: String? = null
}
