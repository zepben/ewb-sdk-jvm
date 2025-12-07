/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.postgres.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable

@Suppress("PropertyName")
abstract class TableNetworkModelProjectComponents : TableIdentifiedObjects() {
    val CREATED: Column = Column(++columnIndex, "created", Column.Type.TIMESTAMP, Nullable.NOT_NULL)
    val UPDATED: Column = Column(++columnIndex, "updated", Column.Type.TIMESTAMP, Nullable.NULL)
    val CLOSED: Column = Column(++columnIndex, "closed", Column.Type.TIMESTAMP, Nullable.NULL)
    val PARENT_MRID: Column = Column(++columnIndex, "parent_mrid", Column.Type.STRING, Nullable.NULL)
}