/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
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
