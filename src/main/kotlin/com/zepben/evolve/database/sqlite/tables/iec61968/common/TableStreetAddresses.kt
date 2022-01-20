/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.common

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
abstract class TableStreetAddresses : TableTownDetails() {

    val POSTAL_CODE = Column(++columnIndex, "postal_code", "TEXT", NOT_NULL)
    val PO_BOX = Column(++columnIndex, "po_box", "TEXT", NOT_NULL)
    val BUILDING_NAME = Column(++columnIndex, "building_name", "TEXT", NOT_NULL)
    val FLOOR_IDENTIFICATION = Column(++columnIndex, "floor_identification", "TEXT", NOT_NULL)
    val NAME = Column(++columnIndex, "name", "TEXT", NOT_NULL)
    val NUMBER = Column(++columnIndex, "number", "TEXT", NOT_NULL)
    val SUITE_NUMBER = Column(++columnIndex, "suite_number", "TEXT", NOT_NULL)
    val TYPE = Column(++columnIndex, "type", "TEXT", NOT_NULL)
    val DISPLAY_ADDRESS = Column(++columnIndex, "display_address", "TEXT", NOT_NULL)

}
