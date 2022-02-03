/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.Companion.add
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.Companion.direction
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.Companion.phase
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.Companion.remove
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.Companion.set

/**
 * Class that holds the traced phase statuses for the current and normal state of the network.
 * See [TracedPhasesBitManipulation] for details on bit representation.
 */
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

    fun directionNormal(nominalPhase: SinglePhaseKind): FeederDirection {
        validPhaseCheck(nominalPhase)
        return direction(normalStatus, nominalPhase)
    }

    fun directionCurrent(nominalPhase: SinglePhaseKind): FeederDirection {
        validPhaseCheck(nominalPhase)
        return direction(currentStatus, nominalPhase)
    }

    fun setNormal(singlePhaseKind: SinglePhaseKind, direction: FeederDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === NONE || direction === FeederDirection.NONE) {
            removeNormal(phaseNormal(nominalPhase), nominalPhase)
            return true
        }

        if (phaseNormal(nominalPhase) === singlePhaseKind && directionNormal(nominalPhase) === direction)
            return false

        normalStatus = set(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun setCurrent(singlePhaseKind: SinglePhaseKind, direction: FeederDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === NONE || direction === FeederDirection.NONE) {
            removeCurrent(phaseCurrent(nominalPhase), nominalPhase)
            return true
        }

        if (phaseCurrent(nominalPhase) === singlePhaseKind && directionCurrent(nominalPhase) === direction)
            return false

        currentStatus = set(currentStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun addNormal(singlePhaseKind: SinglePhaseKind, direction: FeederDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === NONE || direction === FeederDirection.NONE)
            return false
        if (phaseNormal(nominalPhase) !== NONE && singlePhaseKind !== phaseNormal(nominalPhase))
            throw UnsupportedOperationException("Crossing Phases.")
        if (directionNormal(nominalPhase).has(direction))
            return false

        normalStatus = add(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun addCurrent(singlePhaseKind: SinglePhaseKind, direction: FeederDirection, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (singlePhaseKind === NONE || direction === FeederDirection.NONE)
            return false
        if (phaseCurrent(nominalPhase) !== NONE && singlePhaseKind !== phaseCurrent(nominalPhase))
            throw UnsupportedOperationException("Crossing Phases.")
        if (directionCurrent(nominalPhase).has(direction))
            return false

        currentStatus = add(currentStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun removeNormal(singlePhaseKind: SinglePhaseKind, direction: FeederDirection?, nominalPhase: SinglePhaseKind): Boolean {
        validPhaseCheck(nominalPhase)

        if (phaseNormal(nominalPhase) !== singlePhaseKind || !directionNormal(nominalPhase).has(direction!!))
            return false

        normalStatus = remove(normalStatus, singlePhaseKind, direction, nominalPhase)
        return true
    }

    fun removeCurrent(singlePhaseKind: SinglePhaseKind, direction: FeederDirection?, nominalPhase: SinglePhaseKind): Boolean {
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
     * It is primarily used for data serialisation and debugging within official evolve libraries and utilities.
     *
     * Note this property should be considered evolve internal and not for general public use as the underlying
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
     * It is primarily used for data serialisation and debugging within official evolve libraries and utilities.
     *
     * Note this property should be considered evolve internal and not for general public use as the underlying
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
            A, B, C, N, X, Y, s1, s2 -> return
            NONE, INVALID -> throw IllegalArgumentException(String.format("INTERNAL ERROR: Phase {%s} is invalid.", phase))
        }
    }
}
