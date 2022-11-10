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


internal fun ResultSet.getNullableString(queryIndex: Int): String? =
    getString(queryIndex).takeUnless { wasNull() }

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

internal fun ResultSet.getNullableInt(queryIndex: Int): Int? =
    getInt(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getNullableLong(queryIndex: Int): Long? =
    getLong(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getInstant(queryIndex: Int): Instant? =
    getString(queryIndex).takeUnless { wasNull() }?.let { Instant.parse(it) }

internal fun ResultSet.getNullableRatio(numeratorIndex: Int, denominatorIndex: Int): Ratio? =
    getNullableDouble(denominatorIndex)?.let { denominator ->
        getNullableDouble(numeratorIndex)?.let { numerator -> Ratio(numerator, denominator) }
    }
