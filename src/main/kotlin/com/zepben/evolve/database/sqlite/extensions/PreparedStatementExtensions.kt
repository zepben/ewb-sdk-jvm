/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
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
fun PreparedStatement.tryExecuteSingleUpdate(onError: () -> Unit): Boolean {
    if (executeSingleUpdate())
        return true

    onError()

    return false
}

fun PreparedStatement.logFailure(logger: Logger, description: String) {
    logger.warn(
        "Failed to write $description.\n" +
            "SQL: ${sql()}\n" +
            "Fields: ${parameters()}"
    )
}

fun PreparedStatement.setNullableBoolean(queryIndex: Int, value: Boolean?) {
    if (value == null)
        setNull(queryIndex, BOOLEAN)
    else
        setBoolean(queryIndex, value)
}

fun PreparedStatement.setNullableString(queryIndex: Int, value: String?) {
    when (value) {
        null -> setNull(queryIndex, VARCHAR)
        else -> setString(queryIndex, value)
    }
}

fun PreparedStatement.setNullableDouble(queryIndex: Int, value: Double?) {
    when {
        value == null -> this.setNull(queryIndex, DOUBLE)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setDouble(queryIndex, value)
    }
}

fun PreparedStatement.setNullableFloat(queryIndex: Int, value: Float?) {
    when {
        value == null -> this.setNull(queryIndex, FLOAT)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setFloat(queryIndex, value)
    }
}

fun PreparedStatement.setNullableInt(queryIndex: Int, value: Int?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setInt(queryIndex, value)
}

fun PreparedStatement.setNullableLong(queryIndex: Int, value: Long?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setLong(queryIndex, value)
}

fun PreparedStatement.setInstant(queryIndex: Int, value: Instant?) {
    when (value) {
        null -> setNull(queryIndex, VARCHAR)
        else -> setString(queryIndex, value.toString())
    }
}

fun PreparedStatement.setNullableRatio(numeratorIndex: Int, denominatorIndex: Int, value: Ratio?) {
    if (value == null) {
        this.setNull(denominatorIndex, DOUBLE)
        this.setNull(numeratorIndex, DOUBLE)
    } else {
        this.setDouble(denominatorIndex, value.denominator)
        this.setDouble(numeratorIndex, value.numerator)
    }
}

fun PreparedStatement.executeSingleUpdate(): Boolean {
    return executeUpdate() == 1
}

fun PreparedStatement.sql(): String {
    return try {
        accessProtectedProperty<String>("sql")
    } catch (e: PrivilegedActionException) {
        "Failed to extract SQL - " + e.message
    }
}

fun PreparedStatement.parameters(): String {
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
