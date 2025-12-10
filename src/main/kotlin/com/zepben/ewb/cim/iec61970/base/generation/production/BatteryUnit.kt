/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

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
    val controls: MRIDListWrapper<BatteryControl>
        get() = MRIDListWrapper(
            getter = { _batteryControls },
            setter = { _batteryControls = it })

    /**
     * Get the number of entries in the [BatteryControl] collection.
     */
    //
    // NOTE: This is called `numBatteryControls` because `numControls` is already used by `PowerSystemResource`.
    //
    fun numBatteryControls(): Int = _batteryControls?.size ?: 0

    @Deprecated("BOILERPLATE: Use batteryControls.getByMRID(mRID) instead")
    fun getControl(mRID: String): BatteryControl? = controls.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use controls.remove(control) instead")
    fun removeControl(control: BatteryControl): Boolean = controls.remove(control)

    @Deprecated("BOILERPLATE: Use controls.clear() instead")
    fun clearControls(): BatteryUnit {
        controls.clear()
        return this
    }

    private fun validateControl(control: BatteryControl): Boolean {
        return validateReference(control, ::getControl, "A BatteryControl")
    }

}
