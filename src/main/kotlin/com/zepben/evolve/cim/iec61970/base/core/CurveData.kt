/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

/**
 * Multipurpose data points for defining a curve. The use of this generic class is discouraged if a more specific class can be used to specify the X and
 * Y axis values along with their specific data types.
 *
 * @property xValue The data value of the X-axis variable, depending on the X-axis units.
 * @property y1Value The data value of the first Y-axis variable, depending on the Y-axis units.
 * @property y2Value The data value of the second Y-axis variable (if present), depending on the Y-axis units.
 * @property y3Value The data value of the third Y-axis variable (if present), depending on the Y-axis units.
 */
data class CurveData(private val xValue: Float, private val y1Value: Float, private val y2Value: Float? = null, private val y3Value: Float? = null) {

    fun getXValue(): Float = xValue
    fun getY1Value(): Float = y1Value
    fun getY2Value(): Float? = y2Value
    fun getY3Value(): Float? = y3Value

}
