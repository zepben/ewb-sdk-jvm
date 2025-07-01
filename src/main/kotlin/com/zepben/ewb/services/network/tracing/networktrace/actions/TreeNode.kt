/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.actions

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import java.lang.ref.WeakReference

class TreeNode<T : IdentifiedObject>(
    val identifiedObject: T,
    parent: TreeNode<T>?
) {

    val parent: TreeNode<T>?
        get() = _parent.get()

    val children: List<TreeNode<T>>
        get() = _children.asUnmodifiable()

    private val _parent: WeakReference<TreeNode<T>?> = WeakReference(parent)
    private val _children = mutableListOf<TreeNode<T>>()

    internal fun addChild(child: TreeNode<T>) {
        _children.add(child)
    }

    override fun toString(): String {
        return "{object: $identifiedObject, parent: " + (parent?.identifiedObject ?: "") + ", num children: " + children.size + "}"
    }

}
