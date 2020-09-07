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
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.database.MRIDLookupException



/**
 * Get an object associated with this service and throw if it is not found.
 *
 * @param T The type of object to look for. If this is a base class it will search all subclasses.
 * @param mRID The mRID of the object to find.
 * @param typeNameAndMRID The description of the item requesting the lookup.
 *
 * @return The object identified by [mRID] as [T].
 * @throws MRIDLookupException if no objects of type [T] are found with the specified [mRID]
 */
@Throws(MRIDLookupException::class)
inline fun <reified T : IdentifiedObject> BaseService.getOrThrow(mRID: String?, typeNameAndMRID: String): T {
    return get<T>(mRID)
        ?: throw MRIDLookupException("Failed to find ${T::class.simpleName} with mRID $mRID for $typeNameAndMRID")
}

/**
 * Optionally get an object associated with this service and throw if it is not found.
 *
 * @param T The type of object to look for. If this is a base class it will search all subclasses.
 * @param mRID The mRID of the object to find.
 * @param typeNameAndMRID The description of the item requesting the lookup.
 *
 * @return The object identified by [mRID] as [T] if it was found, or null if no [mRID] was supplied.
 * @throws MRIDLookupException if no objects of type [T] are found with the specified [mRID]
 */
@Throws(MRIDLookupException::class)
inline fun <reified T : IdentifiedObject> BaseService.ensureGet(mRID: String?, typeNameAndMRID: String): T? {
    return if (mRID.isNullOrBlank())
        null
    else
        getOrThrow<T>(mRID!!, typeNameAndMRID)
}
