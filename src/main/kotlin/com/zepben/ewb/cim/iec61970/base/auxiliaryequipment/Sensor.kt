/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction

/**
 * This class describes devices that transform a measured quantity into signals that can be presented at displays,
 * used in control or be recorded.
 *
 * @property relayFunctions [ZBEX] The relay functions influenced by this [Sensor].
 */
abstract class Sensor(mRID: String) : AuxiliaryEquipment(mRID) {

    private var _relayFunctions: MutableList<ProtectionRelayFunction>? = null

    @ZBEX
    val relayFunctions: LazyMridList<ProtectionRelayFunction> get() = LazyMridList(
        getter = { _relayFunctions },
        setter = { _relayFunctions = it },
        owner = { this },
        elementDescription = "A ProtectionRelayFunction"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region relayFunctions boilerplate

    @Deprecated(
        message = "Use relayFunctions.size instead.",
        replaceWith = ReplaceWith("relayFunctions.size")
    )
    fun numRelayFunctions(): Int = relayFunctions.size

    @Deprecated(
        message = "Use relayFunctions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("relayFunctions.getByMRID(mRID)")
    )
    fun getRelayFunction(mRID: String): ProtectionRelayFunction? = relayFunctions.getByMrid(mRID)

    @Deprecated(
        message = "Use relayFunctions.add(protectionRelayFunction) instead.",
        replaceWith = ReplaceWith("also { it.relayFunctions.add(protectionRelayFunction) }")
    )
    fun addRelayFunction(protectionRelayFunction: ProtectionRelayFunction): Sensor {
        relayFunctions.add(protectionRelayFunction)
        return this
    }

    @Deprecated(
        message = "Use relayFunctions.remove(relayFunction) instead.",
        replaceWith = ReplaceWith("relayFunctions.remove(relayFunction)")
    )
    fun removeRelayFunction(relayFunction: ProtectionRelayFunction): Boolean = relayFunctions.remove(relayFunction)

    @Deprecated(
        message = "Use relayFunctions.clear() instead.",
        replaceWith = ReplaceWith("relayFunctions.clear()")
    )
    fun clearRelayFunctions(): Sensor {
        relayFunctions.clear()
        return this
    }

    // endregion

    // endregion
}
