/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * A scheme that a group of relay functions implement. For example, typically schemes are primary and secondary, or main and failsafe.
 *
 * @property system [ZBEX] The system this scheme belongs to.
 * @property functions [ZBEX] The functions operated as part of this protection scheme.
 */
@ZBEX
class ProtectionRelayScheme @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    @ZBEX
    var system: ProtectionRelaySystem? = null

    private var _functions: MutableList<ProtectionRelayFunction>? = null

    @ZBEX
    val functions: Collection<ProtectionRelayFunction> get() = _functions.asUnmodifiable()

    /**
     * Get the number of [ProtectionRelayFunction]s operated as a part of this [ProtectionRelayScheme].
     *
     * @return The number of [ProtectionRelayFunction]s operated as a part of this [ProtectionRelayScheme].
     */
    fun numFunctions(): Int = _functions?.size ?: 0

    /**
     * Get a [ProtectionRelayFunction] operated as a part of this [ProtectionRelayScheme] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionRelayFunction]
     * @return The [ProtectionRelayFunction] with the specified [mRID] if it exists, otherwise null
     */
    fun getFunction(mRID: String): ProtectionRelayFunction? = _functions?.getByMRID(mRID)

    /**
     * Associate a [ProtectionRelayFunction] with this [ProtectionRelayScheme].
     *
     * @param function The [ProtectionRelayFunction] to associate with this [ProtectionRelayScheme].
     * @return A reference to this [ProtectionRelayScheme] for fluent use.
     */
    fun addFunction(function: ProtectionRelayFunction): ProtectionRelayScheme {
        if (validateReference(function, ::getFunction, "A ProtectionRelayFunction"))
            return this

        _functions = _functions ?: mutableListOf()
        _functions!!.add(function)

        return this
    }

    /**
     * Disassociate a [ProtectionRelayFunction] from this [ProtectionRelayScheme].
     *
     * @param function The [ProtectionRelayFunction] to disassociate from this [ProtectionRelayScheme].
     * @return true if the [ProtectionRelayFunction] was disassociated.
     */
    fun removeFunction(function: ProtectionRelayFunction): Boolean {
        val ret = _functions.safeRemove(function)
        if (_functions.isNullOrEmpty()) _functions = null
        return ret
    }

    /**
     * Disassociate all [ProtectionRelayFunction]s from this [ProtectionRelayScheme].
     *
     * @return A reference to this [ProtectionRelayScheme] for fluent use.
     */
    fun clearFunctions(): ProtectionRelayScheme {
        _functions = null
        return this
    }

}
