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
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.typeNameAndMRID
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
     * Sequence of position points describing this location, expressed in coordinate system [Location.CoordinateSystem].
     * The returned collection is read only.
     */
    val points: List<PositionPoint> get() = _positionPoints.asUnmodifiable()

    /**
     * Get the number of entries in the [PositionPoint] collection.
     */
    fun numPoints() = _positionPoints?.size ?: 0

    /**
     * Sequence of position points describing this location, expressed in coordinate system [Location.CoordinateSystem].
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
fun Location.forEachPoint(action: (sequenceNumber: Int, point: PositionPoint) -> Unit) = forEachPoint(BiConsumer(action))
