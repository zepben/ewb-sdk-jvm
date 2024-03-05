/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.meas

/**
 * AnalogValue represents an analog MeasurementValue.
 *
 * @property value The value to supervise
 * @property analogMRID The [Analog] MRID of this [AnalogValue]
 */
class AnalogValue : MeasurementValue() {
    var value: Double = 0.0
    var analogMRID: String? = null
}
