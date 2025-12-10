/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.testing.ListWrapper
import com.zepben.ewb.testing.MRIDListWrapper


/**
 * The Curve class is a multipurpose functional relationship between an independent variable (X-axis) and dependent (Y-axis) variables.
 */
abstract class Curve @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _data: MutableList<CurveData>? = null

    /**
     * The point data values that define this curve. The returned collection is read only, sorted by [CurveData.xValue] in ascending order.
     */
    val data: ListWrapper<CurveData>
        get() = ListWrapper(
            getter = { _data },
            setter = { _data = it })

    @Deprecated("BOILERPLATE: Use data.size instead")
    fun numData(): Int = data.size

    /**
     * Get point data values by its xValue.
     *
     * @param x xValue of requested data
     */
    fun getData(x: Float): CurveData? = _data?.find { it.xValue == x }

    /**
     * Get point data values by its xValue.
     *
     * @param x xValue of requested data
     */
    operator fun get(x: Float): CurveData? = getData(x)

    /**
     * Add a data point to this [Curve].
     *
     * @param x The data value of the X-axis variable, depending on the X-axis units.
     * @param y1 The data value of the first Y-axis variable, depending on the Y-axis units.
     * @param y2 The data value of the second Y-axis variable (if present), depending on the Y-axis units.
     * @param y3 The data value of the third Y-axis variable (if present), depending on the Y-axis units.
     * @throws IllegalArgumentException if a [CurveData] for the provided [x] value already exists for this Curve.
     */
    fun addData(x: Float, y1: Float, y2: Float? = null, y3: Float? = null): Curve {
        require(_data.isNullOrEmpty() || _data?.none { cd -> cd.xValue == x } == true) {
            "Unable to add datapoint to ${typeNameAndMRID()}. " +
                "xValue $x is invalid, as data with same xValue already exist in this Curve. "
        }
        _data = _data.or(::mutableListOf) {
            add(CurveData(x, y1, y2, y3))
            sortBy { it.xValue }
        }
        return this
    }

    /**
     * Add a data point to this [Curve].
     *
     * @param curveData data to be added to this curve
     */
    fun addData(curveData: CurveData): Curve = addData(curveData.xValue, curveData.y1Value, curveData.y2Value, curveData.y3Value)

    /**
     * Remove data point from the this [Curve].
     *
     * @return true if data point was removed.
     */
    fun removeData(curveData: CurveData): Boolean {
        val ret = _data?.remove(curveData) == true
        if (_data.isNullOrEmpty()) _data = null
        return ret
    }

    /**
     * Remove data point from the this [Curve].
     *
     * @property x xValue of the data point to be removed
     * @return true if data point was removed.
     */
    fun removeData(x: Float): Boolean {
        val ret = _data?.firstOrNull { it.xValue == x }?.let {
            _data?.remove(it)
        } ?: false
        if (_data.isNullOrEmpty()) _data = null
        return ret
    }

    /**
     * Clear the [CurveData] for this Curve.
     */
    fun clearData(): Curve {
        _data = null
        return this
    }

}
