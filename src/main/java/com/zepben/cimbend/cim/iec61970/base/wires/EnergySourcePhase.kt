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

import com.zepben.cimbend.cim.iec61970.base.core.PowerSystemResource

/**
 * Represents the single phase information of an unbalanced energy source.
 *
 * @property energySource The energy source to which the phase belongs.
 * @property phase Phase of this energy source component. If the energy source wye connected, the connection is from the indicated phase
 *                 to the central ground or neutral point.  If the energy source is delta connected, the phase indicates an energy source connected
 *                 from the indicated phase to the next logical non-neutral phase.
 */
class EnergySourcePhase @JvmOverloads constructor(mRID: String = "") : PowerSystemResource(mRID) {

    var energySource: EnergySource? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("energySource has already been set to $field. Cannot set this field again")
        }

    var phase: SinglePhaseKind = SinglePhaseKind.X
}
