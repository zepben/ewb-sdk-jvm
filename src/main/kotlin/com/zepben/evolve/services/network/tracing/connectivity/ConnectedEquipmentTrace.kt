/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConnectivityNode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

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

    /**
     * @return a limited connected equipment trace that traces equipment on the normal state of the network.
     */
    fun newNormalLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace =
        LimitedConnectedEquipmentTrace(::newNormalConnectedEquipmentTrace, Terminal::normalFeederDirection)

    /**
     * @return a limited connected equipment trace that traces equipment on the current state of the network.
     */
    fun newCurrentLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace =
        LimitedConnectedEquipmentTrace(::newCurrentConnectedEquipmentTrace, Terminal::currentFeederDirection)

    /**
     * Create a new [BasicTraversal] that traverses in the downstream direction using the normal state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    fun newNormalDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        BasicTraversal(createQueueNext(FeederDirection.DOWNSTREAM, Terminal::normalFeederDirection), queue, BasicTracker())

    /**
     * Create a new [BasicTraversal] that traverses in the downstream direction using the current state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    fun newCurrentDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        BasicTraversal(createQueueNext(FeederDirection.DOWNSTREAM, Terminal::currentFeederDirection), queue, BasicTracker())

    /**
     * Create a new [BasicTraversal] that traverses in the upstream direction using the normal state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    fun newNormalUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        BasicTraversal(createQueueNext(FeederDirection.UPSTREAM, Terminal::normalFeederDirection), queue, BasicTracker())

    /**
     * Create a new [BasicTraversal] that traverses in the upstream direction using the current state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    fun newCurrentUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        BasicTraversal(createQueueNext(FeederDirection.UPSTREAM, Terminal::currentFeederDirection), queue, BasicTracker())

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<ConductingEquipmentStep> =
        BasicTraversal.QueueNext { (conductingEquipment, step), traversal ->
            if ((step == 0) || !openTest.isOpen(conductingEquipment, null)) {
                val nextStep = step + 1
                conductingEquipment.terminals
                    .asSequence()
                    .flatMap { t -> t.connectedTerminals().mapNotNull { it.conductingEquipment } }
                    .forEach { traversal.queue.add(ConductingEquipmentStep(it, nextStep)) }
            }
        }

    private fun createQueueNext(direction: FeederDirection, getDirection: Terminal.() -> FeederDirection): BasicTraversal.QueueNext<ConductingEquipment> =
        BasicTraversal.QueueNext { ce, traversal ->
            ce.terminals
                .asSequence()
                .filter { direction in it.getDirection() }
                .flatMap { it.connectedTerminals() }
                .filter { !direction in it.getDirection() }
                .mapNotNull { it.conductingEquipment }
                .distinct()
                .forEach { traversal.queue.add(it) }
        }

}
