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

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.*
import com.zepben.cimbend.common.Resolvers
import com.zepben.cimbend.common.translator.BaseProtoToCim
import com.zepben.cimbend.common.translator.toCim
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram as PBDiagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject as PBDiagramObject
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectPoint as PBDiagramObjectPoint

/************ IEC61970 DIAGRAM LAYOUT ************/
fun toCim(pb: PBDiagram, diagramService: DiagramService): Diagram =
    Diagram(pb.mRID()).apply {
        pb.diagramObjectMRIDsList.forEach { diagramObjectMRID ->
            diagramService.resolveOrDeferReference(Resolvers.diagramObjects(this), diagramObjectMRID)
        }
        orientationKind = OrientationKind.valueOf(pb.orientationKind.name)
        diagramStyle = DiagramStyle.valueOf(pb.diagramStyle.name)
        toCim(pb.io, this, diagramService)
    }

fun toCim(pb: PBDiagramObject, diagramService: DiagramService): DiagramObject =
    DiagramObject(pb.mRID()).apply {
        diagramService.resolveOrDeferReference(Resolvers.diagram(this), pb.diagramMRID)
        diagram?.addDiagramObject(this)
        rotation = pb.rotation
        identifiedObjectMRID = pb.identifiedObjectMRID
        style = DiagramObjectStyle.valueOf(pb.diagramObjectStyle.name)
        pb.diagramObjectPointsList.forEach { addPoint(toCim(it)) }
        toCim(pb.io, this, diagramService)
    }

fun toCim(pb: PBDiagramObjectPoint) =
    DiagramObjectPoint(pb.xPosition, pb.yPosition)

/************ Extensions ************/

fun DiagramService.addFromPb(pb: PBDiagram): Diagram = toCim(pb, this).also { add(it) }
fun DiagramService.addFromPb(pb: PBDiagramObject): DiagramObject = toCim(pb, this).also { add(it) }

/************ Class for Java friendly usage ************/

class DiagramProtoToCim(private val diagramService: DiagramService) : BaseProtoToCim(diagramService) {

    fun addFromPb(pb: PBDiagram) = diagramService.addFromPb(pb)
    fun addFromPb(pb: PBDiagramObject) = diagramService.addFromPb(pb)

}
