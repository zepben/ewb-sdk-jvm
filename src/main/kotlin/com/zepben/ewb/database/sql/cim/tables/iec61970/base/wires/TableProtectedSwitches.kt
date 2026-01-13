/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ProtectedSwitch` columns required for the database table.
 *
 * @property BREAKING_CAPACITY The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
 */
@Suppress("PropertyName")
abstract class TableProtectedSwitches : TableSwitches() {

    val BREAKING_CAPACITY: Column = Column(++columnIndex, "breaking_capacity", Column.Type.INTEGER, NULL)

}
