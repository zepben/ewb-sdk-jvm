/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.services.common.extensions.asUnmodifiable


interface IndexableMutableCollection<T> : MutableCollection<T>, List<T> {
    override fun iterator(): MutableIterator<T>
}

open class LazyValidatedList<T>(
    private val getter: () -> MutableList<T>?,
    private val setter: (MutableList<T>?) -> Unit,
    private val validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractMutableCollection<T>(), IndexableMutableCollection<T> {

    private fun getOrEmpty(): MutableList<T> = getter() ?: mutableListOf()

    private fun clearIfEmpty(list: MutableList<T>) {
        if (list.isEmpty()) {
            setter(null)
        }
    }

    override fun add(element: T): Boolean {
        // If a custom validation method is defined, run it
        validate?.invoke(element)

        // Try to add the item to the backing list
        // If the list is null (empty), instantiate it with the element present
        val result = getter()?.add(element) ?: run {
            setter(mutableListOf(element))
            true
        }

        // On successful addition, sort the list if the sorting order is defined
        if(result) {
            sortBy?.let {
                    selector -> getter()?.sortWith(compareBy(selector))
            }
        }

        return result
    }

    override val size: Int
        get() = getter()?.size ?: 0

    override fun clear() {
        setter(null)
    }

    override fun iterator(): MutableIterator<T> {
        val list = getter() ?: return mutableListOf<T>().iterator()
        val iterator = list.iterator()

        return object : MutableIterator<T> by iterator {
            override fun remove() {
                iterator.remove()
                clearIfEmpty(list)
            }
        }
    }

    override fun get(index: Int): T = getOrEmpty()[index]

    override fun indexOf(element: T): Int = getOrEmpty().indexOf(element)

    override fun lastIndexOf(element: T): Int = getOrEmpty().lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = getOrEmpty().listIterator()

    override fun listIterator(index: Int): ListIterator<T> = getOrEmpty().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = getOrEmpty().subList(fromIndex, toIndex).asUnmodifiable()
}



interface MridCollection<T : Identifiable> : MutableCollection<T> {
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

}



class LazyMridList<T : Identifiable>(
    private val getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    override val owner: Identifiable,
    override val elementDescription: String,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null
) : LazyValidatedList<T>(
    getter, setter, validate, sortBy
), IndexableMutableCollection<T>, MridCollection<T> {

    override fun getByMrid(mRID: String): T? {
        return getter()?.firstOrNull { it.mRID == mRID }
    }

    override fun add(element: T): Boolean {
        // Check for mRID collisions
        // If element is already present, skip;
        // If another element shares mRID, error
        if (!canAddByMrid(element))
            return false

        return super.add(element)
    }

}



class MridList<T : Identifiable>(
    private val list: MutableList<T> = mutableListOf(),
    override val owner: Identifiable,
    override val elementDescription: String,
    private val validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null,
) : AbstractMutableCollection<T>(), IndexableMutableCollection<T>, MridCollection<T> {

    override fun getByMrid(mRID: String): T? =
        list.firstOrNull { it.mRID == mRID }

    override fun add(element: T): Boolean {
        // If element is already present, skip.
        // If another element shares mRID, error.
        if (!canAddByMrid(element))
            return false

        validate?.invoke(element)

        val result = list.add(element)

        if (result)
            sortBy?.let { selector -> list.sortWith(compareBy(selector)) }

        return result
    }

    override val size: Int
        get() = list.size

    override fun iterator(): MutableIterator<T> =
        list.iterator()

    override fun get(index: Int): T = list[index]

    override fun indexOf(element: T): Int = list.indexOf(element)

    override fun lastIndexOf(element: T): Int = list.lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = list.listIterator()

    override fun listIterator(index: Int): ListIterator<T> = list.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = list.subList(fromIndex, toIndex).asUnmodifiable()
}



class LazyMridMap<T : Identifiable>(
    private val getter: () -> MutableMap<String, T>?,
    private val setter: (MutableMap<String, T>?) -> Unit,
    override val owner: Identifiable,
    override val elementDescription: String,
    private val validate: ((T) -> Unit)? = null
) : AbstractMutableCollection<T>(), MridCollection<T> {

    private fun clearIfEmpty(map: MutableMap<String, T>) {
        if (map.isEmpty())
            setter(null)
    }

    override fun getByMrid(mRID: String): T? =
        getter()?.get(mRID)


    override fun add(element: T): Boolean {
        // Check for mRID collisions.
        // If element is already present, skip.
        // If another element shares mRID, error.
        if (!canAddByMrid(element))
            return false

        // If a custom validation method is defined, run it.
        validate?.invoke(element)

        val map = getter() ?: hashMapOf<String, T>().also(setter)

        map[element.mRID] = element
        return true
    }

    override val size: Int
        get() = getter()?.size ?: 0

    override fun clear() {
        setter(null)
    }

    override fun iterator(): MutableIterator<T> {
        val map = getter() ?: return mutableListOf<T>().iterator()
        val iterator = map.values.iterator()

        return object : MutableIterator<T> by iterator {
            override fun remove() {
                iterator.remove()
                clearIfEmpty(map)
            }
        }
    }

    override fun remove(element: T): Boolean {
        val map = getter() ?: return false
        val existing = map[element.mRID]

        if (existing != element)
            return false

        map.remove(element.mRID)
        clearIfEmpty(map)

        return true
    }
}
