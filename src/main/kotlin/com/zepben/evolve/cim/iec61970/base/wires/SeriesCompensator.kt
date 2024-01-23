/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment

/**
 * A Series Compensator is a series capacitor or reactor or an AC transmission line without charging susceptance. It is a two terminal device.
 *
 * @property r Positive sequence resistance in ohms.
 * @property r0 Zero sequence resistance in ohms.
 * @property x Positive sequence reactance in ohms.
 * @property x0 Zero sequence reactance in ohms.
 * @property varistorRatedCurrent The maximum current in amps the varistor is designed to handle at specified duration. It is used for short circuit
 *                                calculations. The attribute shall be a positive value. If null and varistorVoltageThreshold is null, a varistor is not
 *                                present.
 * @property varistorVoltageThreshold The dc voltage in volts at which the varistor starts conducting. It is used for short circuit calculations. If null and
 *                                    varistorRatedCurrent is null, a varistor is not present.
 */
class SeriesCompensator(mRID: String = "") : ConductingEquipment(mRID) {

    var r: Double? = null
    var r0: Double? = null
    var x: Double? = null
    var x0: Double? = null
    var varistorRatedCurrent: Int? = null
    var varistorVoltageThreshold: Int? = null

    val varistorPresent: Boolean get() = varistorRatedCurrent != null || varistorVoltageThreshold != null

}
