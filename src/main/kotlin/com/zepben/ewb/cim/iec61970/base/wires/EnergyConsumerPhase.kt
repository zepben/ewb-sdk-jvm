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
 * A single phase of an energy consumer.
 *
 * @property energyConsumer The energy consumer to which this phase belongs.
 * @property phase Phase of this energy consumer component. If the energy consumer is wye connected, the connection is
 *                 from the indicated phase to the central ground or neutral point.  If the energy consumer is delta
 *                 connected, the phase indicates an energy consumer connected from the indicated phase to the next
 *                 logical non-neutral phase.
 * @property p Active power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage.
 *             Starting value for a steady state solution.
 * @property pFixed Active power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 * @property q Reactive power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage.
 *             Starting value for a steady state solution.
 * @property qFixed Reactive power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 */
class EnergyConsumerPhase @JvmOverloads constructor(mRID: String = "") : PowerSystemResource(mRID) {

    var energyConsumer: EnergyConsumer? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("energyConsumer has already been set to $field. Cannot set this field again")
        }

    var phase: SinglePhaseKind = SinglePhaseKind.X
    var p: Double? = null
    var pFixed: Double? = null
    var q: Double? = null
    var qFixed: Double? = null
}
