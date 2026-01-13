/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.customers

import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableDocuments
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable
import com.zepben.ewb.database.sql.common.tables.Column.Type

/**
 * A class representing the `PricingStructure` columns required for the database table.
 *
 * @property CODE Unique user-allocated key for this pricing structure, used by company representatives to identify the correct price structure for allocating to a customer. For rate schedules it is often prefixed by a state code.
 */
@Suppress("PropertyName")
class TablePricingStructures : TableDocuments() {

    val CODE: Column = Column(++columnIndex, "code", Type.STRING, Nullable.NULL)

    override val name: String = "pricing_structures"

}
