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

import java.security.AccessController
import java.security.PrivilegedActionException
import java.security.PrivilegedExceptionAction
import java.sql.PreparedStatement
import java.sql.Types
import java.time.Instant


fun PreparedStatement.setNullableString(queryIndex: Int, value: String?) {
    when (value) {
        null -> setNull(queryIndex, Types.VARCHAR)
        else -> setString(queryIndex, value)
    }
}

fun PreparedStatement.setInstant(queryIndex: Int, value: Instant?) {
    when (value) {
        null -> setNull(queryIndex, Types.VARCHAR)
        else -> setString(queryIndex, value.toString())
    }
}

fun PreparedStatement.executeSingleUpdate(): Boolean {
    return executeUpdate() == 1
}

fun PreparedStatement.sql(): String {
    return try {
        AccessController.doPrivileged(PrivilegedExceptionAction {
            val field = javaClass.getFieldExt("sql")
            field.isAccessible = true
            field[this].toString()
        } as PrivilegedExceptionAction<String>)
    } catch (e: PrivilegedActionException) {
        "Failed to extract SQL - " + e.message
    }
}

fun PreparedStatement.parameters(): String {
    return try {
        AccessController.doPrivileged(PrivilegedExceptionAction {
            val field = javaClass.getFieldExt("batch")
            field.isAccessible = true
            (field[this] as Array<*>).contentToString()
        } as PrivilegedExceptionAction<String>)
    } catch (e: Exception) {
        "Failed to extract parameters - " + e.message
    }
}
