/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.metering

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `UsagePoint` columns required for the database table.
 *
 * @property LOCATION_MRID Service location where the service delivered by this usage point is consumed.
 * @property IS_VIRTUAL If true, this usage point is virtual, i.e., no physical location exists in the network where a meter could be located to
 *                     collect the meter readings. For example, one may define a virtual usage point to serve as an aggregation of usage for all
 *                     of a company's premises distributed widely across the distribution territory. Otherwise, the usage point is physical,
 *                     i.e., there is a logical point in the network where a meter could be located to collect meter readings.
 * @property CONNECTION_CATEGORY A code used to specify the connection category, e.g., low voltage or low pressure, where the usage point is defined.
 * @property RATED_POWER Active power that this usage point is configured to deliver in watts.
 * @property APPROVED_INVERTER_CAPACITY The approved inverter capacity at this UsagePoint in volt-amperes.
 * @property PHASE_CODE Phase code. Number of wires and specific nominal phases can be deduced from enumeration literal values. For example, ABCN is three-phase,
 *                     four-wire, s12n (splitSecondary12N) is single-phase, three-wire, and s1n and s2n are single-phase, two-wire.
 */
@Suppress("PropertyName")
class TableUsagePoints : TableIdentifiedObjects() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", Column.Type.STRING, NULL)
    val IS_VIRTUAL: Column = Column(++columnIndex, "is_virtual", Column.Type.BOOLEAN, NULL)
    val CONNECTION_CATEGORY: Column = Column(++columnIndex, "connection_category", Column.Type.STRING, NULL)
    val RATED_POWER: Column = Column(++columnIndex, "rated_power", Column.Type.INTEGER, NULL)
    val APPROVED_INVERTER_CAPACITY: Column = Column(++columnIndex, "approved_inverter_capacity", Column.Type.INTEGER, NULL)
    val PHASE_CODE: Column = Column(++columnIndex, "phase_code", Column.Type.STRING, NOT_NULL)

    override val name: String = "usage_points"

}
