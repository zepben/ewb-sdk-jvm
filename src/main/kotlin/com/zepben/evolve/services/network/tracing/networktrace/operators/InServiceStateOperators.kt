/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Equipment

/**
 * Interface for managing the in-service status of equipment.
 */
interface InServiceStateOperators {
    /**
     * Checks if the specified equipment is in service.
     *
     * @param equipment The equipment to check.
     * @return `true` if the equipment is in service; `false` otherwise.
     */
    fun isInService(equipment: Equipment): Boolean

    /**
     * Sets the in-service status of the specified equipment.
     *
     * @param equipment The equipment for which to set the in-service status.
     * @param inService The desired in-service status (`true` for in service, `false` for out of service).
     */
    fun setInService(equipment: Equipment, inService: Boolean)

    companion object {
        /**
         * Instance for managing the normal in-service state of equipment.
         */
        val NORMAL: InServiceStateOperators = NormalInServiceStateOperators()

        /**
         * Instance for managing the current in-service state of equipment.
         */
        val CURRENT: InServiceStateOperators = CurrentInServiceStateOperators()
    }
}

private class NormalInServiceStateOperators : InServiceStateOperators {
    override fun isInService(equipment: Equipment): Boolean = equipment.normallyInService

    override fun setInService(equipment: Equipment, inService: Boolean) {
        equipment.normallyInService = inService
    }
}

private class CurrentInServiceStateOperators : InServiceStateOperators {
    override fun isInService(equipment: Equipment): Boolean = equipment.inService

    override fun setInService(equipment: Equipment, inService: Boolean) {
        equipment.inService = inService
    }
}
