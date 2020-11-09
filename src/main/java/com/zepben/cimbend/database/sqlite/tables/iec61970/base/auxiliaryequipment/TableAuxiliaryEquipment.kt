/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.auxiliaryequipment

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableEquipment

@Suppress("PropertyName")
abstract class TableAuxiliaryEquipment : TableEquipment() {

    val TERMINAL_MRID = Column(++columnIndex, "terminal_mrid", "TEXT", NULL)

}
