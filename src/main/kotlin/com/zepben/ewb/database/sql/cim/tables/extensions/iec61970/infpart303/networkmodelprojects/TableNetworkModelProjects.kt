/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING
import com.zepben.ewb.database.sql.common.tables.Column.Type.TIMESTAMP

/**
 * A class representing the NetworkModelProject columns required for the database table.
 *
 * @property EXTERNAL_STATUS A column storing the status of the project in the external system.
 * @property FORECAST_COMMISSION_DATE A column storing when the project is expected to be commissioned.
 * @property EXTERNAL_DRIVER A column storing the driver of the project.
 */
@Suppress("PropertyName")
class TableNetworkModelProjects : TableNetworkModelProjectComponents() {

    val EXTERNAL_STATUS: Column = Column(++columnIndex, "external_status", STRING, NULL)
    val FORECAST_COMMISSION_DATE: Column = Column(++columnIndex, "forecast_commission_date", TIMESTAMP, NULL)
    val EXTERNAL_DRIVER: Column = Column(++columnIndex, "external_driver", STRING, NULL)

    override val name: String = "network_model_projects"

}
