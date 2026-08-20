/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * This class describes devices that transform a measured quantity into signals that can be presented at displays,
 * used in control or be recorded.
 *
 * @property relayFunctions [ZBEX] The relay functions influenced by this [Sensor].
 */
abstract class Sensor(mRID: String) : AuxiliaryEquipment(mRID) {

    private var _relayFunctions: MutableList<ProtectionRelayFunction>? = null

    @ZBEX
    val relayFunctions: MridCollection<ProtectionRelayFunction> get() = LazyMridList(
        getter = { _relayFunctions },
        setter = { _relayFunctions = it },
        owner = this,
        elementDescription = "A ProtectionRelayFunction"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region relayFunctions boilerplate

    /**
     * Get the number of [ProtectionRelayFunction]s influenced by this [Sensor].
     *
     * @return The number of [ProtectionRelayFunction]s influenced by this [Sensor].
     */
    @Deprecated(
        message = "Use relayFunctions.size instead.",
        replaceWith = ReplaceWith("relayFunctions.size")
    )
    fun numRelayFunctions(): Int = _relayFunctions?.size ?: 0

    /**
     * Get a [ProtectionRelayFunction] influenced by this [Sensor] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionRelayFunction]
     * @return The [ProtectionRelayFunction] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use relayFunctions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("relayFunctions.getByMRID(mRID)")
    )
    fun getRelayFunction(mRID: String): ProtectionRelayFunction? = _relayFunctions?.getByMRID(mRID)

    /**
     * Associate this [Sensor] with a [ProtectionRelayFunction] it influences.
     *
     * @param protectionRelayFunction The [ProtectionRelayFunction] to associate with this [Sensor].
     * @return A reference to this [Sensor] for fluent use.
     */
    @Deprecated(
        message = "Use relayFunctions.add(protectionRelayFunction) instead.",
        replaceWith = ReplaceWith("also { it.relayFunctions.add(protectionRelayFunction) }")
    )
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
    @Deprecated(
        message = "Use relayFunctions.remove(relayFunction) instead.",
        replaceWith = ReplaceWith("relayFunctions.remove(relayFunction)")
    )
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
    @Deprecated(
        message = "Use relayFunctions.clear() instead.",
        replaceWith = ReplaceWith("relayFunctions.clear()")
    )
    fun clearRelayFunctions(): Sensor {
        _relayFunctions = null
        return this
    }

    // endregion

    // endregion
}
