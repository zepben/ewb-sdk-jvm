/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.core

class TableConnectivityNodes : TableIdentifiedObjects() {

    override fun name(): String {
        return "connectivity_nodes"
    }

    override val tableClass: Class<TableConnectivityNodes> = this.javaClass
    override val tableClassInstance: TableConnectivityNodes = this

}
