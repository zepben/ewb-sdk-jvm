/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.LazyMridMap
import com.zepben.ewb.boilerplate.MridCollection
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators

/**
 * A modeling construct to provide a root class for containing equipment.
 * Unless overridden, all functions operating on currentEquipment simply operate on the equipment collection. i.e. currentEquipment = equipment
 */
abstract class EquipmentContainer(mRID: String) : ConnectivityNodeContainer(mRID) {

    private var _equipmentById: MutableMap<String, Equipment>? = null

    /**
     * Contained equipment. The returned collection is read only.
     */
    val equipment: MridCollection<Equipment> get() = LazyMridMap(
        getter = { _equipmentById },
        setter = { _equipmentById = it },
        owner = { this },
        elementDescription = "An Equipment"
    )

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    open val currentEquipment: MridCollection<Equipment> get() = equipment

    /**
     * Convenience function to find all the normal [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the normal feeders for all associated feeders
     */
    fun normalFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        equipment.forEach { equip -> ret.addAll(equip.normalFeeders) }
        return ret
    }

    /**
     * Convenience function to find all the current [Feeder]'s of the [Equipment] associated with this [EquipmentContainer].
     *
     * @return the current feeders for all associated feeders
     */
    fun currentFeeders(): Set<Feeder> {
        val ret = mutableSetOf<Feeder>()
        equipment.forEach { equip -> ret.addAll(equip.currentFeeders) }
        return ret
    }

    /**
     * Retrieve all terminals that are located on the edge of this EquipmentContainer. This is determined by any terminal that connects to another terminal on a
     * ConductingEquipment that is not a member of this EquipmentContainer. This will explicitly exclude equipment with only one terminal that do not
     * provide connectivity to the rest of the network.
     *
     * @param stateOperator The network state to operate on.
     */
    fun edgeTerminals(stateOperator: NetworkStateOperators = NetworkStateOperators.NORMAL): List<Terminal> =
        stateOperator.getEquipment(this)
            .asSequence()
            .filterIsInstance<ConductingEquipment>()
            .flatMap { it.terminals }
            .flatMap { NetworkService.connectedTerminals(it) }
            .filter { it.to?.getContainer(this.mRID) == null }
            .map { it.fromTerminal }
            .distinct()
            .toList()

    // region deprecated dict boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    @Deprecated(
        message = "Use equipment.size instead.",
        replaceWith = ReplaceWith("equipment.size")
    )
    fun numEquipment(): Int = equipment.size

    @Deprecated(
        message = "Use equipment.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("equipment.getByMrid(mRID)")
    )
    fun getEquipment(mRID: String): Equipment? = equipment.getByMrid(mRID)

    @Deprecated(
        message = "Use equipment.add(equipment) instead.",
        replaceWith = ReplaceWith("also { it.equipment.add(equipment) }")
    )
    fun addEquipment(equipment: Equipment): EquipmentContainer {
        this.equipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use equipment.remove(equipment) instead.",
        replaceWith = ReplaceWith("equipment.remove(equipment)")
    )
    fun removeEquipment(equipment: Equipment): Boolean =
        this.equipment.remove(equipment)

    @Deprecated(
        message = "Use equipment.clear() instead.",
        replaceWith = ReplaceWith("also { it.equipment.clear() }")
    )
    fun clearEquipment(): EquipmentContainer {
        equipment.clear()
        return this
    }


    @Deprecated(
        message = "Use currentEquipment.size instead.",
        replaceWith = ReplaceWith("currentEquipment.size")
    )
    open fun numCurrentEquipment(): Int = currentEquipment.size

    @Deprecated(
        message = "Use currentEquipment.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEquipment.getByMrid(mRID)")
    )
    open fun getCurrentEquipment(mRID: String): Equipment? = currentEquipment.getByMrid(mRID)

    @Deprecated(
        message = "Use currentEquipment.add(currentEquipment) instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.add(currentEquipment) }")
    )
    open fun addCurrentEquipment(equipment: Equipment): EquipmentContainer {
        currentEquipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use currentEquipment.remove(currentEquipment) instead.",
        replaceWith = ReplaceWith("currentEquipment.remove(currentEquipment)")
    )
    open fun removeCurrentEquipment(equipment: Equipment): Boolean =
        currentEquipment.remove(equipment)

    @Deprecated(
        message = "Use currentEquipment.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.clear() }")
    )
    open fun clearCurrentEquipment(): EquipmentContainer {
        currentEquipment.clear()
        return this
    }

    // endregion

}
