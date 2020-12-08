/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.extensions

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
