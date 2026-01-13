/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

/**
 * A class representing the `BusbarSection` columns required for the database table.
 */
class TableBusbarSections : TableConnectors() {

    override val name: String = "busbar_sections"

}
