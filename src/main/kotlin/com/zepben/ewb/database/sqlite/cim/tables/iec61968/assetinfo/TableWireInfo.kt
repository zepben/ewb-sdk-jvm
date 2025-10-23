/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
abstract class TableWireInfo : TableAssetInfo() {

    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Column.Type.DOUBLE, NULL)
    val MATERIAL: Column = Column(++columnIndex, "material", Column.Type.STRING, NOT_NULL)

}
