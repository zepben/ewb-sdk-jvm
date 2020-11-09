/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.customers

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisationRoles

@Suppress("PropertyName")
class TableCustomers : TableOrganisationRoles() {

    val KIND = Column(++columnIndex, "kind", "TEXT", NOT_NULL)
    val NUM_END_DEVICES = Column(++columnIndex, "num_end_devices", "INTEGER", NOT_NULL)

    override fun name(): String {
        return "customers"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
