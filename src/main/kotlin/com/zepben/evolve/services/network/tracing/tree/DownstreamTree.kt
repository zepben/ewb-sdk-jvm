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
import com.zepben.evolve.services.network.tracing.networktrace.actions.TreeNode
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue

// TODO [Review]: Move this into NetworkRoutes and push usage of generating trees via `EquipmentTreeBuilder`
class DownstreamTree(
    internal val stateOperators: NetworkStateOperators
) {

    private val traversal: NetworkTrace<TreeNode<ConductingEquipment>> = Tracing.networkTraceBranching(
        networkStateOperators = stateOperators,
        queueFactory = { WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() } },
        branchQueueFactory = { WeightedPriorityQueue.branchQueue { it.path.toTerminal.phases.numPhases() } },
        computeNextT = { currentItem: NetworkTraceStep<TreeNode<ConductingEquipment>>, _, nextPath: StepPath ->
            // TODO [Review]: computeNextT is called for every step regardless on actionStepType because queueing / queue conditions are always
            //                run for every step regardless of actionStepType. Is this a bit yuck? Not sure what to do about it?
            // We just pass the data along on internal steps. As the downstream tree only actions equipment
            // we only want to create a new tree node when we step onto the next equipment
            if (nextPath.tracedInternally) currentItem.data else TreeNode(nextPath.toEquipment, currentItem.data)
        }
    )
        .addNetworkCondition { downstream() }
        .addStepAction { (_, treeNode), _ ->
            // If we visit a node, we add it as a child to its parent
            treeNode.parent?.addChild(treeNode)
        }

    fun run(start: ConductingEquipment): TreeNode<ConductingEquipment> {
        val root = TreeNode(start, null)
        traversal.run(root.identifiedObject, root, canStopOnStartItem = false)
        return root
    }

}
