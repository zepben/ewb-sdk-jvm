/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.infiec61968.infassetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PotentialTransformerInfo` columns required for the database table.
 *
 * @property ACCURACY_CLASS PT accuracy classification.
 * @property NOMINAL_RATIO_DENOMINATOR The denominator of the nominal ratio between the primary and secondary voltage.
 * @property NOMINAL_RATIO_NUMERATOR The numerator of the nominal ratio between the primary and secondary voltage.
 * @property PRIMARY_RATIO Ratio for the primary winding tap changer (numerator).
 * @property PT_CLASS Potential transformer (PT) classification covering burden.
 * @property RATED_VOLTAGE Rated voltage on the primary side in Volts.
 * @property SECONDARY_RATIO Ratio for the secondary winding tap changer (denominator).
 */
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
