/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

import com.zepben.evolve.cim.extensions.ZBEX
import com.zepben.evolve.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.evolve.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * An electrochemical energy storage device.
 *
 * @property batteryState The current state of the battery (charging, full, etc.).
 * @property ratedE Full energy storage capacity of the battery in watt-hours (Wh). The attribute shall be a positive value.
 * @property storedE Amount of energy currently stored in watt-hours (Wh). The attribute shall be a positive value or zero and lower than [BatteryUnit.ratedE].
 * @property controls [ZBEX] The collection of [BatteryControl] controlling this [BatteryUnit]. The returned collection is read only.
 */
class BatteryUnit @JvmOverloads constructor(mRID: String = "") : PowerElectronicsUnit(mRID) {

    var batteryState: BatteryStateKind = BatteryStateKind.UNKNOWN
    var ratedE: Long? = null
    var storedE: Long? = null

    private var _batteryControls: MutableList<BatteryControl>? = null

    @ZBEX
    val controls: List<BatteryControl> get() = _batteryControls.asUnmodifiable()

    /**
     * Get the number of entries in the [BatteryControl] collection.
     */
    //
    // NOTE: This is called `numBatteryControls` because `numControls` is already used by `PowerSystemResource`.
    //
    fun numBatteryControls(): Int = _batteryControls?.size ?: 0

    /**
     * Get a [BatteryControl] of this [BatteryUnit] by its [BatteryControl.mRID]
     *
     * @param mRID the mRID of the required [BatteryControl]
     * @return The [BatteryControl] with the specified [mRID] if it exists, otherwise null
     */
    fun getControl(mRID: String): BatteryControl? = _batteryControls.getByMRID(mRID)

    /**
     * Get a [BatteryControl] of this [BatteryUnit] by its [BatteryControl.controlMode]
     *
     * @param controlMode the control mode of the required [BatteryControl]
     * @return The [BatteryControl] with the specified [BatteryControlMode] if it exists, otherwise null
     */
    fun getControl(controlMode: BatteryControlMode): BatteryControl? = _batteryControls?.firstOrNull { it.controlMode == controlMode }

    /**
     * Add a [BatteryControl] for this [BatteryUnit]
     *
     * @throws IllegalStateException if the [BatteryControl] references another [BatteryUnit]
     * @param control the [BatteryControl] to be added to this [BatteryUnit]
     *
     * @return This [BatteryUnit] for fluent use
     */
    fun addControl(control: BatteryControl): BatteryUnit {
        if (validateControl(control)) return this

        _batteryControls = _batteryControls.or(::mutableListOf) { add(control) }

        return this
    }

    /**
     * @param control the [BatteryControl] to disassociate with this battery unit.
     * @return true if the [BatteryControl] is disassociated.
     */
    fun removeControl(control: BatteryControl): Boolean {
        val ret = _batteryControls.safeRemove(control)
        if (_batteryControls.isNullOrEmpty()) _batteryControls = null
        return ret
    }

    /**
     * Clear all [BatteryControl]'s attached to this [BatteryUnit].
     * @return This [BatteryUnit] for fluent use.
     */
    fun clearControls(): BatteryUnit {
        _batteryControls = null
        return this
    }

    private fun validateControl(control: BatteryControl): Boolean {
        return validateReference(control, ::getControl, "A BatteryControl")
    }

}
