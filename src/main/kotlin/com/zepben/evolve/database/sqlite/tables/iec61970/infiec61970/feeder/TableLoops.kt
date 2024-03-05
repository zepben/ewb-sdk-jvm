/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder

import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

class TableLoops : TableIdentifiedObjects() {

    override fun name(): String {
        return "loops"
    }

    override val tableClass: Class<TableLoops> = this.javaClass
    override val tableClassInstance: TableLoops = this

}
