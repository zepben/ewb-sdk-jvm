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
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the conductor columns required for the database table.
 *
 * @property LENGTH The conductor length.
 * @property DESIGN_TEMPERATURE The conductor design temperature.
 * @property DESIGN_RATING The conductor design rating.
 * @property WIRE_INFO_MRID A link to the wire info for the conductor.
 */
@Suppress("PropertyName")
abstract class TableConductors : TableConductingEquipment() {

    val LENGTH: Column = Column(++columnIndex, "length", Column.Type.DOUBLE, NULL)
    val DESIGN_TEMPERATURE: Column = Column(++columnIndex, "design_temperature", Column.Type.INTEGER, NULL)
    val DESIGN_RATING: Column = Column(++columnIndex, "design_rating", Column.Type.DOUBLE, NULL)
    val WIRE_INFO_MRID: Column = Column(++columnIndex, "wire_info_mrid", Column.Type.STRING, NULL)

}
