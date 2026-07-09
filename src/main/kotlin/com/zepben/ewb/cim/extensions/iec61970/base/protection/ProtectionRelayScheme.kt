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
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

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
class ProtectionRelayScheme(mRID: String) : IdentifiedObject(mRID) {

    @ZBEX
    var system: ProtectionRelaySystem? = null

    private var _functions: MutableList<ProtectionRelayFunction>? = null

    @ZBEX
    val functions: LazyMridList<ProtectionRelayFunction> get() = LazyMridList(
        getter = { _functions },
        setter = { _functions = it },
        owner = this,
        elementDescription = "A ProtectionRelayFunction"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region functions boilerplate

    @Deprecated(
        message = "Use functions.size instead.",
        replaceWith = ReplaceWith("functions.size")
    )
    fun numFunctions(): Int = functions.size

    @Deprecated(
        message = "Use functions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("functions.getByMRID(mRID)")
    )
    fun getFunction(mRID: String): ProtectionRelayFunction? = functions.getByMrid(mRID)

    @Deprecated(
        message = "Use functions.add(function) instead.",
        replaceWith = ReplaceWith("also { it.functions.add(function) }")
    )
    fun addFunction(function: ProtectionRelayFunction): ProtectionRelayScheme {
        functions.add(function)
        return this
    }

    @Deprecated(
        message = "Use functions.remove(function) instead.",
        replaceWith = ReplaceWith("functions.remove(function)")
    )
    fun removeFunction(function: ProtectionRelayFunction): Boolean = functions.remove(function)

    @Deprecated(
        message = "Use functions.clear() instead.",
        replaceWith = ReplaceWith("functions.clear()")
    )
    fun clearFunctions(): ProtectionRelayScheme {
        functions.clear()
        return this
    }

    // endregion

    // endregion
}
