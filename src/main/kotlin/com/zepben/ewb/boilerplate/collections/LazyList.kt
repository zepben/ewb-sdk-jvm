/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections


/**
 * A list-like wrapper backed by a nullable list.
 *
 * A backing value of `null` is exposed as an empty collection. The backing
 * list is created when the first item is added and reset to `null` when
 * the last item is removed or the collection is cleared.
 *
 * Example:
 *
 * ```kotlin
 * class Container {
 *     private var backingItems: MutableList<String>? = null
 *
 *     val items = LazyList(
 *         getter = { backingItems },
 *         setter = { backingItems = it },
 *     )
 * }
 *
 * val container = Container()
 *
 * check(container.items.isEmpty())
 * check(container.backingItems == null)
 *
 * container.items.add("value")
 * check(container.backingItems == mutableListOf("value"))
 *
 * container.items.clear()
 * check(container.backingItems == null)
 * ```
 */
open class LazyList<T>(
    protected val getter: () -> MutableList<T>?,
    protected val setter: (MutableList<T>?) -> Unit,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractBackedList<T>(validate, sortBy) {

    /** Returns the backing list, or an unbound empty list when absent. */
    override fun getCollection(): MutableList<T> = getter() ?: mutableListOf()

    /** Resets an empty backing list to `null`. */
    protected fun clearIfEmpty() {
        if (getter()?.isEmpty() == true) {
            setter(null)
        }
    }

    /**
     * Adds [element] to the backing list, creating it if needed.
     *
     * This hook performs storage; validation and optional sorting are inherited.
     */
    override fun addRaw(element: T): Boolean =
        getter()?.add(element) ?: run {
            setter(mutableListOf(element))
            true
        }

    /** Resets the backing list after its last element is removed. */
    override fun postRemove(element: T) = clearIfEmpty()

    /** Resets the backing list to `null`. */
    override fun clearCollection(collection: MutableCollection<T>) {
        setter(null)
    }

}
