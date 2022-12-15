/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableRecloseSequences : TableIdentifiedObjects() {

    val PROTECTED_SWITCH_MRID = Column(++columnIndex, "protected_switch_mrid", "TEXT", NULL)
    val RECLOSE_DELAY = Column(++columnIndex, "reclose_delay", "NUMBER", NULL)
    val RECLOSE_STEP = Column(++columnIndex, "reclose_step", "INTEGER", NULL)

    override fun name(): String {
        return "reclose_sequences"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
