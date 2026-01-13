/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING

/**
 * A class representing the ElectronicAddress columns required for the database table.
 *
 * @property POSTAL_CODE The postal code for the address.
 * @property PO_BOX The post office box.
 * @property BUILDING_NAME The name of a building.
 * @property FLOOR_IDENTIFICATION The identification by name or number, expressed as text, of the floor in the building as part of this address.
 * @property STREET_NAME The name of the street.
 * @property NUMBER The designator of the specific location on the street.
 * @property SUITE_NUMBER The number of the apartment or suite.
 * @property TYPE The type of street. Examples include: street, circle, boulevard, avenue, road, drive, etc.
 * @property DISPLAY_ADDRESS The address as it should be displayed to a user.
 * @property BUILDING_NUMBER The number of the building.
 */
@Suppress("PropertyName")
abstract class TableStreetAddresses : TableTownDetails() {

    val POSTAL_CODE: Column = Column(++columnIndex, "postal_code", STRING, NULL)
    val PO_BOX: Column = Column(++columnIndex, "po_box", STRING, NULL)
    val BUILDING_NAME: Column = Column(++columnIndex, "building_name", STRING, NULL)
    val FLOOR_IDENTIFICATION: Column = Column(++columnIndex, "floor_identification", STRING, NULL)
    val STREET_NAME: Column = Column(++columnIndex, "name", STRING, NULL)
    val NUMBER: Column = Column(++columnIndex, "number", STRING, NULL)
    val SUITE_NUMBER: Column = Column(++columnIndex, "suite_number", STRING, NULL)
    val TYPE: Column = Column(++columnIndex, "type", STRING, NULL)
    val DISPLAY_ADDRESS: Column = Column(++columnIndex, "display_address", STRING, NULL)
    val BUILDING_NUMBER: Column = Column(++columnIndex, "building_number", STRING, NULL)

}
