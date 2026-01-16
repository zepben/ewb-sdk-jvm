/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61968.assetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `RelayInfo` columns required for the database table.
 *
 * @property CURVE_SETTING The type of curve used for the Relay.
 * @property RECLOSE_FAST True if recloseDelays are associated with a fast Curve, false otherwise.
 */
@Suppress("PropertyName")
class TableRelayInfo : TableAssetInfo() {

    val CURVE_SETTING: Column = Column(++columnIndex, "curve_setting", Column.Type.STRING, NULL)
    val RECLOSE_FAST: Column = Column(++columnIndex, "reclose_fast", Column.Type.BOOLEAN, NULL)

    override val name: String = "relay_info"

}
