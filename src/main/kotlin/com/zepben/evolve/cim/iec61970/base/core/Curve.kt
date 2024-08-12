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
 */
abstract class Curve @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _curveData: MutableList<CurveData>? = null

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
        if (_curveData?.any { cd -> cd.xValue == x } == true)
            throw IllegalArgumentException("A CurveData with x point $x already exists, please remove it first.")
        _curveData = _curveData ?: mutableListOf()
        _curveData!!.add(CurveData(x, y1, y2, y3))
        _curveData!!.sortBy { it.xValue }

        return this
    }

    /**
     * The individual curves for this synchronous machines. The returned collection is read only.
     */
    val data: Collection<CurveData> get() = _curveData.asUnmodifiable()

    fun getCurveData(x: Float): CurveData? = _curveData?.find { it.xValue == x }

    fun hasCurveData(): Boolean = (_curveData != null)

    fun addCurveData(curveData: CurveData): Curve = addCurveData(curveData.xValue, curveData.y1Value, curveData.y2Value, curveData.y3Value)

    /**
     * Remove [curveData] from the this [Curve].
     *
     * @return true if [curveData] was removed.
     */
    fun removeCurveData(curveData: CurveData): Boolean {
        val ret = _curveData?.remove(curveData) == true
        if (_curveData.isNullOrEmpty()) _curveData = null
        return ret
    }

    fun removeCurveData(x: Float): Boolean {
        val ret = _curveData?.firstOrNull { it.xValue == x }?.let {
            _curveData?.remove(it)
        } ?: false
        if (_curveData.isNullOrEmpty()) _curveData = null
        return ret
    }

    /**
     * Clear the [CurveData] for this Curve.
     */
    fun clearCurveData(): Curve {
        _curveData = null
        return this
    }

}
