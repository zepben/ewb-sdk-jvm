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
 * A [LazyList] with explicit index-based insertion and deletion.
 *
 * It retains the nullable backing-list behaviour of [LazyList],
 * creating the backing list when an item is inserted and resetting it to
 * `null` when the final item is deleted.
 *
 * This class deliberately does not implement [MutableList]. Indexed mutation
 * is exposed by its own methods without promising mutable list iterators or
 * mutable sublist views, neither of which can reliably follow backing-list
 * replacement.
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
class LazyIndexList<T : Any>(
    getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    val owner: Identifiable,
    val elementDescription: String,
) : LazyList<T>(getter, setter) {

    private fun validateIndex(index: Int) {
        // NOTE: This error should ideally be handled by collection. possibly should replace in coming versions

        require(index in 0..size) {
            "$elementDescription could not be added to ${owner.typeNameAndMRID()}. " +
                "Sequence number $index is invalid. Expected a value between 0 and $size. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }
    }

    override fun add(element: T): Boolean {
        add(size, element)
        return true
    }

    /**
     * Adds [element] at [index] when the index is valid, creating the backing list if needed.
     *
     * This overload performs storage only: no mRID check, validation, backfill, or sorting.
     */
    fun add(index: Int, element: T) {
        val data = getter()
        validateIndex(index)
        data
            ?.add(index, element)
            ?: setter(mutableListOf(element))
    }

    fun removeAt(index: Int): T =
        getCollection().removeAt(index).also(::postRemove)

    /** Removes and returns the element at [index], or `null` if absent. */
    fun removeAtOrNull(index: Int): T? =
        if (index in indices) removeAt(index) else null

}
