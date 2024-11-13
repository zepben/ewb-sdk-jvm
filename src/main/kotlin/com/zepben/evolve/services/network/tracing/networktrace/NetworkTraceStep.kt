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
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath

/**
 * Represents a single step in a network trace, containing information about the path taken and associated data.
 *
 * @param T The type of additional data associated with the trace step.
 * @property path The path representing the transition from one terminal to another.
 * @property data Additional data associated with this step in the trace.
 */
// TODO [Review]: Is there a benefit to `out T` here?
class NetworkTraceStep<out T>(
    val path: StepPath,
    // TODO [Review]: Do we like the name data?
    val data: T
) {
    operator fun component1(): StepPath = path
    operator fun component2(): T = data
}

/**
 * Represents the path taken in a network trace step, detailing the transition from one terminal to another.
 *
 * A limitation of the network trace is that all terminals must have associated conducting equipment. This means that if the [fromTerminal]
 * or [toTerminal] have `null` conducting equipment an [IllegalStateException] will be thrown.
 *
 * @property fromTerminal The terminal that was stepped from.
 * @property toTerminal The terminal that was stepped to.
 * @property numTerminalSteps The count of terminals stepped on along this path.
 * @property numEquipmentSteps The count of equipment stepped on along this path.
 * @property nominalPhasePaths A list of nominal phase paths traced in this step. If this is empty, phases have been ignored.
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
    /**
     * The conducting equipment associated with the [fromTerminal].
     */
    val fromEquipment: ConductingEquipment =
        fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

    /**
     * The conducting equipment associated with the [toTerminal].
     */
    val toEquipment: ConductingEquipment =
        toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

    /**
     * `true` if the from and to terminals belong to the same equipment; `false` otherwise.
     */
    val tracedInternally: Boolean = fromEquipment == toEquipment
}
