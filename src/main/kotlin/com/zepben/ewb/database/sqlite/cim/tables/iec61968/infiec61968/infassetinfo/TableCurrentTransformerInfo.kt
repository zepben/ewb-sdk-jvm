/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableCurrentTransformerInfo : TableAssetInfo() {

    val ACCURACY_CLASS: Column = Column(++columnIndex, "accuracy_class", Column.Type.STRING, NULL)
    val ACCURACY_LIMIT: Column = Column(++columnIndex, "accuracy_limit", Column.Type.DOUBLE, NULL)
    val CORE_COUNT: Column = Column(++columnIndex, "core_count", Column.Type.INTEGER, NULL)
    val CT_CLASS: Column = Column(++columnIndex, "ct_class", Column.Type.STRING, NULL)
    val KNEE_POINT_VOLTAGE: Column = Column(++columnIndex, "knee_point_voltage", Column.Type.INTEGER, NULL)
    val MAX_RATIO_DENOMINATOR: Column = Column(++columnIndex, "max_ratio_denominator", Column.Type.DOUBLE, NULL)
    val MAX_RATIO_NUMERATOR: Column = Column(++columnIndex, "max_ratio_numerator", Column.Type.DOUBLE, NULL)
    val NOMINAL_RATIO_DENOMINATOR: Column = Column(++columnIndex, "nominal_ratio_denominator", Column.Type.DOUBLE, NULL)
    val NOMINAL_RATIO_NUMERATOR: Column = Column(++columnIndex, "nominal_ratio_numerator", Column.Type.DOUBLE, NULL)
    val PRIMARY_RATIO: Column = Column(++columnIndex, "primary_ratio", Column.Type.DOUBLE, NULL)
    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Column.Type.INTEGER, NULL)
    val SECONDARY_FLS_RATING: Column = Column(++columnIndex, "secondary_fls_rating", Column.Type.INTEGER, NULL)
    val SECONDARY_RATIO: Column = Column(++columnIndex, "secondary_ratio", Column.Type.DOUBLE, NULL)
    val USAGE: Column = Column(++columnIndex, "usage", Column.Type.STRING, NULL)

    override val name: String = "current_transformer_info"

}
