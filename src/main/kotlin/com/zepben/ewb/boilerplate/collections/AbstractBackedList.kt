/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

import com.zepben.ewb.boilerplate.collections.interfaces.ArcList


/**
 * An [AbstractBackedCollection] with sequence-style indexed access.
 *
 * Integer indexes return individual items and ranges return lists. When a sort
 * selector is supplied, successful additions reorder the backing list.
 * Does not allow indexed mutation.
 */
abstract class AbstractBackedList<T>(
    validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null,
) :
    AbstractBackedCollection<T>(validate),
    ArcList<T> {

    abstract override fun getCollection(): MutableList<T>

    /**
     * Adds [element] when validation and backing storage accept it.
     *
     * This override performs validation, storage, and optional sorting, but no mRID check or backfill.
     */
    override fun add(element: T): Boolean {
        val added = super.add(element)
        sortBy?.let { getCollection().sortWith(compareBy(it)) }
        return added
    }

    override fun get(index: Int): T =
        getCollection()[index]

    override fun indexOf(element: T): Int =
        getCollection().indexOf(element)

    override fun lastIndexOf(element: T): Int =
        getCollection().lastIndexOf(element)

    override fun listIterator(): ListIterator<T> =
        getCollection().listIterator()

    override fun listIterator(index: Int): ListIterator<T> =
        getCollection().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        getCollection().subList(fromIndex, toIndex)

}
