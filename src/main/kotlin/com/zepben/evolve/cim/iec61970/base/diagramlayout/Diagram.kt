/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.diagramlayout

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.extensions.validateReference

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

        if (diagramObject.diagram == null)
            diagramObject.diagram = this

        require(diagramObject.diagram === this) {
            "${diagramObject.typeNameAndMRID()} `diagram` property references ${diagramObject.diagram!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
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
