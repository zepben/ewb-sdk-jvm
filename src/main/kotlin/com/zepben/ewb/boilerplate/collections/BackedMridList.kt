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
 * An mRID collection backed by a non-nullable list.
 *
 * Items are kept in insertion order and retrieved by mRID using a linear search.
 * Adding enforces mRID uniqueness and can optionally apply backfill,
 * validation, and sorting.
 *
 * Unlike [LazyMridList], clearing the collection leaves an empty backing list
 * rather than resetting the backing field to `null`.
 */
open class BackedMridList<T : Identifiable, O : Identifiable>(
    private val list: MutableList<T> = mutableListOf(),
    override val owner: O,
    override val elementDescription: String,
    override val backfill: Backfill<T, O>? = null,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null,
) : AbstractMridList<T, O>(validate, sortBy) {

    override fun getCollection(): MutableList<T> = list

    override fun getByMrid(mRID: String): T? =
        list.firstOrNull { it.mRID == mRID }

}
