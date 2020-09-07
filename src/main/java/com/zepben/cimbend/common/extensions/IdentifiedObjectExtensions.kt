/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common.extensions

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject


fun IdentifiedObject.nameAndMRID(): String = if (name.isBlank()) mRID else "'$name' [$mRID]"
fun IdentifiedObject.typeNameAndMRID(): String = if (name.isBlank()) "${javaClass.simpleName} $mRID" else "${javaClass.simpleName} $name [$mRID]"

internal fun <T : IdentifiedObject> Iterable<T>?.containsMRID(mRID: String): Boolean {
    return getByMRID(mRID) != null
}

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
