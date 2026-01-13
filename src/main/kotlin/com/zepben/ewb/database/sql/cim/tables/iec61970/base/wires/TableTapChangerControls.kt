/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TapChangerControl` columns required for the database table.
 *
 * @property LIMIT_VOLTAGE Maximum allowed regulated voltage on the PT secondary, regardless of line drop compensation. Sometimes referred to as first-house protection.
 * @property LINE_DROP_COMPENSATION If true, then line drop compensation is to be applied.
 * @property LINE_DROP_R Line drop compensator resistance setting for normal (forward) power flow in Ohms.
 * @property LINE_DROP_X Line drop compensator reactance setting for normal (forward) power flow in Ohms.
 * @property REVERSE_LINE_DROP_R Line drop compensator resistance setting for reverse power flow in Ohms.
 * @property REVERSE_LINE_DROP_X Line drop compensator reactance setting for reverse power flow in Ohms.
 * @property FORWARD_LDC_BLOCKING True implies this tap changer turns off/ignores reverse current flows for line drop compensation when power flow is reversed and
 * no reverse line drop is set.
 * @property TIME_DELAY The time delay for the tap changer in seconds.
 * @property CO_GENERATION_ENABLED True implies cogeneration mode is enabled and that the control will regulate to the new source bushing (downline bushing),
 * keeping locations downline from experiencing overvoltage situations.
 */
@Suppress("PropertyName")
class TableTapChangerControls : TableRegulatingControls() {

    val LIMIT_VOLTAGE: Column = Column(++columnIndex, "limit_voltage", Column.Type.INTEGER, NULL)
    val LINE_DROP_COMPENSATION: Column = Column(++columnIndex, "line_drop_compensation", Column.Type.BOOLEAN, NULL)
    val LINE_DROP_R: Column = Column(++columnIndex, "line_drop_r", Column.Type.DOUBLE, NULL)
    val LINE_DROP_X: Column = Column(++columnIndex, "line_drop_x", Column.Type.DOUBLE, NULL)
    val REVERSE_LINE_DROP_R: Column = Column(++columnIndex, "reverse_line_drop_r", Column.Type.DOUBLE, NULL)
    val REVERSE_LINE_DROP_X: Column = Column(++columnIndex, "reverse_line_drop_x", Column.Type.DOUBLE, NULL)
    val FORWARD_LDC_BLOCKING: Column = Column(++columnIndex, "forward_ldc_blocking", Column.Type.BOOLEAN, NULL)
    val TIME_DELAY: Column = Column(++columnIndex, "time_delay", Column.Type.DOUBLE, NULL)
    val CO_GENERATION_ENABLED: Column = Column(++columnIndex, "co_generation_enabled", Column.Type.BOOLEAN, NULL)

    override val name: String = "tap_changer_controls"

}
