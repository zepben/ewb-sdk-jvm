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
                if ((equipment.isNullOrEmpty() && currentEquipment.isNullOrEmpty()) || normalHeadTerminal == null) value else throw IllegalStateException("Feeder $mRID has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
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
