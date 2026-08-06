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
 * Implementations provide the current mutable contents through [getCollection].
 * Element validation and the add lifecycle are centralised here, with hooks for
 * specialised acceptance checks, preparation, storage, and post-add work.
 * Individual removal, bulk removal and retention are supplied by
 * [AbstractMutableCollection] through [iterator].
 *
 * [postRemove] is invoked after every individual removal, including removals
 * made through a mutable iterator. [clear] clears the backing storage directly,
 * avoiding repeated removal bookkeeping when bulk cleanup is unnecessary.
 */
abstract class AbstractBackedCollection<T>(
    private val validate: ((T) -> Unit)? = null,
) :
    AbstractMutableCollection<T>() {

    /** Returns the current backing collection. */
    protected abstract fun getCollection(): MutableCollection<T>

    /**
     * Attempts to add [element] to the backing collection.
     *
     * This hook performs storage only: no mRID check, validation, backfill, or sorting.
     */
    protected open fun addRaw(element: T): Boolean =
        getCollection().add(element)

    /**
     * Adds [element] when validation and backing storage accept it.
     *
     * This override performs validation and storage, but no mRID check, backfill, or sorting.
     */
    override fun add(element: T): Boolean {
        validate?.invoke(element)

        return addRaw(element)
    }

    /** Performs cleanup after [element] is removed. */
    protected open fun postRemove(element: T) = Unit

    /** Clears [collection]. */
    protected open fun clearCollection(collection: MutableCollection<T>) {
        collection.clear()
    }

    /** Clears [collection] and returns its former elements. */
    protected open fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        val elements = collection.toList()
        clearCollection(collection)
        return elements
    }

    /** Returns a mutable iterator that invokes removal cleanup. */
    final override fun iterator(): MutableIterator<T> =
        CallbackMutableIterator(getCollection().iterator(), ::postRemove)

    final override val size: Int
        get() = getCollection().size

    /** Clears the backing collection. */
    override fun clear() = clearCollection(getCollection())

    /** Returns whether the backing collection contains [element]. */
    override fun contains(element: T): Boolean =
        getCollection().contains(element)

    /** Returns whether the backing collection contains every [elements] item. */
    override fun containsAll(elements: Collection<T>): Boolean =
        getCollection().containsAll(elements)

    /** Returns whether the backing collection is empty. */
    override fun isEmpty(): Boolean =
        getCollection().isEmpty()
}

/** A mutable iterator that reports removed elements. */
private class CallbackMutableIterator<T>(
    private val delegate: MutableIterator<T>,
    private val afterRemove: (T) -> Unit
) : MutableIterator<T> by delegate {

    /** Wraps the current element, including nullable values. */
    private class Current<T>(val element: T)

    private var current: Current<T>? = null

    /** Returns and records the next element. */
    override fun next(): T = delegate.next().also { current = Current(it) }

    /** Removes the current element and invokes the callback. */
    override fun remove() {
        val removed = checkNotNull(current) { "remove() called before next()" }
        delegate.remove()
        current = null
        afterRemove(removed.element)
    }
}
