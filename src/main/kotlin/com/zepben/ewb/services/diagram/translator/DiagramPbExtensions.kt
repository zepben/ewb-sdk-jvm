/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.diagram.translator

import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject


fun Diagram.mRID(): String = io.mrid
fun DiagramObject.mRID(): String = io.mrid
