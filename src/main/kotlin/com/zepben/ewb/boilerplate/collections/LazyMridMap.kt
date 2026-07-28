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


class LazyMridMap<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableMap<String, T>?,
    private val setter: (MutableMap<String, T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    val backfill: Backfill<T, O>? = null,
    private val validate: ((T) -> Unit)? = null
) : AbstractBackedCollection<T>(), MridCollection<T> {

    private fun clearIfEmpty() {
        if (getter()?.isEmpty() == true)
            setter(null)
    }

    override fun getByMrid(mRID: String): T? =
        getter()?.get(mRID)

    override fun getCollection(): Collection<T> =
        getter()?.values ?: emptyList()


    override fun add(element: T): Boolean {
        // Check for mRID collisions.
        // If element is already present, skip.
        // If another element shares mRID, error.
        if (!canAddByMrid(element))
            return false

        backfill?.apply(owner, element)

        // If a custom validation method is defined, run it.
        validate?.invoke(element)

        val map = getter() ?: hashMapOf<String, T>().also(setter)

        map[element.mRID] = element
        return true
    }


    override fun contains(element: T): Boolean =
        getter()?.get(element.mRID) === element

    override fun clear() {
        val old = getCollection()
        setter(null)
        backfill?.also { old.forEach { backfill.clear(it) } }
    }



    override fun remove(element: T): Boolean {
        val map = getter() ?: return false
        val existing = map[element.mRID]

        if (existing != element)
            return false

        map.remove(element.mRID)
        clearIfEmpty()

        backfill?.clear(element)

        return true
    }
}


