/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database

import java.sql.ResultSet
import java.time.Instant

internal fun ResultSet.getNullableBoolean(columnName: String): Boolean? =
    getBoolean(columnName).takeUnless { wasNull() }

internal fun ResultSet.getNullableString(columnName: String): String? =
    getString(columnName).takeUnless { wasNull() }

internal fun ResultSet.getNullableDouble(columnName: String): Double? {
    // Annoyingly getDouble will return 0.0 for string values, so we need to check all 0.0's for NaN.
    val dbl = getDouble(columnName).takeUnless { wasNull() }
    return if (dbl == 0.0) {
        when (getString(columnName).uppercase()) {
            "NAN" -> Double.NaN
            else -> dbl
        }
    } else
        dbl
}

internal fun ResultSet.getNullableInt(columnName: String): Int? =
    getInt(columnName).takeUnless { wasNull() }

internal fun ResultSet.getInstant(columnName: String): Instant? =
    getString(columnName).takeUnless { wasNull() }?.let { Instant.parse(it) }
