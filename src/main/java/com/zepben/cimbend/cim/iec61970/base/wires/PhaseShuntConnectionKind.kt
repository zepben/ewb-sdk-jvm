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
 * The configuration of phase connections for a single terminal device such as a load or capacitor.
 *
 * @property UNKNOWN
 * @property D Delta Connection
 * @property Y Wye connection
 * @property Yn Wye, with neutral brought out for grounding.
 * @property I Independent winding, for single-phase connections.
 * @property G Ground connection; use when explicit connection to ground needs to be expressed in combination with the phase * code, such as for electrical wire/cable or for meters.
 */
enum class PhaseShuntConnectionKind {

    UNKNOWN,
    D,
    Y,
    Yn,
    I,
    G,
}
