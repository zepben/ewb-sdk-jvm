/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
abstract class TableShuntCompensators : TableRegulatingCondEq() {

    val GROUNDED = Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL)
    val NOM_U = Column(++columnIndex, "nom_u", "INTEGER", NOT_NULL)
    val PHASE_CONNECTION = Column(++columnIndex, "phase_connection", "TEXT", NOT_NULL)
    val SECTIONS = Column(++columnIndex, "sections", "NUMBER", NOT_NULL)

}
