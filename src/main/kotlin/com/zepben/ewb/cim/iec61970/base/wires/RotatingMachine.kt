/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A rotating machine which may be used as a generator or motor.
 *
 * @property ratedPowerFactor Power factor (nameplate data). It is primarily used for short circuit data exchange according to IEC 60909.
 *                            The attribute cannot be a negative value.
 * @property ratedS Nameplate apparent power rating for the unit in volt-amperes (VA). The attribute shall have a positive value.
 * @property ratedU Rated voltage in volts (nameplate data, Ur in IEC 60909-0). It is primarily used for short circuit data exchange according to IEC 60909.
 *                  The attribute shall be a positive value.
 * @property p Active power injection in watts. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property q Reactive power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 */
abstract class RotatingMachine(mRID: String) : RegulatingCondEq(mRID) {

    var ratedPowerFactor: Double? = null
    var ratedS: Double? = null
    var ratedU: Int? = null
    var p: Double? = null
    var q: Double? = null

}
