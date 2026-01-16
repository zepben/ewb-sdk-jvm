/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.metering

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetContainers
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `EndDevice` columns required for the database table.
 *
 * @property CUSTOMER_MRID Customer owning this end device.
 * @property SERVICE_LOCATION_MRID Service location whose service delivery is measured by this end device.
 */
@Suppress("PropertyName")
abstract class TableEndDevices : TableAssetContainers() {

    val CUSTOMER_MRID: Column = Column(++columnIndex, "customer_mrid", Column.Type.STRING, NULL)
    val SERVICE_LOCATION_MRID: Column = Column(++columnIndex, "service_location_mrid", Column.Type.STRING, NULL)

}
