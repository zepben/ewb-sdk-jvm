/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.testing

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

val listMutabilityError = NotImplementedError("List modification is limited to add(item), clear(), remove(item) methods.")

open class ListWrapper<T> (
    protected val getter: () -> MutableList<T>?,
    protected val setter: (MutableList<T>?) -> Unit,
    protected val customAdd: ((T) -> Boolean)? = null
): MutableList<T> {

    protected fun getNotNull(): MutableList<T> {
        return getter()?:mutableListOf()
    }

    override fun add(element: T): Boolean{
        return customAdd
            ?.invoke(element)
            ?:defaultAdd(element)
    }

    override fun add(index: Int, element: T) {
        throw listMutabilityError
    }

    protected open fun defaultAdd(element: T): Boolean {
        return getter()
            ?.add(element)
            ?: run {
                setter(mutableListOf(element))
                true
            }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        throw listMutabilityError
    }

    override fun addAll(elements: Collection<T>): Boolean {
        throw listMutabilityError
    }
    override fun clear() {
        setter(null)
    }

    override fun listIterator(): MutableListIterator<T> {
        return getNotNull().listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return getNotNull().listIterator(index)
    }

    override fun remove(element: T): Boolean {
        val list = getNotNull()
        val result = list.remove(element)
        if (list.isEmpty())
            clear()
        return result
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        throw listMutabilityError
    }

    override fun removeAt(index: Int): T {
        throw listMutabilityError
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return getNotNull().retainAll(elements)
    }

    override fun set(index: Int, element: T): T {
        throw listMutabilityError
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return getNotNull().subList(fromIndex, toIndex)
    }

    override val size: Int
    get() = getter()?.size?:0

    override fun isEmpty(): Boolean {
        return getter()?.isEmpty()?:true
    }

    override fun contains(element: T): Boolean {
        return getter()?.contains(element)?:false
    }

    override fun iterator(): MutableIterator<T> {
        return getNotNull().iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return getter()?.containsAll(elements)?: !elements.isEmpty()
    }

    override fun get(index: Int): T {
        return getNotNull()[index]
    }

    override fun indexOf(element: T): Int {
        return getNotNull().indexOf(element)
    }

    override fun lastIndexOf(element: T): Int {
        return getNotNull().lastIndexOf(element)
    }
}

open class MRIDListWrapper<S: IdentifiedObject> private constructor(
    getter: () -> MutableList<S>?,
    setter: (MutableList<S>?) -> Unit,
    customAdd: ((S) -> Boolean)? = null,
    val validate: ((S) -> Boolean)? = null,
    val link: ((S) -> Unit)? = null,
): ListWrapper<S>(getter, setter, customAdd) {

    constructor(
        getter: () -> MutableList<S>?,
        setter: (MutableList<S>?) -> Unit,
        customAdd: ((S) -> Boolean)? = null,
    ): this(getter, setter, customAdd, null, null)

    constructor(
        getter: () -> MutableList<S>?,
        setter: (MutableList<S>?) -> Unit,
        validate: ((S) -> Boolean)? = null,
        link: ((S) -> Unit)? = null,
    ): this(getter, setter, null, validate, link)

    override fun defaultAdd(element: S): Boolean {
        val list = getNotNull()
        for(other in list) {
            if (other.mRID == element.mRID) {
                if (other === element)
                    return false
                throw IllegalArgumentException("mRID DUPLICATES! (TODO: message)")
            }
        }

        validate?.invoke(element)

        link?.invoke(element)

        return super.defaultAdd(element)
    }
}

open class CustomListWrapper<T> (
    getter: () -> MutableList<T>?,
    setter: (MutableList<T>?) -> Unit,
    customAdd: ((T) -> Boolean)? = null,
    protected val customClear: (() -> Unit)? = null,
    protected val customRemove: ((T) -> Boolean)? = null,
) : ListWrapper<T>(getter, setter, customAdd){

    override fun clear() {
        return customClear
            ?.invoke()
            ?:super.clear()
    }

    override fun remove(element: T): Boolean{
        return customRemove
            ?.invoke(element)
            ?:super.remove(element)
    }

}

class A {

    private var _items: MutableList<Int>? = null
    val items: ListWrapper<Int>
        get() = ListWrapper(
                getter = { _items },
                setter = { _items = it }
            )

    private var _uniques: MutableList<Int>? = null
    val uniques: ListWrapper<Int>
        get() = ListWrapper(
            getter = { _uniques },
            setter = { _uniques = it },
            customAdd = { addUnique(it) }
        )

    private fun addUnique(item: Int): Boolean {
        if (_uniques != null)
            for (other in _uniques!!)
                if(other == item)
                    return false

        _uniques?.add(item)?:run {
            _uniques = mutableListOf(item)
        }

        return true
    }

    @Deprecated("BOILERPLATE: Use items.add(item) instead")
    fun addItem(item: Int) {
        _items?.add(item)
    }

    @Deprecated("BOILERPLATE: Use items.clear() instead")
    fun clearItems() {
        _items?.clear()
    }

    @Deprecated("BOILERPLATE: Use items.size instead")
    fun numItems(): Int {
        return _items?.size ?: 0
    }

    fun getItems(): MutableList<Int>? {
        return _items
    }


}

fun main() {

    var a = A()

    a.addItem(1)
    a.addItem(2)
    a.addItem(3)
    assert(a.numItems() == 3)
    a.clearItems()
    assert(a.numItems() == 0)
    a.addItem(42)
    assert(a.numItems() == 1)

    a = A()
    a.items.add(1)
    a.items.add(2)
    a.items.add(3)
    assert(a.items.size == 3)
    a.items.clear()
    assert(a.getItems() == null)
    assert(a.items.size == 0)
    a.items.add(42)
    assert(a.items.size == 1)

    a.uniques.add(1)
    a.uniques.add(2)
    a.uniques.add(3)
    assert(a.uniques.size == 3)
    a.uniques.clear()
    assert(a.uniques.size == 0)
    a.uniques.add(42)
    assert(a.uniques.size == 1)
    a.uniques.add(42)
    assert(a.uniques.size == 1)
}