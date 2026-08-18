/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.Equipment
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
class ProtectionRelaySystem(mRID: String) : Equipment(mRID) {

    @ZBEX
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN

    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    @ZBEX
    val schemes: MridCollection<ProtectionRelayScheme> get() = LazyMridList(
        getter = { _schemes },
        setter = { _schemes = it },
        owner = this,
        elementDescription = "A ProtectionRelayScheme"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region schemes boilerplate

    /**
     * Returns the number of schemes for this [ProtectionRelaySystem]
     */
    @Deprecated(
        message = "Use schemes.size instead.",
        replaceWith = ReplaceWith("schemes.size")
    )
    fun numSchemes(): Int = _schemes?.size ?: 0

    /**
     * Get a scheme for this [ProtectionRelaySystem] by its mRID.
     *
     * @param mRID The mRID of the [ProtectionRelayScheme]
     * @return The [ProtectionRelayScheme] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use schemes.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("schemes.getByMRID(mRID)")
    )
    fun getScheme(mRID: String): ProtectionRelayScheme? = _schemes.getByMRID(mRID)

    /**
     * Add a scheme to this [ProtectionRelaySystem]
     * @param scheme The scheme to add.
     * @return This [ProtectionRelaySystem] for fluent use.
     */
    @Deprecated(
        message = "Use schemes.add(scheme) instead.",
        replaceWith = ReplaceWith("also { it.schemes.add(scheme) }")
    )
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
    @Deprecated(
        message = "Use schemes.remove(scheme) instead.",
        replaceWith = ReplaceWith("schemes.remove(scheme)")
    )
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean {
        val ret = _schemes.safeRemove(scheme)
        if (_schemes.isNullOrEmpty()) _schemes = null
        return ret
    }

    /**
     * Clear [schemes].
     * @return This [ProtectionRelaySystem] for fluent use.
     */
    @Deprecated(
        message = "Use schemes.clear() instead.",
        replaceWith = ReplaceWith("schemes.clear()")
    )
    fun clearSchemes(): ProtectionRelaySystem {
        _schemes = null
        return this
    }

    // endregion

    // endregion
}
