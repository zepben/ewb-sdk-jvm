/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.protection.ProtectionEquipment
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * A ProtectedSwitch is a switching device that can be operated by ProtectionEquipment.
 *
 * @property breakingCapacity The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
 */
abstract class ProtectedSwitch(mRID: String = "") : Switch(mRID) {

    var breakingCapacity: Int? = null
    private var _operatedByProtectionEquipment: MutableList<ProtectionEquipment>? = null

    /**
     * All [ProtectionEquipment]s operating this [ProtectedSwitch]. Collection is read-only.
     *
     * @return A read-only [Collection] of [ProtectionEquipment]s operating this [ProtectedSwitch].
     */
    val operatedByProtectionEquipment: Collection<ProtectionEquipment> get() = _operatedByProtectionEquipment.asUnmodifiable()

    /**
     * Get the number of [ProtectionEquipment]s operating this [ProtectedSwitch].
     *
     * @return The number of [ProtectionEquipment]s operating this [ProtectedSwitch].
     */
    fun numOperatedByProtectionEquipment(): Int = _operatedByProtectionEquipment?.size ?: 0

    /**
     * Get a [ProtectionEquipment] operating this [ProtectedSwitch] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionEquipment]
     * @return The [ProtectionEquipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getOperatedByProtectionEquipment(mRID: String): ProtectionEquipment? = _operatedByProtectionEquipment?.getByMRID(mRID)

    /**
     * Associate this [ProtectedSwitch] with a [ProtectionEquipment] operating it.
     *
     * @param protectionEquipment The [ProtectionEquipment] to associate with this [ProtectedSwitch].
     * @return A reference to this [ProtectedSwitch] for fluent use.
     */
    fun addOperatedByProtectionEquipment(protectionEquipment: ProtectionEquipment): ProtectedSwitch {
        if (validateReference(protectionEquipment, ::getOperatedByProtectionEquipment, "A ProtectionEquipment"))
            return this

        _operatedByProtectionEquipment = _operatedByProtectionEquipment ?: mutableListOf()
        _operatedByProtectionEquipment!!.add(protectionEquipment)

        return this
    }

    /**
     * Disassociate this [ProtectedSwitch] from a [ProtectionEquipment].
     *
     * @param protectionEquipment The [ProtectionEquipment] to disassociate from this [ProtectedSwitch].
     * @return true if the [ProtectionEquipment] was disassociated.
     */
    fun removeOperatedByProtectionEquipment(protectionEquipment: ProtectionEquipment?): Boolean {
        val ret = _operatedByProtectionEquipment.safeRemove(protectionEquipment)
        if (_operatedByProtectionEquipment.isNullOrEmpty()) _operatedByProtectionEquipment = null
        return ret
    }

    /**
     * Disassociate all [ProtectionEquipment]s from this [ProtectedSwitch].
     *
     * @return A reference to this [ProtectedSwitch] for fluent use.
     */
    fun clearOperatedByProtectionEquipment(): ProtectedSwitch {
        _operatedByProtectionEquipment = null
        return this
    }

}
