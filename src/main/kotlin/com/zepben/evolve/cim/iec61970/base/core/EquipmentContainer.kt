/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.validateReference

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
     * Convenience function to find all of the normal [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the normal feeders for all associated feeders
     */
    fun normalFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.normalFeeders) }
        return ret
    }

    /**
     * Convenience function to find all of the current [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the current feeders for all associated feeders
     */
    fun currentFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.currentFeeders) }
        return ret
    }

    /**
     * Convenience function to find all of the normal [LvFeeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the normal feeders for all associated feeders
     */
    fun normalLvFeeders(): Set<LvFeeder> {
        val ret = mutableSetOf<LvFeeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.normalLvFeeders) }
        return ret
    }

    /**
     * Convenience function to find all of the current [LvFeeder] of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the current feeders for all associated feeders
     */
    fun currentLvFeeders(): Set<LvFeeder> {
        val ret = mutableSetOf<LvFeeder>()
        _equipmentById?.values?.forEach { equip -> ret.addAll(equip.currentLvFeeders) }
        return ret
    }

    /**
     * Convenience function to add an equipment in the current network state. Works if this is a [Feeder] or an [LvFeeder].
     * TODO: This is kinda hack-y, and is caused by the fact that Equipment::currentContainers can contain any type of
     *       EquipmentContainer but only Feeder and LvFeeder have currentEquipment.
     */
    fun tryAddCurrentEquipment(equipment: Equipment): EquipmentContainer {
        when (this) {
            is Feeder -> addCurrentEquipment(equipment)
            is LvFeeder -> addCurrentEquipment(equipment)
        }
        return this
    }
}
