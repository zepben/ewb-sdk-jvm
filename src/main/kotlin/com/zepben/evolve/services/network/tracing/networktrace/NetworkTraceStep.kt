/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath

class NetworkTraceStep<T>(
    val path: StepPath,
    // TODO [Review]: Do we like the name data?
    val data: T
) {
    operator fun component1(): StepPath = path
    operator fun component2(): T = data
}

/**
 * A class that holds information about where a NetworkTrace has stepped from and to. See [NetworkTrace] for more information.
 *
 */
data class StepPath(
    val fromTerminal: Terminal,
    val toTerminal: Terminal,
    val numTerminalSteps: Int,
    val numEquipmentSteps: Int,
    val nominalPhasePaths: List<NominalPhasePath> = emptyList(),
    // This will need to be added when we add clamps to the model. The proposed idea is that if an AcLineSegment has multiple clamps and your current
    // path is one of the clamps, one of the next StepPaths will be another clamp on the AcLineSegment, essentially jumping over the AcLineSegment for the Path.
    // If there is a cut on the AcLineSegment, you may need to know about it, primarily because the cut could create an open point between the clamps that needs
    // to prevent queuing as part of an "open test".
    // abstract val viaSegment: AcLineSegment? = null,
) {
    val fromEquipment: ConductingEquipment =
        fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

    val toEquipment: ConductingEquipment =
        toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

    val tracedInternally: Boolean = fromEquipment == toEquipment
}
