/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.cim.tables.Column

@Suppress("PropertyName")
class TablePetersenCoils : TableEarthFaultCompensators() {

    val X_GROUND_NOMINAL: Column = Column(++columnIndex, "x_ground_nominal", "NUMBER", Column.Nullable.NULL)

    override val name: String = "petersen_coil"

}
