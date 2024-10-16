/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.networktrace.conditions.DirectionCondition
import com.zepben.evolve.services.network.tracing.networktrace.conditions.EquipmentStepLimitCondition
import com.zepben.evolve.services.network.tracing.networktrace.conditions.EquipmentTypeStepLimitCondition
import com.zepben.evolve.services.network.tracing.networktrace.conditions.OpenCondition
import com.zepben.evolve.services.network.tracing.traversalV2.QueueCondition
import com.zepben.evolve.services.network.tracing.traversalV2.TraversalCondition
import kotlin.reflect.KClass

private typealias NetworkTraceCondition<T> = TraversalCondition<NetworkTraceStep<T>>
private typealias NetworkTraceQueueCondition<T> = QueueCondition<NetworkTraceStep<T>>

object Conditions {

    fun <T> upstream(getDirection: (Terminal) -> FeederDirection): NetworkTraceCondition<T> =
        DirectionCondition(FeederDirection.UPSTREAM, getDirection)

    fun <T> FeederDirectionStateOperations.upstream(): NetworkTraceCondition<T> =
        upstream(this::getDirection)

    fun <T> downstream(getDirection: (Terminal) -> FeederDirection): NetworkTraceCondition<T> =
        DirectionCondition(FeederDirection.DOWNSTREAM, getDirection)

    fun <T> FeederDirectionStateOperations.downstream(): NetworkTraceCondition<T> =
        downstream(this::getDirection)

    fun <T> stopAtOpen(openTest: OpenTest, phase: SinglePhaseKind? = null): NetworkTraceQueueCondition<T> =
        OpenCondition(openTest, phase)

    fun <T> OpenStateOperators.stopAtOpen(phase: SinglePhaseKind? = null): NetworkTraceQueueCondition<T> =
        stopAtOpen(this::isOpen, phase)

    fun <T> limitEquipmentSteps(limit: Int): NetworkTraceCondition<T> =
        EquipmentStepLimitCondition(limit)

    fun <T> limitEquipmentSteps(limit: Int, equipmentType: KClass<out ConductingEquipment>): NetworkTraceCondition<T> =
        EquipmentTypeStepLimitCondition(limit, equipmentType)

    fun <T> limitEquipmentSteps(limit: Int, equipmentType: Class<out ConductingEquipment>): NetworkTraceCondition<T> =
        limitEquipmentSteps(limit, equipmentType.kotlin)

}
