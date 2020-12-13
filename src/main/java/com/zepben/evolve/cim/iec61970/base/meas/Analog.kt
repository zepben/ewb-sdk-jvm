/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.meas

/**
 * Analog represents an analog Measurement.
 *
 * @property positiveFlowIn If true then this measurement is an active power, reactive power or current with the convention that a positive value measured at the Terminal means power is flowing into the related PowerSystemResource.
 */
class Analog @JvmOverloads constructor(mRID: String = "") : Measurement(mRID) {
    var positiveFlowIn: Boolean = false
}
