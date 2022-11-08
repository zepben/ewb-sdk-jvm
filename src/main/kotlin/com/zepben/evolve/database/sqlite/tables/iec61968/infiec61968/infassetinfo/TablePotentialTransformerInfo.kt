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
class TablePotentialTransformerInfo : TableAssetInfo() {

    val ACCURACY_CLASS = Column(++columnIndex, "accuracy_class", "TEXT", NULL)
    val NOMINAL_RATIO_DENOMINATOR = Column(++columnIndex, "nominal_ratio_denominator", "NUMBER", NULL)
    val NOMINAL_RATIO_NUMERATOR = Column(++columnIndex, "nominal_ratio_numerator", "NUMBER", NULL)
    val PRIMARY_RATIO = Column(++columnIndex, "primary_ratio", "NUMBER", NULL)
    val PT_CLASS = Column(++columnIndex, "pt_class", "TEXT", NULL)
    val RATED_VOLTAGE = Column(++columnIndex, "rated_voltage", "INTEGER", NULL)
    val SECONDARY_RATIO = Column(++columnIndex, "secondary_ratio", "NUMBER", NULL)

    override fun name(): String {
        return "potential_transformer_info"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}