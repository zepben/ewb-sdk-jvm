/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.infiec61970.feeder

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * A branch of LV network starting at a distribution substation and continuing until the end of the LV network.
 *
 * @property normalHeadTerminal The normal head terminal of this LV feeder, typically the LV terminal of a distribution substation.
 */
class LvFeeder @JvmOverloads constructor(mRID: String = "") : EquipmentContainer(mRID) {

    var normalHeadTerminal: Terminal? = null
        set(value) {
            field =
                if ((equipment.isEmpty() && currentEquipment.isEmpty()) || normalHeadTerminal == null) value else throw IllegalStateException("Feeder $mRID has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
        }

    private var _normalEnergizingFeedersById: MutableMap<String?, Feeder>? = null
    private var _currentEquipmentById: MutableMap<String?, Equipment>? = null

    /**
     * The HV/MV feeders that energize this LV feeder. The returned collection is read only.
     */
    val normalEnergizingFeeders: Collection<Feeder> get() = _normalEnergizingFeedersById?.values.asUnmodifiable()

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    val currentEquipment: Collection<Equipment> get() = _currentEquipmentById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the normal [Feeder] collection.
     */
    fun numNormalEnergizingFeeders() = _normalEnergizingFeedersById?.size ?: 0

    /**
     * Energizing feeder using the normal state of the network.
     *
     * @param mRID the mRID of the required normal [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getNormalEnergizingFeeder(mRID: String) = _normalEnergizingFeedersById?.get(mRID)

    /**
     * @param feeder the HV/MV feeder to associate with this LV feeder in the normal state of the network.
     */
    fun addNormalEnergizingFeeder(feeder: Feeder): LvFeeder {
        if (validateReference(feeder, ::getNormalEnergizingFeeder, "A normal energizing Feeder"))
            return this

        _normalEnergizingFeedersById = _normalEnergizingFeedersById ?: mutableMapOf()
        _normalEnergizingFeedersById!!.putIfAbsent(feeder.mRID, feeder)

        return this
    }

    /**
     * @param feeder the HV/MV feeder to disassociate from this LV feeder in the normal state of the network.
     */
    fun removeNormalEnergizingFeeder(feeder: Feeder?): Boolean {
        val ret = _normalEnergizingFeedersById?.remove(feeder?.mRID)
        if (_normalEnergizingFeedersById.isNullOrEmpty()) _normalEnergizingFeedersById = null
        return ret != null
    }

    fun clearNormalEnergizingFeeders(): LvFeeder {
        _normalEnergizingFeedersById = null
        return this
    }

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
     * @param equipment the equipment to associate with this LV feeder in the current state of the network.
     */
    fun addCurrentEquipment(equipment: Equipment): LvFeeder {
        if (validateReference(equipment, ::getNormalEnergizingFeeder, "A current Equipment"))
            return this

        _currentEquipmentById = _currentEquipmentById ?: mutableMapOf()
        _currentEquipmentById!!.putIfAbsent(equipment.mRID, equipment)

        return this
    }

    /**
     * @param equipment the equipment to disassociate with this LV feeder in the current state of the network.
     */
    fun removeCurrentEquipment(equipment: Equipment?): Boolean {
        val ret = _currentEquipmentById?.remove(equipment?.mRID)
        if (_currentEquipmentById.isNullOrEmpty()) _currentEquipmentById = null
        return ret != null
    }

    fun clearCurrentEquipment(): LvFeeder {
        _currentEquipmentById = null
        return this
    }

}
