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
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment

/**
 * A generic device designed to close, or open, or both, one or more electric circuits.  All switches are two terminal devices including grounding switches.
 *
 * NOTE: The normal and currently open properties are implemented as an integer rather than a boolean to allow for the caching of
 *       measurement values if the switch is operating un-ganged. These values will cache the latest values from the measurement
 *       value for each phase of the switch.
 *
 * @property normalOpen The attribute is used in cases when no Measurement for the status value is present. If the Switch has a status measurement
 *                      the Discrete.normalValue is expected to match with the Switch.normalOpen.
 * @property open The attribute tells if the switch is considered open when used as input to topology processing.
 */
abstract class Switch(mRID: String = "") : ConductingEquipment(mRID) {

    internal var normalOpen: Int = 0
    internal var open: Int = 0

    /**
     * Helper function to check if the switch is normally open.
     * A null phase means check if any phase is normally open.
     *
     * @param phase the phase to check the normal status.
     * @return the status of the phase in its normal state.
     */
    @JvmOverloads
    fun isNormallyOpen(phase: SinglePhaseKind? = null) = checkIsOpen(normalOpen, phase)

    /**
     * Helper function to check if the switch is currently open.
     * A null phase means check if any phase is currently open.
     *
     * @param phase the phase to check the current status.
     * @return the status of the phase in its current state.
     */
    @JvmOverloads
    fun isOpen(phase: SinglePhaseKind? = null) = checkIsOpen(open, phase)

    /**
     * @param isNormallyOpen indicates if the phase(s) should be opened.
     * @param phase the phase to set the normal status. If unset/null will default to all phases.
     * @return this [Switch]
     */
    @JvmOverloads
    fun setNormallyOpen(isNormallyOpen: Boolean, phase: SinglePhaseKind? = null): Switch {
        normalOpen = calculateOpenState(normalOpen, isNormallyOpen, phase)
        return this
    }

    /**
     * @param isOpen indicates if the phase(s) should be opened.
     * @param phase the phase to set the current status. If unset/null will default to all phases.
     * @return this [Switch]
     */
    @JvmOverloads
    fun setOpen(isOpen: Boolean, phase: SinglePhaseKind? = null): Switch {
        open = calculateOpenState(open, isOpen, phase)
        return this
    }

    private fun checkIsOpen(currentState: Int, phase: SinglePhaseKind?): Boolean =
        when (phase) {
            SinglePhaseKind.NONE, SinglePhaseKind.INVALID -> throw IllegalArgumentException("Invalid phase specified.")
            null -> currentState != 0
            else -> (currentState and phase.bitMask()) != 0
        }

    private fun calculateOpenState(currentState: Int, isOpen: Boolean, phase: SinglePhaseKind?): Int =
        when (phase) {
            SinglePhaseKind.NONE, SinglePhaseKind.INVALID -> throw IllegalArgumentException("Invalid phase specified.")
            null -> if (isOpen) 0b1111 else 0
            else -> if (isOpen) currentState or phase.bitMask() else currentState and phase.bitMask().inv()
        }
}
