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
 * A class representing the EarthFaultCompensator columns required for the database table.
 *
 * @property R The Nominal resistance of device in ohms.
 */
@Suppress("PropertyName")
abstract class TableEarthFaultCompensators : TableConductingEquipment() {

    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)

}
