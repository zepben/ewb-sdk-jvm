/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.meas

import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Measurement` columns required for the database table.
 *
 * @property POWER_SYSTEM_RESOURCE_MRID The MRID of the power system resource that contains the measurement.
 * @property REMOTE_SOURCE_MRID The [RemoteSource] taking the measurement.
 * @property TERMINAL_MRID A measurement may be associated with a terminal in the network.
 * @property PHASES Indicates to which phases the measurement applies and avoids the need to use 'measurementType'
 *                  to also encode phase information (which would explode the types). The phase information in
 *                  Measurement, along with 'measurementType' and 'phases' uniquely defines a Measurement for a device,
 *                  based on normal network phase. Their meaning will not change when the computed energizing phasing
 *                  is changed due to jumpers or other reasons. If the attribute is missing three phases (ABC) shall
 *                  be assumed.
 * @property UNIT_SYMBOL Specifies the type of measurement.  For example, this specifies if the measurement represents
 *                      an indoor temperature, outdoor temperature, bus voltage, line flow, etc.
 *                      When the measurementType is set to "Specialization", the type of Measurement is defined in
 *                      more detail by the specialized class which inherits from Measurement.
 */
@Suppress("PropertyName")
abstract class TableMeasurements : TableIdentifiedObjects() {

    val POWER_SYSTEM_RESOURCE_MRID: Column = Column(++columnIndex, "power_system_resource_mrid", Column.Type.STRING, NULL)
    val REMOTE_SOURCE_MRID: Column = Column(++columnIndex, "remote_source_mrid", Column.Type.STRING, NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)
    val PHASES: Column = Column(++columnIndex, "phases", Column.Type.STRING, NOT_NULL)
    val UNIT_SYMBOL: Column = Column(++columnIndex, "unit_symbol", Column.Type.STRING, NOT_NULL)

    init {
        addNonUniqueIndexes(
            listOf(POWER_SYSTEM_RESOURCE_MRID),
            listOf(REMOTE_SOURCE_MRID),
            listOf(TERMINAL_MRID)
        )
    }

}
