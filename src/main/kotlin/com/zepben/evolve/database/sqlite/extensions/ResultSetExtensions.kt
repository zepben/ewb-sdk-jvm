/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.evolve.database.sql.extensions.getNullableDouble
import java.sql.ResultSet

fun ResultSet.getNullableRatio(numeratorIndex: Int, denominatorIndex: Int): Ratio? =
    getNullableDouble(denominatorIndex)?.let { denominator ->
        getNullableDouble(numeratorIndex)?.let { numerator -> Ratio(numerator, denominator) }
    }
