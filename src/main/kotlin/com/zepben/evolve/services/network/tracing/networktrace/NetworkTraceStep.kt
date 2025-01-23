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
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep.Type

/**
 * Represents a single step in a network trace, containing information about the path taken and associated data.
 *
 * @param T The type of additional data associated with the trace step.
 * @property path The path representing the transition from one terminal to another.
 * @property numTerminalSteps The count of terminals stepped on along this path.
 * @property numEquipmentSteps The count of equipment stepped on along this path.
 * @property data Additional data associated with this step in the trace.
 * @property type The [Type] of this step.
 */
class NetworkTraceStep<out T>(
    val path: Path,
    val numTerminalSteps: Int,
    val numEquipmentSteps: Int,
    val data: T
) {

    /**
     * Returns the [Type] of the step. This will be [Type.INTERNAL] if [Path.tracedInternally] is true, [Type.EXTERNAL] when [Path.tracedExternally] is true
     * and will never be [Type.ALL] which is used in other NetworkTrace functionality to determine if all steps should be used for that particular function.
     *
     * @return [Type.INTERNAL] with [Path.tracedInternally] is true, [Type.EXTERNAL] when [Path.tracedExternally] is true
     */
    val type: Type get() = if (path.tracedInternally) Type.INTERNAL else Type.EXTERNAL

    operator fun component1(): Path = path
    operator fun component2(): T = data

    /**
     * Represents the path taken in a network trace step, detailing the transition from one terminal to another.
     *
     * A limitation of the network trace is that all terminals must have associated conducting equipment. This means that if the [fromTerminal]
     * or [toTerminal] have `null` conducting equipment an [IllegalStateException] will be thrown.
     *
     * @property fromTerminal The terminal that was stepped from.
     * @property toTerminal The terminal that was stepped to.
     * @property nominalPhasePaths A list of nominal phase paths traced in this step. If this is empty, phases have been ignored.
     * @property fromEquipment The conducting equipment associated with the [fromTerminal].
     * @property toEquipment The conducting equipment associated with the [toTerminal].
     * @property tracedInternally `true` if the from and to terminals belong to the same equipment; `false` otherwise.
     * @property tracedExternally `true` if the from and to terminals belong to different equipment; `false` otherwise.
     */
    data class Path(
        val fromTerminal: Terminal,
        val toTerminal: Terminal,
        val nominalPhasePaths: List<NominalPhasePath> = emptyList(),

        // This will need to be added when we add clamps to the model. The proposed idea is that if an AcLineSegment has multiple clamps and your current
        // path is one of the clamps, one of the next step paths will be another clamp on the AcLineSegment, essentially jumping over the AcLineSegment for the Path.
        // If there is a cut on the AcLineSegment, you may need to know about it, primarily because the cut could create an open point between the clamps that needs
        // to prevent queuing as part of an "open test".
        // NOTE: Not sure if we will actually need this in the constructor as you could technically pull it back off the Cut or Clamp from / to equipment.
        //       There is just a performance hit to compute that vs if you already have it when constructing the object.
        // abstract val viaSegment: AcLineSegment? = null,
    ) {
        val fromEquipment: ConductingEquipment =
            fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

        val toEquipment: ConductingEquipment =
            toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

        val tracedInternally: Boolean get() = fromEquipment == toEquipment

        val tracedExternally: Boolean get() = !tracedInternally
    }

    /**
     * Indicates either the of a NetworkTraceStep, or which [NetworkTraceStep.type] types to match when a type option is required.
     */
    enum class Type {
        /**
         * Indicates that all network trace steps should be matched, regardless of [NetworkTraceStep.type].
         * Be aware that this means that [NetworkTraceStep.type] will never have this value.
         */
        ALL,

        /**
         * Indicates that a [NetworkTraceStep] is a step where the path traced 'internally' from one terminal to another on the same piece of equipment.
         */
        INTERNAL,

        /**
         * Indicates that a [NetworkTraceStep] is a step where the path traced 'externally' from one terminal to another on different pieces of equipment.
         */
        EXTERNAL
    }
}
