/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.StopConditionWithContextValue
import kotlin.reflect.KClass

internal class EquipmentTypeStepLimitCondition<T>(
    val limit: Int,
    val equipmentType: KClass<out ConductingEquipment>
) : StopConditionWithContextValue<NetworkTraceStep<T>, Int> {
    override fun shouldStop(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return (context.value) >= limit
    }

    // TODO [Review]: Should this be 1 if the first item matches the equipmentType or always start at 0?
    override fun computeInitialValue(item: NetworkTraceStep<T>): Int =
        if (matchesEquipmentType(item.path.toEquipment)) 1 else 0

    override val key: String = "sdk:${equipmentType.simpleName}Count"

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<T>, currentItem: NetworkTraceStep<T>, currentValue: Int): Int {
        return when {
            nextItem.path.tracedInternally -> currentValue
            matchesEquipmentType(nextItem.path.toEquipment) -> currentValue + 1
            else -> currentValue
        }
    }

    private fun matchesEquipmentType(conductingEquipment: ConductingEquipment): Boolean =
        conductingEquipment::class.java.isAssignableFrom(equipmentType.java)
}
