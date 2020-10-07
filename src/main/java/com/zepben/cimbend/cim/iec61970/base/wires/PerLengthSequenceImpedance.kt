/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

/**
 * Sequence impedance and admittance parameters per unit length, for transposed lines of 1, 2, or 3 phases. For 1-phase lines, define x=x0=xself. For 2-phase lines, define x=xs-xm and x0=xs+xm.
 *
 * @property r Positive sequence series resistance, per unit of length.
 * @property x Positive sequence series reactance, per unit of length.
 * @property bch Positive sequence shunt (charging) susceptance, per unit of length.
 * @property gch Positive sequence shunt (charging) conductance, per unit of length.
 * @property r0 Zero sequence series resistance, per unit of length.
 * @property x0 Zero sequence series reactance, per unit of length.
 * @property b0ch Zero sequence shunt (charging) susceptance, per unit of length.
 * @property g0ch Zero sequence shunt (charging) conductance, per unit of length.
 */
class PerLengthSequenceImpedance @JvmOverloads constructor(mRID: String = "") : PerLengthImpedance(mRID) {

    var r: Double = 0.0
    var x: Double = 0.0
    var bch: Double = 0.0
    var gch: Double = 0.0
    var r0: Double = 0.0
    var x0: Double = 0.0
    var b0ch: Double = 0.0
    var g0ch: Double = 0.0
}
