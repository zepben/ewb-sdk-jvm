/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections


/**
 * An [ArcCollection] whose contents are stored elsewhere.
 *
 * Implementations provide the current mutable contents through [getCollection].
 * Element validation and the add lifecycle are centralised here, with hooks for
 * specialised acceptance checks, preparation, storage, and post-add work.
 * [postRemove] is invoked after every successful individual removal. [clear]
 * clears the backing storage directly, avoiding repeated removal bookkeeping
 * when bulk cleanup is unnecessary.
 */
abstract class AbstractBackedCollection<T>(
    private val validate: ((T) -> Unit)? = null,
) :
    ArcCollection<T> {

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

    /** Returns a traversal-only iterator over the current backing collection. */
    final override fun iterator(): Iterator<T> {
        val delegate = getCollection().iterator()
        return object : Iterator<T> {
            override fun hasNext(): Boolean = delegate.hasNext()
            override fun next(): T = delegate.next()
        }
    }

    final override val size: Int
        get() = getCollection().size

    override fun clear() = clearRaw(getCollection())

    override fun contains(element: T): Boolean =
        getCollection().contains(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        getCollection().containsAll(elements)

    override fun isEmpty(): Boolean =
        getCollection().isEmpty()

    override fun toString(): String =
        getCollection().toString()

    override fun remove(element: T): Boolean {
        val iterator = getCollection().iterator()
        while (iterator.hasNext()) {
            val stored = iterator.next()
            if (stored == element) {
                iterator.remove()
                postRemove(stored)
                return true
            }
        }

        return false
    }

}
