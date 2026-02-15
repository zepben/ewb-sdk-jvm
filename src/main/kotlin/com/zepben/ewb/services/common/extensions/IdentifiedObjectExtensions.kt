/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.extensions

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

internal fun <T : IdentifiedObject> Iterable<T>?.getByMRID(mRID: String): T? {
    return this?.firstOrNull { it.mRID == mRID }
}

internal fun IdentifiedObject.validateReference(other: IdentifiedObject, getter: (String) -> IdentifiedObject?, typeDescription: String): Boolean =
    validateReference(other, IdentifiedObject::mRID, getter) { "$typeDescription with mRID ${other.mRID}" }

internal fun <T> IdentifiedObject.validateReference(other: T, getIdentifier: T.() -> String, getter: (String) -> T?, describeOther: () -> String): Boolean {
    val getResult = getter(other.getIdentifier())
    if (getResult == other)
        return true

    require((getResult == null) || (getResult == other)) { "${describeOther()} already exists in ${typeNameAndMRID()}." }
    return false
}
