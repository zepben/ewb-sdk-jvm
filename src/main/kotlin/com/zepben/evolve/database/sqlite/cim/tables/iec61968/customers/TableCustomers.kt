/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61968.customers

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableOrganisationRoles

/**
 * A class representing the customer columns required for the database table.
 *
 * @property KIND A column storing the customer kind.
 * @property NUM_END_DEVICES A column storing the number of end devices related to this customer.
 * @property SPECIAL_NEED A column storing the special needs of this customer.
 */
@Suppress("PropertyName")
class TableCustomers : TableOrganisationRoles() {

    val KIND: Column = Column(++columnIndex, "kind", "TEXT", NOT_NULL)
    val NUM_END_DEVICES: Column = Column(++columnIndex, "num_end_devices", "INTEGER", NULL)
    val SPECIAL_NEED: Column = Column(++columnIndex, "special_need", "TEXT", NULL)

    override val name: String = "customers"

}
