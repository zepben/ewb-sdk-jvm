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
 * List-shaped specialisation of [MridCollection].
 *
 * Kotlin only permits one concrete superclass. This class represents the
 * deliberate intersection of the mRID collection and backed-list branches so
 * downstream mRID list implementations do not duplicate list delegation or
 * sorting behaviour.
 */
abstract class AbstractMridList<T : Identifiable, O: Identifiable>(
    validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null,
) : MridBackfillCollection<T, O>(validate), List<T> {

    abstract override fun getCollection(): MutableList<T>

    /**
     * Adds [element] when its mRID is available and validation succeeds.
     *
     * This override performs the mRID check, backfill, validation, storage, and optional sorting.
     */
    override fun add(element: T): Boolean {
        val added = super.add(element)
        sortBy?.let { getCollection().sortWith(compareBy(it)) }
        return added
    }

    override fun get(index: Int): T = getCollection()[index]

    override fun indexOf(element: T): Int = getCollection().indexOf(element)

    override fun lastIndexOf(element: T): Int = getCollection().lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = getCollection().listIterator()

    override fun listIterator(index: Int): ListIterator<T> = getCollection().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        getCollection().subList(fromIndex, toIndex)

}
