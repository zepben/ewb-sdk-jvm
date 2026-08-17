/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

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


    /** Returns the element with [mRID], or `null` when it is not present. */
    abstract fun getByMrid(mRID: String): T?

    operator fun get(mRID: String): T? = getByMrid(mRID)

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
        return super.add(element)
    }

}
