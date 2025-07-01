/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

/**
 * A class representing the GroundingImpedance columns required for the database table.
 *
 * @property X A column storing the Reactance of device in ohms.
 */
@Suppress("PropertyName")
class TableGroundingImpedances : TableEarthFaultCompensators() {

    val X: Column = Column(++columnIndex, "x", "NUMBER", NULL)

    override val name: String = "grounding_impedances"

}
