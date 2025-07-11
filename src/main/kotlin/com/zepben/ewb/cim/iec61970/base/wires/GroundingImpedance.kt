/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A fixed impedance device used for grounding.
 *
 * @property x Reactance of device in ohms.
 */
class GroundingImpedance @JvmOverloads constructor(mRID: String = "") : EarthFaultCompensator(mRID) {

    var x: Double? = null

}
