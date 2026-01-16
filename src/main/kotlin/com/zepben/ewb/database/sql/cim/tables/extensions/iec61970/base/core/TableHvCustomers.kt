/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.core

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableEquipmentContainers

/**
 * A class representing the `HvCustomer` columns required for the database table.
 */
class TableHvCustomers : TableEquipmentContainers() {

    override val name: String = "hv_customers"
}
