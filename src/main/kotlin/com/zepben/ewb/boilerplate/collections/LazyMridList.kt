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
 * nullable backing-list behaviour from [LazyList].
 */
open class LazyMridList<T : Identifiable, O : Identifiable>(
    getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    val backfill: Backfill<T, O>? = null,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null
) : LazyList<T>(
    getter, setter, validate, sortBy
), MridCollection<T> {

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

        return super.add(element)
    }

    override fun remove(element: T): Boolean {
        val removed =  super.remove(element)
        if (removed)
            backfill?.clear(element)
        return removed
    }

    override fun clear() {
        val old = getter() ?: emptyList()
        super.clear()
        backfill?.also { old.forEach { backfill.clear(it) } }
    }

}
