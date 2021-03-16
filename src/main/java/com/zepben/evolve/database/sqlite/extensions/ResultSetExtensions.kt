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

internal fun ResultSet.getNullableString(queryIndex: Int): String? =
    getString(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getNullableDouble(queryIndex: Int): Double? =
    getDouble(queryIndex).takeUnless { wasNull() }

internal fun ResultSet.getInstant(queryIndex: Int): Instant? =
    getString(queryIndex).takeUnless { wasNull() }?.let { Instant.parse(it) }
