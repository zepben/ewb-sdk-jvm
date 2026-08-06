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
 * Base collection for objects identified by a unique `mRID`.
 *
 * Provides lookup by mRID and prepares additions by enforcing mRID uniqueness
 * and applying optional backfill before base collection validation.
 */
abstract class MridCollection<T : Identifiable>(
    validate: ((T) -> Unit)? = null,
) : AbstractBackedCollection<T>(validate) {
    abstract val owner: Identifiable
    abstract val elementDescription: String

    open val backfill: Backfill<T, *>? = null

    /** Returns the element with [mRID], or `null` when it is not present. */
    abstract fun getByMrid(mRID: String): T?

    /** Returns the element with [mRID], or `null` when it is not present. */
    operator fun get(mRID: String): T? = getByMrid(mRID)

    /** Accepts a new mRID, ignores the same instance, and rejects collisions. */
    fun canAddByMrid(element: T): Boolean {
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
    @Suppress("UNCHECKED_CAST")
    override fun add(element: T): Boolean {
        // NOTE: All downstream implementations of this are typed properly,
        //          ensuring nothing blows up. The lack of backfill typing
        //          is intended to ease the use of MridCollection as type for public lists
        if (!canAddByMrid(element))
            return false
        // Concrete collections expose matching owner/backfill types, but the
        // public MridCollection API intentionally erases the owner type.
        (backfill as Backfill<T, Identifiable>?)?.apply(owner, element)
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
