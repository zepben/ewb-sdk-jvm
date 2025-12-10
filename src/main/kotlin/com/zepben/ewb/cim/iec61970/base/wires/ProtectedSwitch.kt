/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * A ProtectedSwitch is a switching device that can be operated by [ProtectionRelayFunction]s.
 *
 * @property breakingCapacity The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
 * @property relayFunctions The [ProtectionRelayFunction]s operating this [ProtectedSwitch].
 */
abstract class ProtectedSwitch(mRID: String = "") : Switch(mRID) {

    var breakingCapacity: Int? = null
    private var _relayFunctions: MutableList<ProtectionRelayFunction>? = null

    val relayFunctions: MRIDListWrapper<ProtectionRelayFunction>
        get() = MRIDListWrapper(
            getter = { _relayFunctions },
            setter = { _relayFunctions = it })

    @Deprecated("BOILERPLATE: Use relayFunctions.size instead")
    fun numRelayFunctions(): Int = relayFunctions.size

    @Deprecated("BOILERPLATE: Use relayFunctions.getByMRID(mRID) instead")
    fun getRelayFunction(mRID: String): ProtectionRelayFunction? = relayFunctions.getByMRID(mRID)

    /**
     * Associate this [ProtectedSwitch] with a [ProtectionRelayFunction] operating it.
     *
     * @param relayFunction The [ProtectionRelayFunction] to associate with this [ProtectedSwitch].
     * @return A reference to this [ProtectedSwitch] for fluent use.
     */
    fun addRelayFunction(relayFunction: ProtectionRelayFunction): ProtectedSwitch {
        if (validateReference(relayFunction, ::getRelayFunction, "A ProtectionRelayFunction"))
            return this

        _relayFunctions = _relayFunctions ?: mutableListOf()
        _relayFunctions!!.add(relayFunction)

        return this
    }

    @Deprecated("BOILERPLATE: Use relayFunctions.remove(relayFunction) instead")
    fun removeRelayFunction(relayFunction: ProtectionRelayFunction): Boolean = relayFunctions.remove(relayFunction)

    /**
     * Disassociate all [ProtectionRelayFunction]s from this [ProtectedSwitch].
     *
     * @return A reference to this [ProtectedSwitch] for fluent use.
     */
    fun clearRelayFunctions(): ProtectedSwitch {
        _relayFunctions = null
        return this
    }

}
