/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableEnergyConsumers : TableEnergyConnections() {

    val CUSTOMER_COUNT = Column(++columnIndex, "customer_count", "INTEGER", NOT_NULL)
    val GROUNDED = Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL)
    val P = Column(++columnIndex, "p", "NUMBER", NOT_NULL)
    val Q = Column(++columnIndex, "q", "NUMBER", NOT_NULL)
    val P_FIXED = Column(++columnIndex, "p_fixed", "NUMBER", NOT_NULL)
    val Q_FIXED = Column(++columnIndex, "q_fixed", "NUMBER", NOT_NULL)
    val PHASE_CONNECTION = Column(++columnIndex, "phase_connection", "TEXT", NOT_NULL)

    override fun name(): String {
        return "energy_consumers"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
