/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.asUnmodifiable


/**
 * The Curve class is a multipurpose functional relationship between an independent variable (X-axis) and dependent (Y-axis) variables.
 *
 * @property _curveDatas The point data values that define this curve.
 */
abstract class Curve @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _curveDatas: MutableList<CurveData>? = null

    /**
     * Add a curveData to the Curve.
     * The curveData in the underlying collection will be sorted by [x] Value in ascending order.
     *
     * @param x The data value of the X-axis variable, depending on the X-axis units.
     * @param y1 The data value of the first Y-axis variable, depending on the Y-axis units.
     * @param y2 The data value of the second Y-axis variable (if present), depending on the Y-axis units.
     * @param y3 The data value of the third Y-axis variable (if present), depending on the Y-axis units.
     * @throws IllegalArgumentException if a curveData for the provided [x] value already exists for this Curve.
     */
    fun addCurveData(x: Float, y1: Float, y2: Float? = null, y3: Float? = null): Curve {
        if (_curveDatas?.any { cd -> cd.getXValue() == x } == true)
            throw IllegalArgumentException("A CurveData with x point $x already exists, please remove it first.")
        _curveDatas = _curveDatas ?: mutableListOf()
        _curveDatas!!.add(CurveData(x, y1, y2, y3))
        _curveDatas!!.sortBy { it.getXValue() }

        return this
    }

    /**
     * The individual curves for this synchronous machines. The returned collection is read only.
     */
    val data: Collection<CurveData> get() = _curveDatas.asUnmodifiable()

    fun getCurveData(x: Float): CurveData? = _curveDatas?.find { it.getXValue() == x }

    fun hasCurveData(): Boolean = (_curveDatas != null)

    fun addCurveData(curveData: CurveData): Curve = addCurveData(curveData.getXValue(), curveData.getY1Value(), curveData.getY2Value(), curveData.getY3Value())

    /**
     * Remove [curveData] from the [_curveDatas] collection.
     *
     * @return true if [curveData] was removed.
     */
    fun removeCurveData(curveData: CurveData): Boolean {
        val ret = _curveDatas?.remove(curveData) == true
        if (_curveDatas.isNullOrEmpty()) _curveDatas = null
        return ret
    }

    fun removeCurveData(x: Float): Boolean {
        val ret = _curveDatas?.firstOrNull { it.getXValue() == x }?.let {
            _curveDatas?.remove(it)
        } ?: false
        if (_curveDatas.isNullOrEmpty()) _curveDatas = null
        return ret
    }

    /**
     * Clear the [_curveDatas] for this Curve.
     */
    fun clearCurveDatas(): Curve {
        _curveDatas = null
        return this
    }

}
