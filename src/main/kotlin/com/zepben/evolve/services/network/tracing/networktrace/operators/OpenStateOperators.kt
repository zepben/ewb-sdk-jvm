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

interface OpenStateOperators {
    fun isOpen(conductingEquipment: ConductingEquipment, phase: SinglePhaseKind? = null): Boolean
    fun setOpen(conductingEquipment: ConductingEquipment, isOpen: Boolean, phase: SinglePhaseKind? = null)

    companion object {
        val NORMAL: OpenStateOperators = object : OpenStateOperators {
            override fun isOpen(conductingEquipment: ConductingEquipment, phase: SinglePhaseKind?): Boolean =
                !conductingEquipment.normallyInService || (conductingEquipment is Switch && conductingEquipment.isNormallyOpen(phase))

            override fun setOpen(conductingEquipment: ConductingEquipment, isOpen: Boolean, phase: SinglePhaseKind?) {
                when (conductingEquipment) {
                    is Switch -> conductingEquipment.setNormallyOpen(isOpen, phase)
                    // TODO [Review]: Do we want to do this?
                    else -> conductingEquipment.normallyInService = false
                }
            }
        }

        val CURRENT: OpenStateOperators = object : OpenStateOperators {
            override fun isOpen(conductingEquipment: ConductingEquipment, phase: SinglePhaseKind?): Boolean =
                !conductingEquipment.inService || (conductingEquipment is Switch && conductingEquipment.isOpen(phase))

            override fun setOpen(conductingEquipment: ConductingEquipment, isOpen: Boolean, phase: SinglePhaseKind?) {
                when (conductingEquipment) {
                    is Switch -> conductingEquipment.setOpen(isOpen, phase)
                    // TODO [Review]: Do we want to do this?
                    else -> conductingEquipment.inService = false
                }
            }
        }
    }
}
