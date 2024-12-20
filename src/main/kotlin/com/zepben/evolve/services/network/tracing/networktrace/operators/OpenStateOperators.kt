/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch

/**
 * Interface for managing the open state of conducting equipment, typically switches.
 */
interface OpenStateOperators {
    /**
     * Checks if the specified switch is open. Optionally checking the state of a specific phase.
     *
     * @param switch The switch to check open state.
     * @param phase The specific phase to check, or `null` to check if any phase is open.
     * @return `true` if open; `false` otherwise.
     */
    fun isOpen(switch: Switch, phase: SinglePhaseKind? = null): Boolean

    /**
     * Convince overload that checks if the [conductingEquipment] is a [Switch] before checking if it is open
     *
     * @param conductingEquipment The conducting equipment to check open state.
     * @param phase The specific phase to check, or `null` to check if any phase is open.
     * @return `true` if the conducting equipment is a [Switch] and it is open; `false` otherwise.
     */
    fun isOpen(conductingEquipment: ConductingEquipment, phase: SinglePhaseKind? = null): Boolean =
        conductingEquipment is Switch && isOpen(conductingEquipment, phase)

    /**
     * Sets the open state of the specified switch. Optionally applies the state to a specific phase.
     *
     * @param switch The switch for which to set the open state.
     * @param isOpen The desired open state (`true` for open, `false` for closed).
     * @param phase The specific phase to set, or `null` to apply to all phases.
     */
    fun setOpen(switch: Switch, isOpen: Boolean, phase: SinglePhaseKind? = null)

    companion object {
        /**
         * Instance for managing the normal open state of conducting equipment.
         */
        val NORMAL: OpenStateOperators = NormalOpenStateOperators()

        /**
         * Instance for managing the current open state of conducting equipment.
         */
        val CURRENT: OpenStateOperators = CurrentOpenStateOperators()
    }
}

private class NormalOpenStateOperators : OpenStateOperators {
    override fun isOpen(switch: Switch, phase: SinglePhaseKind?): Boolean = switch.isNormallyOpen(phase)

    override fun setOpen(switch: Switch, isOpen: Boolean, phase: SinglePhaseKind?) {
        switch.setNormallyOpen(isOpen, phase)
    }
}

private class CurrentOpenStateOperators : OpenStateOperators {
    override fun isOpen(switch: Switch, phase: SinglePhaseKind?): Boolean = switch.isOpen(phase)

    override fun setOpen(switch: Switch, isOpen: Boolean, phase: SinglePhaseKind?) {
        switch.setOpen(isOpen, phase)
    }
}
