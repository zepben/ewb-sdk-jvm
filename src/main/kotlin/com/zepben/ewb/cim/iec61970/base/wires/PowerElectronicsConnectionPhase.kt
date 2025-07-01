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
 * A single phase of a power electronics connection.
 *
 * @property powerElectronicsConnection The power electronics connection to which the phase belongs.
 * @property p Active power injection. Load sign convention is used, i.e. positive sign means flow into the equipment from the network.
 * @property phase Phase of this energy producer component. If the energy producer is wye connected, the connection is from the indicated phase to the central
 *                 ground or neutral point. If the energy producer is delta connected, the phase indicates an energy producer connected from the indicated phase to the next
 *                 logical non-neutral phase.
 * @property q Reactive power injection. Load sign convention is used, i.e. positive sign means flow into the equipment from the network.
 */
class PowerElectronicsConnectionPhase @JvmOverloads constructor(mRID: String = "") : PowerSystemResource(mRID) {

    var powerElectronicsConnection: PowerElectronicsConnection? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("powerElectronicsConnection has already been set to $field. Cannot set this field again")
        }

    var p: Double? = null
    var phase: SinglePhaseKind = SinglePhaseKind.X
    var q: Double? = null
}
