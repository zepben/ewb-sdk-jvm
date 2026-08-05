/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections


/**
 * A mutable collection interface whose contents are stored elsewhere.
 *
 * Mutation is unordered, providing [add] and [remove] but not indexed access
 *
 * Implementations provide the current contents through [getCollection]
 * and define how mutation affects the backing storage.
 */
abstract class AbstractBackedCollection<T> :
    AbstractCollection<T>() {

    protected abstract fun getCollection(): Collection<T>

    abstract fun add(element: T): Boolean

    fun addAll(elements: Collection<T>): Boolean = elements.all { add(it) }

    abstract fun remove(element: T): Boolean

    fun removeAll(elements: Collection<T>): Boolean = elements.all { remove(it) }

    abstract fun clear()

    override val size: Int
        get() = getCollection().size

    override fun iterator(): Iterator<T> =
        getCollection().iterator()

    override fun contains(element: T): Boolean =
        getCollection().contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        getCollection().containsAll(elements)

    override fun isEmpty(): Boolean =
        getCollection().isEmpty()
}

