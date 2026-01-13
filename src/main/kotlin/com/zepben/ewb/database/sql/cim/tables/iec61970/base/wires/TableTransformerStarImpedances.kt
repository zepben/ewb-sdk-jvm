/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TransformerStarImpedance` columns required for the database table.
 *
 * @property R Resistance of the transformer end in ohms.
 * @property R0 Zero sequence series resistance of the transformer end in ohms.
 * @property X Positive sequence series reactance of the transformer end in ohms.
 * @property X0 Zero sequence series reactance of the transformer end in ohms.
 * @property TRANSFORMER_END_INFO_MRID Transformer end datasheet used to calculate this transformer star impedance.
 */
@Suppress("PropertyName")
class TableTransformerStarImpedances : TableIdentifiedObjects() {

    val R: Column = Column(++columnIndex, "R", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "R0", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "X", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "X0", Column.Type.DOUBLE, NULL)
    val TRANSFORMER_END_INFO_MRID: Column = Column(++columnIndex, "transformer_end_info_mrid", Column.Type.STRING, NULL)

    override val name: String = "transformer_star_impedances"

    init {
        addUniqueIndexes(
            listOf(TRANSFORMER_END_INFO_MRID)
        )
    }

}
