/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.diagram.translator

import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.base.diagramlayout.*
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.services.common.translator.toCim
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram as PBDiagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject as PBDiagramObject
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectPoint as PBDiagramObjectPoint

// ######################
// # IEC61970 Base Core #
// ######################

fun DiagramService.addFromPb(pb: PBNameType): NameType = toCim(pb, this)

// ################################
// # IEC61970 Base Diagram Layout #
// ################################

fun toCim(pb: PBDiagram, diagramService: DiagramService): Diagram =
    Diagram(pb.mRID()).apply {
        pb.diagramObjectMRIDsList.forEach { diagramObjectMRID ->
            diagramService.resolveOrDeferReference(Resolvers.diagramObjects(this), diagramObjectMRID)
        }
        orientationKind = mapOrientationKind.toCim(pb.orientationKind)
        diagramStyle = mapDiagramStyle.toCim(pb.diagramStyle)
        toCim(pb.io, this, diagramService)
    }

fun toCim(pb: PBDiagramObject, diagramService: DiagramService): DiagramObject =
    DiagramObject(pb.mRID()).apply {
        diagramService.resolveOrDeferReference(Resolvers.diagram(this), pb.diagramMRID)
        diagram?.addDiagramObject(this)
        rotation = pb.rotation
        identifiedObjectMRID = pb.identifiedObjectMRID.takeIf { it.isNotBlank() }
        style = pb.diagramObjectStyleSet.takeIf { pb.hasDiagramObjectStyleNull() }
        pb.diagramObjectPointsList.forEach { addPoint(toCim(it)) }
        toCim(pb.io, this, diagramService)
    }

fun toCim(pb: PBDiagramObjectPoint): DiagramObjectPoint =
    DiagramObjectPoint(pb.xPosition, pb.yPosition)

fun DiagramService.addFromPb(pb: PBDiagram): Diagram? = tryAddOrNull(toCim(pb, this))
fun DiagramService.addFromPb(pb: PBDiagramObject): DiagramObject? = tryAddOrNull(toCim(pb, this))

// #################################
// # Class for Java friendly usage #
// #################################

class DiagramProtoToCim(private val diagramService: DiagramService) : BaseProtoToCim() {

    // ######################
    // # IEC61970 Base Core #
    // ######################

    fun addFromPb(pb: PBNameType): NameType = diagramService.addFromPb(pb)

    // ################################
    // # IEC61970 Base Diagram Layout #
    // ################################

    fun addFromPb(pb: PBDiagram): Diagram? = diagramService.addFromPb(pb)
    fun addFromPb(pb: PBDiagramObject): DiagramObject? = diagramService.addFromPb(pb)

}
