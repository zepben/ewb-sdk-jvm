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
 * Adds setting typed backref on elements to [MridCollection]
 *
 * NOTE: This class can be inlined with [MridCollection].
 *      It is only distinct because [MridCollection] is used for typing,
 *      and we do not want to set the owner type explicitly. This separation lets
 *      us type things as ``MridList<Item>`` and have the owner type inferred from constructor arguments.
 */
abstract class MridBackfillCollection<T : Identifiable, O: Identifiable>(
    validate: ((T) -> Unit)? = null,
): MridCollection<T>(validate) {
    open val backfill: Backfill<T, O>? = null
    abstract override val owner: O

    override fun add(element: T): Boolean {
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
