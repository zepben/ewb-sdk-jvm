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
import com.zepben.ewb.testing.MRIDListWrapper

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
    val schemes: MRIDListWrapper<ProtectionRelayScheme>
        get() = MRIDListWrapper(
            getter = { _schemes },
            setter = { _schemes = it })

    @Deprecated("BOILERPLATE: Use schemes.size instead")
    fun numSchemes(): Int = schemes.size

    @Deprecated("BOILERPLATE: Use schemes.getByMRID(mRID) instead")
    fun getScheme(mRID: String): ProtectionRelayScheme? = schemes.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use schemes.remove(scheme) instead")
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean = schemes.remove(scheme)

    @Deprecated("BOILERPLATE: Use schemes.clear() instead")
    fun clearSchemes(): ProtectionRelaySystem {
        schemes.clear()
        return this
    }

}
