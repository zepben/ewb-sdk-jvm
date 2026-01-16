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
 * A class representing the `CurrentTransformerInfo` columns required for the database table.
 *
 * @property ACCURACY_CLASS CT accuracy classification.
 * @property ACCURACY_LIMIT Accuracy limit.
 * @property CORE_COUNT Number of cores.
 * @property CT_CLASS CT classification; i.e. class 10P.
 * @property KNEE_POINT_VOLTAGE Maximum voltage in volts across the secondary terminals where the CT still displays linear characteristics.
 * @property MAX_RATIO_DENOMINATOR The denominator of the maximum ratio between the primary and secondary current.
 * @property MAX_RATIO_NUMERATOR The numerator of the maximum ratio between the primary and secondary current.
 * @property NOMINAL_RATIO_DENOMINATOR The denominator of the nominal ratio between the primary and secondary current; i.e. 100:5
 * @property NOMINAL_RATIO_NUMERATOR The numerator of the nominal ratio between the primary and secondary current; i.e. 100:5
 * @property PRIMARY_RATIO Ratio for the primary winding tap changer (numerator).
 * @property RATED_CURRENT Rated current on the primary side in amperes.
 * @property SECONDARY_FLS_RATING Full load secondary (FLS) rating for secondary winding in amperes.
 * @property SECONDARY_RATIO Ratio for the secondary winding tap changer (denominator).
 * @property USAGE Intended usage of the CT; i.e. metering, protection.
 */
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
