/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableShuntCompensatorInfo : TableAssetInfo() {

    val MAX_POWER_LOSS = Column(++columnIndex, "max_power_loss", "INTEGER", NULL)
    val RATED_CURRENT = Column(++columnIndex, "rated_current", "INTEGER", NULL)
    val RATED_REACTIVE_POWER = Column(++columnIndex, "rated_reactive_power", "INTEGER", NULL)
    val RATED_VOLTAGE = Column(++columnIndex, "rated_voltage", "INTEGER", NULL)

    override fun name(): String {
        return "shunt_compensator_info"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
