/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

import com.zepben.evolve.cim.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * This class describes devices that transform a measured quantity into signals that can be presented at displays,
 * used in control or be recorded.
 *
 * @property relayFunctions The relay functions influenced by this [Sensor].
 */
abstract class Sensor(mRID: String = "") : AuxiliaryEquipment(mRID) {

    private var _relayFunctions: MutableList<ProtectionRelayFunction>? = null

    val relayFunctions: Collection<ProtectionRelayFunction> get() = _relayFunctions.asUnmodifiable()

    /**
     * Get the number of [ProtectionRelayFunction]s influenced by this [Sensor].
     *
     * @return The number of [ProtectionRelayFunction]s influenced by this [Sensor].
     */
    fun numRelayFunctions(): Int = _relayFunctions?.size ?: 0

    /**
     * Get a [ProtectionRelayFunction] influenced by this [Sensor] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionRelayFunction]
     * @return The [ProtectionRelayFunction] with the specified [mRID] if it exists, otherwise null
     */
    fun getRelayFunction(mRID: String): ProtectionRelayFunction? = _relayFunctions?.getByMRID(mRID)

    /**
     * Associate this [Sensor] with a [ProtectionRelayFunction] it influences.
     *
     * @param protectionRelayFunction The [ProtectionRelayFunction] to associate with this [Sensor].
     * @return A reference to this [Sensor] for fluent use.
     */
    fun addRelayFunction(protectionRelayFunction: ProtectionRelayFunction): Sensor {
        if (validateReference(protectionRelayFunction, ::getRelayFunction, "A ProtectionRelayFunction"))
            return this

        _relayFunctions = _relayFunctions ?: mutableListOf()
        _relayFunctions!!.add(protectionRelayFunction)

        return this
    }

    /**
     * Disassociate this [Sensor] from a [ProtectionRelayFunction].
     *
     * @param relayFunction The [ProtectionRelayFunction] to disassociate from this [Sensor].
     * @return true if the [ProtectionRelayFunction] was disassociated.
     */
    fun removeRelayFunction(relayFunction: ProtectionRelayFunction): Boolean {
        val ret = _relayFunctions.safeRemove(relayFunction)
        if (_relayFunctions.isNullOrEmpty()) _relayFunctions = null
        return ret
    }

    /**
     * Disassociate all [ProtectionRelayFunction]s from this [Sensor].
     *
     * @return A reference to this [Sensor] for fluent use.
     */
    fun clearRelayFunctions(): Sensor {
        _relayFunctions = null
        return this
    }

}
