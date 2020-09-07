/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.validateReference

/**
 * A modeling construct to provide a root class for containing equipment.
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
    fun numEquipment() = _equipmentById?.size ?: 0

    /**
     * Contained equipment.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getEquipment(mRID: String) = _equipmentById?.get(mRID)

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
     * Convenience function to find all of the normal feeders of the equipment associated with this equipment container.
     *
     * @return the normal feeders for all associated feeders
     */
    fun normalFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.normalFeeders) }
        return ret
    }

    /**
     * Convenience function to find all of the current feeders of the equipment associated with this equipment container.
     *
     * @return the current feeders for all associated feeders
     */
    fun currentFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.currentFeeders) }
        return ret
    }
}
