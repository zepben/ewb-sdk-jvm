/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections


/**
 * An [AbstractBackedCollection] with sequence-style indexed access.
 *
 * Integer indexes return individual items and ranges return lists.
 *
 * Collection mutation remains unordered; indexed mutation is only exposed by
 * specialisations such as [LazyIndexList].
 */
abstract class AbstractBackedList<T> :
    AbstractBackedCollection<T>(),
    List<T> {

    abstract override fun getCollection(): MutableList<T>

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
