/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.*
import com.zepben.ewb.database.sql.Column.Type
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
abstract class TableWireInfo : TableAssetInfo() {

    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Type.DOUBLE, NULL)
    val MATERIAL: Column = Column(++columnIndex, "material", Type.STRING, NOT_NULL)
    val SIZE_DESCRIPTION: Column = Column(++columnIndex, "size_description", Type.STRING, NULL)
    val STRAND_COUNT: Column = Column(++columnIndex, "strand_count", Type.STRING, NULL)
    val CORE_STRAND_COUNT: Column = Column(++columnIndex, "core_strand_count", Type.STRING, NULL)
    val INSULATED: Column = Column(++columnIndex, "insulated", Type.BOOLEAN, NULL)
    val INSULATION_MATERIAL: Column = Column(++columnIndex, "insulation_material", Type.STRING, NOT_NULL)
    val INSULATION_THICKNESS: Column = Column(++columnIndex, "insulation_thickness", Type.DOUBLE, NULL)
}
