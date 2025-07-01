/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTrace
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.limitEquipmentSteps
import com.zepben.ewb.services.network.tracing.networktrace.operators.FeederDirectionStateOperations
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.networktrace.operators.OpenStateOperators
import com.zepben.ewb.services.network.tracing.traversal.QueueCondition
import com.zepben.ewb.services.network.tracing.traversal.StopCondition
import kotlin.reflect.KClass

/**
 * Provides a collection of predefined conditions for use in network traces.
 */
object Conditions {

    /**
     * Creates a [NetworkTrace] condition that will cause tracing a feeder upstream (towards the head terminal).
     * This uses [FeederDirectionStateOperations.getDirection] receiver instance method within the condition.
     *
     * This variant is used to enable a DSL style syntax when setting up a [NetworkTrace].
     * ```
     * trace.addCondition { upstream() }
     * ```
     *
     * @return A [NetworkTraceQueueCondition] that results in upstream tracing.
     */
    @JvmStatic
    fun <T> NetworkStateOperators.upstream(): QueueCondition<NetworkTraceStep<T>> =
        withDirection(FeederDirection.UPSTREAM)

    /**
     * Creates a [NetworkTrace] condition that will cause tracing a feeder downstream (away from the head terminal).
     * This uses [FeederDirectionStateOperations.getDirection] receiver instance method within the condition.
     *
     * This variant is used to enable a DSL style syntax when setting up a [NetworkTrace].
     * ```
     * trace.addCondition { downstream() }
     * ```
     *
     * @return A [NetworkTraceQueueCondition] that results in downstream tracing.
     */
    @JvmStatic
    fun <T> NetworkStateOperators.downstream(): QueueCondition<NetworkTraceStep<T>> =
        withDirection(FeederDirection.DOWNSTREAM)

    /**
     * Creates a [NetworkTrace] condition that will cause tracing only terminals with directions that match [direction].
     * This uses [FeederDirectionStateOperations.getDirection] receiver instance method within the condition.
     *
     * This variant is used to enable a DSL style syntax when setting up a [NetworkTrace].
     * ```
     * trace.addCondition { withDirection(FeederDirection.BOTH) }
     * ```
     *
     * @return A [NetworkTraceQueueCondition] that results in upstream tracing.
     */
    @JvmStatic
    fun <T> NetworkStateOperators.withDirection(direction: FeederDirection): QueueCondition<NetworkTraceStep<T>> =
        DirectionCondition(direction, this)

    /**
     * Creates a [NetworkTrace] condition that will cause a trace to not queue through open equipment.
     *
     * @param openTest A function that returns if the equipment is open, optionally per phase
     * @param phase The phase to test the open status; `null` to ignore phases and check if it is open on any phase.
     * @return A [NetworkTraceQueueCondition] that results in not tracing through open equipment.
     */
    @JvmStatic
    fun <T> stopAtOpen(openTest: (Switch, SinglePhaseKind?) -> Boolean, phase: SinglePhaseKind? = null): QueueCondition<NetworkTraceStep<T>> =
        OpenCondition(openTest, phase)

    /**
     * Creates a [NetworkTrace] condition that will cause a trace to not queue through open equipment.
     * This uses [OpenStateOperators.isOpen] receiver instance method within the condition.
     *
     * This variant is used to enable a DSL style syntax when setting up a [NetworkTrace].
     * ```
     * trace.addCondition { stopAtOpen() }
     * ```
     *
     * @param phase The phase to test the open status; `null` to ignore phases and check if it is open on any phase.
     * @return A [NetworkTraceQueueCondition] that results in not queueing through open equipment.
     */
    @JvmStatic
    fun <T> OpenStateOperators.stopAtOpen(phase: SinglePhaseKind? = null): QueueCondition<NetworkTraceStep<T>> =
        stopAtOpen(this::isOpen, phase)

    /**
     * Creates a [NetworkTrace] condition that stops tracing a path once a specified number of equipment steps have been reached.
     *
     * @param limit The maximum number of equipment steps allowed before stopping.
     * @return A [NetworkTraceStopCondition] that stops tracing the path once the step limit is reached.
     */
    @JvmStatic
    fun <T> limitEquipmentSteps(limit: Int): StopCondition<NetworkTraceStep<T>> =
        EquipmentStepLimitCondition(limit)

    /**
     * Creates a [NetworkTrace] condition that stops tracing a path once a specified number of steps
     * have been taken on a particular type of [ConductingEquipment] within the current trace path.
     *
     * @param limit The maximum number of steps allowed on the specified equipment type before stopping.
     * @param equipmentType The class of the equipment type to track against the limit.
     * @return A [NetworkTraceStopCondition] that stops the trace when the step limit is reached for the specified equipment type.
     */
    @JvmStatic
    fun <T> limitEquipmentSteps(limit: Int, equipmentType: KClass<out ConductingEquipment>): StopCondition<NetworkTraceStep<T>> =
        EquipmentTypeStepLimitCondition(limit, equipmentType)

    /**
     * Overload of [limitEquipmentSteps] for Java interop.
     *s
     * @param limit The maximum number of steps allowed on the specified equipment type before stopping.
     * @param equipmentType The class of the equipment type to track against the limit.
     * @return A [NetworkTraceStopCondition] that stops the trace when the step limit is reached for the specified equipment type.
     */
    @JvmStatic
    fun <T> limitEquipmentSteps(limit: Int, equipmentType: Class<out ConductingEquipment>): StopCondition<NetworkTraceStep<T>> =
        limitEquipmentSteps(limit, equipmentType.kotlin)

}
