/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.meas

import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl

/**
 * Control is used for supervisory/device control. It represents control outputs that are used to change the state in a
 * process, e.g. close or open breaker, a set point value or a raise lower command.
 *
 * @property powerSystemResourceMRID Regulating device governed by this control output.
 * @property remoteControl The remote point controlling the physical actuator.
 */
class Control @JvmOverloads constructor(mRID: String = "") : IoPoint(mRID) {

    var powerSystemResourceMRID: String? = null
    var remoteControl: RemoteControl? = null
}
