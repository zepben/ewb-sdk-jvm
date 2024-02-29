/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

/**
 * No-load test results determine core admittance parameters. They include exciting current and core loss measurements from applying voltage to one
 * winding. The excitation may be positive sequence or zero sequence. The test may be repeated at different voltages to measure saturation.
 *
 * @property energisedEndVoltage Voltage applied to the winding (end) during test in volts.
 * @property excitingCurrent Exciting current measured from a positive-sequence or single-phase excitation test as a percentage.
 * @property excitingCurrentZero Exciting current measured from a zero-sequence open-circuit excitation test as a percentage.
 * @property loss Losses measured from a positive-sequence or single-phase excitation test in watts.
 * @property lossZero Losses measured from a zero-sequence excitation test in watts.
 */
class NoLoadTest @JvmOverloads constructor(mRID: String = "") : TransformerTest(mRID) {

    var energisedEndVoltage: Int? = null
    var excitingCurrent: Double? = null
    var excitingCurrentZero: Double? = null
    var loss: Int? = null
    var lossZero: Int? = null

}
