/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.operations

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A document that can be associated with equipment to describe any sort of restrictions compared with the
 * original manufacturer's specification or with the usual operational practice e.g.
 * temporary maximum loadings, maximum switching current, do not operate if bus couplers are open, etc.
 *
 *
 * In the UK, for example, if a breaker or switch ever mal-operates, this is reported centrally and utilities
 * use their asset systems to identify all the installed devices of the same manufacturer's type.
 * They then apply operational restrictions in the operational systems to warn operators of potential problems.
 * After appropriate inspection and maintenance, the operational restrictions may be removed.
 */
class OperationalRestriction(mRID: String) : Document(mRID) {

    private var _equipment: MutableList<Equipment>? = null

    /**
     * All equipment to which this restriction applies. The returned collection is read only.
     */
    val equipment: MridCollection<Equipment> get() = LazyMridList(
        getter = { _equipment },
        setter = { _equipment = it },
        owner = this,
        elementDescription = "An Equipment",
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region equipment boilerplate

    /**
     * Get the number of entries in the [Equipment] collection.
     */
    @Deprecated(
        message = "Use equipment.size instead.",
        replaceWith = ReplaceWith("equipment.size")
    )
    fun numEquipment(): Int = _equipment?.size ?: 0

    /**
     * All equipments to which this restriction applies.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use equipment.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("equipment.getByMRID(mRID)")
    )
    fun getEquipment(mRID: String): Equipment? = _equipment?.firstOrNull { it.mRID == mRID }

    /**
     * Add equipment to which this restriction applies.
     *
     * @param equipment the equipment to add.
     * @return A reference to this [OperationalRestriction] to allow fluent use.
     */
    @Deprecated(
        message = "Use this.equipment.add(equipment) instead.",
        replaceWith = ReplaceWith("also { it.equipment.add(equipment) }")
    )
    fun addEquipment(equipment: Equipment): OperationalRestriction {
        if (validateReference(equipment, ::getEquipment, "An Equipment"))
            return this

        _equipment = _equipment ?: mutableListOf()
        _equipment!!.add(equipment)

        return this
    }

    /**
     * Remove equipment already associated with this restriction.
     *
     * @param equipment The equipment tor remove.
     * @return true if [equipment] is removed from the collection.
     */
    @Deprecated(
        message = "Use this.equipment.remove(equipment) instead.",
        replaceWith = ReplaceWith("this.equipment.remove(equipment)")
    )
    fun removeEquipment(equipment: Equipment): Boolean {
        val ret = _equipment?.remove(equipment) == true
        if (_equipment.isNullOrEmpty()) _equipment = null
        return ret
    }

    /**
     * Clear the collection of equipment to which this restriction applies.
     *
     * @return A reference to this [OperationalRestriction] to allow fluent use.
     */
    @Deprecated(
        message = "Use equipment.clear() instead.",
        replaceWith = ReplaceWith("equipment.clear()")
    )
    fun clearEquipment(): OperationalRestriction {
        _equipment = null
        return this
    }

    // endregion

    // endregion
}
