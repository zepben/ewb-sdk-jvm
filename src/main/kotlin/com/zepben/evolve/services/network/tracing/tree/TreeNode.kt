/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import java.lang.ref.WeakReference
import kotlin.math.max

class TreeNode(
    val conductingEquipment: ConductingEquipment,
    parent: TreeNode?
) {

    val parent: TreeNode?
        get() = _parent.get()

    val children: List<TreeNode>
        get() = _children.asUnmodifiable()

    val sortWeight: Int by lazy { max(1, conductingEquipment.terminals.maxOf { it.phases.singlePhases().size }) }

    private val _parent: WeakReference<TreeNode?> = WeakReference(parent)
    private val _children = mutableListOf<TreeNode>()

    internal fun addChild(child: TreeNode) {
        _children.add(child)
    }

    override fun toString(): String {
        return "{conductingEquipment: " + conductingEquipment.mRID + ", parent: " + (parent?.conductingEquipment?.mRID
            ?: "") + ", num children: " + children.size + "}"
    }

}
