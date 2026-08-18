/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.ewb.boilerplate.collections.LazyIndexList
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import java.util.function.BiConsumer

/**
 * The place, scene, or point of something where someone or something has been, is, and/or will be at a given moment in time.
 * It can be defined with one or more position points (coordinates) in a given coordinate system.
 *
 * @property mainAddress Main address of the location.
 */
class Location(mRID: String) : IdentifiedObject(mRID) {

    var mainAddress: StreetAddress? = null
    private var _positionPoints: MutableList<PositionPoint>? = null

    /**
     * Sequence of [PositionPoint]s describing this location.
     * The returned collection is read only.
     */
    val points: LazyIndexList<PositionPoint> get() = LazyIndexList(
        { _positionPoints },
        { _positionPoints = it },
        this,
        "A PositionPoint"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region points boilerplate

    @Deprecated(
        message = "Use points.size instead.",
        replaceWith = ReplaceWith("points.size")
    )
    fun numPoints(): Int = points.size

    @Deprecated(
        message = "Use points.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("points.getOrNull(sequenceNumber)")
    )
    fun getPoint(sequenceNumber: Int): PositionPoint? = points.getOrNull(sequenceNumber)

    @Deprecated(
        message = "Use points.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("points.forEachIndexed(action::accept)")
    )
    fun forEachPoint(action: BiConsumer<Int, PositionPoint>) = points.forEachIndexed(action::accept)

    @Deprecated(
        message = "Use points.add(sequenceNumber, positionPoint) instead.",
        replaceWith = ReplaceWith("also { it.points.add(sequenceNumber, positionPoint) }")
    )
    @JvmOverloads
    fun addPoint(positionPoint: PositionPoint, sequenceNumber: Int = numPoints()): Location = apply {
        points.add(sequenceNumber, positionPoint)
    }

    @Deprecated(
        message = "Use points.remove(positionPoint) instead.",
        replaceWith = ReplaceWith("points.remove(positionPoint)")
    )
    fun removePoint(positionPoint: PositionPoint): Boolean = points.remove(positionPoint)

    @Deprecated(
        message = "Use points.removeAtOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("points.removeAtOrNull(sequenceNumber)")
    )
    fun removePoint(sequenceNumber: Int): PositionPoint? = points.removeAtOrNull(sequenceNumber)

    @Deprecated(
        message = "Use points.clear() instead.",
        replaceWith = ReplaceWith("also { it.points.clear() }")
    )
    fun clearPoints(): Location = apply {
        points.clear()
    }

    // endregion

    // endregion
}

/**
 * Perform the specified action against each [PositionPoint].
 *
 * @param action The action to perform on each [PositionPoint]
 */
fun Location.forEachPoint(action: (sequenceNumber: Int, point: PositionPoint) -> Unit): Unit = forEachPoint(BiConsumer(action))
