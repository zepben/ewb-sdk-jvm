/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable

@Suppress("PropertyName")
class TableNetworkModelProjects : TableNetworkModelProjectComponents() {

    val EXTERNAL_STATUS: Column = Column(++columnIndex, "external_status", Column.Type.STRING, Nullable.NULL)
    val FORECAST_COMMISSION_DATE: Column = Column(++columnIndex, "forecast_commission_date", Column.Type.TIMESTAMP, Nullable.NULL)
    val EXTERNAL_DRIVER: Column = Column(++columnIndex, "external_driver", Column.Type.STRING, Nullable.NULL)

    override val name: String = "network_model_projects"

}
