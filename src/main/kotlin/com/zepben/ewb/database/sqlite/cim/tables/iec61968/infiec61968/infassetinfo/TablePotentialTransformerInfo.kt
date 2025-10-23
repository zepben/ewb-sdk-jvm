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
class TablePotentialTransformerInfo : TableAssetInfo() {

    val ACCURACY_CLASS: Column = Column(++columnIndex, "accuracy_class", Column.Type.STRING, NULL)
    val NOMINAL_RATIO_DENOMINATOR: Column = Column(++columnIndex, "nominal_ratio_denominator", Column.Type.DOUBLE, NULL)
    val NOMINAL_RATIO_NUMERATOR: Column = Column(++columnIndex, "nominal_ratio_numerator", Column.Type.DOUBLE, NULL)
    val PRIMARY_RATIO: Column = Column(++columnIndex, "primary_ratio", Column.Type.DOUBLE, NULL)
    val PT_CLASS: Column = Column(++columnIndex, "pt_class", Column.Type.STRING, NULL)
    val RATED_VOLTAGE: Column = Column(++columnIndex, "rated_voltage", Column.Type.INTEGER, NULL)
    val SECONDARY_RATIO: Column = Column(++columnIndex, "secondary_ratio", Column.Type.DOUBLE, NULL)

    override val name: String = "potential_transformer_info"

}
