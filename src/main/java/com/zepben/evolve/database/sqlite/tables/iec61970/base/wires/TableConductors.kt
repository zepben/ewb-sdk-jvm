/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableConductingEquipment

@Suppress("PropertyName")
abstract class TableConductors : TableConductingEquipment() {

    val LENGTH = Column(++columnIndex, "length", "NUMBER")
    val WIRE_INFO_MRID = Column(++columnIndex, "wire_info_mrid", "TEXT", NULL)

}
