/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.diagram.translator

import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramStyle
import com.zepben.ewb.cim.iec61970.base.diagramlayout.OrientationKind
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramStyle as PBDiagramStyle
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.OrientationKind as PBOrientationKind

internal val mapOrientationKind = EnumMapper(OrientationKind.entries, PBOrientationKind.entries)
internal val mapDiagramStyle = EnumMapper(DiagramStyle.entries, PBDiagramStyle.entries)
