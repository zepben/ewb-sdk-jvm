/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.cim.tables.Column
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableSynchronousMachines : TableRotatingMachines() {

    val BASE_Q: Column = Column(++columnIndex, "base_q", "NUMBER", NULL)
    val CONDENSER_P: Column = Column(++columnIndex, "condenser_p", "INTEGER", NULL)
    val EARTHING: Column = Column(++columnIndex, "earthing", "BOOLEAN", NULL)
    val EARTHING_STAR_POINT_R: Column = Column(++columnIndex, "earthing_star_point_r", "NUMBER", NULL)
    val EARTHING_STAR_POINT_X: Column = Column(++columnIndex, "earthing_star_point_x", "NUMBER", NULL)
    val IKK: Column = Column(++columnIndex, "ikk", "NUMBER", NULL)
    val MAX_Q: Column = Column(++columnIndex, "max_q", "NUMBER", NULL)
    val MAX_U: Column = Column(++columnIndex, "max_u", "INTEGER", NULL)
    val MIN_Q: Column = Column(++columnIndex, "min_q", "NUMBER", NULL)
    val MIN_U: Column = Column(++columnIndex, "min_u", "INTEGER", NULL)
    val MU: Column = Column(++columnIndex, "mu", "NUMBER", NULL)
    val R: Column = Column(++columnIndex, "r", "NUMBER", NULL)
    val R0: Column = Column(++columnIndex, "r0", "NUMBER", NULL)
    val R2: Column = Column(++columnIndex, "r2", "NUMBER", NULL)
    val SAT_DIRECT_SUBTRANS_X: Column = Column(++columnIndex, "sat_direct_subtrans_x", "NUMBER", NULL)
    val SAT_DIRECT_SYNC_X: Column = Column(++columnIndex, "sat_direct_sync_x", "NUMBER", NULL)
    val SAT_DIRECT_TRANS_X: Column = Column(++columnIndex, "sat_direct_trans_x", "NUMBER", NULL)
    val X0: Column = Column(++columnIndex, "x0", "NUMBER", NULL)
    val X2: Column = Column(++columnIndex, "x2", "NUMBER", NULL)
    val TYPE: Column = Column(++columnIndex, "type", "TEXT", NULL)
    val OPERATING_MODE: Column = Column(++columnIndex, "operating_mode", "TEXT", NULL)

    override val name: String = "synchronous_rotating_machine"

}
