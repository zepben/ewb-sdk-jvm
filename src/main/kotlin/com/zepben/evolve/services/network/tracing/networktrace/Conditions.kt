/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.networktrace.conditions.*
import com.zepben.evolve.services.network.tracing.traversalV2.TraversalCondition
import kotlin.reflect.KClass

private typealias NetworkTraceCondition<T> = TraversalCondition<NetworkTraceStep<T>>

object Conditions {

    fun <T> upstream(directionSelector: DirectionSelector): NetworkTraceCondition<T> =
        DirectionCondition(FeederDirection.UPSTREAM, directionSelector)

    fun <T> normallyUpstream(): NetworkTraceCondition<T> =
        upstream(DirectionSelector.NORMAL_DIRECTION)

    fun <T> currentlyUpstream(): NetworkTraceCondition<T> =
        upstream(DirectionSelector.CURRENT_DIRECTION)

    fun <T> downstream(directionSelector: DirectionSelector): NetworkTraceCondition<T> =
        DirectionCondition(FeederDirection.DOWNSTREAM, directionSelector)

    fun <T> normallyDownstream(): NetworkTraceCondition<T> =
        downstream(DirectionSelector.NORMAL_DIRECTION)

    fun <T> currentlyDownstream(): NetworkTraceCondition<T> =
        downstream(DirectionSelector.CURRENT_DIRECTION)

    fun <T> stopAtOpen(openTest: OpenTest, phase: SinglePhaseKind? = null): NetworkTraceCondition<T> =
        OpenCondition(openTest, phase)

    fun <T> stopAtNormallyOpen(phase: SinglePhaseKind? = null): NetworkTraceCondition<T> =
        stopAtOpen(OpenTest.NORMALLY_OPEN, phase)

    fun <T> stopAtCurrentlyOpen(phase: SinglePhaseKind? = null): NetworkTraceCondition<T> =
        stopAtOpen(OpenTest.CURRENTLY_OPEN, phase)

    fun <T> limitEquipmentSteps(limit: Int): NetworkTraceCondition<T> =
        EquipmentStepLimitCondition(limit)

    fun <T> limitEquipmentSteps(limit: Int, equipmentType: KClass<out ConductingEquipment>): NetworkTraceCondition<T> =
        EquipmentTypeStepLimitCondition(limit, equipmentType)

    fun <T> limitEquipmentSteps(limit: Int, equipmentType: Class<out ConductingEquipment>): NetworkTraceCondition<T> =
        limitEquipmentSteps(limit, equipmentType.kotlin)

    fun <T> withPhases(phases: PhaseCode): NetworkTraceCondition<T> = PhaseCondition(phases)

}
