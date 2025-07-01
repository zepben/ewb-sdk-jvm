/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.traversal.StepContext
import com.zepben.ewb.services.network.tracing.traversal.StopCondition

class EquipmentStepLimitCondition<T>(
    val limit: Int
) : StopCondition<NetworkTraceStep<T>> {
    override fun shouldStop(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return item.numEquipmentSteps >= limit
    }
}
