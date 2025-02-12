/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import java.sql.PreparedStatement
import java.sql.Types

fun PreparedStatement.setNullableRatio(numeratorIndex: Int, denominatorIndex: Int, value: Ratio?) {
    if (value == null) {
        this.setNull(denominatorIndex, Types.DOUBLE)
        this.setNull(numeratorIndex, Types.DOUBLE)
    } else {
        this.setDouble(denominatorIndex, value.denominator)
        this.setDouble(numeratorIndex, value.numerator)
    }
}
