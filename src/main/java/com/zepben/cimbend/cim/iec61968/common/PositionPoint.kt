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
