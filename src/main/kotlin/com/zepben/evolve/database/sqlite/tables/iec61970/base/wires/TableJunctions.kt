/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

class TableJunctions : TableConnectors() {

    override fun name(): String {
        return "junctions"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
