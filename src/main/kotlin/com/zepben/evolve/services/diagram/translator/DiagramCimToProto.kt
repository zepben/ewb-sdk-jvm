/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.diagram.translator

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.evolve.services.common.translator.BaseCimToProto
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram as PBDiagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject as PBDiagramObject
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectPoint as PBDiagramObjectPoint
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramStyle as PBDiagramStyle
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.OrientationKind as PBOrientationKind

/************ IEC61970 DIAGRAM LAYOUT ************/

fun toPb(cim: Diagram, pb: PBDiagram.Builder): PBDiagram.Builder =
    pb.apply {
        clearDiagramObjectMRIDs()
        cim.diagramObjects.forEach { addDiagramObjectMRIDs(it.mRID) }
        orientationKind = PBOrientationKind.valueOf(cim.orientationKind.name)
        diagramStyle = PBDiagramStyle.valueOf(cim.diagramStyle.name)
        toPb(cim, ioBuilder)
    }

fun toPb(cim: DiagramObject, pb: PBDiagramObject.Builder): PBDiagramObject.Builder =
    pb.apply {
        cim.diagram?.let { diagramMRID = it.mRID } ?: clearDiagramMRID()
        cim.identifiedObjectMRID?.let { identifiedObjectMRID = it } ?: clearIdentifiedObjectMRID()
        cim.style?.let { diagramObjectStyle = it } ?: clearDiagramObjectStyle()
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

fun Diagram.toPb(): PBDiagram = toPb(this, PBDiagram.newBuilder()).build()
fun DiagramObject.toPb(): PBDiagramObject = toPb(this, PBDiagramObject.newBuilder()).build()

/************ Class for Java friendly usage ************/

class DiagramCimToProto : BaseCimToProto() {

    // IEC61970 DIAGRAM LAYOUT
    fun toPb(cim: Diagram): PBDiagram = cim.toPb()
    fun toPb(cim: DiagramObject): PBDiagramObject = cim.toPb()

}
