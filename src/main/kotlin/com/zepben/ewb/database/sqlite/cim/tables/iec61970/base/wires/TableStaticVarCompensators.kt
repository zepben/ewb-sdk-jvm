/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableStaticVarCompensators : TableRegulatingCondEq() {

    val CAPACITIVE_RATING: Column = Column(++columnIndex, "capacitive_rating", "NUMBER", NULL)
    val INDUCTIVE_RATING: Column = Column(++columnIndex, "inductive_rating", "NUMBER", NULL)
    val Q: Column = Column(++columnIndex, "q", "NUMBER", NULL)
    val SVC_CONTROL_MODE: Column = Column(++columnIndex, "svc_control_mode", "TEXT", NOT_NULL)
    val VOLTAGE_SET_POINT: Column = Column(++columnIndex, "voltage_set_point", "INTEGER", NULL)

    override val name: String = "static_var_compensators"

}
