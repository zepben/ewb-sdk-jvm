/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer

interface EquipmentContainerStateOperations {
    fun getEquipment(container: EquipmentContainer): Collection<Equipment>
    fun getContainers(equipment: Equipment): Collection<EquipmentContainer>
    fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer)
    fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment)
    fun associateEquipmentAndContainer(equipment: Equipment, container: EquipmentContainer) {
        addEquipmentToContainer(equipment, container)
        addContainerToEquipment(container, equipment)
    }

    companion object {
        val NORMAL = object : EquipmentContainerStateOperations {
            override fun getEquipment(container: EquipmentContainer): Collection<Equipment> = container.equipment

            override fun getContainers(equipment: Equipment): Collection<EquipmentContainer> = equipment.containers

            override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
                equipment.addContainer(container)
            }

            override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
                container.addEquipment(equipment)
            }
        }

        val CURRENT = object : EquipmentContainerStateOperations {
            override fun getEquipment(container: EquipmentContainer): Collection<Equipment> = container.currentEquipment

            override fun getContainers(equipment: Equipment): Collection<EquipmentContainer> = equipment.currentContainers

            override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
                equipment.addCurrentContainer(container)
            }

            override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
                container.addCurrentEquipment(equipment)
            }
        }
    }
}
