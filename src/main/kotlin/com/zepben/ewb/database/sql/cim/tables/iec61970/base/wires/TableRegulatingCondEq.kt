/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.wires.RegulatingCondEq
import com.zepben.ewb.cim.iec61970.base.wires.RegulatingControl
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `RegulatingCondEq` columns required for the database table.
 *
 * @property CONTROL_ENABLED Specifies the regulation status of the equipment.  True is regulating, false is not regulating.
 * @property REGULATING_CONTROL_MRID The [RegulatingControl] associated with this [RegulatingCondEq]
 */
@Suppress("PropertyName")
abstract class TableRegulatingCondEq : TableEnergyConnections() {

    val CONTROL_ENABLED: Column = Column(++columnIndex, "control_enabled", Column.Type.BOOLEAN, NULL)
    val REGULATING_CONTROL_MRID: Column = Column(++columnIndex, "regulating_control_mrid", Column.Type.STRING, NULL)

}
