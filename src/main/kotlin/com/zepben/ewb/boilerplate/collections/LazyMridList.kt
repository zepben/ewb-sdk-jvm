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
 * A nullable-list implementation of [MridCollection].
 *
 * Inherits mRID lookup and uniqueness semantics from [MridCollection] and
 * exposes list-style indexed reads through [AbstractMridList].
 */
open class LazyMridList<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableList<T>?,
    private val setter: (MutableList<T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    override val backfill: Backfill<T, O>? = null,
    private val validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractMridList<T>() {

    override fun getCollection(): MutableList<T> = getter() ?: mutableListOf()

    override fun getByMrid(mRID: String): T? {
        return getter()?.firstOrNull { it.mRID == mRID }
    }

    override fun add(element: T): Boolean {
        // Check for mRID collisions
        // If element is already present, skip;
        // If another element shares mRID, error
        if (!canAddByMrid(element))
            return false

        backfill?.apply(owner, element)

        validate?.invoke(element)

        val result = getter()?.add(element) ?: run {
            setter(mutableListOf(element))
            true
        }

        if (result)
            sortBy?.let { selector -> getter()?.sortWith(compareBy(selector)) }

        return result
    }

    override fun postRemove(element: T) {
        super.postRemove(element)
        if (getter()?.isEmpty() == true)
            setter(null)
    }

    override fun clearCollection(collection: MutableCollection<T>) {
        setter(null)
    }

    override fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        setter(null)
        return collection
    }
}
