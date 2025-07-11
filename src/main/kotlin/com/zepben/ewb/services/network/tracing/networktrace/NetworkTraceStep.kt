/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep.Type

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

    override fun toString(): String {
        return "NetworkTraceStep(path=$path, numTerminalSteps=$numTerminalSteps, numEquipmentSteps=$numEquipmentSteps, data=$data)"
    }

    /**
     * Represents the path taken in a network trace step, detailing the transition from one terminal to another.
     *
     * A limitation of the network trace is that all terminals must have associated conducting equipment. This means that if the [fromTerminal]
     * or [toTerminal] have `null` conducting equipment an [IllegalStateException] will be thrown.
     *
     * No validation is done on the [traversedAcLineSegment] against the [fromTerminal] and [toTerminal]. It assumes the creator knows what they are doing
     * and thus avoids the overhead of validation as this class will have lots if instances created as part of a [NetworkTrace].
     *
     * @property fromTerminal The terminal that was stepped from.
     * @property toTerminal The terminal that was stepped to.
     * @property traversedAcLineSegment If the fromTerminal and toTerminal path was via an AcLineSegment, this is the segment that was traversed.
     * @property nominalPhasePaths A list of nominal phase paths traced in this step. If this is empty, phases have been ignored.
     * @property fromEquipment The conducting equipment associated with the [fromTerminal].
     * @property toEquipment The conducting equipment associated with the [toTerminal].
     * @property tracedInternally `true` if the from and to terminals belong to the same equipment; `false` otherwise.
     * @property tracedExternally `true` if the from and to terminals belong to different equipment; `false` otherwise.
     * @property didTraverseAcLineSegment `true` if [traversedAcLineSegment] is not null; `false` otherwise.
     */
    data class Path(
        val fromTerminal: Terminal,
        val toTerminal: Terminal,
        val traversedAcLineSegment: AcLineSegment? = null,
        val nominalPhasePaths: List<NominalPhasePath> = emptyList(),
    ) {
        val fromEquipment: ConductingEquipment =
            fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

        val toEquipment: ConductingEquipment =
            toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")

        val tracedInternally: Boolean get() = fromEquipment == toEquipment

        val tracedExternally: Boolean get() = !tracedInternally

        val didTraverseAcLineSegment: Boolean get() = traversedAcLineSegment != null
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
