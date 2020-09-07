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

/**
 * A point in a given space defined by 3 coordinates and associated to a diagram object.  The coordinates may be positive
 * or negative as the origin does not have to be in the corner of a diagram.
 *
 * @property xPosition The X coordinate of this point.
 * @property yPosition The Y coordinate of this point.
 */
data class DiagramObjectPoint(val xPosition: Double, val yPosition: Double)
