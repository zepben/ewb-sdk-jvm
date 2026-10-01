/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.collections.LazyList
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerEndRatedS
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformerEnd


/** A list of [TransformerEndRatedS] for a given [PowerTransformerEnd]. */
class TransformerEndRatedSList(
    getter: () -> MutableList<TransformerEndRatedS>?,
    setter: (MutableList<TransformerEndRatedS>?) -> Unit,
    validate: ((TransformerEndRatedS) -> Unit)? = null,
    sortBy: ((TransformerEndRatedS) -> Comparable<*>?)? = null
): LazyList<TransformerEndRatedS>(
    getter = getter,
    setter = setter,
    validate = validate,
    sortBy = sortBy
) {

    /**
     * Adds a rated-power entry when inherited validation and storage accept it.
     *
     * This overload delegates validation, storage, and optional sorting.
     */
    fun add(
        ratedS: Int,
        coolingType: TransformerCoolingType = TransformerCoolingType.UNKNOWN,
    ): Boolean = add(TransformerEndRatedS(coolingType, ratedS))

    /** Returns the entry for [coolingType], or `null`. */
    operator fun get(coolingType: TransformerCoolingType): TransformerEndRatedS? = firstOrNull { it.coolingType == coolingType }

    /**
     * Remove the [TransformerEndRatedS] from the `sRatings` collection with a cooling type of [coolingType]
     *
     * @param coolingType The [TransformerCoolingType] to remove.
     * @return The [TransformerEndRatedS] that was removed, or null if none was removed.
     */
    fun removeByCoolingType(coolingType: TransformerCoolingType): TransformerEndRatedS? =
        firstOrNull { it.coolingType == coolingType }?.also {
            remove(it)
        }

}
