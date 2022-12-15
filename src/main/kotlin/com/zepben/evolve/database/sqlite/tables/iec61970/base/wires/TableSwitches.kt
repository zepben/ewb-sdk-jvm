/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableConductingEquipment

@Suppress("PropertyName")
abstract class TableSwitches : TableConductingEquipment() {

    val NORMAL_OPEN = Column(++columnIndex, "normal_open", "INTEGER", NOT_NULL)
    val OPEN = Column(++columnIndex, "open", "INTEGER", NOT_NULL)
    val RATED_CURRENT = Column(++columnIndex, "rated_current", "INTEGER", NULL)
    val SWITCH_INFO_MRID = Column(++columnIndex, "switch_info_mrid", "TEXT", NULL)

}
