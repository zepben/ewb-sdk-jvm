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
package com.zepben.cimbend.database.sqlite.extensions

import java.sql.ResultSet
import java.time.Instant


fun ResultSet.getNullableString(queryIndex: Int): String? {
    val value = getString(queryIndex)
    return if (wasNull()) null else value
}

fun ResultSet.getNullableDouble(queryIndex: Int): Double {
    val value = getDouble(queryIndex)
    return if (wasNull()) Double.NaN else value
}

fun ResultSet.getInstant(queryIndex: Int): Instant? {
    val value = getString(queryIndex)
    return if (wasNull()) null else Instant.parse(value)
}
