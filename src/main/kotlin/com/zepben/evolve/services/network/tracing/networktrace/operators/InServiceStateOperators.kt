/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment

interface InServiceStateOperators {
    fun isInService(conductingEquipment: ConductingEquipment): Boolean
    fun setInService(conductingEquipment: ConductingEquipment, inService: Boolean)

    companion object {
        val NORMAL: InServiceStateOperators = object : InServiceStateOperators {
            override fun isInService(conductingEquipment: ConductingEquipment): Boolean = conductingEquipment.normallyInService

            override fun setInService(conductingEquipment: ConductingEquipment, inService: Boolean) {
                conductingEquipment.normallyInService = inService
            }
        }

        val CURRENT: InServiceStateOperators = object : InServiceStateOperators {
            override fun isInService(conductingEquipment: ConductingEquipment): Boolean = conductingEquipment.inService

            override fun setInService(conductingEquipment: ConductingEquipment, inService: Boolean) {
                conductingEquipment.inService = inService
            }
        }
    }
}
