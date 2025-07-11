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
class EquipmentTreeBuilder : StepActionWithContextValue<NetworkTraceStep<*>, EquipmentTreeNode> {
    private val _roots: MutableMap<ConductingEquipment, EquipmentTreeNode> = mutableMapOf()

    /**
     * The root nodes in the tree. These represent the start items in the trace.
     */
    val roots: Collection<EquipmentTreeNode> get() = _roots.values

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
    }

    fun clear() {
        _roots.clear()
    }

    override val key: String = UUID.randomUUID().toString()
}
