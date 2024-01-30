/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
abstract class TableRegulatingControls : TablePowerSystemResources() {

    val DISCRETE = Column(++columnIndex, "discrete", "BOOLEAN", NULL)
    val MODE = Column(++columnIndex, "mode", "TEXT", NOT_NULL)
    val MONITORED_PHASE = Column(++columnIndex, "monitored_phase", "TEXT", NOT_NULL)
    val TARGET_DEADBAND = Column(++columnIndex, "target_deadband", "NUMBER", NULL)
    val TARGET_VALUE = Column(++columnIndex, "target_value", "NUMBER", NULL)
    val ENABLED = Column(++columnIndex, "enabled", "BOOLEAN", NULL)
    val MAX_ALLOWED_TARGET_VALUE = Column(++columnIndex, "max_allowed_target_value", "NUMBER", NULL)
    val MIN_ALLOWED_TARGET_VALUE = Column(++columnIndex, "min_allowed_target_value", "NUMBER", NULL)
    val RATED_CURRENT = Column(++columnIndex, "rated_current", "NUMBER", NULL)
    val TERMINAL_MRID = Column(++columnIndex, "terminal_mrid", "TEXT", NULL)

}
