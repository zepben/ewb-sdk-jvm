/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

class DownstreamTree(
    private val openTest: OpenTest,
    private val directionSelector: DirectionSelector
) {

    private val traversal: NetworkTrace<TreeNode> = Tracing.connectedEquipmentTrace(
        { WeightedPriorityQueue.processQueue { it.data.sortWeight } },
        branching = true,
        computeNextT = this::createNextTreeNode
    )
        .addQueueCondition(this::canQueue)
        .addStepAction(this::addItemAsChild)

    fun run(start: ConductingEquipment): TreeNode {
        val root = TreeNode(start, null)
        traversal.run(root.conductingEquipment, false, root)
        return root
    }

    private fun createNextTreeNode(currentItem: NetworkTraceStep<TreeNode>, context: StepContext, nextPath: StepPath): TreeNode {
        // We don't add the new tree node as a child here as we may not actually visit it based on other conditions.
        return TreeNode(nextPath.toEquipment, currentItem.data)
    }

    private fun canQueue(item: NetworkTraceStep<TreeNode>, context: StepContext): Boolean {
        val fromTerminal = item.path.fromTerminal ?: return false
        return !openTest.isOpen(item.path.fromEquipment, null) && FeederDirection.DOWNSTREAM in directionSelector.select(fromTerminal).value
    }

    private fun addItemAsChild(item: NetworkTraceStep<TreeNode>, context: StepContext) {
        // If we visit a node, we add it as a child to its parent
        item.data.parent?.addChild(item.data)
    }

}
