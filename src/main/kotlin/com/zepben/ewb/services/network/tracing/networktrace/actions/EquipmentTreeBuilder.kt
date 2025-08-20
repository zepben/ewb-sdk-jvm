/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.actions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTrace
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.traversal.StepAction
import com.zepben.ewb.services.network.tracing.traversal.StepActionWithContextValue
import com.zepben.ewb.services.network.tracing.traversal.StepContext
import java.util.*

private typealias EquipmentTreeNode = TreeNode<ConductingEquipment>

/**
 * A [StepAction] that can be added to a [NetworkTrace] to build a tree structure representing the paths taken during a trace.
 * The [roots] are the start items of the trace and the children of a tree node represent the next step paths from a given step in the trace.
 */
class EquipmentTreeBuilder(calculateLeaves: Boolean? = false) : StepActionWithContextValue<NetworkTraceStep<*>, EquipmentTreeNode> {
    private val _roots: MutableMap<ConductingEquipment, EquipmentTreeNode> = mutableMapOf()
    private val _leaves: MutableList<EquipmentTreeNode> = mutableListOf()
    private val _calculateLeaves: Boolean = calculateLeaves?: false

    /**
     * The root nodes in the tree. These represent the start items in the trace.
     */
    val roots: Collection<EquipmentTreeNode> get() = _roots.values

    /**
     * Leaf nodes in the tree. These represent any ending point of the trace.
     */
    val leaves: Collection<EquipmentTreeNode> get() = _leaves.takeIf { _calculateLeaves } ?: throw IllegalArgumentException("Leaves were not calculated, you must pass calculateLeaves = true to the EquipmentTreeBuilder when creating.")

    override fun computeInitialValue(item: NetworkTraceStep<*>): EquipmentTreeNode {
        val node = _roots.getOrPut(item.path.toEquipment) { TreeNode(item.path.toEquipment, null) }
        return node
    }

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<*>, currentItem: NetworkTraceStep<*>, currentValue: EquipmentTreeNode): EquipmentTreeNode {
        return if (nextItem.path.tracedInternally) currentValue else TreeNode(nextItem.path.toEquipment, currentValue)
    }

    override fun apply(item: NetworkTraceStep<*>, context: StepContext) {
        val currentNode = context.value
        currentNode.parent?.addChild(currentNode)

        currentNode.takeIf {_calculateLeaves}?.let {
            _leaves.add(it) // add this node to _leaves as it has no children
            it.parent?.let { parent ->
                _leaves.remove(parent) // this nodes parent now has a child, it's not a leaf anymore
            }
        }
    }

    fun clear() {
        _roots.clear()
    }

    override val key: String = UUID.randomUUID().toString()
}
