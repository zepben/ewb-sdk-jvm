/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.cim.iec61970.base.core.Identifiable


/**
 * Base collection for objects identified by a unique `mRID`.
 *
 * Provides lookup by mRID, enforces mRID uniqueness, and maintains an optional
 * typed back-reference from each element to the collection owner.
 */
abstract class AbstractMridCollection<T : Identifiable, O : Identifiable>(
    validate: ((T) -> Unit)? = null,
) : AbstractBackedCollection<T>(validate), MridCollection<T> {

    abstract val owner: O
    abstract val elementDescription: String
    open val backfill: Backfill<T, O>? = null


    /** Returns the element with [mRID], or `null` when it is not present. */
    abstract override fun getByMrid(mRID: String): T?

    override operator fun get(mRID: String): T? = getByMrid(mRID)

    /** Accepts a new mRID, ignores the same instance, and rejects collisions. */
    protected fun canAddByMrid(element: T): Boolean {
        val existing = getByMrid(element.mRID) ?: return true

        require(existing === element) {
            "$elementDescription with mRID ${element.mRID} already exists in ${owner.typeNameAndMRID()}."
        }

        return false
    }

    /**
     * Adds [element] when its mRID is available and validation succeeds.
     *
     * This override performs the mRID check, backfill, validation, and storage, but no sorting.
     */
    override fun add(element: T): Boolean {
        if (!canAddByMrid(element))
            return false

        backfill?.set(element, owner)
        return super.add(element)
    }

    /** Clears [element]'s backfill after removal. */
    override fun postRemove(element: T) {
        backfill?.clear(element)
    }

    /** Clears the collection and all backfilled references. */
    override fun clear() {
        val activeBackfill = backfill ?: return super.clear()
        clearAndCopy(getCollection()).forEach(activeBackfill::clear)
    }

}
