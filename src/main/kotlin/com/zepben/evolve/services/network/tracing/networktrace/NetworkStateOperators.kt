/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection

interface NetworkStateOperators : OpenStateOperators, FeederDirectionStateOperations, EquipmentContainerStateOperations, ServiceStateOperators {
    // TODO [Review]: Should OpenTest become OpenOperators and DirectionSelector become DirectionOperators?

    companion object {
        val NORMAL: NetworkStateOperators = object : NetworkStateOperators,
            OpenStateOperators by OpenStateOperators.NORMAL,
            FeederDirectionStateOperations by FeederDirectionStateOperations.NORMAL,
            EquipmentContainerStateOperations by EquipmentContainerStateOperations.NORMAL,
            ServiceStateOperators by ServiceStateOperators.NORMAL {}

        val CURRENT: NetworkStateOperators = object : NetworkStateOperators,
            OpenStateOperators by OpenStateOperators.CURRENT,
            FeederDirectionStateOperations by FeederDirectionStateOperations.CURRENT,
            EquipmentContainerStateOperations by EquipmentContainerStateOperations.CURRENT,
            ServiceStateOperators by ServiceStateOperators.CURRENT {}
    }
}

interface ServiceStateOperators {
    fun isInService(conductingEquipment: ConductingEquipment): Boolean

    companion object {
        val NORMAL: ServiceStateOperators = object : ServiceStateOperators {
            override fun isInService(conductingEquipment: ConductingEquipment): Boolean = conductingEquipment.normallyInService
        }

        val CURRENT: ServiceStateOperators = object : ServiceStateOperators {
            override fun isInService(conductingEquipment: ConductingEquipment): Boolean = conductingEquipment.inService
        }
    }
}

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

interface FeederDirectionStateOperations {
    fun getDirection(terminal: Terminal): FeederDirection
    fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean
    fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean
    fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean

    companion object {
        val NORMAL: FeederDirectionStateOperations = object : FeederDirectionStateOperations {
            override fun getDirection(terminal: Terminal): FeederDirection = terminal.normalFeederDirection

            override fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                if (terminal.normalFeederDirection == direction)
                    return false

                terminal.normalFeederDirection = direction
                return true
            }

            override fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.normalFeederDirection
                val new = previous + direction
                if (new == previous)
                    return false

                terminal.normalFeederDirection = new
                return true
            }

            override fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.normalFeederDirection
                val new = previous - direction
                if (new == previous)
                    return false

                terminal.normalFeederDirection = new
                return true
            }
        }

        val CURRENT: FeederDirectionStateOperations = object : FeederDirectionStateOperations {
            override fun getDirection(terminal: Terminal): FeederDirection = terminal.currentFeederDirection

            override fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                if (terminal.currentFeederDirection == direction)
                    return false

                terminal.currentFeederDirection = direction
                return true
            }

            override fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.currentFeederDirection
                val new = previous + direction
                if (new == previous)
                    return false

                terminal.currentFeederDirection = new
                return true
            }

            override fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.currentFeederDirection
                val new = previous - direction
                if (new == previous)
                    return false

                terminal.currentFeederDirection = new
                return true
            }
        }
    }
}

interface EquipmentContainerStateOperations {
    fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer)
    fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment)
    fun associateEquipmentAndContainer(equipment: Equipment, container: EquipmentContainer) {
        addEquipmentToContainer(equipment, container)
        addContainerToEquipment(container, equipment)
    }

    companion object {
        val NORMAL = object : EquipmentContainerStateOperations {
            override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
                equipment.addContainer(container)
            }

            override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
                container.addEquipment(equipment)
            }
        }

        val CURRENT = object : EquipmentContainerStateOperations {
            override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
                equipment.addCurrentContainer(container)
            }

            override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
                container.addCurrentEquipment(equipment)
            }
        }
    }
}
