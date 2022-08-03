/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.traversals.Tracker

/**
 * A specialised tracker for traversals that use [ConductingEquipmentStep].
 *
 * Will consider something visited only if the number of steps is greater than or equal to minimum number of steps used to get to an item previously. This
 * means that the same item can be visited multiple times if a short path is traversed.
 */
class ConductingEquipmentStepTracker : Tracker<ConductingEquipmentStep> {

    private val minimumSteps = mutableMapOf<ConductingEquipment, Int>()

    override fun hasVisited(item: ConductingEquipmentStep): Boolean = minimumSteps[item.conductingEquipment]?.let { it <= item.step } ?: false

    override fun visit(item: ConductingEquipmentStep): Boolean {
        val previousSteps = minimumSteps[item.conductingEquipment] ?: Int.MAX_VALUE
        val newSteps = item.step.coerceAtMost(previousSteps)

        return if (newSteps < previousSteps) {
            minimumSteps[item.conductingEquipment] = newSteps
            true
        } else
            false
    }

    override fun clear() {
        minimumSteps.clear()
    }

}
