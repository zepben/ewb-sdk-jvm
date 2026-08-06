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
 * A mutable [LazyList] with index-based access, insertion, replacement, and deletion.
 *
 * It retains the nullable backing-list behaviour of [LazyList],
 * creating the backing list when an item is inserted and resetting it to
 * `null` when the final item is deleted. Sublists are unsupported because
 * they cannot follow replacement of the nullable backing list.
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
) : LazyList<T>(getter, setter), MutableList<T> {

    /** Requires [index] to identify an element, or the end when [allowEnd] is set. */
    private fun validateIndex(index: Int, allowEnd: Boolean) {
        // NOTE: This should ideally be handled by collection. possibly should replace in coming versions
        val upperBound = if (allowEnd) size else lastIndex
        require(index in 0..upperBound) {
            "$elementDescription could not be added to ${owner.typeNameAndMRID()}. " +
                "Sequence number $index is invalid. Expected a value between 0 and $upperBound. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }
    }

    /**
     * Adds [element] at [index] when the index is valid, creating the backing list if needed.
     *
     * This overload performs storage only: no mRID check, validation, backfill, or sorting.
     */
    override fun add(index: Int, element: T) {
        val data = getter()
        validateIndex(index, allowEnd = true)
        data
            ?.add(index, element)
            ?: setter(mutableListOf(element))
    }

    /** Adds [elements] at [index], creating the backing list if needed. */
    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        validateIndex(index, allowEnd = true)

        if (elements.isEmpty())
            return false

        return getter()?.addAll(index, elements) ?: run {
            setter(elements.toMutableList())
            true
        }
    }

    /** Removes and returns the element at [index]. */
    override fun removeAt(index: Int): T =
        getCollection().removeAt(index).also(::postRemove)

    /** Removes and returns the element at [index], or `null` if absent. */
    fun removeAtOrNull(index: Int): T? =
        if (index in indices) removeAt(index) else null

    /** Replaces and returns the element at [index]. */
    override fun set(index: Int, element: T): T {
        validateIndex(index, allowEnd = false)
        return getCollection().set(index, element)
    }

    /** Returns a mutable list iterator at the start. */
    override fun listIterator(): MutableListIterator<T> =
        listIterator(0)

    /** Returns a mutable list iterator at [index]. */
    override fun listIterator(index: Int): MutableListIterator<T> {
        val data = getter() ?: mutableListOf()
        return CallbackMutableListIterator(
            data.listIterator(index),
            ::postRemove
        ){
            if (getter() == null)
                setter(data)
        }
    }

    /** Rejects sublists because they cannot follow nullable backing-list replacement. */
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        throw UnsupportedOperationException("LazyIndexList does not support sublists")

}

/** A list iterator that reports removals and additions. */
private class CallbackMutableListIterator<T>(
    private val listDelegate: MutableListIterator<T>,
    afterRemove: (T) -> Unit,
    private val afterAdd: () -> Unit,
) : CallbackMutableIterator<T>(listDelegate, afterRemove), MutableListIterator<T> {

    /** Adds [element] and invokes the callback. */
    override fun add(element: T) {
        listDelegate.add(element)
        current = null
        afterAdd()
    }

    /** Checks whether a previous element exists. */
    override fun hasPrevious(): Boolean = listDelegate.hasPrevious()

    /** Checks the next element index. */
    override fun nextIndex(): Int = listDelegate.nextIndex()

    /** Checks and records the previous element. */
    override fun previous(): T = listDelegate.previous().also { current = Current(it) }

    /** Checks the previous element index. */
    override fun previousIndex(): Int = listDelegate.previousIndex()

    /** Replaces the current element with [element]. */
    override fun set(element: T) = listDelegate.set(element)
}
