/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.StopConditionWithContextValue
import kotlin.reflect.KClass

internal class EquipmentTypeStepLimitCondition<T>(
    private val limit: Int,
    private val equipmentType: KClass<out ConductingEquipment>
) : StopConditionWithContextValue<NetworkTraceStep<T>, Int>() {
    override fun shouldStop(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return (context.getValue<Int>(key) ?: 0) >= limit
    }

    override fun computeInitialValue(nextItem: NetworkTraceStep<T>): Int = 0

    override val key: String = "sdk:${equipmentType.simpleName}Count"

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<T>, currentValue: Int): Int {
        return when {
            nextItem.path.tracedInternally -> currentValue
            nextItem.path.toEquipment::class.java.isAssignableFrom(equipmentType.java) -> (currentValue) + 1
            else -> currentValue
        }
    }
}
