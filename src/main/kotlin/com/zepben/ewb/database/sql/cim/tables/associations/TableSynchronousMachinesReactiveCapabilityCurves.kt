/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between SynchronousMachines and ReactiveCapabilityCurves.
 *
 * @property SYNCHRONOUS_MACHINE_MRID The mRID of SynchronousMachines.
 * @property REACTIVE_CAPABILITY_CURVE_MRID The mRID of ReactiveCapabilityCurves.
 */
@Suppress("PropertyName")
class TableSynchronousMachinesReactiveCapabilityCurves : SqlTable() {

    val SYNCHRONOUS_MACHINE_MRID: Column = Column(++columnIndex, "synchronous_machine_mrid", Column.Type.STRING, NOT_NULL)
    val REACTIVE_CAPABILITY_CURVE_MRID: Column = Column(++columnIndex, "reactive_capability_curve_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "synchronous_machines_reactive_capability_curves"

    init {
        addUniqueIndexes(
            listOf(SYNCHRONOUS_MACHINE_MRID, REACTIVE_CAPABILITY_CURVE_MRID)
        )

        addNonUniqueIndexes(
            listOf(SYNCHRONOUS_MACHINE_MRID),
            listOf(REACTIVE_CAPABILITY_CURVE_MRID)
        )
    }

}
