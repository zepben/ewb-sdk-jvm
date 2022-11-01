/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableCurrentTransformerInfo : TableAssetInfo() {

    val ACCURACY_CLASS = Column(++columnIndex, "accuracy_class", "TEXT", NULL)
    val ACCURACY_LIMIT = Column(++columnIndex, "accuracy_limit", "NUMBER", NULL)
    val CORE_COUNT = Column(++columnIndex, "core_count", "INTEGER", NULL)
    val CT_CLASS = Column(++columnIndex, "ct_class", "TEXT", NULL)
    val KNEE_POINT_VOLTAGE = Column(++columnIndex, "knee_point_voltage", "INTEGER", NULL)
    val MAX_RATIO_DENOMINATOR = Column(++columnIndex, "max_ratio_denominator", "NUMBER", NULL)
    val MAX_RATIO_NUMERATOR = Column(++columnIndex, "max_ratio_numerator", "NUMBER", NULL)
    val NOMINAL_RATIO_DENOMINATOR = Column(++columnIndex, "nominal_ratio_denominator", "NUMBER", NULL)
    val NOMINAL_RATIO_NUMERATOR = Column(++columnIndex, "nominal_ratio_numerator", "NUMBER", NULL)
    val PRIMARY_RATIO = Column(++columnIndex, "primary_ratio", "NUMBER", NULL)
    val RATED_CURRENT = Column(++columnIndex, "rated_current", "INTEGER", NULL)
    val SECONDARY_FLS_RATING = Column(++columnIndex, "secondary_fls_rating", "INTEGER", NULL)
    val SECONDARY_RATIO = Column(++columnIndex, "secondary_ratio", "NUMBER", NULL)
    val USAGE = Column(++columnIndex, "usage", "TEXT", NULL)

    override fun name(): String {
        return "current_transformer_info"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
