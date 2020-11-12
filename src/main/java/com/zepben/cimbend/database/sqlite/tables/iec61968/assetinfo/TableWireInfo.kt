/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
abstract class TableWireInfo : TableAssetInfo() {

    val RATED_CURRENT = Column(++columnIndex, "rated_current", "NUMBER", NOT_NULL)
    val MATERIAL = Column(++columnIndex, "material", "TEXT", NOT_NULL)

}
