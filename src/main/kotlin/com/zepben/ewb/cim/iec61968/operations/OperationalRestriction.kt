/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.operations

import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

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
class OperationalRestriction @JvmOverloads constructor(mRID: String = "") : Document(mRID) {

    private var _equipment: MutableList<Equipment>? = null

    /**
     * All equipment to which this restriction applies. The returned collection is read only.
     */
    val equipment: MRIDListWrapper<Equipment>
        get() = MRIDListWrapper(
            getter = { _equipment },
            setter = { _equipment = it })

    @Deprecated("BOILERPLATE: Use equipment.size instead")
    fun numEquipment(): Int = equipment.size

    /**
     * All equipments to which this restriction applies.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getEquipment(mRID: String): Equipment? = _equipment?.firstOrNull { it.mRID == mRID }

    /**
     * Add equipment to which this restriction applies.
     *
     * @param equipment the equipment to add.
     * @return A reference to this [OperationalRestriction] to allow fluent use.
     */
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
     */
    fun removeEquipment(equipment: Equipment): Boolean {
        val ret = _equipment?.remove(equipment) == true
        if (_equipment.isNullOrEmpty()) _equipment = null
        return ret
    }

    fun clearEquipment(): OperationalRestriction {
        _equipment = null
        return this
    }
}
