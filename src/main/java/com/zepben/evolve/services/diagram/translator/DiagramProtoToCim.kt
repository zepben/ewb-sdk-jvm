/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.diagram.translator

import com.zepben.evolve.cim.iec61970.base.diagramlayout.*
import com.zepben.evolve.services.common.Resolvers
import com.zepben.evolve.services.common.translator.BaseProtoToCim
import com.zepben.evolve.services.common.translator.toCim
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.mRID
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

fun DiagramService.addFromPb(pb: PBDiagram): Diagram? = tryAddOrNull(toCim(pb, this))
fun DiagramService.addFromPb(pb: PBDiagramObject): DiagramObject? = tryAddOrNull(toCim(pb, this))

/************ Class for Java friendly usage ************/

class DiagramProtoToCim(private val diagramService: DiagramService) : BaseProtoToCim(diagramService) {

    fun addFromPb(pb: PBDiagram): Diagram? = diagramService.addFromPb(pb)
    fun addFromPb(pb: PBDiagramObject): DiagramObject? = diagramService.addFromPb(pb)

}
