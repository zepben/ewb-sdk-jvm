/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

/**
 * A mutable collection whose contents are stored elsewhere.
 *
 * Implementations provide the current mutable contents through [getCollection]
 * and define how elements are added. Individual removal, bulk removal and
 * retention are supplied by [AbstractMutableCollection] through [iterator].
 *
 * [postRemove] is invoked after every individual removal, including removals
 * made through a mutable iterator. [clear] clears the backing storage directly,
 * avoiding repeated removal bookkeeping when bulk cleanup is unnecessary.
 */
abstract class AbstractBackedCollection<T> :
    AbstractMutableCollection<T>() {

    protected abstract fun getCollection(): MutableCollection<T>

    abstract override fun add(element: T): Boolean

    protected open fun postRemove(element: T) = Unit

    protected open fun clearCollection(collection: MutableCollection<T>) {
        collection.clear()
    }

    protected open fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        val elements = collection.toList()
        clearCollection(collection)
        return elements
    }

    final override fun iterator(): MutableIterator<T> =
        CallbackMutableIterator(getCollection().iterator(), ::postRemove)

    final override val size: Int
        get() = getCollection().size

    override fun clear() = clearCollection(getCollection())

    override fun contains(element: T): Boolean =
        getCollection().contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        getCollection().containsAll(elements)

    override fun isEmpty(): Boolean =
        getCollection().isEmpty()
}

private class CallbackMutableIterator<T>(
    private val delegate: MutableIterator<T>,
    private val afterRemove: (T) -> Unit
) : MutableIterator<T> by delegate {

    private class Current<T>(val element: T)

    private var current: Current<T>? = null

    override fun next(): T = delegate.next().also { current = Current(it) }

    override fun remove() {
        val removed = checkNotNull(current) { "remove() called before next()" }
        delegate.remove()
        current = null
        afterRemove(removed.element)
    }
}
