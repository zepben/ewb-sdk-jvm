/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * A relay system for controlling ProtectedSwitches.
 *
 * @property protectionKind [ZBEX] The kind of protection being provided by this protection equipment.
 * @property schemes [ZBEX] The schemes implemented by this ProtectionRelaySystem.
 */
@ZBEX
class ProtectionRelaySystem @JvmOverloads constructor(mRID: String = "") : Equipment(mRID) {

    @ZBEX
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN

    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    @ZBEX
    val schemes: Collection<ProtectionRelayScheme> get() = _schemes.asUnmodifiable()

    /**
     * Returns the number of schemes for this [ProtectionRelaySystem]
     */
    fun numSchemes(): Int = _schemes?.size ?: 0

    /**
     * Get a scheme for this [ProtectionRelaySystem] by its mRID.
     *
     * @param mRID The mRID of the [ProtectionRelayScheme]
     * @return The [ProtectionRelayScheme] with the specified [mRID] if it exists, otherwise null
     */
    fun getScheme(mRID: String): ProtectionRelayScheme? = _schemes.getByMRID(mRID)

    /**
     * Add a scheme to this [ProtectionRelaySystem]
     * @param scheme The scheme to add.
     * @return This [ProtectionRelaySystem] for fluent use.
     */
    fun addScheme(
        scheme: ProtectionRelayScheme,
    ): ProtectionRelaySystem {
        if (validateReference(scheme, ::getScheme, "A ProtectionRelayScheme"))
            return this

        _schemes = _schemes ?: mutableListOf()
        _schemes!!.add(scheme)

        return this
    }

    /**
     * Remove a scheme from this [ProtectionRelaySystem].
     * @param scheme The [ProtectionRelayScheme] to remove.
     * @return true if the scheme was removed.
     */
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean {
        val ret = _schemes.safeRemove(scheme)
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
