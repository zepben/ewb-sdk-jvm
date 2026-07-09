/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.operations

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61970.base.core.Equipment

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
    val equipment: LazyMridList<Equipment> get() = LazyMridList(
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

    @Deprecated(
        message = "Use equipment.size instead.",
        replaceWith = ReplaceWith("equipment.size")
    )
    fun numEquipment(): Int = equipment.size

    @Deprecated(
        message = "Use equipment.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("equipment.getByMRID(mRID)")
    )
    fun getEquipment(mRID: String): Equipment? = equipment.getByMrid(mRID)

    @Deprecated(
        message = "Use this.equipment.add(equipment) instead.",
        replaceWith = ReplaceWith("also { it.equipment.add(equipment) }")
    )
    fun addEquipment(equipment: Equipment): OperationalRestriction {
        this.equipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use this.equipment.remove(equipment) instead.",
        replaceWith = ReplaceWith("this.equipment.remove(equipment)")
    )
    fun removeEquipment(equipment: Equipment): Boolean = this.equipment.remove(equipment)

    @Deprecated(
        message = "Use equipment.clear() instead.",
        replaceWith = ReplaceWith("equipment.clear()")
    )
    fun clearEquipment(): OperationalRestriction {
        equipment.clear()
        return this
    }

    // endregion

    // endregion
}
