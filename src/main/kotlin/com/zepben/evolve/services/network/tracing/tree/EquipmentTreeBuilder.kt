/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.traversal.StepActionWithContextValue
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import java.util.*

class EquipmentTreeBuilder : StepActionWithContextValue<NetworkTraceStep<*>, TreeNode> {
    private val _roots: MutableMap<ConductingEquipment, TreeNode> = mutableMapOf()

    val roots: Collection<TreeNode> get() = _roots.values

    override fun computeInitialValue(item: NetworkTraceStep<*>): TreeNode {
        val node = _roots.getOrPut(item.path.toEquipment) { TreeNode(item.path.toEquipment, null) }
        return node
    }

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<*>, currentItem: NetworkTraceStep<*>, currentValue: TreeNode): TreeNode {
        return if (nextItem.path.tracedInternally) currentValue else TreeNode(nextItem.path.toEquipment, currentValue)
    }

    override fun apply(item: NetworkTraceStep<*>, context: StepContext) {
        val currentNode = context.value
        currentNode.parent?.addChild(currentNode)
    }

    override val key: String = UUID.randomUUID().toString()

    companion object {
        fun <T> NetworkTrace<T>.equipmentTree(conductingEquipment: ConductingEquipment, data: T, phases: PhaseCode? = null): TreeNode {
            val treeBuilder = EquipmentTreeBuilder()
            this.addStepAction(treeBuilder)
            run(conductingEquipment, data, phases)
            return treeBuilder.roots.first()
        }

        fun NetworkTrace<Unit>.equipmentTree(conductingEquipment: ConductingEquipment, phases: PhaseCode? = null): TreeNode {
            val treeBuilder = EquipmentTreeBuilder()
            this.addStepAction(treeBuilder)
            run(conductingEquipment, phases)
            return treeBuilder.roots.first()
        }
    }
}
