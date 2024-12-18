/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.extensions.iec61968.metering

import com.zepben.evolve.cim.extensions.ZBEX
import com.zepben.evolve.cim.iec61968.metering.ControlledAppliance
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunction
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunctionKind

/**
 * [ZBEX]
 * PAN function that an end device supports, distinguished by 'kind'.
 *
 * @property kind [ZBEX] Kind of this function.
 * @property appliance [ZBEX] The appliances being controlled.
 */
@ZBEX
class PanDemandResponseFunction @JvmOverloads constructor(mRID: String = "") : EndDeviceFunction(mRID) {

    @ZBEX
    var kind: EndDeviceFunctionKind = EndDeviceFunctionKind.UNKNOWN

    @ZBEX
    var appliance: ControlledAppliance?
        get() = applianceBitmask?.let { ControlledAppliance(it) }
        set(ca) {
            applianceBitmask = ca?.bitmask
        }

    /**
     * The bitmask representation of the appliances being controlled.
     */
    internal var applianceBitmask: Int? = null

    /**
     * Add an appliance to the appliances being controlled.
     *
     * @param appliance The appliance to add.
     * @return True if the controlled appliances were updated.
     */
    fun addAppliance(appliance: ControlledAppliance.Appliance): Boolean {
        val previous = applianceBitmask

        applianceBitmask = (applianceBitmask ?: 0) or appliance.bitmask

        return applianceBitmask != previous
    }

    /**
     * Add appliances to the appliances being controlled.
     *
     * @param appliances The appliances to add.
     * @return True if the controlled appliances were updated.
     */
    fun addAppliances(vararg appliances: ControlledAppliance.Appliance): Boolean {
        require(appliances.isNotEmpty()) { "You must provide at least one appliance to add" }
        val previous = applianceBitmask

        applianceBitmask = appliances.fold(applianceBitmask ?: 0) { bitmask, next -> bitmask or next.bitmask }

        return applianceBitmask != previous
    }

    /**
     * Remove an appliance from the appliances being controlled.
     *
     * @param appliance The appliance to remove.
     * @return True if the controlled appliances were updated.
     */
    fun removeAppliance(appliance: ControlledAppliance.Appliance): Boolean {
        val previous = applianceBitmask

        applianceBitmask = (applianceBitmask ?: 0) and appliance.bitmask.inv()

        return applianceBitmask != previous
    }

    /**
     * Remove appliances from the appliances being controlled.
     *
     * @param appliances Additional appliances to remove.
     * @return True if the controlled appliances were updated.
     */
    fun removeAppliances(vararg appliances: ControlledAppliance.Appliance): Boolean {
        require(appliances.isNotEmpty()) { "You must provide at least one appliance to remove" }
        val previous = applianceBitmask

        applianceBitmask = (applianceBitmask ?: 0) and appliances.fold(0) { bitmask, next -> bitmask or next.bitmask }.inv()

        return applianceBitmask != previous
    }

}
