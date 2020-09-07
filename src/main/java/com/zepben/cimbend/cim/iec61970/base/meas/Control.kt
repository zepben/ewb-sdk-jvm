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
package com.zepben.cimbend.cim.iec61970.base.meas

import com.zepben.cimbend.cim.iec61970.base.scada.RemoteControl

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
