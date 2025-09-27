/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.extensions

import com.zepben.ewb.services.common.extensions.internEmpty
import java.sql.ResultSet
import java.time.Instant

internal fun ResultSet.getNullableBoolean(queryIndex: Int): Boolean? =
    getBoolean(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getNullableString(queryIndex: Int): String? =
    getString(queryIndex).takeUnless { wasNull() }?.internEmpty()

internal fun ResultSet.getNullableDouble(queryIndex: Int): Double? {
    // Annoyingly getDouble will return 0.0 for string values, so we need to check all 0.0's for NaN.
    val dbl = getDouble(queryIndex).takeUnless { wasNull() }
    return if (dbl == 0.0) {
        when (getString(queryIndex).uppercase()) {
            "NAN" -> Double.NaN
            else -> dbl
        }
    } else
        dbl
}

internal fun ResultSet.getNullableFloat(queryIndex: Int): Float? {
    // Annoyingly getFloat will return 0.0 for string values, so we need to check all 0.0's for NaN.
    val float = getFloat(queryIndex).takeUnless { wasNull() }
    return if (float == 0.0f) {
        when (getString(queryIndex).uppercase()) {
            "NAN" -> Float.NaN
            else -> float
        }
    } else
        float
}

internal fun ResultSet.getNullableInt(queryIndex: Int): Int? =
    getInt(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getNullableLong(queryIndex: Int): Long? =
    getLong(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getInstant(queryIndex: Int): Instant? =
    getString(queryIndex).takeUnless { wasNull() }?.let { Instant.parse(it) }
