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

    /** Clears the backing [collection]. */
    protected open fun clearRaw(collection: MutableCollection<T>) {
        collection.clear()
    }

    /** Clears the backing [collection] and returns its former elements. */
    protected open fun clearAndCopy(collection: MutableCollection<T>): Collection<T> {
        val elements = collection.toList()
        clearRaw(collection)
        return elements
    }

    /**
     * Returns an iterator over the current backing collection.
     *
     * An iterator remains attached to the backing instance that existed when it
     * was created. Lazy implementations may later replace that instance when
     * their backing storage transitions between empty and populated states.
     */
    final override fun iterator(): VolatileIterator<T> =
        CallbackMutableIterator(getCollection().iterator(), ::postRemove)

    final override val size: Int
        get() = getCollection().size

    override fun clear() = clearRaw(getCollection())

    override fun contains(element: T): Boolean =
        getCollection().contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        getCollection().containsAll(elements)

    override fun isEmpty(): Boolean =
        getCollection().isEmpty()

}

/**
 * A mutable iterator that may become detached from replaceable backing storage.
 *
 * Traversal-only iterator. Misses concurrent modification issues when list is empty.
 * Make sure you retrieve an up-to-date iterator after any modifications to the collection.
 */
abstract class VolatileIterator<T> : MutableIterator<T> {

    @Deprecated(
        "Removing through this iterator may detach it from replaceable backing storage. " +
            "Remove through the collection and fetch a new iterator instance"
    )
    abstract override fun remove()
}

/** A mutable iterator that reports removed elements. */
internal open class CallbackMutableIterator<T>(
    protected val delegate: MutableIterator<T>,
    private val postRemove: (T) -> Unit
) : VolatileIterator<T>() {

    /** Wraps the current element, including nullable values. */
    protected class Current<T>(val element: T)

    protected var current: Current<T>? = null

    override fun hasNext(): Boolean = delegate.hasNext()

    override fun next(): T = delegate.next().also { current = Current(it) }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun remove() {
        val removed = checkNotNull(current) { "iterator.remove() called without a current element" }
        delegate.remove()
        current = null
        postRemove(removed.element)
    }

}
