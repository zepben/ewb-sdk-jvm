/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableConductingEquipment

/**
 * A class representing the conductor columns required for the database table.
 *
 * @property LENGTH A column storing the conductor length.
 * @property DESIGN_TEMPERATURE A column storing the conductor design temperature.
 * @property DESIGN_RATING A column storing the conductor design rating.
 * @property WIRE_INFO_MRID A column storing a link to the wire info for the conductor.
 */
@Suppress("PropertyName")
abstract class TableConductors : TableConductingEquipment() {

    val LENGTH: Column = Column(++columnIndex, "length", "NUMBER", NULL)
    val DESIGN_TEMPERATURE: Column = Column(++columnIndex, "design_temperature", "INTEGER", NULL)
    val DESIGN_RATING: Column = Column(++columnIndex, "design_rating", "NUMBER", NULL)
    val WIRE_INFO_MRID: Column = Column(++columnIndex, "wire_info_mrid", "TEXT", NULL)

}
