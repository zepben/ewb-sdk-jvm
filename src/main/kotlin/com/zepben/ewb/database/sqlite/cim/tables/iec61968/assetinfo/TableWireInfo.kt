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

/**
 * Wire data that can be specified per line segment phase, or for the line segment as a whole in case its phases all have the same wire characteristics.
 *
 * @property ratedCurrent Current carrying capacity of the wire under stated thermal conditions.
 * @property material Conductor material.
 * @property sizeDescription Describes the wire gauge or cross section (e.g., 4/0,
 * @property strandCount Number of strands in the conductor.
 * @property coreStrandCount (if used) Number of strands in the steel core.
 * @property insulated True if conductor is insulated.
 * @property insulatationMaterial (if insulated conductor) Material used for insulation.
 * @property insulatationThickness (if insulated conductor) Thickness of the insulation.
 */
@Suppress("PropertyName")
abstract class TableWireInfo : TableAssetInfo() {

    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Type.DOUBLE, NULL)
    val MATERIAL: Column = Column(++columnIndex, "material", Type.STRING, NOT_NULL)
    val SIZE_DESCRIPTION: Column = Column(++columnIndex, "size_description", Type.STRING, NULL)
    val STRAND_COUNT: Column = Column(++columnIndex, "strand_count", Type.STRING, NULL)
    val CORE_STRAND_COUNT: Column = Column(++columnIndex, "core_strand_count", Type.STRING, NULL)
    val INSULATED: Column = Column(++columnIndex, "insulated", Type.BOOLEAN, NULL)
    val INSULATATION_MATERIAL: Column = Column(++columnIndex, "insulatation_material", Type.STRING, NOT_NULL)
    val INSULATATION_THICKNESS: Column = Column(++columnIndex, "insulatation_thickness", Type.DOUBLE, NULL)
}
