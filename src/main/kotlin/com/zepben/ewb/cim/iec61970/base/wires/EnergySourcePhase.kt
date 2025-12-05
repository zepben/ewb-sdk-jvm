/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource

/**
 * Represents the single phase information of an unbalanced energy source.
 *
 * @property energySource The energy source to which the phase belongs.
 * @property phase Phase of this energy source component. If the energy source wye connected, the connection is from the indicated phase
 *                 to the central ground or neutral point.  If the energy source is delta connected, the phase indicates an energy source connected
 *                 from the indicated phase to the next logical non-neutral phase.
 */
class EnergySourcePhase(mRID: String) : PowerSystemResource(mRID) {

    var energySource: EnergySource? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("energySource has already been set to $field. Cannot set this field again")
        }

    var phase: SinglePhaseKind = SinglePhaseKind.X
}
