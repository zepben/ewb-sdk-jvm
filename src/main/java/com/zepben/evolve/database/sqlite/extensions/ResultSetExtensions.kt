/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.extensions

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
