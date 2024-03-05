/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

class DownstreamTree(
    private val openTest: OpenTest,
    private val directionSelector: DirectionSelector
) {

    private val traversal = BranchRecursiveTraversal(
        this::addAndQueueNext,
        { WeightedPriorityQueue.processQueue(TreeNode::sortWeight) },
        { TreeNodeTracker() },
        { WeightedPriorityQueue.branchQueue(TreeNode::sortWeight) }
    )

    fun run(start: ConductingEquipment?): TreeNode {
        val root = TreeNode(start!!, null)
        traversal.run(root)
        return root
    }

    private fun addAndQueueNext(current: TreeNode?, traversal: BranchRecursiveTraversal<TreeNode>) {
        // Loop through each of the terminals on the current conducting equipment
        current?.conductingEquipment?.terminals?.forEach { downTerminal: Terminal ->
            // Find all the nominal phases which are going out
            val downPhases = getDownPhases(downTerminal)
            if (downPhases.isNotEmpty())
                queueConnectedTerminals(traversal, current, downTerminal, downPhases)
        }
    }

    private fun getDownPhases(terminal: Terminal): Set<SinglePhaseKind> {
        val direction = directionSelector.select(terminal).value
        if (FeederDirection.DOWNSTREAM !in direction)
            return mutableSetOf()

        val conductingEquipment = terminal.conductingEquipment!!
        return terminal.phases.singlePhases
            .asSequence()
            .filter { !openTest.isOpen(conductingEquipment, it) }
            .toSet()
    }

    private fun queueConnectedTerminals(
        traversal: BranchRecursiveTraversal<TreeNode>,
        current: TreeNode,
        downTerminal: Terminal,
        downPhases: Set<SinglePhaseKind>
    ) {
        // Get all the terminals connected to terminals with phases going out
        val upTerminals = NetworkService.connectedTerminals(downTerminal, downPhases)

        // Make sure we do not loop back out the incoming terminal if its direction is both.
        if (upTerminals.any { it.to == current.parent?.conductingEquipment })
            return

        val queueNext = if (upTerminals.size > 1 || downTerminal.conductingEquipment!!.numTerminals() > 2)
            { next: TreeNode -> traversal.branchQueue.add(traversal.branchSupplier().setStart(next)) }
        else
            { next: TreeNode -> traversal.queue.add(next) }

        upTerminals
            .asSequence()
            .mapNotNull { it.to }
            .forEach {
                val next = TreeNode(it, current)

                // Only branch to the next item if we have not already been there.
                if (!traversal.hasVisited(next)) {
                    current.addChild(next)
                    queueNext(next)
                }
            }

    }

}
