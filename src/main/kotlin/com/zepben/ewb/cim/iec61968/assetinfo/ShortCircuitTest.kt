/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

/**
 * Short-circuit test results determine mesh impedance parameters. They include load losses and leakage impedances. For three-phase windings, the excitation
 * can be a positive sequence (the default) or a zero sequence. There shall be at least one grounded winding.
 *
 * @property current Short circuit current in amps.
 * @property energisedEndStep Tap step number for the energised end of the test pair.
 * @property groundedEndStep Tap step number for the grounded end of the test pair.
 * @property leakageImpedance Leakage impedance measured from a positive-sequence or single-phase short-circuit test in ohms.
 * @property leakageImpedanceZero Leakage impedance measured from a zero-sequence short-circuit test in ohms.
 * @property loss Load losses from a positive-sequence or single-phase short-circuit test in watts.
 * @property lossZero Load losses from a zero-sequence short-circuit test in watts.
 * @property power Short circuit apparent power in VA.
 * @property voltage Short circuit voltage as a percentage.
 * @property voltageOhmicPart Short Circuit Voltage – Ohmic Part as a percentage.
 */
class ShortCircuitTest @JvmOverloads constructor(mRID: String = "") : TransformerTest(mRID) {

    var current: Double? = null
    var energisedEndStep: Int? = null
    var groundedEndStep: Int? = null
    var leakageImpedance: Double? = null
    var leakageImpedanceZero: Double? = null
    var loss: Int? = null
    var lossZero: Int? = null
    var power: Int? = null
    var voltage: Double? = null
    var voltageOhmicPart: Double? = null

}
