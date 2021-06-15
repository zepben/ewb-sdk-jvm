/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.diagram

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.evolve.services.common.ObjectDifference

//
// NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
//       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
//       function, so make sure you check the code coverage
//
@Suppress("unused")
class DiagramServiceComparator : BaseServiceComparator() {

    private fun compareDiagram(source: Diagram, target: Diagram): ObjectDifference<Diagram> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareValues(Diagram::diagramStyle, Diagram::orientationKind)
            compareIdReferenceCollections(Diagram::diagramObjects)
        }

    private fun compareDiagramObject(source: DiagramObject, target: DiagramObject): ObjectDifference<DiagramObject> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferences(DiagramObject::diagram)
            compareValues(DiagramObject::identifiedObjectMRID, DiagramObject::style)
            compareDoubles(DiagramObject::rotation)
            compareIndexedValueCollections(DiagramObject::points)
        }

}
