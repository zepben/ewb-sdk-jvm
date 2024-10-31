/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue


class DownstreamTree(
    networkStateOperators: NetworkStateOperators
) {

    private val traversal: NetworkTrace<TreeNode> = Tracing.connectedEquipmentTrace(
        networkStateOperators = networkStateOperators,
        queueFactory = { WeightedPriorityQueue.processQueue { it.data.sortWeight } },
        branchQueueFactory = { WeightedPriorityQueue.branchQueue { it.data.sortWeight } },
        computeNextT = { currentItem: NetworkTraceStep<TreeNode>, _, nextPath: StepPath ->
            TreeNode(nextPath.toEquipment, currentItem.data)
        }
    )
        .addNetworkCondition { downstream() }
        .addStepAction { (_, treeNode), _ ->
            // If we visit a node, we add it as a child to its parent
            treeNode.parent?.addChild(treeNode)
        }

    fun run(start: ConductingEquipment): TreeNode {
        val root = TreeNode(start, null)
        traversal.run(root.conductingEquipment, root, canStopOnStartItem = false)
        return root
    }

}
