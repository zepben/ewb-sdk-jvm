/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ShuntCompensatorInfo` columns required for the database table.
 *
 * @property MAX_POWER_LOSS Maximum allowed apparent power loss in watts.
 * @property RATED_CURRENT Rated current in amperes.
 * @property RATED_REACTIVE_POWER Rated reactive power in volt-amperes reactive.
 * @property RATED_VOLTAGE Rated voltage in volts.
 */
@Suppress("PropertyName")
class TableShuntCompensatorInfo : TableAssetInfo() {

    val MAX_POWER_LOSS: Column = Column(++columnIndex, "max_power_loss", Column.Type.INTEGER, NULL)
    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Column.Type.INTEGER, NULL)
    val RATED_REACTIVE_POWER: Column = Column(++columnIndex, "rated_reactive_power", Column.Type.INTEGER, NULL)
    val RATED_VOLTAGE: Column = Column(++columnIndex, "rated_voltage", Column.Type.INTEGER, NULL)

    override val name: String = "shunt_compensator_info"

}
