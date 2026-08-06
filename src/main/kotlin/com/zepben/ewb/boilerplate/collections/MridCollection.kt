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
 * Provides lookup by mRID and rejects distinct objects with duplicate mRIDs.
 */
abstract class MridCollection<T : Identifiable> : AbstractBackedCollection<T>() {
    abstract val owner: Identifiable
    abstract val elementDescription: String

    open val backfill: Backfill<T, *>? = null

    /** Returns the element with [mRID], or `null` when it is not present. */
    abstract fun getByMrid(mRID: String): T?

    fun canAddByMrid(element: T): Boolean {
        val existing = getByMrid(element.mRID) ?: return true

        require(existing === element) {
            "$elementDescription with mRID ${element.mRID} already exists in ${owner.typeNameAndMRID()}."
        }

        return false
    }

    override fun postRemove(element: T) {
        backfill?.clear(element)
    }

    override fun clear() {
        val activeBackfill = backfill ?: return super.clear()
        clearAndCopy(getCollection()).forEach(activeBackfill::clear)
    }
}
