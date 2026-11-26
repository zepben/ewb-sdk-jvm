/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

import com.zepben.ewb.boilerplate.relations.BatteryControlList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * An electrochemical energy storage device.
 *
 * @property batteryState The current state of the battery (charging, full, etc.).
 * @property ratedE Full energy storage capacity of the battery in watt-hours (Wh). The attribute shall be a positive value.
 * @property storedE Amount of energy currently stored in watt-hours (Wh). The attribute shall be a positive value or zero and lower than [BatteryUnit.ratedE].
 * @property controls [ZBEX] The collection of [BatteryControl] controlling this [BatteryUnit]. The returned collection is read only.
 */
class BatteryUnit(mRID: String) : PowerElectronicsUnit(mRID) {

    var batteryState: BatteryStateKind = BatteryStateKind.UNKNOWN
    var ratedE: Long? = null
    var storedE: Long? = null

    private var _batteryControls: MutableList<BatteryControl>? = null

    @ZBEX
    val controls: BatteryControlList
        get() = BatteryControlList(
            getter = { _batteryControls },
            setter = { _batteryControls = it },
            owner = this,
            elementDescription = "A BatteryControl"
        )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region controls boilerplate

    @Deprecated("Helper for a deprecated function")
    private fun validateControl(control: BatteryControl): Boolean {
        return validateReference(control, ::getControl, "A BatteryControl")
    }

    //
    // NOTE: This is called `numBatteryControls` because `numControls` is already used by `PowerSystemResource`.
    //
    @Deprecated(
        message = "Use controls.size instead.",
        replaceWith = ReplaceWith("controls.size")
    )
    fun numBatteryControls(): Int = _batteryControls?.size ?: 0

    /**
     * Get a [BatteryControl] of this [BatteryUnit] by its [BatteryControl.mRID]
     *
     * @param mRID the mRID of the required [BatteryControl]
     * @return The [BatteryControl] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use controls.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("controls.getByMRID(mRID)")
    )
    fun getControl(mRID: String): BatteryControl? = _batteryControls.getByMRID(mRID)

    /**
     * Get a [BatteryControl] of this [BatteryUnit] by its [BatteryControl.controlMode]
     *
     * @param controlMode the control mode of the required [BatteryControl]
     * @return The [BatteryControl] with the specified [BatteryControlMode] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use controls.getByMode(controlMode) instead.",
        replaceWith = ReplaceWith("controls.getByMode(controlMode)")
    )
    fun getControl(controlMode: BatteryControlMode): BatteryControl? = _batteryControls?.firstOrNull { it.controlMode == controlMode }


    /**
     * Add a [BatteryControl] for this [BatteryUnit]
     *
     * @throws IllegalStateException if the [BatteryControl] references another [BatteryUnit]
     * @param control the [BatteryControl] to be added to this [BatteryUnit]
     *
     * @return This [BatteryUnit] for fluent use
     */
    @Deprecated(
        message = "Use controls.add(control) instead.",
        replaceWith = ReplaceWith("also { it.controls.add(control) }")
    )
    fun addControl(control: BatteryControl): BatteryUnit {
        if (validateControl(control)) return this

        _batteryControls = _batteryControls.or(::mutableListOf) { add(control) }

        return this
    }

    /**
     * @param control the [BatteryControl] to disassociate with this battery unit.
     * @return true if the [BatteryControl] is disassociated.
     */
    @Deprecated(
        message = "Use controls.remove(control) instead.",
        replaceWith = ReplaceWith("controls.remove(control)")
    )
    fun removeControl(control: BatteryControl): Boolean {
        val ret = _batteryControls.safeRemove(control)
        if (_batteryControls.isNullOrEmpty()) _batteryControls = null
        return ret
    }

    /**
     * Clear all [BatteryControl]'s attached to this [BatteryUnit].
     * @return This [BatteryUnit] for fluent use.
     */
    @Deprecated(
        message = "Use controls.clear() instead.",
        replaceWith = ReplaceWith("controls.clear()")
    )
    fun clearControls(): BatteryUnit {
        _batteryControls = null
        return this
    }

    // endregion

    // endregion

}
