/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo

class TableCableInfo : TableWireInfo() {

    override fun name(): String {
        return "cable_info"
    }

    override val tableClass: Class<TableCableInfo> = this.javaClass
    override val tableClassInstance: TableCableInfo = this

}
