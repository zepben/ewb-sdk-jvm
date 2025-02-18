/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql.extensions

import org.slf4j.Logger
import java.security.AccessController
import java.security.PrivilegedActionException
import java.security.PrivilegedExceptionAction
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types.*
import java.time.Instant
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Throws(SQLException::class)
internal fun PreparedStatement.tryExecuteSingleUpdate(onError: () -> Unit): Boolean {
    if (executeSingleUpdate())
        return true

    onError()

    return false
}

internal fun PreparedStatement.logFailure(logger: Logger, description: String) {
    logger.warn(
        "Failed to write $description.\n" +
            "SQL: ${sql()}\n" +
            "Fields: ${parameters()}"
    )
}

internal fun PreparedStatement.setNullableBoolean(queryIndex: Int, value: Boolean?) {
    if (value == null)
        setNull(queryIndex, BOOLEAN)
    else
        setBoolean(queryIndex, value)
}

internal fun PreparedStatement.setNullableString(queryIndex: Int, value: String?) {
    when (value) {
        null -> setNull(queryIndex, VARCHAR)
        else -> setString(queryIndex, value)
    }
}

internal fun PreparedStatement.setNullableDouble(queryIndex: Int, value: Double?) {
    when {
        value == null -> this.setNull(queryIndex, DOUBLE)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setDouble(queryIndex, value)
    }
}

internal fun PreparedStatement.setNullableFloat(queryIndex: Int, value: Float?) {
    when {
        value == null -> this.setNull(queryIndex, FLOAT)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setFloat(queryIndex, value)
    }
}

internal fun PreparedStatement.setNullableInt(queryIndex: Int, value: Int?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setInt(queryIndex, value)
}

internal fun PreparedStatement.setNullableLong(queryIndex: Int, value: Long?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setLong(queryIndex, value)
}

internal fun PreparedStatement.setInstant(queryIndex: Int, value: Instant?) {
    when (value) {
        null -> setNull(queryIndex, VARCHAR)
        else -> setString(queryIndex, value.toString())
    }
}

internal fun PreparedStatement.executeSingleUpdate(): Boolean {
    return executeUpdate() == 1
}

internal fun PreparedStatement.sql(): String {
    return try {
        accessProtectedProperty<String>("sql")
    } catch (e: PrivilegedActionException) {
        "Failed to extract SQL - " + e.message
    }
}

internal fun PreparedStatement.parameters(): String {
    return try {
        accessProtectedProperty<Array<*>>("batch").contentToString()
    } catch (e: Exception) {
        "Failed to extract parameters - " + e.message
    }
}

private inline fun <reified T : Any> PreparedStatement.accessProtectedProperty(propertyName: String): T =
    AccessController.doPrivileged(PrivilegedExceptionAction {
        val prop = this::class.memberProperties.first { it.name == propertyName }
        prop.isAccessible = true
        prop.getter.call(this)
    }) as T
