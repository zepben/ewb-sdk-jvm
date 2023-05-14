/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableTapChangerControls : TableRegulatingControls() {

    val LIMIT_VOLTAGE = Column(++columnIndex, "limit_voltage", "NUMBER", NULL)
    val LINE_DROP_COMPENSATION = Column(++columnIndex, "line_drop_compensation", "BOOLEAN", NULL)
    val LINE_DROP_R = Column(++columnIndex, "line_drop_r", "NUMBER", NULL)
    val LINE_DROP_X = Column(++columnIndex, "line_drop_x", "NUMBER", NULL)
    val REVERSE_LINE_DROP_R = Column(++columnIndex, "reverse_line_drop_r", "NUMBER", NULL)
    val REVERSE_LINE_DROP_X = Column(++columnIndex, "reverse_line_drop_x", "NUMBER", NULL)
    val FORWARD_LDC_BLOCKING = Column(++columnIndex, "forward_ldc_blocking", "BOOLEAN", NULL)
    val TIME_DELAY = Column(++columnIndex, "time_delay", "NUMBER", NULL)
    val CO_GENERATION_ENABLED = Column(++columnIndex, "co_generation_enabled", "BOOLEAN", NULL)

    override fun name(): String {
        return "tap_changer_controls"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
