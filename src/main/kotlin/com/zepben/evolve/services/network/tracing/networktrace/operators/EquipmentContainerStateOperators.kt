/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder

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
     * Retrieves a collection of feeders that energize the given LV feeder.
     *
     * @param lvFeeder The LV feeder for which to get the energizing feeders.
     * @return A collection of feeders that energize the given LV feeder.
     */
    fun getEnergizingFeeders(lvFeeder: LvFeeder): Collection<Feeder>

    /**
     * Retrieves a collection of LV feeders energized by the given feeder.
     *
     * @param feeder The feeder for which to get the energized LV feeders.
     * @return A collection of LV feeders energized by the given feeder.
     */
    fun getEnergizedLvFeeders(feeder: Feeder): Collection<LvFeeder>

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

    /**
     * Removes the specified equipment from the given container.
     *
     * @param equipment The equipment to remove from the container.
     * @param container The container from which the equipment will be removed.
     */
    fun removeEquipmentFromContainer(equipment: Equipment, container: EquipmentContainer)

    /**
     * Removes the specified container from the given equipment.
     *
     * @param container The container to remove from the equipment.
     * @param equipment The equipment from which the container will be removed.
     */
    fun removeContainerFromEquipment(container: EquipmentContainer, equipment: Equipment)

    /**
     * Remove a bidirectional association between the specified equipment and container.
     *
     * @param equipment The equipment to disassociate with the container.
     * @param container The container to disassociate with the equipment.
     */
    fun disassociateEquipmentAndContainer(equipment: Equipment, container: EquipmentContainer) {
        removeEquipmentFromContainer(equipment, container)
        removeContainerFromEquipment(container, equipment)
    }

    /**
     * Adds the specified energizing feeder to the given lvFeeder.
     *
     * @param feeder The energizing feeder to add to the lvFeeder.
     * @param lvFeeder The lvFeeder to which the feeder will be added.
     */
    fun addEnergizingFeederToLvFeeder(feeder: Feeder, lvFeeder: LvFeeder)

    /**
     * Adds the specified energized lvFeeder to the given feeder.
     *
     * @param lvFeeder The energized lvFeeder to add to the feeder.
     * @param feeder The feeder to which the lvFeeder will be added.
     */
    fun addEnergizedLvFeederToFeeder(lvFeeder: LvFeeder, feeder: Feeder)

    /**
     * Establishes a bidirectional association between the specified feeder and LV feeder.
     *
     * @param feeder The feeder energizing the lv feeder.
     * @param lvFeeder The lv feeder energized by the feeder.
     */
    fun associateEnergizingFeeder(feeder: Feeder, lvFeeder: LvFeeder) {
        addEnergizingFeederToLvFeeder(feeder, lvFeeder)
        addEnergizedLvFeederToFeeder(lvFeeder, feeder)
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

    override fun getEnergizingFeeders(lvFeeder: LvFeeder): Collection<Feeder> = lvFeeder.normalEnergizingFeeders

    override fun getEnergizedLvFeeders(feeder: Feeder): Collection<LvFeeder> = feeder.normalEnergizedLvFeeders

    override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
        container.addEquipment(equipment)
    }

    override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.addContainer(container)
    }

    override fun removeEquipmentFromContainer(equipment: Equipment, container: EquipmentContainer) {
        container.removeEquipment(equipment)
    }

    override fun removeContainerFromEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.removeContainer(container)
    }

    override fun addEnergizingFeederToLvFeeder(feeder: Feeder, lvFeeder: LvFeeder) {
        lvFeeder.addNormalEnergizingFeeder(feeder)
    }

    override fun addEnergizedLvFeederToFeeder(lvFeeder: LvFeeder, feeder: Feeder) {
        feeder.addNormalEnergizedLvFeeder(lvFeeder)
    }
}

private class CurrentEquipmentContainerStateOperators : EquipmentContainerStateOperators {
    override fun getEquipment(container: EquipmentContainer): Collection<Equipment> = container.currentEquipment

    override fun getContainers(equipment: Equipment): Collection<EquipmentContainer> = equipment.currentContainers

    override fun getEnergizingFeeders(lvFeeder: LvFeeder): Collection<Feeder> = lvFeeder.currentEnergizingFeeders

    override fun getEnergizedLvFeeders(feeder: Feeder): Collection<LvFeeder> = feeder.currentEnergizedLvFeeders

    override fun addEquipmentToContainer(equipment: Equipment, container: EquipmentContainer) {
        container.addCurrentEquipment(equipment)
    }

    override fun addContainerToEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.addCurrentContainer(container)
    }

    override fun removeEquipmentFromContainer(equipment: Equipment, container: EquipmentContainer) {
        container.removeCurrentEquipment(equipment)
    }

    override fun removeContainerFromEquipment(container: EquipmentContainer, equipment: Equipment) {
        equipment.removeCurrentContainer(container)
    }

    override fun addEnergizingFeederToLvFeeder(feeder: Feeder, lvFeeder: LvFeeder) {
        lvFeeder.addCurrentEnergizingFeeder(feeder)
    }

    override fun addEnergizedLvFeederToFeeder(lvFeeder: LvFeeder, feeder: Feeder) {
        feeder.addCurrentEnergizedLvFeeder(lvFeeder)
    }
}
