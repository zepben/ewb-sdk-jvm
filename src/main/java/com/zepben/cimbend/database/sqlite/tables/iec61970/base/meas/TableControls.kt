/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NULL

@Suppress("PropertyName")
class TableControls : TableIoPoints() {

    val POWER_SYSTEM_RESOURCE_MRID = Column(++columnIndex, "power_system_resource_mrid", "TEXT", NULL)

    override fun name(): String {
        return "controls"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
