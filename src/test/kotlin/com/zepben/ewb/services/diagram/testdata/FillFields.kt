/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.diagram.testdata

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.diagramlayout.*
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.diagram.DiagramService

// ################################
// # IEC61970 Base Diagram Layout #
// ################################

fun Diagram.fillFields(service: DiagramService, includeRuntime: Boolean = true): Diagram {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    diagramStyle = DiagramStyle.GEOGRAPHIC
    orientationKind = OrientationKind.NEGATIVE

    for (i in 0..1)
        addDiagramObject(DiagramObject().also { service.add(it) })

    return this
}

fun DiagramObject.fillFields(service: DiagramService, includeRuntime: Boolean = true): DiagramObject {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    diagram = Diagram().also { service.add(it) }
    diagram?.addDiagramObject(this)

    identifiedObjectMRID = "io_mrid"
    style = "CONDUCTOR_UNKNOWN"
    rotation = 1.1

    for (i in 0..1)
        addPoint(DiagramObjectPoint(i * 1.1, i * 10.01))

    return this
}
