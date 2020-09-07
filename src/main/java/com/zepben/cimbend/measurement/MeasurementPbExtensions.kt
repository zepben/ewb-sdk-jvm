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
package com.zepben.cimbend.measurement

import com.zepben.protobuf.cim.iec61970.base.meas.Control
import com.zepben.protobuf.cim.iec61970.base.meas.IoPoint
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource


fun Control.mRID(): String = ip.mRID()
fun IoPoint.mRID(): String = io.mrid
fun Measurement.mRID(): String = io.mrid

fun RemoteControl.mRID(): String = rp.mRID()
fun RemotePoint.mRID(): String = io.mrid
fun RemoteSource.mRID(): String = rp.mRID()
