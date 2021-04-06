/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.assets

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableStreetlights : TableAssets() {

    val POLE_MRID = Column(++columnIndex, "pole_mrid", "TEXT", NULL)
    val LAMP_KIND = Column(++columnIndex, "lamp_kind", "TEXT", NOT_NULL)
    val LIGHT_RATING = Column(++columnIndex, "light_rating", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "streetlights"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
