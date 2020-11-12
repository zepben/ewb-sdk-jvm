/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.metering

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.TableAssetContainers

@Suppress("PropertyName")
abstract class TableEndDevices : TableAssetContainers() {

    val CUSTOMER_MRID = Column(++columnIndex, "customer_mrid", "TEXT", NULL)
    val SERVICE_LOCATION_MRID = Column(++columnIndex, "service_location_mrid", "TEXT", NULL)

}
