/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.meas

/**
 * DiscreteValue represents a discrete MeasurementValue.
 *
 * @property value The value to supervise
 * @property discreteMRID The [Discrete] MRID of this [DiscreteValue].
 */
class DiscreteValue : MeasurementValue() {
    var value: Int = 0
    var discreteMRID: String? = null
}
