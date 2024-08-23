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
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableConductingEquipment

/**
 * An abstract class for EarthFaultCompensator Tables.
 *
 * @property R A column storing the Nominal resistance of device in ohms.
 */

@Suppress("PropertyName")
abstract class TableEarthFaultCompensators : TableConductingEquipment() {

    val R: Column = Column(++columnIndex, "r", "NUMBER", NULL)

}
