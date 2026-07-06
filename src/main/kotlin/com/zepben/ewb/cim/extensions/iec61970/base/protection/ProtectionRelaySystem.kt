/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.Equipment

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
    val schemes: LazyMridList<ProtectionRelayScheme> get() = LazyMridList(
        getter = { _schemes },
        setter = { _schemes = it },
        owner = { this },
        elementDescription = "A ProtectionRelayScheme"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    @Deprecated(
        message = "Use schemes.size instead.",
        replaceWith = ReplaceWith("schemes.size")
    )
    fun numSchemes(): Int = schemes.size

    @Deprecated(
        message = "Use schemes.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("schemes.getByMRID(mRID)")
    )
    fun getScheme(mRID: String): ProtectionRelayScheme? = schemes.getByMrid(mRID)

    @Deprecated(
        message = "Use schemes.remove(scheme) instead.",
        replaceWith = ReplaceWith("schemes.remove(scheme)")
    )
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean = schemes.remove(scheme)

    @Deprecated(
        message = "Use schemes.clear() instead.",
        replaceWith = ReplaceWith("schemes.clear()")
    )
    fun clearSchemes(): ProtectionRelaySystem {
        schemes.clear()
        return this
    }

    @Deprecated(
        message = "Use schemes.add(scheme) instead.",
        replaceWith = ReplaceWith("also { it.schemes.add(scheme) }")
    )
    fun addScheme( scheme: ProtectionRelayScheme, ): ProtectionRelaySystem {
        schemes.add(scheme)
        return this
    }

    // endregion
}
