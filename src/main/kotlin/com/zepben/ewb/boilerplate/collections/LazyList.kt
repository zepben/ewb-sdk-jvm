/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

import com.zepben.ewb.cim.iec61970.base.core.Identifiable

class LazyList<T>(
    val getter: () -> MutableList<T>?,
    val setter: (MutableList<T>?) -> Unit,
    val owner: Identifiable,
    val elementDescription: String,
) : LazyCollection<T>(getter, setter, null, null) {

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