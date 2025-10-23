/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
abstract class TableShuntCompensators : TableRegulatingCondEq() {

    val SHUNT_COMPENSATOR_INFO_MRID: Column = Column(++columnIndex, "shunt_compensator_info_mrid", Column.Type.STRING, NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", Column.Type.BOOLEAN, NULL)
    val NOM_U: Column = Column(++columnIndex, "nom_u", Column.Type.INTEGER, NULL)
    val PHASE_CONNECTION: Column = Column(++columnIndex, "phase_connection", Column.Type.STRING, NOT_NULL)
    val SECTIONS: Column = Column(++columnIndex, "sections", Column.Type.DOUBLE, NULL)

}
