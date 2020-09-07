/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network.model

import com.zepben.annotations.EverythingIsNonnullByDefault
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.PhaseDirection
import com.zepben.cimbend.network.model.TracedPhasesBitManipulation.Companion.add
import com.zepben.cimbend.network.model.TracedPhasesBitManipulation.Companion.direction
import com.zepben.cimbend.network.model.TracedPhasesBitManipulation.Companion.phase
import com.zepben.cimbend.network.model.TracedPhasesBitManipulation.Companion.remove
import com.zepben.cimbend.network.model.TracedPhasesBitManipulation.Companion.set

/**
 * Class that holds the traced phase statuses for the current and normal state of the network.
 * See [TracedPhasesBitManipulation] for details on bit representation.
 */
@EverythingIsNonnullByDefault
data class TracedPhases(
    private var normalStatus: Int = 0,
    private var currentStatus: Int = 0
) {

    /* -- Public Methods -- */
    fun phaseNormal(nominalPhase: SinglePhaseKind): SinglePhaseKind {
        validPhaseCheck(nominalPhase)
        return phase(normalStatus, nominalPhase)
    }

    fun phaseCurrent(nominalPhase: SinglePhaseKind): SinglePhaseKind {
        validPhaseCheck(nominalPhase)
        return phase(currentStatus, nominalPhase)
    }

    fun directionNormal(nominalPhase: SinglePhaseKind): PhaseDirection {
        validPhaseCheck(nominalPhase)
        return direction(normalStatus, nominalPhase)
    }

    fun directionCurrent(nominalPhase: SinglePhaseKind): PhaseDirection {
        validPhaseCheck(nominalPhase)
        return direction(currentStatus, nominalPhase)
    }

    fun setNormal(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === SinglePhaseKind.NONE || direction === PhaseDirection.NONE) {
            removeNormal(phaseNormal(nominalPhase), nominalPhase)
            return true
        }

        if (phaseNormal(nominalPhase) === singlePhaseKind && directionNormal(nominalPhase) === direction)
            return false

        normalStatus = set(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun setCurrent(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === SinglePhaseKind.NONE || direction === PhaseDirection.NONE) {
            removeCurrent(phaseCurrent(nominalPhase), nominalPhase)
            return true
        }

        if (phaseCurrent(nominalPhase) === singlePhaseKind && directionCurrent(nominalPhase) === direction)
            return false

        currentStatus = set(currentStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun addNormal(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === SinglePhaseKind.NONE || direction === PhaseDirection.NONE)
            return false
        if (phaseNormal(nominalPhase) !== SinglePhaseKind.NONE && singlePhaseKind !== phaseNormal(nominalPhase))
            throw UnsupportedOperationException("Crossing Phases.")
        if (directionNormal(nominalPhase).has(direction))
            return false

        normalStatus = add(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun addCurrent(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === SinglePhaseKind.NONE || direction === PhaseDirection.NONE)
            return false
        if (phaseCurrent(nominalPhase) !== SinglePhaseKind.NONE && singlePhaseKind !== phaseCurrent(nominalPhase))
            throw UnsupportedOperationException("Crossing Phases.")
        if (directionCurrent(nominalPhase).has(direction))
            return false

        currentStatus = add(currentStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun removeNormal(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection?, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (phaseNormal(nominalPhase) !== singlePhaseKind || !directionNormal(nominalPhase).has(direction!!))
            return false

        normalStatus = remove(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun removeCurrent(singlePhaseKind: SinglePhaseKind, direction: PhaseDirection?, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (phaseCurrent(nominalPhase) !== singlePhaseKind || !directionCurrent(nominalPhase).has(direction!!))
            return false

        currentStatus = remove(currentStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun removeNormal(singlePhaseKind: SinglePhaseKind, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind !== phaseNormal(nominalPhase))
            return false

        normalStatus = remove(normalStatus, nominalPhase)
        return true
    }

    fun removeCurrent(singlePhaseKind: SinglePhaseKind, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind !== phaseCurrent(nominalPhase))
            return false

        currentStatus = remove(currentStatus, nominalPhase)
        return true
    }

    /**
     * The underlying implementation value tracking the normal status.
     * It is primarily used for data serialisation and debugging within official cimbend libraries and utilities.
     *
     * Note this property should be considered cimbend internal and not for general public use as the underlying
     * data structure to store the status could change at any time (and thus be a breaking change).
     * Use at your own risk.
     */
    var normalStatusInternal: Int
        get() = normalStatus
        set(value) {
            normalStatus = value
        }

    /**
     * The underlying implementation value tracking the current status.
     * It is primarily used for data serialisation and debugging within official cimbend libraries and utilities.
     *
     * Note this property should be considered cimbend internal and not for general public use as the underlying
     * data structure to store the status could change at any time (and thus be a breaking change).
     * Use at your own risk.
     */
    var currentStatusInternal: Int
        get() = currentStatus
        set(value) {
            currentStatus = value
        }

    override fun toString(): String {
        val normal = PhaseCode.ABCN.singlePhases().joinToString(prefix = "{", postfix = "}") { "${phaseNormal(it)}:${directionNormal(it)}" }
        val current = PhaseCode.ABCN.singlePhases().joinToString(prefix = "{", postfix = "}") { "${phaseCurrent(it)}:${directionCurrent(it)}" }
        return "TracedPhases(normalStatus=$normal, currentStatus=$current)"
    }

    private fun validPhaseCheck(phase: SinglePhaseKind) {
        when (phase) {
            SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N, SinglePhaseKind.X, SinglePhaseKind.Y -> return
            SinglePhaseKind.NONE, SinglePhaseKind.INVALID -> throw IllegalArgumentException(String.format("INTERNAL ERROR: Phase {%s} is invalid.", phase))
        }
    }
}
