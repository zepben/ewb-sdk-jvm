/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.cim.iec61970.base.core.Identifiable


/**
 * An mRID collection backed by a nullable map.
 *
 * Items are stored by their `mRID`, while iteration exposes the map values.
 * A backing value of `null` is treated as an empty collection. The map is
 * created when the first item is added and reset to `null` when the collection
 * becomes empty.
 *
 * Example:
 *
 * ```kotlin
 * container.items.add(item)
 *
 * check(container.backingItems == mutableMapOf(item.mRID to item))
 * check(container.items.getByMrid(item.mRID) === item)
 *
 * container.items.remove(item)
 * check(container.backingItems == null)
 * ```
 */
open class LazyMridMap<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableMap<String, T>?,
    private val setter: (MutableMap<String, T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    override val backfill: Backfill<T, O>? = null,
    validate: ((T) -> Unit)? = null
) : MridBackfillCollection<T, O>(validate) {

    /** Resets an empty backing map to `null`. */
    private fun clearIfEmpty() {
        if (getter()?.isEmpty() == true)
            setter(null)
    }

    override fun getByMrid(mRID: String): T? =
        getter()?.get(mRID)

    /** Returns the map values, or an unbound empty collection when absent. */
    override fun getCollection(): MutableCollection<T> =
        getter()?.values ?: mutableListOf()

    /**
     * Adds [element] by mRID, creating the backing map if needed.
     *
     * This hook performs storage; mRID check, backfill, and validation are inherited.
     */
    override fun addRaw(element: T): Boolean {
        val map = getter() ?: hashMapOf<String, T>().also(setter)

        map[element.mRID] = element
        return true
    }

    /** Returns whether [element] is stored under its mRID. */
    override fun contains(element: T): Boolean =
        getter()?.get(element.mRID) === element

    /** Returns whether every element is stored under its mRID. */
    override fun containsAll(elements: Collection<T>): Boolean =
        elements.all { contains(it) }

    /** Removes [element] only when the stored instance matches. */
    override fun remove(element: T): Boolean {
        val map = getter() ?: return false
        val existing = map[element.mRID]

        if (existing !== element)
            return false

        map.remove(element.mRID)
        postRemove(existing)

        return true
    }

    /** Resets an empty map and clears backfill. */
    override fun postRemove(element: T) {
        clearIfEmpty()
        super.postRemove(element)
    }

    /** Resets the backing map to `null`. */
    override fun clearRaw(collection: MutableCollection<T>) {
        setter(null)
    }

    /** Resets the backing map and returns its former values. */
    override fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        setter(null)
        return collection
    }

}
