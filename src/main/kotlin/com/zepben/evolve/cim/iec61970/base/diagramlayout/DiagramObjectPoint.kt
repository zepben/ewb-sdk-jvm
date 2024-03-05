/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.diagramlayout

/**
 * A point in a given space defined by 3 coordinates and associated to a diagram object.  The coordinates may be positive
 * or negative as the origin does not have to be in the corner of a diagram.
 *
 * @property xPosition The X coordinate of this point.
 * @property yPosition The Y coordinate of this point.
 */
data class DiagramObjectPoint(val xPosition: Double, val yPosition: Double)
