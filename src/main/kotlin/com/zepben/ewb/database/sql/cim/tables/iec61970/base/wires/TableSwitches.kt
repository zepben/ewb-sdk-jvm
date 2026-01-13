/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableConductingEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the Switch columns required for the database table.
 *
 * @property NORMAL_OPEN The normally open state of the switch.
 * @property OPEN The currently open state of the switch.
 * @property RATED_CURRENT The rated current of the switch.
 * @property SWITCH_INFO_MRID The mRID of the catalog information for this switch.
 */
@Suppress("PropertyName")
abstract class TableSwitches : TableConductingEquipment() {

    val NORMAL_OPEN: Column = Column(++columnIndex, "normal_open", Column.Type.INTEGER, NOT_NULL)
    val OPEN: Column = Column(++columnIndex, "open", Column.Type.INTEGER, NOT_NULL)
    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Column.Type.DOUBLE, NULL)
    val SWITCH_INFO_MRID: Column = Column(++columnIndex, "switch_info_mrid", Column.Type.STRING, NULL)

}
