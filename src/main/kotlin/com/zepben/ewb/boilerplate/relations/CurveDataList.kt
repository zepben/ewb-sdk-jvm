/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.collections.LazyList
import com.zepben.ewb.cim.iec61970.base.core.Curve
import com.zepben.ewb.cim.iec61970.base.core.CurveData

/** A list of [CurveData] for a given [Curve]. */
class CurveDataList(
    getter: () -> MutableList<CurveData>?,
    setter: (MutableList<CurveData>?) -> Unit,
    validate: ((CurveData) -> Unit)? = null,
    sortBy: ((CurveData) -> Comparable<*>?)? = null
): LazyList<CurveData>(
    getter = getter,
    setter = setter,
    validate = validate,
    sortBy = sortBy
) {

    /**
     * Get point data values by its xValue.
     *
     * @param x xValue of requested data
     */
    fun get(x: Float) = find { it.xValue == x }

    /**
     * Remove data point from the this [Curve].
     *
     * @param x xValue of the data point to be removed
     * @return true if data point was removed.
     */
    fun removeAt(x: Float): Boolean =
        firstOrNull { it.xValue == x }?.let { remove(it) } ?: false

}
