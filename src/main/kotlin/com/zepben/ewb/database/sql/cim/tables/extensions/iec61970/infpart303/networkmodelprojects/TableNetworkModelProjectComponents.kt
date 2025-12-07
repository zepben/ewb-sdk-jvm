/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column

/**
 * A class representing the NetworkModelProjectComponent columns required for the database table.
 *
 * @property CREATED A column storing when the component was created.
 * @property UPDATED A column storing when the component was last updated.
 * @property CLOSED A column storing when the component was deleted.
 * @property PARENT_MRID A column storing the mRID of the contained NetworkModelProjectComponent.
 */
@Suppress("PropertyName")
abstract class TableNetworkModelProjectComponents : TableIdentifiedObjects() {
    val CREATED: Column = Column(++columnIndex, "created", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val UPDATED: Column = Column(++columnIndex, "updated", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val CLOSED: Column = Column(++columnIndex, "closed", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val PARENT_MRID: Column = Column(++columnIndex, "parent_mrid", Column.Type.STRING, Column.Nullable.NULL)
}
