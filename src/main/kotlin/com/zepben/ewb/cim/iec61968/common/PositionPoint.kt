/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

/**
 * Set of spatial coordinates that determine a point, defined in the coordinate system specified in 'Location.CoordinateSystem'.
 * Use a single position point instance to describe a point-oriented location. Use a sequence of position points to describe a
 * line-oriented object (physical location of non-point oriented objects like cables or lines), or area of an object (like a
 * substation or a geographical zone - in this case, have first and last position point with the same values).
 *
 * @property xPosition X axis position.
 * @property yPosition Y axis position.
 */
data class PositionPoint(val xPosition: Double, val yPosition: Double) {

    init {
        require(yPosition >= -90 && yPosition <= 90) { "Latitude is out of range. Expected -90 to 90, got $yPosition." }
        require(xPosition >= -180 && xPosition <= 180) { "Longitude is out of range. Expected -180 to 180, got $xPosition." }
    }
}
