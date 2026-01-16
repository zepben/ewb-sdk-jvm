/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableConductingEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `SeriesCompensator` columns required for the database table.
 *
 * @property R Positive sequence resistance in ohms.
 * @property R0 Zero sequence resistance in ohms.
 * @property X Positive sequence reactance in ohms.
 * @property X0 Zero sequence reactance in ohms.
 * @property VARISTOR_RATED_CURRENT The maximum current in amps the varistor is designed to handle at specified duration. It is used for short circuit
 * calculations. The attribute shall be a positive value. If null and varistorVoltageThreshold is null, a varistor is not present.
 * @property VARISTOR_VOLTAGE_THRESHOLD The dc voltage in volts at which the varistor starts conducting. It is used for short circuit calculations. If null and
 * varistorRatedCurrent is null, a varistor is not present.
 */
@Suppress("PropertyName")
class TableSeriesCompensators : TableConductingEquipment() {

    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "r0", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "x0", Column.Type.DOUBLE, NULL)
    val VARISTOR_RATED_CURRENT: Column = Column(++columnIndex, "varistor_rated_current", Column.Type.INTEGER, NULL)
    val VARISTOR_VOLTAGE_THRESHOLD: Column = Column(++columnIndex, "varistor_voltage_threshold", Column.Type.INTEGER, NULL)

    override val name: String = "series_compensators"

}
