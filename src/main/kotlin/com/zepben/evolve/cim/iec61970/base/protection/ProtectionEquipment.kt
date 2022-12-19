/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * An electrical device designed to respond to input conditions in a prescribed manner and after specified conditions are met to cause contact operation or
 * similar abrupt change in associated electric control circuits, or simply to display the detected condition. Protection equipment is associated with
 * conducting equipment and usually operate circuit breakers.
 *
 * @property relayDelayTime The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind The kind of protection being provided by this protection equipment.
 */
abstract class ProtectionEquipment(mRID: String = "") : Equipment(mRID) {

    var relayDelayTime: Double? = null
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN
    private var _protectedSwitches: MutableList<ProtectedSwitch>? = null

    /**
     * All [ProtectedSwitch]es operated by this [ProtectionEquipment]. Collection is read-only.
     *
     * @return A read-only [Collection] of [ProtectedSwitch]es operated by this [ProtectionEquipment].
     */
    val protectedSwitches: Collection<ProtectedSwitch> get() = _protectedSwitches.asUnmodifiable()

    /**
     * Get the number of [ProtectedSwitch]es operated by this [ProtectionEquipment].
     *
     * @return The number of [ProtectedSwitch]es operated by this [ProtectionEquipment].
     */
    fun numProtectedSwitches(): Int = _protectedSwitches?.size ?: 0

    /**
     * Get a [ProtectedSwitch] operated by this [ProtectionEquipment] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectedSwitch]
     * @return The [ProtectedSwitch] with the specified [mRID] if it exists, otherwise null
     */
    fun getProtectedSwitch(mRID: String): ProtectedSwitch? = _protectedSwitches?.getByMRID(mRID)

    /**
     * Associate this [ProtectionEquipment] with a [ProtectedSwitch] that it operates.
     *
     * @param protectedSwitch The [ProtectedSwitch] to associate with this [ProtectionEquipment].
     * @return A reference to this [ProtectionEquipment] for fluent use.
     */
    fun addProtectedSwitch(protectedSwitch: ProtectedSwitch): ProtectionEquipment {
        if (validateReference(protectedSwitch, ::getProtectedSwitch, "A ProtectedSwitch"))
            return this

        _protectedSwitches = _protectedSwitches ?: mutableListOf()
        _protectedSwitches!!.add(protectedSwitch)

        return this
    }

    /**
     * Disassociate this [ProtectionEquipment] from a [ProtectedSwitch].
     *
     * @param protectedSwitch The [ProtectedSwitch] to disassociate from this [ProtectionEquipment].
     * @return true if the [ProtectedSwitch] was disassociated.
     */
    fun removeProtectedSwitch(protectedSwitch: ProtectedSwitch?): Boolean {
        val ret = _protectedSwitches.safeRemove(protectedSwitch)
        if (_protectedSwitches.isNullOrEmpty()) _protectedSwitches = null
        return ret
    }

    /**
     * Disassociate all [ProtectedSwitch]es from this [ProtectionEquipment].
     *
     * @return A reference to this [ProtectionEquipment] for fluent use.
     */
    fun clearProtectedSwitches(): ProtectionEquipment {
        _protectedSwitches = null
        return this
    }

}
