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
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.phases.PhaseDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseSelector
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

class DownstreamTree(
    private val openTest: OpenTest,
    private val phaseSelector: PhaseSelector
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
        val outPhases = mutableSetOf<SinglePhaseKind>()
        current?.conductingEquipment?.terminals?.forEach { outTerminal: Terminal ->
            // Find all the nominal phases which are going out
            getOutPhases(outTerminal, outPhases)
            if (outPhases.size > 0)
                queueConnectedTerminals(traversal, current, outTerminal, outPhases)
        }
    }

    private fun getOutPhases(terminal: Terminal, outPhases: MutableSet<SinglePhaseKind>) {
        outPhases.clear()
        val conductingEquipment = terminal.conductingEquipment!!
        for (phase in terminal.phases.singlePhases()) {
            if (!openTest.isOpen(conductingEquipment, phase)) {
                if (phaseSelector.status(terminal, phase).direction.has(PhaseDirection.OUT))
                    outPhases.add(phase)
            }
        }
    }

    private fun queueConnectedTerminals(
        traversal: BranchRecursiveTraversal<TreeNode>,
        current: TreeNode,
        outTerminal: Terminal,
        outPhases: Set<SinglePhaseKind>
    ) {
        // Get all the terminals connected to terminals with phases going out
        val inTerminals = connectedTerminals(outTerminal, outPhases)

        // Make sure we do not loop back out the incoming terminal if its direction is both.
        if (inTerminals.any { it.to == current.parent?.conductingEquipment })
            return

        val queueNext = if (inTerminals.size > 1 || outTerminal.conductingEquipment!!.numTerminals() > 2)
            { next: TreeNode -> traversal.branchQueue.add(traversal.branchSupplier().setStart(next)) }
        else
            { next: TreeNode -> traversal.queue.add(next) }

        inTerminals
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
