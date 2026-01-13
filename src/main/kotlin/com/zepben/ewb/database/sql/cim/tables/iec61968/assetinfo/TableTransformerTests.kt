/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TransformerTest` columns required for the database table.
 *
 * @property BASE_POWER Base power at which the tests are conducted, usually equal to the ratedS of one of the involved transformer ends in VA.
 * @property TEMPERATURE Temperature at which the test is conducted in degrees Celsius.
 */
@Suppress("PropertyName")
abstract class TableTransformerTests : TableAssetInfo() {

    val BASE_POWER: Column = Column(++columnIndex, "base_power", Column.Type.INTEGER, NULL)
    val TEMPERATURE: Column = Column(++columnIndex, "temperature", Column.Type.DOUBLE, NULL)

}
