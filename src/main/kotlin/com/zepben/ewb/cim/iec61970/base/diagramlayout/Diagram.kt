/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.diagramlayout

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridMap
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * The diagram being exchanged.  The coordinate system is a standard Cartesian coordinate system and the orientation attribute defines the orientation.
 *
 * @property diagramStyle A Diagram may have a DiagramStyle.
 * @property orientationKind Coordinate system orientation of the diagram.
 */
class Diagram(mRID: String) : IdentifiedObject(mRID) {

    var diagramStyle: DiagramStyle = DiagramStyle.SCHEMATIC
    var orientationKind: OrientationKind = OrientationKind.POSITIVE

    private var _diagramObjects: MutableMap<String, DiagramObject>? = null

    /**
     * The diagramObjects belonging to this object.
     */
    val diagramObjects: MridCollection<DiagramObject> get() = LazyMridMap(
        getter = { _diagramObjects },
        setter = { _diagramObjects = it },
        owner = this,
        elementDescription = "A DiagramObject",
        backfill = Backfill(
            { it.diagram },
            { it, dia -> it.diagram = dia },
            DiagramObject::diagram
        ),
    )


    // region deprecated dict boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    /**
     * Get the number of entries in the [DiagramObject] collection.
     */
    @Deprecated(
        message = "Use diagramObjects.size instead.",
        replaceWith = ReplaceWith("diagramObjects.size")
    )
    fun numDiagramObjects(): Int = _diagramObjects?.size ?: 0

    /**
     * A diagram is made up of multiple diagram objects.
     *
     * @param mRID the mRID of the required [DiagramObject]
     * @return The [DiagramObject] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use diagramObjects.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("diagramObjects.getByMrid(mRID)")
    )
    fun getDiagramObject(mRID: String): DiagramObject? = _diagramObjects?.get(mRID)

    /**
     * @param diagramObject The diagram object to add to the [DiagramObject] collection.
     */
    @Deprecated(
        message = "Use diagramObjects.add(diagramObject) instead.",
        replaceWith = ReplaceWith("also { it.diagramObjects.add(diagramObject) }")
    )
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
    @Deprecated(
        message = "Use diagramObjects.remove(diagramObject) instead.",
        replaceWith = ReplaceWith("diagramObjects.remove(diagramObject)")
    )
    fun removeDiagramObject(diagramObject: DiagramObject): Boolean {
        val ret = _diagramObjects?.remove(diagramObject.mRID) != null
        if (_diagramObjects.isNullOrEmpty()) clearDiagramObjects()
        return ret
    }

    /**
     * Removes all diagram objects from the [DiagramObject] collection.
     */
    @Deprecated(
        message = "Use diagramObjects.clear() instead.",
        replaceWith = ReplaceWith("also { it.diagramObjects.clear() }")
    )
    fun clearDiagramObjects(): Diagram {
        _diagramObjects = null
        return this
    }

    // endregion

}
