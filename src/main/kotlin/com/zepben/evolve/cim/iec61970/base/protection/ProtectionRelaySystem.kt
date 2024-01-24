/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.common.extensions.asUnmodifiable

/**
 * A relay system for controlling ProtectedSwitches.
 *
 * @property protectionKind The kind of protection being provided by this protection equipment.
 */
class ProtectionRelaySystem(mRID: String) : Equipment(mRID) {

    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN
    
    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    /**
     * The schemes implemented by this [ProtectionRelaySystem].
     *
     * @return An unmodifiable [List] of the [ProtectionRelayScheme]s implemented by this [ProtectionRelaySystem].
     */
    val schemes: List<ProtectionRelayScheme> get() = _schemes.asUnmodifiable()

    /**
     * Returns the number of schemes for this [ProtectionRelaySystem]
     */
    fun numSchemes(): Int = _schemes?.size ?: 0

    /**
     * Add a scheme
     * @param scheme The scheme to add.
     * @param index The index into the list to add the scheme at. Defaults to the end of the list.
     * @return This [ProtectionRelaySystem] for fluent use.
     */
    fun addScheme(
        scheme: ProtectionRelayScheme,
        index: Int = numSchemes()
    ): ProtectionRelaySystem {
        _schemes = _schemes ?: mutableListOf()
        _schemes!!.add(index, scheme)

        return this
    }

    /**
     * Remove a scheme from the list.
     * @param index The index of the scheme to remove.
     * @return The scheme that was removed, or null if no scheme was present at [index].
     */
    fun removeScheme(index: Int): ProtectionRelayScheme? {
        val ret = _schemes?.removeAt(index)
        if (_schemes.isNullOrEmpty()) _schemes = null
        return ret
    }

    /**
     * Clear [schemes].
     * @return This [ProtectionRelaySystem] for fluent use.
     */
    fun clearSchemes(): ProtectionRelaySystem {
        _schemes = null
        return this
    }

}
