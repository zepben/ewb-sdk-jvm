/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import kotlin.reflect.KMutableProperty1


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
        elements.all { contains(it) }

    override fun isEmpty(): Boolean =
        getCollection().isEmpty()
}

abstract class AbstractBackedList<T> :
    AbstractBackedCollection<T>(),
    List<T> {

    abstract override fun getCollection(): List<T>

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


open class LazyValidatedList<T>(
    private val getter: () -> MutableList<T>?,
    private val setter: (MutableList<T>?) -> Unit,
    private val validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null
) : AbstractBackedList<T>() {

    override fun getCollection(): MutableList<T> = getter() ?: mutableListOf()

    private fun clearIfEmpty() {
        if (getter()?.isEmpty() == true) {
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

    override fun remove(element: T): Boolean = getter()?.remove(element).also { clearIfEmpty() } ?: false

}



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

interface MridList<T : Identifiable> : MridCollection<T>, List<T>

class LazyMridList<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    val backfill: Backfill<T, O>? = null,
    validate: ((T) -> Unit)? = null,
    sortBy: ((T) -> Comparable<*>?)? = null
) : LazyValidatedList<T>(
    getter, setter, validate, sortBy
), MridList<T> {

    override fun getByMrid(mRID: String): T? {
        return getter()?.firstOrNull { it.mRID == mRID }
    }

    override fun add(element: T): Boolean {
        // Check for mRID collisions
        // If element is already present, skip;
        // If another element shares mRID, error
        if (!canAddByMrid(element))
            return false

        backfill?.apply(owner, element)

        return super.add(element)
    }

    override fun remove(element: T): Boolean =
        super.remove(element).also { backfill?.clear(element) }

    override fun clear() {
        val old = getter() ?: emptyList<T>()
        super.clear()
        backfill?.also { old.forEach { backfill.clear(it) } }
    }

}



open class RefMridList<T : Identifiable, O : Identifiable>(
    private val list: MutableList<T> = mutableListOf(),
    override val owner: O,
    override val elementDescription: String,
    val backfill: Backfill<T, O>? = null,
    private val validate: ((T) -> Unit)? = null,
    private val sortBy: ((T) -> Comparable<*>?)? = null,
) : AbstractBackedList<T>(), MridList<T> {


    override fun getCollection(): List<T> = list

    override fun getByMrid(mRID: String): T? =
        list.firstOrNull { it.mRID == mRID }

    override fun add(element: T): Boolean {
        // If element is already present, skip.
        // If another element shares mRID, error.
        if (!canAddByMrid(element))
            return false

        backfill?.apply(owner, element)

        validate?.invoke(element)

        val result = list.add(element)

        if (result)
            sortBy?.let { selector -> list.sortWith(compareBy(selector)) }

        return result
    }


    override fun remove(element: T): Boolean {
        val result = list.remove(element)
        if (result)
            backfill?.clear(element)
        return result
    }


    override fun clear() {
        val old = list.toList()
        list.clear()
        backfill?.also { old.forEach { backfill.clear(it) } }
    }

}



class LazyMridMap<T : Identifiable, O : Identifiable>(
    private val getter: () -> MutableMap<String, T>?,
    private val setter: (MutableMap<String, T>?) -> Unit,
    override val owner: O,
    override val elementDescription: String,
    val backfill: Backfill<T, O>? = null,
    private val validate: ((T) -> Unit)? = null
) : AbstractBackedCollection<T>(), MridCollection<T> {

    private fun clearIfEmpty() {
        if (getter()?.isEmpty() == true)
            setter(null)
    }

    override fun getByMrid(mRID: String): T? =
        getter()?.get(mRID)

    override fun getCollection(): Collection<T> =
        getter()?.values ?: emptyList()


    override fun add(element: T): Boolean {
        // Check for mRID collisions.
        // If element is already present, skip.
        // If another element shares mRID, error.
        if (!canAddByMrid(element))
            return false

        backfill?.apply(owner, element)

        // If a custom validation method is defined, run it.
        validate?.invoke(element)

        val map = getter() ?: hashMapOf<String, T>().also(setter)

        map[element.mRID] = element
        return true
    }


    override fun contains(element: T): Boolean =
        getter()?.get(element.mRID) === element

    override fun clear() {
        val old = getCollection()
        setter(null)
        backfill?.also { old.forEach { backfill.clear(it) } }
    }



    override fun remove(element: T): Boolean {
        val map = getter() ?: return false
        val existing = map[element.mRID]

        if (existing != element)
            return false

        map.remove(element.mRID)
        clearIfEmpty()

        backfill?.clear(element)

        return true
    }
}



class LazyIndexedList<T>(
    val getter: () -> MutableList<T>?,
    val setter: (MutableList<T>?) -> Unit,
    val owner: Identifiable,
    val elementDescription: String,
    sortBy: ((T) -> Comparable<*>?)? = null
) : LazyValidatedList<T>(getter, setter, null, sortBy) {

    fun add(index: Int, element: T) {
        val data = getter()
        require(index in 0..size) {
            "Unable to add $elementDescription to ${owner.typeNameAndMRID()}. " +
                "Sequence number $index is invalid. Expected a value between 0 and ${size}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }
        data
            ?.add(index, element)
            ?:setter(mutableListOf(element))
    }

    override fun add(element: T): Boolean  {
        add(size, element)
        return true
    }

    fun removeAt(index: Int): T? {
        return if(index in 0..size) {
            getter()?.removeAt(index)
        } else
            null
    }

}

class Backfill<T : Identifiable, O : Identifiable>(
    val getter: (T) -> Identifiable?,
    val setter: (T, O?) -> Unit,
    val backfillProp: KMutableProperty1<T, O?>,
) {
    fun apply(owner: O, element: T) {
        if (getter(element) == null)
            setter(element, owner)

        val ref = getter(element)
        require(ref === owner) {
            "${element.typeNameAndMRID()} `${backfillProp.name}` property references ${ref?.typeNameAndMRID()}, expected ${owner.typeNameAndMRID()}."
        }
    }

    fun clear(element: T) {
        setter(element, null)
    }

}