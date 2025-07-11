/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A linear shunt compensator has banks or sections with equal admittance values.
 * @property b0PerSection Zero sequence shunt (charging) susceptance per section
 * @property bPerSection Positive sequence shunt (charging) susceptance per section
 * @property g0PerSection Zero sequence shunt (charging) conductance per section
 * @property gPerSection Positive sequence shunt (charging) conductance per section
 */
class LinearShuntCompensator @JvmOverloads constructor(mRID: String = "") : ShuntCompensator(mRID) {

    var b0PerSection: Double? = null
    var bPerSection: Double? = null
    var g0PerSection: Double? = null
    var gPerSection: Double? = null
}
