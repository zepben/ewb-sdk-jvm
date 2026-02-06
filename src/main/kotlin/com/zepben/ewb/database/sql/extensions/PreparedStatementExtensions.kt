/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.extensions

import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
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

/**
 * Execute a [PreparedStatement] that should update exactly 1 entry.
 *
 * @param onError A callback which is triggered if the update fails, or updates too many records.
 * @return true is a single record is updates, otherwise false.
 */
@Throws(SQLException::class)
internal fun PreparedStatement.tryExecuteSingleUpdate(onError: () -> Unit): Boolean {
    if (executeSingleUpdate())
        return true

    onError()

    return false
}

/**
 * A helper function to log a failure when using a [PreparedStatement].
 *
 * @param logger The logger to use for the failure message.
 * @param description The description of what was being written when the failure occurred.
 */
internal fun PreparedStatement.logFailure(logger: Logger, description: String) {
    logger.warn(
        "Failed to write $description.\n" +
            "SQL: ${sql()}\n" +
            "Fields: ${parameters()}"
    )
}

/**
 * Set a column to a nullable `Boolean`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableBoolean(queryIndex: Int, value: Boolean?) {
    if (value == null)
        setNull(queryIndex, BOOLEAN)
    else
        setBoolean(queryIndex, value)
}

/**
 * Set a column to a nullable `String`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableString(queryIndex: Int, value: String?) {
    when (value) {
        null -> setNull(queryIndex, VARCHAR)
        else -> setString(queryIndex, value)
    }
}

/**
 * Set a column to a nullable `Double`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableDouble(queryIndex: Int, value: Double?) {
    when {
        value == null -> this.setNull(queryIndex, DOUBLE)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setDouble(queryIndex, value)
    }
}

/**
 * Set a column to a nullable `Float`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableFloat(queryIndex: Int, value: Float?) {
    when {
        value == null -> this.setNull(queryIndex, FLOAT)
        value.isNaN() -> this.setString(queryIndex, "NaN")
        else -> this.setFloat(queryIndex, value)
    }
}

/**
 * Set a column to a nullable `Int`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableInt(queryIndex: Int, value: Int?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setInt(queryIndex, value)
}

/**
 * Set a column to a nullable `Long`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the column.
 */
internal fun PreparedStatement.setNullableLong(queryIndex: Int, value: Long?) {
    if (value == null)
        this.setNull(queryIndex, INTEGER)
    else
        this.setLong(queryIndex, value)
}

/**
 * Set a column to a nullable `Ratio`.
 * @param numeratorIndex The index of the column to set as the numerator.
 * @param denominatorIndex The index of the column to set as the denominator.
 * @param value The value to assign to the columns.
 */
internal fun PreparedStatement.setNullableRatio(numeratorIndex: Int, denominatorIndex: Int, value: Ratio?) {
    if (value == null) {
        this.setNull(denominatorIndex, DOUBLE)
        this.setNull(numeratorIndex, DOUBLE)
    } else {
        this.setDouble(denominatorIndex, value.denominator)
        this.setDouble(numeratorIndex, value.numerator)
    }
}

/**
 * Set a column to a nullable `Instant`.
 * @param queryIndex The index of the column to set.
 * @param value The value to assign to the columns.
 */
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
