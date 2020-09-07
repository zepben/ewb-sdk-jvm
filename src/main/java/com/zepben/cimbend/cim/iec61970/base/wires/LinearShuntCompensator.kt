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
 * A linear shunt compensator has banks or sections with equal admittance values.
 * @property b0PerSection Zero sequence shunt (charging) susceptance per section
 * @property bPerSection Positive sequence shunt (charging) susceptance per section
 * @property g0PerSection Zero sequence shunt (charging) conductance per section
 * @property gPerSection Positive sequence shunt (charging) conductance per section
 */
class LinearShuntCompensator @JvmOverloads constructor(mRID: String = "") : ShuntCompensator(mRID) {

    var b0PerSection: Double = 0.0
    var bPerSection: Double = 0.0
    var g0PerSection: Double = 0.0
    var gPerSection: Double = 0.0
}
