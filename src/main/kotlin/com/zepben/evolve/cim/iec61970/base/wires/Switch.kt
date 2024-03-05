/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.SwitchInfo
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment

/**
 * A generic device designed to close, or open, or both, one or more electric circuits.  All switches are two terminal devices including grounding switches.
 *
 * NOTE: The normal and currently open properties are implemented as an integer rather than a boolean to allow for the caching of
 *       measurement values if the switch is operating un-ganged. These values will cache the latest values from the measurement
 *       value for each phase of the switch.
 *
 * @property ratedCurrent The maximum continuous current carrying capacity in amps governed by the device material and construction.
 *                        The attribute shall be a positive value.
 * @property normalOpen The attribute is used in cases when no Measurement for the status value is present. If the Switch has a status measurement
 *                      the Discrete.normalValue is expected to match with the Switch.normalOpen.
 * @property open The attribute tells if the switch is considered open when used as input to topology processing.
 * @property assetInfo Datasheet information for this Switch.
 */
abstract class Switch(mRID: String = "") : ConductingEquipment(mRID) {

    override var assetInfo: SwitchInfo? = null

    var ratedCurrent: Int? = null
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
    fun isNormallyOpen(phase: SinglePhaseKind? = null): Boolean = checkIsOpen(normalOpen, phase)

    /**
     * Helper function to check if the switch is currently open.
     * A null phase means check if any phase is currently open.
     *
     * @param phase the phase to check the current status.
     * @return the status of the phase in its current state.
     */
    @JvmOverloads
    fun isOpen(phase: SinglePhaseKind? = null): Boolean = checkIsOpen(open, phase)

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
            else -> (currentState and phase.bitMask) != 0
        }

    private fun calculateOpenState(currentState: Int, isOpen: Boolean, phase: SinglePhaseKind?): Int =
        when (phase) {
            SinglePhaseKind.NONE, SinglePhaseKind.INVALID -> throw IllegalArgumentException("Invalid phase specified.")
            null -> if (isOpen) 0b1111 else 0
            else -> if (isOpen) currentState or phase.bitMask else currentState and phase.bitMask.inv()
        }
}
