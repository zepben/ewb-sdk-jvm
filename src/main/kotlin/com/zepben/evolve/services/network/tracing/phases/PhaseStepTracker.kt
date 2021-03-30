/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.annotations.EverythingIsNonnullByDefault
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.traversals.Tracker

/**
 *
 * A specialised tracker that tracks the cores that have been visited on a piece of conducting equipment. When attempting to visit
 * for the second time, this tracker will return false if the cores being tracked are a subset of those already visited.
 * For example, if you visit A1 on cores 0, 1, 2 and later attempt to visit A1 on core 0, 1, visit will return false,
 * but an attempt to visit on cores 2, 3 would return true as 3 has not been visited before.
 *
 * This tracker does not support null items.
 */
@EverythingIsNonnullByDefault
class PhaseStepTracker : Tracker<PhaseStep> {

    private val visited = mutableMapOf<ConductingEquipment, MutableSet<SinglePhaseKind>>()

    override fun hasVisited(item: PhaseStep): Boolean = item.phases.containsAll(visited.getOrDefault(item.conductingEquipment, emptySet()))

    override fun visit(item: PhaseStep): Boolean = visited.computeIfAbsent(item.conductingEquipment) { mutableSetOf() }.addAll(item.phases)

    override fun clear() {
        visited.clear()
    }

}
