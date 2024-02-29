/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * A modeling construct to provide a root class for containing equipment.
 * Unless overridden, all functions operating on currentEquipment simply operate on the equipment collection. i.e. currentEquipment = equipment
 */
abstract class EquipmentContainer(mRID: String = "") : ConnectivityNodeContainer(mRID) {

    private var _equipmentById: MutableMap<String?, Equipment>? = null

    /**
     * Contained equipment. The returned collection is read only.
     */
    val equipment: Collection<Equipment> get() = _equipmentById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the [Equipment] collection.
     */
    fun numEquipment(): Int = _equipmentById?.size ?: 0

    /**
     * Contained equipment.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getEquipment(mRID: String): Equipment? = _equipmentById?.get(mRID)

    /**
     * @param equipment the equipment to associate with this equipment container.
     */
    fun addEquipment(equipment: Equipment): EquipmentContainer {
        if (validateReference(equipment, ::getEquipment, "An Equipment"))
            return this

        _equipmentById = _equipmentById ?: mutableMapOf()
        _equipmentById!![equipment.mRID] = equipment

        return this
    }

    /**
     * @param equipment the equipment to disassociate from this equipment container.
     */
    fun removeEquipment(equipment: Equipment?): Boolean {
        val ret = _equipmentById?.remove(equipment?.mRID) != null
        if (_equipmentById.isNullOrEmpty()) _equipmentById = null
        return ret
    }

    /**
     * Clear all Equipment associated with this [EquipmentContainer]
     */
    fun clearEquipment(): EquipmentContainer {
        _equipmentById = null
        return this
    }

    /**
     * Convenience function to find all the normal [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the normal feeders for all associated feeders
     */
    fun normalFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.normalFeeders) }
        return ret
    }

    /**
     * Convenience function to find all the current [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the current feeders for all associated feeders
     */
    fun currentFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.currentFeeders) }
        return ret
    }

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    open val currentEquipment: Collection<Equipment> get() = equipment

    /**
     * Get the number of entries in the current [Equipment] collection.
     */
    open fun numCurrentEquipment(): Int = numEquipment()

    /**
     * Contained equipment using the current state of the network.
     *
     * @param mRID the mRID of the required current [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    open fun getCurrentEquipment(mRID: String): Equipment? = getEquipment(mRID)

    /**
     * @param equipment the equipment to associate with this equipment container in the current state of the network.
     */
    open fun addCurrentEquipment(equipment: Equipment): EquipmentContainer = addEquipment(equipment)

    /**
     * @param equipment the equipment to disassociate from this equipment container in the current state of the network.
     */
    open fun removeCurrentEquipment(equipment: Equipment?): Boolean = removeEquipment(equipment)

    /**
     * Clear all Equipment associated with this [Feeder]
     */
    open fun clearCurrentEquipment(): EquipmentContainer = clearEquipment()
}
