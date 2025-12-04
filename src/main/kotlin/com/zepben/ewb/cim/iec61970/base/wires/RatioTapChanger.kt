/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * Mechanism for changing transformer winding tap positions.
 *
 * @property transformerEnd Transformer end to which this ratio tap changer belongs.
 * @property stepVoltageIncrement Tap step increment, in per cent of neutral voltage, per step position.
 */
class RatioTapChanger(mRID: String) : TapChanger(mRID) {

    var transformerEnd: TransformerEnd? = null
    var stepVoltageIncrement: Double? = null
}
