/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

import com.zepben.ewb.cim.iec61970.base.core.Identifiable


/**
 * A [LazyList] with index-based insertion and deletion.
 *
 * It retains the nullable backing-list behaviour of [LazyList],
 * creating the backing list when an item is inserted and resetting it to
 * `null` when the final item is deleted.
 *
 * Example:
 *
 * ```kotlin
 * container.items.add(0, "value")
 * check(container.backingItems == mutableListOf("value"))
 *
 * container.items.removeAt(0)
 * check(container.backingItems == null)
 * ```
 */
class LazyIndexList<T>(
    getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    val owner: Identifiable,
    val elementDescription: String,
) : LazyList<T>(getter, setter) {

    /**
     * Adds [element] at [index] when the index is valid, creating the backing list if needed.
     *
     * This overload performs storage only: no mRID check, validation, backfill, or sorting.
     */
    fun add(index: Int, element: T) {
        val data = getter()
        require(index in 0..size) {
            "Unable to add $elementDescription to ${owner.typeNameAndMRID()}. " +
                "Sequence number $index is invalid. Expected a value between 0 and ${size}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }
        data
            ?.add(index, element)
            ?:setter(mutableListOf(element))
    }

    /** Removes and returns the element at [index], or `null` if absent. */
    fun removeAt(index: Int): T? {
        if (index !in indices)
            return null

        return getter()!!.removeAt(index).also(::postRemove)
    }

}
