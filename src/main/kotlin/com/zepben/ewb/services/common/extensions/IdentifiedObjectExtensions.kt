/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.extensions

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject


fun IdentifiedObject.nameAndMRID(): String = if (name.isNullOrBlank()) mRID else "'$name' [$mRID]"
fun IdentifiedObject.typeNameAndMRID(): String = if (name.isNullOrBlank()) "${javaClass.simpleName} $mRID" else "${javaClass.simpleName} $name [$mRID]"

internal fun <T : IdentifiedObject> Iterable<T>?.getByMRID(mRID: String): T? {
    return this?.firstOrNull { it.mRID == mRID }
}

internal fun IdentifiedObject.validateReference(other: IdentifiedObject, getter: (String) -> IdentifiedObject?, typeDescription: String): Boolean {
    val getResult = getter(other.mRID)
    if (getResult == other)
        return true

    require((getResult == null) || (getResult == other)) { "$typeDescription with mRID ${other.mRID} already exists in ${typeNameAndMRID()}." }
    return false
}
