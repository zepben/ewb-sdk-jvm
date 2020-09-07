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
package com.zepben.cimbend.cim.iec61970.base.diagramlayout

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.nameAndMRID
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * The diagram being exchanged.  The coordinate system is a standard Cartesian coordinate system and the orientation attribute defines the orientation.
 *
 * @property diagramStyle A Diagram may have a DiagramStyle.
 * @property orientationKind Coordinate system orientation of the diagram.
 */
class Diagram @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var diagramStyle: DiagramStyle = DiagramStyle.SCHEMATIC
    var orientationKind: OrientationKind = OrientationKind.POSITIVE

    private var _diagramObjects: MutableMap<String?, DiagramObject>? = null

    /**
     * The diagram objects belonging to this diagram. The returned collection is read only.
     */
    val diagramObjects: Collection<DiagramObject> get() = _diagramObjects?.values.asUnmodifiable()

    /**
     * Get the number of entries in the [DiagramObject] collection.
     */
    fun numDiagramObjects() = _diagramObjects?.size ?: 0

    /**
     * A diagram is made up of multiple diagram objects.
     *
     * @param mRID the mRID of the required [DiagramObject]
     * @return The [DiagramObject] with the specified [mRID] if it exists, otherwise null
     */
    fun getDiagramObject(mRID: String) = _diagramObjects?.get(mRID)

    /**
     * @param diagramObject The diagram object to add to the [DiagramObject] collection.
     */
    fun addDiagramObject(diagramObject: DiagramObject): Diagram {
        if (validateReference(diagramObject, ::getDiagramObject, "A DiagramObject"))
            return this

        require(diagramObject.diagram === this) {
            if (diagramObject.diagram == null)
                "Diagram has not been set for ${diagramObject.typeNameAndMRID()}"
            else
                "${diagramObject.typeNameAndMRID()} references another Diagram ${diagramObject.diagram!!.nameAndMRID()}, expected ${nameAndMRID()}."
        }

        _diagramObjects = _diagramObjects ?: mutableMapOf()
        _diagramObjects!!.putIfAbsent(diagramObject.mRID, diagramObject)

        return this
    }

    /**
     * @param diagramObject The diagram object to remove from the [DiagramObject] collection.
     */
    fun removeDiagramObject(diagramObject: DiagramObject?): Boolean {
        val ret = _diagramObjects?.remove(diagramObject?.mRID) != null
        if (_diagramObjects.isNullOrEmpty()) clearDiagramObjects()
        return ret
    }

    /**
     * Removes all diagram objects from the [DiagramObject] collection.
     */
    fun clearDiagramObjects(): Diagram {
        _diagramObjects = null
        return this
    }
}
