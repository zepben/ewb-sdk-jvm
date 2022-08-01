/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * A class that creates commonly used connectivity based traces. These ignore phases, they are purely to trace equipment that
 * are connected in any way. You can add custom step actions and stop conditions to the returned traversal.
 */
object ConnectedEquipmentTrace {

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectedEquipmentTrace(): ConnectedEquipmentTraversal =
        ConnectedEquipmentTraversal(queueNext(OpenTest.IGNORE_OPEN), BasicQueue.depthFirst(), ConductingEquipmentStepTracker())

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectedEquipmentBreadthTrace(): ConnectedEquipmentTraversal =
        ConnectedEquipmentTraversal(queueNext(OpenTest.IGNORE_OPEN), BasicQueue.breadthFirst(), ConductingEquipmentStepTracker())

    /**
     * @return a traversal that traces equipment that are connected stopping at normally open points.
     */
    fun newNormalConnectedEquipmentTrace(): ConnectedEquipmentTraversal =
        ConnectedEquipmentTraversal(queueNext(OpenTest.NORMALLY_OPEN), BasicQueue.depthFirst(), ConductingEquipmentStepTracker())

    /**
     * @return a traversal that traces equipment that are connected stopping at currently open points.
     */
    fun newCurrentConnectedEquipmentTrace(): ConnectedEquipmentTraversal =
        ConnectedEquipmentTraversal(queueNext(OpenTest.CURRENTLY_OPEN), BasicQueue.depthFirst(), ConductingEquipmentStepTracker())

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<ConductingEquipmentStep> =
        BasicTraversal.QueueNext { (conductingEquipment, step), traversal ->
            if (!openTest.isOpen(conductingEquipment, null)) {
                val nextStep = step + 1
                conductingEquipment.terminals
                    .asSequence()
                    .flatMap { t -> t.connectedTerminals().mapNotNull { it.conductingEquipment } }
                    .forEach { traversal.queue.add(ConductingEquipmentStep(it, nextStep)) }
            }
        }

}
