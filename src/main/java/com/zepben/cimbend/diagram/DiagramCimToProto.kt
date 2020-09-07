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
package com.zepben.cimbend.diagram

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.cimbend.common.translator.BaseCimToProto
import com.zepben.cimbend.common.translator.toPb
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram as PBDiagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject as PBDiagramObject
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectPoint as PBDiagramObjectPoint
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectStyle as PBDiagramObjectStyle
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramStyle as PBDiagramStyle
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.OrientationKind as PBOrientationKind

/************ IEC61970 DIAGRAM LAYOUT ************/
fun toPb(cim: Diagram, pb: PBDiagram.Builder): PBDiagram.Builder =
    pb.apply {
        clearDiagramObjectMRIDs()
        cim.diagramObjects.forEach { addDiagramObjectMRIDs(it.mRID) }
        orientationKind = PBOrientationKind.valueOf(pb.orientationKind.name)
        diagramStyle = PBDiagramStyle.valueOf(pb.diagramStyle.name)
        toPb(cim, ioBuilder)
    }

fun toPb(cim: DiagramObject, pb: PBDiagramObject.Builder): PBDiagramObject.Builder =
    pb.apply {
        cim.diagram?.let { diagramMRID = it.mRID } ?: clearDiagramMRID()
        cim.identifiedObjectMRID?.let { identifiedObjectMRID = it } ?: clearIdentifiedObjectMRID()
        diagramObjectStyle = PBDiagramObjectStyle.valueOf(cim.style.name)
        rotation = cim.rotation
        clearDiagramObjectPoints()
        cim.points.forEach { point -> addDiagramObjectPoints(toPb(point, PBDiagramObjectPoint.newBuilder())) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: DiagramObjectPoint, pb: PBDiagramObjectPoint.Builder): PBDiagramObjectPoint.Builder =
    pb.apply {
        xPosition = cim.xPosition
        yPosition = cim.yPosition
    }

/************ Extension ************/

fun Diagram.toPb(): PBDiagram = toPb(this, PBDiagram.newBuilder()).build()
fun DiagramObject.toPb(): PBDiagramObject = toPb(this, PBDiagramObject.newBuilder()).build()

/************ Class for Java friendly usage ************/

class DiagramCimToProto() : BaseCimToProto() {
    fun toPb(cim: Diagram): PBDiagram = cim.toPb()
    fun toPb(cim: DiagramObject): PBDiagramObject = cim.toPb()
}
