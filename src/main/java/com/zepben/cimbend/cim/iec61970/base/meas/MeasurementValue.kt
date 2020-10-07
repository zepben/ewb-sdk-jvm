/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.meas

import java.time.Instant

/**
 * The current state for a measurement.
 * A state value is an instance of a measurement from a specific source.
 * Measurements can be associated with many state values,
 * each representing a different source for the measurement.
 *
 * @property timeStamp The time when the value was last updated.
 */
abstract class MeasurementValue {
    var timeStamp: Instant? = null
}
