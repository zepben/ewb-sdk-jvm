/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.diagramlayout

/**
 * The orientation of the coordinate system with respect to top, left, and the coordinate number system.
 */
enum class OrientationKind {

    /**
     * For 2D diagrams, a positive orientation will result in X values increasing from left to right and Y values increasing
     * from bottom to top.  This is also known as a right hand orientation.
     */
    POSITIVE,

    /**
     * For 2D diagrams, a negative orientation gives the left-hand orientation (favoured by computer graphics displays)
     * with X values increasing from left to right and Y values increasing from top to bottom. This is also known as
     * a left hand orientation.
     */
    NEGATIVE

}
