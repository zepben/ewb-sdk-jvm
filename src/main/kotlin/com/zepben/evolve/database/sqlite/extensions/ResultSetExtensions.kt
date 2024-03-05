/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import java.sql.ResultSet
import java.time.Instant


fun ResultSet.getNullableBoolean(queryIndex: Int): Boolean? =
    getBoolean(queryIndex).takeUnless { wasNull() }

fun ResultSet.getNullableString(queryIndex: Int): String? =
    getString(queryIndex).takeUnless { wasNull() }

fun ResultSet.getNullableDouble(queryIndex: Int): Double? {
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

fun ResultSet.getNullableFloat(queryIndex: Int): Float? {
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

fun ResultSet.getNullableInt(queryIndex: Int): Int? =
    getInt(queryIndex).takeUnless { wasNull() }

fun ResultSet.getNullableLong(queryIndex: Int): Long? =
    getLong(queryIndex).takeUnless { wasNull() }

fun ResultSet.getInstant(queryIndex: Int): Instant? =
    getString(queryIndex).takeUnless { wasNull() }?.let { Instant.parse(it) }

fun ResultSet.getNullableRatio(numeratorIndex: Int, denominatorIndex: Int): Ratio? =
    getNullableDouble(denominatorIndex)?.let { denominator ->
        getNullableDouble(numeratorIndex)?.let { numerator -> Ratio(numerator, denominator) }
    }
