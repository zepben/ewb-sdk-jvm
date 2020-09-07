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
import com.zepben.cimbend.common.BaseServiceComparator
import com.zepben.cimbend.common.ObjectDifference

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
            compareValues(DiagramObject::identifiedObjectMRID, DiagramObject::style, DiagramObject::rotation)
            compareIndexedValueCollections(DiagramObject::points)
        }
}
