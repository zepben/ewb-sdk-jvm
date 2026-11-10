/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.relations.CurveDataList


/**
 * The Curve class is a multipurpose functional relationship between an independent variable (X-axis) and dependent (Y-axis) variables.
 */
abstract class Curve(mRID: String) : IdentifiedObject(mRID) {

    private var _data: MutableList<CurveData>? = null

    /**
     * The point data values that define this curve. The returned collection is read only, sorted by [CurveData.xValue] in ascending order.
     */
    val data: CurveDataList
        get() = CurveDataList(
            { _data },
            { _data = it },
            validate = { validateData(it) },
            sortBy = { it.xValue }
        )

    private fun validateData(curveData: CurveData) {
        require(_data.isNullOrEmpty() || _data?.none { cd -> cd.xValue == curveData.xValue } == true) {
            "Unable to add datapoint to ${typeNameAndMRID()}. " +
                "xValue ${curveData.xValue} is invalid, as data with same xValue already exist in this Curve. "
        }
    }


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region data boilerplate

    /**
     * Returns number of data point for this [Curve].
     */
    @Deprecated(
        message = "Use data.size instead.",
        replaceWith = ReplaceWith("data.size")
    )
    fun numData(): Int = _data?.size ?: 0

    /**
     * Get point data values by its xValue.
     *
     * @param x xValue of requested data
     */
    @Deprecated(
        message = "Use data.get(x) instead.",
        replaceWith = ReplaceWith("data.get(x)")
    )
    fun getData(x: Float): CurveData? = _data?.find { it.xValue == x }

    /**
     * Get point data values by its xValue.
     *
     * @param x xValue of requested data
     */
    @Deprecated(
        message = "Use data.get(x) instead.",
        replaceWith = ReplaceWith("data.get(x)")
    )
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
    @Deprecated(
        message = "Use data.add(CurveData(x, y1, y2, y3)) instead.",
        replaceWith = ReplaceWith("also { it.data.add(CurveData(x, y1, y2, y3)) }")
    )
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
    @Deprecated(
        message = "Use data.add(curveData) instead.",
        replaceWith = ReplaceWith("also { it.data.add(curveData) }")
    )
    fun addData(curveData: CurveData): Curve = addData(curveData.xValue, curveData.y1Value, curveData.y2Value, curveData.y3Value)

    /**
     * Remove data point from the this [Curve].
     *
     * @return true if data point was removed.
     */
    @Deprecated(
        message = "Use data.remove(curveData) instead.",
        replaceWith = ReplaceWith("data.remove(curveData)")
    )
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
    @Deprecated(
        message = "Use data.remove(x) instead.",
        replaceWith = ReplaceWith("data.remove(x)")
    )
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
    @Deprecated(
        message = "Use data.clear() instead.",
        replaceWith = ReplaceWith("also { it.data.clear() }")
    )
    fun clearData(): Curve {
        _data = null
        return this
    }

    // endregion

    // endregion

}
