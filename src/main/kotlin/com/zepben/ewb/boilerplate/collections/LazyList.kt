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
    protected val validate: ((T) -> Unit)? = null,
    protected val sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractBackedList<T>() {

    override fun getCollection(): MutableList<T> = getter() ?: mutableListOf()

    private fun clearIfEmpty() {
        if (getter()?.isEmpty() == true) {
            setter(null)
        }
    }

    override fun add(element: T): Boolean {
        // If a custom validation method is defined, run it
        validate?.invoke(element)

        // Try to add the item to the backing list
        // If the list is null (empty), instantiate it with the element present
        val result = getter()?.add(element) ?: run {
            setter(mutableListOf(element))
            true
        }

        // On successful addition, sort the list if the sorting order is defined
        if(result) {
            sortBy?.let {
                    selector -> getter()?.sortWith(compareBy(selector))
            }
        }

        return result
    }

    override val size: Int
        get() = getter()?.size ?: 0


    override fun clear() {
        setter(null)
    }

    override fun remove(element: T): Boolean = getter()?.remove(element).also { clearIfEmpty() } ?: false

}