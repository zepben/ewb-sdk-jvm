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
import com.zepben.ewb.services.common.translator.AddFromPbResult
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.services.common.translator.getOrAddFromPb
import com.zepben.ewb.services.common.translator.toCim
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.protobuf.dc.DiagramIdentifiedObject
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.DIAGRAM
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.DIAGRAMOBJECT
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.Diagram as PBDiagram
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObject as PBDiagramObject
import com.zepben.protobuf.cim.iec61970.base.diagramlayout.DiagramObjectPoint as PBDiagramObjectPoint

/**
 * An extension to add a converted copy of the protobuf [DiagramIdentifiedObject] to the [DiagramService].
 *
 * @receiver The [DiagramService] to add the converted [pb] into.
 * @param pb The [DiagramIdentifiedObject] to add to the [DiagramService].
 * @return The result of trying to add the item to the service.
 */
fun DiagramService.addFromPb(pb: DiagramIdentifiedObject): AddFromPbResult =
    when (pb.identifiedObjectCase) {
        DIAGRAM -> getOrAddFromPb(pb.diagram.mRID()) { addFromPb(pb.diagram) }
        DIAGRAMOBJECT -> getOrAddFromPb(pb.diagramObject.mRID()) { addFromPb(pb.diagramObject) }
        IdentifiedObjectCase.OTHER, IdentifiedObjectCase.IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${pb.identifiedObjectCase} is not supported by the diagram service")
    }

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * An extension to add a converted copy of the protobuf [PBNameType] to the [DiagramService].
 */
fun DiagramService.addFromPb(pb: PBNameType): NameType = toCim(pb, this)

// ################################
// # IEC61970 Base Diagram Layout #
// ################################

/**
 * Convert the protobuf [PBDiagram] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDiagram] to convert.
 * @param diagramService The [DiagramService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Diagram].
 */
fun toCim(pb: PBDiagram, diagramService: DiagramService): Diagram =
    Diagram(pb.mRID()).apply {
        pb.diagramObjectMRIDsList.forEach { diagramObjectMRID ->
            diagramService.resolveOrDeferReference(Resolvers.diagramObjects(this), diagramObjectMRID)
        }
        orientationKind = mapOrientationKind.toCim(pb.orientationKind)
        diagramStyle = mapDiagramStyle.toCim(pb.diagramStyle)
        toCim(pb.io, this, diagramService)
    }

/**
 * Convert the protobuf [PBDiagramObject] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDiagramObject] to convert.
 * @param diagramService The [DiagramService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [DiagramObject].
 */
fun toCim(pb: PBDiagramObject, diagramService: DiagramService): DiagramObject =
    DiagramObject(pb.mRID()).apply {
        diagramService.resolveOrDeferReference(Resolvers.diagram(this), pb.diagramMRID)
        diagram?.addDiagramObject(this)
        rotation = pb.rotation
        identifiedObjectMRID = pb.identifiedObjectMRID.takeIf { it.isNotBlank() }
        style = pb.diagramObjectStyleSet.takeUnless { pb.hasDiagramObjectStyleNull() }
        pb.diagramObjectPointsList.forEach { addPoint(toCim(it)) }
        toCim(pb.io, this, diagramService)
    }

/**
 * Convert the protobuf [PBDiagramObjectPoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDiagramObjectPoint] to convert.
 * @return The converted [pb] as a CIM [DiagramObjectPoint].
 */
fun toCim(pb: PBDiagramObjectPoint): DiagramObjectPoint =
    DiagramObjectPoint(pb.xPosition, pb.yPosition)

/**
 * An extension to add a converted copy of the protobuf [PBDiagram] to the [DiagramService].
 */
fun DiagramService.addFromPb(pb: PBDiagram): Diagram? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBDiagramObject] to the [DiagramService].
 */
fun DiagramService.addFromPb(pb: PBDiagramObject): DiagramObject? = tryAddOrNull(toCim(pb, this))

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 *
 * @property diagramService The [DiagramService] all converted objects should be added to.
 */
class DiagramProtoToCim(val diagramService: DiagramService) : BaseProtoToCim() {

    /**
     * Add a converted copy of the protobuf [DiagramIdentifiedObject] to the [DiagramService].
     *
     * @param pb The [DiagramIdentifiedObject] to convert.
     * @return The converted [AddFromPbResult] containing information on how the add was handled.
     */
    fun addFromPb(pb: DiagramIdentifiedObject): AddFromPbResult =
        diagramService.addFromPb(pb)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBNameType] to the [DiagramService].
     *
     * @param pb The [PBNameType] to convert.
     * @return The converted [NameType]
     */
    fun addFromPb(pb: PBNameType): NameType = diagramService.addFromPb(pb)

    // ################################
    // # IEC61970 Base Diagram Layout #
    // ################################

    /**
     * Add a converted copy of the protobuf [PBDiagram] to the [DiagramService].
     *
     * @param pb The [PBDiagram] to convert.
     * @return The converted [Diagram]
     */
    fun addFromPb(pb: PBDiagram): Diagram? = diagramService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBDiagramObject] to the [DiagramService].
     *
     * @param pb The [PBDiagramObject] to convert.
     * @return The converted [DiagramObject]
     */
    fun addFromPb(pb: PBDiagramObject): DiagramObject? = diagramService.addFromPb(pb)

}
