/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.scada

import com.zepben.ewb.cim.iec61970.base.meas.Control

/**
 * Remote controls are outputs that are sent by the remote unit to actuators in the process.

 * @property control The [Control] for the [RemoteControl] point.
 */
class RemoteControl(mRID: String) : RemotePoint(mRID) {

    var control: Control? = null
}
