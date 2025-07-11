/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.traversal.StepContext
import com.zepben.ewb.services.network.tracing.traversal.StopConditionWithContextValue
import kotlin.reflect.KClass

internal class EquipmentTypeStepLimitCondition<T>(
    val limit: Int,
    val equipmentType: KClass<out ConductingEquipment>
) : StopConditionWithContextValue<NetworkTraceStep<T>, Int> {

    override fun shouldStop(item: NetworkTraceStep<T>, context: StepContext): Boolean = context.value >= limit

    override val key: String = "sdk:${equipmentType.simpleName}Count"

    override fun computeInitialValue(item: NetworkTraceStep<T>): Int = 0

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<T>, currentItem: NetworkTraceStep<T>, currentValue: Int): Int {
        return when {
            nextItem.path.tracedInternally -> currentValue
            matchesEquipmentType(nextItem.path.toEquipment) -> currentValue + 1
            else -> currentValue
        }
    }

    private fun matchesEquipmentType(conductingEquipment: ConductingEquipment): Boolean =
        equipmentType.java.isAssignableFrom(conductingEquipment::class.java)
}
