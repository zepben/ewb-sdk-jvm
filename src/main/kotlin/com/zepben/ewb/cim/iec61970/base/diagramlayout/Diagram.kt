/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.diagramlayout

import com.zepben.ewb.boilerplate.LazyMridMap
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

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
    val diagramObjects: LazyMridMap<DiagramObject> get() = LazyMridMap(
        getter = { _diagramObjects },
        setter = { _diagramObjects = it },
        owner = this,
        elementDescription = "A DiagramObject",
        validate = ::validateDiagramObject
    )

    private fun validateDiagramObject(diagramObject: DiagramObject) {
        if (diagramObject.diagram == null)
            diagramObject.diagram = this

        require(diagramObject.diagram === this) {
            "${diagramObject.typeNameAndMRID()} `diagram` property references ${diagramObject.diagram!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
    }

    // region deprecated dict boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    @Deprecated(
        message = "Use diagramObjects.size instead.",
        replaceWith = ReplaceWith("diagramObjects.size")
    )
    fun numDiagramObjects(): Int = diagramObjects.size

    @Deprecated(
        message = "Use diagramObjects.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("diagramObjects.getByMrid(mRID)")
    )
    fun getDiagramObject(mRID: String): DiagramObject? = diagramObjects.getByMrid(mRID)

    @Deprecated(
        message = "Use diagramObjects.add(diagramObject) instead.",
        replaceWith = ReplaceWith("also { it.diagramObjects.add(diagramObject) }")
    )
    fun addDiagramObject(diagramObject: DiagramObject): Diagram {
        diagramObjects.add(diagramObject)
        return this
    }

    @Deprecated(
        message = "Use diagramObjects.remove(diagramObject) instead.",
        replaceWith = ReplaceWith("diagramObjects.remove(diagramObject)")
    )
    fun removeDiagramObject(diagramObject: DiagramObject): Boolean =
        diagramObjects.remove(diagramObject)

    @Deprecated(
        message = "Use diagramObjects.clear() instead.",
        replaceWith = ReplaceWith("also { it.diagramObjects.clear() }")
    )
    fun clearDiagramObjects(): Diagram {
        diagramObjects.clear()
        return this
    }

    // endregion

}
