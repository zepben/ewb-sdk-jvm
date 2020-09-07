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
 * A collection of equipment for organizational purposes, used for grouping distribution resources.
 *
 * The organization of a feeder does not necessarily reflect connectivity or current operation state.
 *
 * @property normalHeadTerminal The normal head terminal or terminals of the feeder.
 * @property normalEnergizingSubstation The substation that nominally energizes the feeder.  Also used for naming purposes.
 */
class Feeder @JvmOverloads constructor(mRID: String = "") : EquipmentContainer(mRID) {

    var normalHeadTerminal: Terminal? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("normalHeadTerminal has already been set to $field. Cannot set this field again")
        }

    var normalEnergizingSubstation: Substation? = null
    private var _currentEquipmentById: MutableMap<String?, Equipment>? = null

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    val currentEquipment: Collection<Equipment> get() = _currentEquipmentById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the current [Equipment] collection.
     */
    fun numCurrentEquipment() = _currentEquipmentById?.size ?: 0

    /**
     * Contained equipment using the current state of the network.
     *
     * @param mRID the mRID of the required current [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getCurrentEquipment(mRID: String) = _currentEquipmentById?.get(mRID)

    /**
     * @param equipment the equipment to associate with this equipment container in the current state of the network.
     */
    fun addCurrentEquipment(equipment: Equipment): Feeder {
        if (validateReference(equipment, ::getCurrentEquipment, "A current Equipment"))
            return this

        _currentEquipmentById = _currentEquipmentById ?: mutableMapOf()
        _currentEquipmentById!!.putIfAbsent(equipment.mRID, equipment)

        return this
    }

    /**
     * @param equipment the equipment to disassociate from this equipment container in the current state of the network.
     */
    fun removeCurrentEquipment(equipment: Equipment?): Boolean {
        val ret = _currentEquipmentById?.remove(equipment?.mRID)
        if (_currentEquipmentById.isNullOrEmpty()) _currentEquipmentById = null
        return ret != null
    }

    /**
     * Clear all Equipment associated with this [Feeder]
     */
    fun clearCurrentEquipment(): Feeder {
        _currentEquipmentById = null
        return this
    }
}
