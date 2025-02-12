/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableConductingEquipment

/**
 * A class representing the Switch columns required for the database table.
 *
 * @property NORMAL_OPEN A column storing the normally open state of the switch.
 * @property OPEN A column storing the currently open state of the switch.
 * @property RATED_CURRENT A column storing the rated current of the switch.
 * @property SWITCH_INFO_MRID A column storing the mRID of the catalog information for this switch.
 */
@Suppress("PropertyName")
abstract class TableSwitches : TableConductingEquipment() {

    val NORMAL_OPEN: Column = Column(++columnIndex, "normal_open", "INTEGER", NOT_NULL)
    val OPEN: Column = Column(++columnIndex, "open", "INTEGER", NOT_NULL)
    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", "NUMBER", NULL)
    val SWITCH_INFO_MRID: Column = Column(++columnIndex, "switch_info_mrid", "TEXT", NULL)

}
