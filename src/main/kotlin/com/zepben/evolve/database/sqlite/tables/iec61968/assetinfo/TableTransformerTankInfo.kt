/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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
class TableTransformerTankInfo : TableAssetInfo() {

    val POWER_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "power_transformer_info_mrid", "TEXT", NULL)

    override fun name(): String {
        return "transformer_tank_info"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(POWER_TRANSFORMER_INFO_MRID))

        return cols
    }


    override val tableClass: Class<TableTransformerTankInfo> = this.javaClass
    override val tableClassInstance: TableTransformerTankInfo = this

}
