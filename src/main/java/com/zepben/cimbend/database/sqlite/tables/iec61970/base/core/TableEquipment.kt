/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core

import com.zepben.cimbend.database.Column

@Suppress("PropertyName")
abstract class TableEquipment : TablePowerSystemResources() {

    val NORMALLY_IN_SERVICE = Column(++columnIndex, "normally_in_service", "BOOLEAN")
    val IN_SERVICE = Column(++columnIndex, "in_service", "BOOLEAN")

}
