/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import java.util.*

/**
 * Class that records which cores were traced to get to a given conducting equipment during a trace.
 * Allows a trace to continue only on the cores used to get to the current step in the trace.
 */
class PhaseStep private constructor(
    val conductingEquipment: ConductingEquipment,
    phases: Collection<SinglePhaseKind>,
    val previous: ConductingEquipment?
) {

    val phases: Set<SinglePhaseKind> = phases.toSet()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is PhaseStep -> false
        else -> conductingEquipment == other.conductingEquipment &&
            phases == other.phases
    }

    override fun hashCode(): Int = Objects.hash(conductingEquipment, phases)

    override fun toString(): String =
        "PhaseStep{" +
            "current = " + conductingEquipment.mRID +
            ", phases = " + phases +
            ", previous = " + previous +
            '}'

    companion object {

        @JvmStatic
        fun startAt(conductingEquipment: ConductingEquipment, phases: Collection<SinglePhaseKind>): PhaseStep =
            PhaseStep(conductingEquipment, phases, null)

        @JvmStatic
        fun startAt(conductingEquipment: ConductingEquipment, phaseCode: PhaseCode): PhaseStep =
            startAt(conductingEquipment, phaseCode.singlePhases)

        @JvmStatic
        fun continueAt(conductingEquipment: ConductingEquipment, phases: Collection<SinglePhaseKind>, previous: ConductingEquipment?): PhaseStep =
            PhaseStep(conductingEquipment, phases, previous)

        @JvmStatic
        fun continueAt(conductingEquipment: ConductingEquipment, phaseCode: PhaseCode, previous: ConductingEquipment?): PhaseStep =
            continueAt(conductingEquipment, phaseCode.singlePhases, previous)

    }

}
