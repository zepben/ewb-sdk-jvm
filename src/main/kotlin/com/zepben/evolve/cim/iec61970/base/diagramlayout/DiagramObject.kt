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
import java.util.function.BiConsumer

/**
 * An object that defines one or more points in a given space. This object can be associated with anything
 * that specializes IdentifiedObject. For single line diagrams such objects typically include such items as
 * analog values, breakers, disconnectors, power transformers, and transmission lines.
 *
 * @property diagram A diagram object is part of a diagram.
 * @property identifiedObjectMRID The domain object to which this diagram object is associated.
 * @property style A diagram object has a style associated that provides a reference for the style used in the originating system.
 * @property rotation Sets the angle of rotation of the diagram object.  Zero degrees is pointing to the top of the diagram.  Rotation is clockwise.
 */
class DiagramObject @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var diagram: Diagram? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("diagram has already been set to $field. Cannot set this field again")
        }

    var identifiedObjectMRID: String? = null
    var style: String? = null
    var rotation: Double = 0.0
    private var _diagramObjectPoints: MutableList<DiagramObjectPoint>? = null

    /**
     * The points for this diagram object. The returned collection is read only.
     */
    val points: List<DiagramObjectPoint> get() = _diagramObjectPoints.asUnmodifiable()

    /**
     * Get a count of the [DiagramObjectPoint]'s associated with this [DiagramObject]
     */
    fun numPoints() = _diagramObjectPoints?.size ?: 0

    /**
     * A diagram object can have 0 or more points to reflect its layout position, routing
     * (for polylines) or boundary (for polygons). Index in the list corresponds to the sequence number
     */
    fun getPoint(sequenceNumber: Int) = _diagramObjectPoints?.get(sequenceNumber)

    /**
     * Get a [DiagramObjectPoint] by its sequenceNumber relative to this [DiagramObject]
     */
    operator fun get(sequenceNumber: Int) = getPoint(sequenceNumber)

    /**
     * Java interop forEachIndexed. Performs the given [action] on each element.
     *
     * @param action The action to perform on each [DiagramObjectPoint]
     */
    fun forEachPoint(action: BiConsumer<Int, DiagramObjectPoint>) {
        _diagramObjectPoints?.forEachIndexed(action::accept)
    }

    /**
     * Add a [DiagramObjectPoint] to this [DiagramObject]
     * @param diagramObjectPoint The [DiagramObjectPoint] to add
     * @param sequenceNumber The sequence number of the [DiagramObjectPoint].
     */
    @JvmOverloads
    fun addPoint(diagramObjectPoint: DiagramObjectPoint, sequenceNumber: Int = numPoints()): DiagramObject {
        require(sequenceNumber in 0..(numPoints())) {
            "Unable to add DiagramObjectPoint to ${typeNameAndMRID()}. " +
                "Sequence number $sequenceNumber is invalid. Expected a value between 0 and ${numPoints()}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }

        _diagramObjectPoints = _diagramObjectPoints ?: mutableListOf()
        _diagramObjectPoints!!.apply { add(sequenceNumber, diagramObjectPoint) }

        return this
    }

    /**
     * Remove a [DiagramObjectPoint] from this [DiagramObject]
     * @param diagramObjectPoint The [DiagramObjectPoint] to remove.
     * @return true if the [DiagramObjectPoint] was removed.
     */
    fun removePoint(diagramObjectPoint: DiagramObjectPoint?): Boolean {
        val ret = _diagramObjectPoints?.remove(diagramObjectPoint) == true
        if (_diagramObjectPoints.isNullOrEmpty()) _diagramObjectPoints = null
        return ret
    }

    /**
     * Clear all [DiagramObjectPoint]'s from this [DiagramObject]
     */
    fun clearPoints(): DiagramObject {
        _diagramObjectPoints = null
        return this
    }
}

/**
 * Performs the given [action] on each element.
 *
 * @param action The action to perform on each [DiagramObjectPoint]
 */
fun DiagramObject.forEachPoint(action: (sequenceNumber: Int, point: DiagramObjectPoint) -> Unit) = forEachPoint(BiConsumer(action))
