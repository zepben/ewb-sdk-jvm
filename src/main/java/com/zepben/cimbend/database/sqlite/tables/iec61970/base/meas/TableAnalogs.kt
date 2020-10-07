/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL

/**
 * Represents the analogs table
 */
class TableAnalogs : TableMeasurements() {
    val POSITIVE_FLOW_IN = Column(
        ++columnIndex,
        "positive_flow_in",
        "BOOLEAN",
        NOT_NULL
    )

    override fun name(): String {
        return "analogs"
    }

    override val tableClass: Class<*>
        get() = TableAnalogs::class.java
    override val tableClassInstance: Any
        get() = this
}