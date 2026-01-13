/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assets

import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Streetlight` columns required for the database table.
 *
 * @property POLE_MRID The [Pole] this [Streetlight] is attached to.
 * @property LIGHT_RATING Power rating of light
 * @property LAMP_KIND The kind of lamp
 */
@Suppress("PropertyName")
class TableStreetlights : TableAssets() {

    val POLE_MRID: Column = Column(++columnIndex, "pole_mrid", Column.Type.STRING, NULL)
    val LAMP_KIND: Column = Column(++columnIndex, "lamp_kind", Column.Type.STRING, NOT_NULL)
    val LIGHT_RATING: Column = Column(++columnIndex, "light_rating", Column.Type.INTEGER, NULL)

    override val name: String = "streetlights"

}
