/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
class TableEnergyConsumerPhases : TablePowerSystemResources() {

    val ENERGY_CONSUMER_MRID: Column = Column(++columnIndex, "energy_consumer_mrid", "TEXT", NOT_NULL)
    val PHASE: Column = Column(++columnIndex, "phase", "TEXT", NOT_NULL)
    val P: Column = Column(++columnIndex, "p", "NUMBER", NULL)
    val Q: Column = Column(++columnIndex, "q", "NUMBER", NULL)
    val P_FIXED: Column = Column(++columnIndex, "p_fixed", "NUMBER", NULL)
    val Q_FIXED: Column = Column(++columnIndex, "q_fixed", "NUMBER", NULL)

    override val name: String = "energy_consumer_phases"

    init {
        addUniqueIndexes(
            listOf(ENERGY_CONSUMER_MRID, PHASE)
        )

        addNonUniqueIndexes(
            listOf(ENERGY_CONSUMER_MRID)
        )
    }

}
