/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.associations

import com.zepben.evolve.database.sqlite.cim.tables.Column
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable

/**
 * A class representing the association between SynchronousMachines and ReactiveCapabilityCurves.
 *
 * @property SYNCHRONOUS_MACHINE_MRID A column storing the mRID of SynchronousMachines.
 * @property REACTIVE_CAPABILITY_CURVE_MRID A column storing the mRID of ReactiveCapabilityCurves.
 */
@Suppress("PropertyName")
class TableSynchronousMachinesReactiveCapabilityCurves : SqliteTable() {

    val SYNCHRONOUS_MACHINE_MRID: Column = Column(++columnIndex, "synchronous_machine_mrid", "TEXT", NOT_NULL)
    val REACTIVE_CAPABILITY_CURVE_MRID: Column = Column(++columnIndex, "reactive_capability_curve_mrid", "TEXT", NOT_NULL)

    override val name: String = "synchronous_machines_reactive_capability_curves"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(SYNCHRONOUS_MACHINE_MRID, REACTIVE_CAPABILITY_CURVE_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(SYNCHRONOUS_MACHINE_MRID))
            add(listOf(REACTIVE_CAPABILITY_CURVE_MRID))
        }

}
