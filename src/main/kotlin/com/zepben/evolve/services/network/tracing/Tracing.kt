/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConnectivityNode
import com.zepben.evolve.services.network.tracing.connectivity.*
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.RemoveDirection
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.*
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

/**
 * A utility class intended to be a one-stop shop to instantiate all your common network traces!
 */
object Tracing {

    @JvmStatic
    fun <T> createBasicDepthTrace(queueNext: BasicTraversal.QueueNext<T>): BasicTraversal<T> {
        return BasicTraversal(queueNext, BasicQueue.depthFirst(), BasicTracker())
    }

    @JvmStatic
    fun <T> createBasicBreadthTrace(queueNext: BasicTraversal.QueueNext<T>): BasicTraversal<T> {
        return BasicTraversal(queueNext, BasicQueue.breadthFirst(), BasicTracker())
    }

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new [ConnectedEquipmentTraversal] instance.
     */
    @JvmStatic
    fun connectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newConnectedEquipmentTrace()

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new [ConnectedEquipmentTraversal] instance.
     */
    @JvmStatic
    fun connectedEquipmentBreadthTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newConnectedEquipmentBreadthTrace()

    /**
     * Creates a new traversal that traces equipment that are connected stopping at normally open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new [ConnectedEquipmentTraversal] instance.
     */
    fun normalConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newNormalConnectedEquipmentTrace()

    /**
     * Creates a new traversal that traces equipment that are connected stopping at currently open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new [ConnectedEquipmentTraversal] instance.
     */
    fun currentConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newCurrentConnectedEquipmentTrace()


    /**
     * Creates a new limited traversal that traces equipment that are connected stopping at normally open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * The trace can be limited by the number of steps, or the feeder direction.
     *
     * @return The new [LimitedConnectedEquipmentTrace] instance.
     */
    fun normalLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newNormalLimitedConnectedEquipmentTrace()

    /**
     * Creates a new limited traversal that traces equipment that are connected stopping at normally open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * The trace can be limited by the number of steps, or the feeder direction.
     *
     * @return The new [LimitedConnectedEquipmentTrace] instance.
     */
    fun currentLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newCurrentLimitedConnectedEquipmentTrace()

    /**
     * Create a new [BasicTraversal] that traverses in the downstream direction using the normal state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    @JvmStatic
    @JvmOverloads
    fun normalDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        ConnectedEquipmentTrace.newNormalDownstreamEquipmentTrace(queue)

    /**
     * Create a new [BasicTraversal] that traverses in the downstream direction using the current state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    @JvmStatic
    @JvmOverloads
    fun currentDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        ConnectedEquipmentTrace.newCurrentDownstreamEquipmentTrace(queue)

    /**
     * Create a new [BasicTraversal] that traverses in the upstream direction using the normal state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    @JvmStatic
    @JvmOverloads
    fun normalUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        ConnectedEquipmentTrace.newNormalUpstreamEquipmentTrace(queue)

    /**
     * Create a new [BasicTraversal] that traverses in the upstream direction using the current state of the network. The trace works on [ConductingEquipment],
     * and ignores phase connectivity, instead considering things to be connected if they share a [ConnectivityNode].
     *
     * @param queue An optional parameter to allow you to change the queue being used for the traversal. The default value is a LIFO queue.
     * @return The [BasicTraversal].
     */
    @JvmStatic
    @JvmOverloads
    fun currentUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
        ConnectedEquipmentTrace.newCurrentUpstreamEquipmentTrace(queue)

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun connectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityTrace()

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun connectivityBreadthTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityBreadthTrace()

    /**
     * Creates a new traversal that traces equipment that are connected stopping at normally open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    fun normalConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newNormalConnectivityTrace()

    /**
     * Creates a new traversal that traces equipment that are connected stopping at currently open points. This ignores phases etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    fun currentConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newCurrentConnectivityTrace()

    /**
     * Creates a new phase based trace ignoring the state of open phases
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun phaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newTrace()

    /**
     * Creates a new phase based trace stopping on normally open phases
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun normalPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalTrace()

    /**
     * Creates a new phase based trace stopping at currently open phases
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun currentPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentTrace()

    /**
     * Creates a new downstream trace based on phases and the normal state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun normalDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalDownstreamTrace()

    /**
     * Creates a new downstream trace based on phases and the current state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun currentDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentDownstreamTrace()

    /**
     * Creates a new upstream trace based on phases and the normal state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun normalUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalUpstreamTrace()

    /**
     * Creates a new upstream trace based on phases and the current state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    @JvmStatic
    fun currentUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentUpstreamTrace()

    /**
     * Returns an instance of [SetPhases] convenience class for setting phases on a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun setPhases(): SetPhases = SetPhases()

    /**
     * Returns an instance of [SetDirection] convenience class for setting feeder directions on a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun setDirection(): SetDirection = Tracing.normalSetDirection()

    /**
     * Returns an instance of [PhaseInferrer] convenience class for inferring phases on a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun phaseInferrer(): PhaseInferrer = PhaseInferrer()

    /**
     * Returns an instance of [RemovePhases] convenience class for removing phases from a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun removePhases(): RemovePhases = RemovePhases()

    /**
     * Returns an instance of [RemoveDirection] convenience class for removing feeder directions from a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun removeDirection(): RemoveDirection = RemoveDirection()

    /**
     * Returns an instance of [AssignToFeeders] convenience class for assigning equipment containers to HV/MV feeders on a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun assignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders()

    /**
     * Returns an instance of [AssignToFeeders] convenience class for assigning equipment containers to LV feeders on a network.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun assignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders()

    /**
     * Returns an instance of [DownstreamTree] convenience class for tracing using the
     * normal state of a network
     * .
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun normalDownstreamTree(): DownstreamTree = DownstreamTree(DirectionSelector.NORMAL_DIRECTION)

    /**
     * Returns an instance of [DownstreamTree] convenience class for tracing using the
     * current state of a network
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun currentDownstreamTree(): DownstreamTree = DownstreamTree(DirectionSelector.CURRENT_DIRECTION)

    /**
     * Returns an instance of [FindWithUsagePoints] convenience class for finding conducting equipment with attached usage points.
     *
     * @return A new traversal instance.
     */
//    @JvmStatic
//    fun findWithUsagePoints(): FindWithUsagePoints = FindWithUsagePoints()

}
