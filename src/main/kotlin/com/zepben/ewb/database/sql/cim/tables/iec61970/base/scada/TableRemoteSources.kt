/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.scada

import com.zepben.ewb.cim.iec61970.base.meas.Measurement
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `RemoteSource` columns required for the database table.
 *
 * @property MEASUREMENT_MRID The [Measurement] for the [RemoteSource] point.
 */
@Suppress("PropertyName")
class TableRemoteSources : TableRemotePoints() {

    val MEASUREMENT_MRID: Column = Column(++columnIndex, "measurement_mrid", Column.Type.STRING, NULL)

    override val name: String = "remote_sources"

}
