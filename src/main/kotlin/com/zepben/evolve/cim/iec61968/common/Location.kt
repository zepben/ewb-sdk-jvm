/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.common

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import java.util.function.BiConsumer

/**
 * The place, scene, or point of something where someone or something has been, is, and/or will be at a given moment in time.
 * It can be defined with one or more position points (coordinates) in a given coordinate system.
 *
 * @property mainAddress Main address of the location.
 */
class Location @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var mainAddress: StreetAddress? = null
    private var _positionPoints: MutableList<PositionPoint>? = null

    /**
     * Sequence of [PositionPoint]s describing this location.
     * The returned collection is read only.
     */
    val points: List<PositionPoint> get() = _positionPoints.asUnmodifiable()

    /**
     * Get the number of entries in the [PositionPoint] collection.
     */
    fun numPoints(): Int = _positionPoints?.size ?: 0

    /**
     * Get a [PositionPoint] of this [Location] by its sequence number.
     *
     * @param sequenceNumber the sequence number of the required [PositionPoint]
     * @return The [PositionPoint] with the specified [sequenceNumber] if it exists, otherwise null
     */
    fun getPoint(sequenceNumber: Int): PositionPoint? = _positionPoints?.getOrNull(sequenceNumber)

    /**
     * Java interop forEachIndexed. Perform the specified action against each [PositionPoint].
     *
     * @param action The action to perform on each [PositionPoint]
     */
    fun forEachPoint(action: BiConsumer<Int, PositionPoint>) {
        _positionPoints?.forEachIndexed(action::accept)
    }

    @JvmOverloads
    fun addPoint(positionPoint: PositionPoint, sequenceNumber: Int = numPoints()): Location {
        require(sequenceNumber in 0..(numPoints())) {
            "Unable to add PositionPoint to ${typeNameAndMRID()}. " +
                "Sequence number $sequenceNumber is invalid. Expected a value between 0 and ${numPoints()}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }

        _positionPoints = _positionPoints ?: mutableListOf()
        _positionPoints!!.add(sequenceNumber, positionPoint)

        return this
    }

    fun removePoint(positionPoint: PositionPoint?): Boolean {
        val ret = _positionPoints?.remove(positionPoint) == true
        if (_positionPoints.isNullOrEmpty()) _positionPoints = null
        return ret
    }

    fun clearPoints(): Location {
        _positionPoints = null
        return this
    }
}

/**
 * Perform the specified action against each [PositionPoint].
 *
 * @param action The action to perform on each [PositionPoint]
 */
fun Location.forEachPoint(action: (sequenceNumber: Int, point: PositionPoint) -> Unit): Unit = forEachPoint(BiConsumer(action))
