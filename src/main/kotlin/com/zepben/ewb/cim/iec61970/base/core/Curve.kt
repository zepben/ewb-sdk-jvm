/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.collections.AbstractBackedList
import com.zepben.ewb.boilerplate.collections.LazyValidatedList


/**
 * The Curve class is a multipurpose functional relationship between an independent variable (X-axis) and dependent (Y-axis) variables.
 */
abstract class Curve(mRID: String) : IdentifiedObject(mRID) {

    private var _data: MutableList<CurveData>? = null

    /**
     * The point data values that define this curve. The returned collection is read only, sorted by [CurveData.xValue] in ascending order.
     */
    val data: AbstractBackedList<CurveData> get() = LazyValidatedList(
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

    @Deprecated(
        message = "Use data.size instead.",
        replaceWith = ReplaceWith("data.size")
    )
    fun numData(): Int = data.size

    @Deprecated(
        message = "Use data.get(x) instead.",
        replaceWith = ReplaceWith("data.get(x)")
    )
    fun getData(x: Float): CurveData? = data.get(x)

    @Deprecated(
        message = "Use data.get(x) instead.",
        replaceWith = ReplaceWith("data.get(x)")
    )
    operator fun get(x: Float): CurveData? = data.get(x)

    @Deprecated(
        message = "Use data.add(CurveData(x, y1, y2, y3)) instead.",
        replaceWith = ReplaceWith("also { it.data.add(CurveData(x, y1, y2, y3)) }")
    )
    fun addData(x: Float, y1: Float, y2: Float? = null, y3: Float? = null): Curve {
        data.add(CurveData(x, y1, y2, y3))
        return this
    }

    @Deprecated(
        message = "Use data.add(curveData) instead.",
        replaceWith = ReplaceWith("also { it.data.add(curveData) }")
    )
    fun addData(curveData: CurveData): Curve {
        data.add(curveData)
        return this
    }

    @Deprecated(
        message = "Use data.remove(curveData) instead.",
        replaceWith = ReplaceWith("data.remove(curveData)")
    )
    fun removeData(curveData: CurveData): Boolean = data.remove(curveData)

    @Deprecated(
        message = "Use data.remove(x) instead.",
        replaceWith = ReplaceWith("data.remove(x)")
    )
    fun removeData(x: Float): Boolean = data.removeAt(x)

    @Deprecated(
        message = "Use data.clear() instead.",
        replaceWith = ReplaceWith("also { it.data.clear() }")
    )
    fun clearData(): Curve {
        data.clear()
        return this
    }

    // endregion

    // endregion
}

typealias CurveDataList = AbstractBackedList<CurveData>

/**
 * Get point data values by its xValue.
 *
 * @param x xValue of requested data
 */
fun CurveDataList.get(x: Float) = find { it.xValue == x }

/**
 * Remove data point from the this [Curve].
 *
 * @property x xValue of the data point to be removed
 * @return true if data point was removed.
 */
fun CurveDataList.removeAt(x: Float): Boolean =
    firstOrNull { it.xValue == x }?.let { remove(it) } ?: false
