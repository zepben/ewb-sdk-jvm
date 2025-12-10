/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * This class describes devices that transform a measured quantity into signals that can be presented at displays,
 * used in control or be recorded.
 *
 * @property relayFunctions [ZBEX] ]The relay functions influenced by this [Sensor].
 */
abstract class Sensor(mRID: String = "") : AuxiliaryEquipment(mRID) {

    private var _relayFunctions: MutableList<ProtectionRelayFunction>? = null

    @ZBEX
    val relayFunctions: MRIDListWrapper<ProtectionRelayFunction>
        get() = MRIDListWrapper(
            getter = { _relayFunctions },
            setter = { _relayFunctions = it })

    @Deprecated("BOILERPLATE: Use relayFunctions.size instead")
    fun numRelayFunctions(): Int = relayFunctions.size

    @Deprecated("BOILERPLATE: Use relayFunctions.getByMRID(mRID) instead")
    fun getRelayFunction(mRID: String): ProtectionRelayFunction? = relayFunctions.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use relayFunctions.remove(relayFunction) instead")
    fun removeRelayFunction(relayFunction: ProtectionRelayFunction): Boolean = relayFunctions.remove(relayFunction)

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
