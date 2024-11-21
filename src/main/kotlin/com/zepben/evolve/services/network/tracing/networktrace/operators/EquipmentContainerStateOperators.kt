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

/**
 * Defines operations for managing relationships between [Equipment] and [EquipmentContainer].
 */
interface EquipmentContainerStateOperators {

    /**
     * Get the collection of equipment associated with the given container.
     *
     * @param container The container for which to get the associated equipment.
     * @return A collection of equipment in the specified container.
     */
    fun getEquipment(container: EquipmentContainer): Collection<Equipment>

    /**
     * Retrieves a collection of containers associated with the given equipment.
     *
     * @param equipment The equipment for which to get the associated containers.
     * @return A collection of containers that contain the specified equipment.
     */
    fun getContainers(equipment: Equipment): Collection<EquipmentContainer>

    /**
     * Adds the specified equipment to the given container.
     *
     * @param equipment The equipment to add to the container.
     * @param container The container to which the equipment will be added.
     */
    fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer)

    /**
     * Adds the specified container to the given equipment.
     *
     * @param container The container to add to the equipment.
     * @param equipment The equipment to which the container will be added.
     */
    fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment)

    /**
     * Establishes a bidirectional association between the specified equipment and container.
     *
     * @param equipment The equipment to associate with the container.
     * @param container The container to associate with the equipment.
     */
    fun associateEquipmentAndContainer(equipment: Equipment, container: EquipmentContainer) {
        addEquipmentToContainer(equipment, container)
        addContainerToEquipment(container, equipment)
    }

    companion object {

        /**
         * Instance for normal network state equipment-container relationships.
         */
        @JvmStatic
        val NORMAL: EquipmentContainerStateOperators = NormalEquipmentContainerStateOperators()

        /**
         * Instance for current network state equipment-container relationships.
         */
        @JvmStatic
        val CURRENT: EquipmentContainerStateOperators = CurrentEquipmentContainerStateOperators()
    }
}


private class NormalEquipmentContainerStateOperators : EquipmentContainerStateOperators {
    override fun getEquipment(container: EquipmentContainer): Collection<Equipment> = container.equipment

    override fun getContainers(equipment: Equipment): Collection<EquipmentContainer> = equipment.containers

    override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
        container.addEquipment(equipment)
    }

    override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.addContainer(container)
    }
}

private class CurrentEquipmentContainerStateOperators : EquipmentContainerStateOperators {
    override fun getEquipment(container: EquipmentContainer): Collection<Equipment> = container.currentEquipment

    override fun getContainers(equipment: Equipment): Collection<EquipmentContainer> = equipment.currentContainers

    override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
        container.addCurrentEquipment(equipment)
    }

    override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.addCurrentContainer(container)
    }
}
