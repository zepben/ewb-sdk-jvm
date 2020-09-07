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
    var p: Double = 0.0
    var pFixed: Double = 0.0
    var q: Double = 0.0
    var qFixed: Double = 0.0
}
