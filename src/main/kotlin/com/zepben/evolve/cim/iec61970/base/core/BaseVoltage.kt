/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

/**
 * Defines a system base voltage which is referenced.
 * @property nominalVoltage The power system resource's base voltage.
 */
class BaseVoltage @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var nominalVoltage: Int = 0
}
