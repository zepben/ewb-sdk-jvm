/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ConductingEquipment` columns required for the database table.
 *
 * @property BASE_VOLTAGE_MRID Base voltage of this conducting equipment.  Use only when there is no voltage level container used and only one base
 *                       voltage applies.  For example, not used for transformers.
 */
@Suppress("PropertyName")
abstract class TableConductingEquipment : TableEquipment() {

    val BASE_VOLTAGE_MRID: Column = Column(++columnIndex, "base_voltage_mrid", Column.Type.STRING, NULL)

}
