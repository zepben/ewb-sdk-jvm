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
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.WeightedPriorityQueue

class DownstreamTree(
    openTest: OpenTest,
    directionSelector: DirectionSelector
) {

    private val traversal: NetworkTrace<TreeNode> = Tracing.connectedEquipmentTrace(
        { WeightedPriorityQueue.processQueue { it.data.sortWeight } },
        { WeightedPriorityQueue.branchQueue { it.data.sortWeight } },
        computeNextT = ::createNextTreeNode
    )
        .addConditions(downstream(directionSelector), stopAtOpen(openTest))
        .addStepAction(::addItemAsChild)

    fun run(start: ConductingEquipment): TreeNode {
        val root = TreeNode(start, null)
        traversal.run(root.conductingEquipment, root, false)
        return root
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createNextTreeNode(currentItem: NetworkTraceStep<TreeNode>, unused: StepContext, nextPath: StepPath): TreeNode {
        return TreeNode(nextPath.toEquipment, currentItem.data)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun addItemAsChild(item: NetworkTraceStep<TreeNode>, context: StepContext) {
        // If we visit a node, we add it as a child to its parent
        item.data.parent?.addChild(item.data)
    }

}
