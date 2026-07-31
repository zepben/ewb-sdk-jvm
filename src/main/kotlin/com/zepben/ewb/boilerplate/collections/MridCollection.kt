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
 * A collection of objects identified by a unique `mRID`.
 *
 * Provides lookup by mRID and rejects distinct objects with duplicate mRIDs.
 */
interface MridCollection<T : Identifiable> : Collection<T> {
    val owner: Identifiable
    val elementDescription: String


    fun getByMrid(mRID: String): T?

    fun canAddByMrid(element: T): Boolean {
        val existing = getByMrid(element.mRID) ?: return true

        require(existing === element) {
            "$elementDescription with mRID ${element.mRID} already exists in ${owner.typeNameAndMRID()}."
        }

        return false
    }

    fun add(element: T): Boolean

    fun remove(element: T): Boolean

    fun clear()

}
