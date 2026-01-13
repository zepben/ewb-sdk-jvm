/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `AuxiliaryEquipment` columns required for the database table.
 *
 * @property TERMINAL_MRID The [Terminal] at the equipment where the [AuxiliaryEquipment] is attached.
 */
@Suppress("PropertyName")
abstract class TableAuxiliaryEquipment : TableEquipment() {

    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)

}
