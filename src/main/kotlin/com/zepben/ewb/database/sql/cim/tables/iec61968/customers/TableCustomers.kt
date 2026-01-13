/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.customers

import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableOrganisationRoles
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the customer columns required for the database table.
 *
 * @property KIND The customer kind.
 * @property NUM_END_DEVICES The number of end devices related to this customer.
 * @property SPECIAL_NEED The special needs of this customer.
 */
@Suppress("PropertyName")
class TableCustomers : TableOrganisationRoles() {

    val KIND: Column = Column(++columnIndex, "kind", Column.Type.STRING, NOT_NULL)
    val NUM_END_DEVICES: Column = Column(++columnIndex, "num_end_devices", Column.Type.INTEGER, NULL)
    val SPECIAL_NEED: Column = Column(++columnIndex, "special_need", Column.Type.STRING, NULL)

    override val name: String = "customers"

}
