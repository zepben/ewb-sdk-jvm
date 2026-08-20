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
 * A nullable-list implementation of [AbstractMridCollection].
 *
 * Inherits mRID lookup and uniqueness semantics from [AbstractMridCollection] and
 * exposes list-style indexed reads through [AbstractMridList].
 *
 * Iterators remain attached to the backing list present when they are created.
 * If the backing field transitions between `null` and a list while an iterator
 * is retained, that iterator does not follow the replacement list. Obtain a new
 * iterator after mutating this collection through another reference.
 */
open class LazyMridList<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableList<T>?,
    private val setter: (MutableList<T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    override val backfill: Backfill<T, O>? = null,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractMridList<T, O>(validate, sortBy) {

    /** Returns the backing list, or an unbound empty list when absent. */
    override fun getCollection(): MutableList<T> = getter() ?: mutableListOf()

    override fun getByMrid(mRID: String): T? {
        return getter()?.firstOrNull { it.mRID == mRID }
    }

    /**
     * Adds [element] to the backing list, creating it if needed.
     *
     * This hook performs storage; mRID check, backfill, validation, and optional sorting are inherited.
     */
    override fun addRaw(element: T): Boolean =
        getter()?.add(element) ?: run {
            setter(mutableListOf(element))
            true
        }

    /** Clears backfill and resets an empty backing list. */
    override fun postRemove(element: T) {
        super.postRemove(element)
        if (getter()?.isEmpty() == true)
            setter(null)
    }

    /** Resets the backing list to `null`. */
    override fun clearRaw(collection: MutableCollection<T>) {
        setter(null)
    }

    /** Resets the backing list and returns its former elements. */
    override fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        setter(null)
        return collection
    }

}
