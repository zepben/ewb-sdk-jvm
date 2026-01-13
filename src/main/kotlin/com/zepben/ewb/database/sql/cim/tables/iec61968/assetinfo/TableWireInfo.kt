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
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type

/**
 * A class representing the `WireInfo` columns required for the database table.
 *
 * @property RATED_CURRENT Current carrying capacity of the wire under stated thermal conditions.
 * @property MATERIAL Conductor material
 * @property SIZE_DESCRIPTION Describes the wire gauge or cross section (e.g., 4/0,
 * @property STRAND_COUNT Number of strands in the conductor.
 * @property CORE_STRAND_COUNT (if used) Number of strands in the steel core.
 * @property INSULATED True if conductor is insulated.
 * @property INSULATION_MATERIAL (if insulated conductor) Material used for insulation.
 * @property INSULATION_THICKNESS (if insulated conductor) Thickness of the insulation.
 */
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
