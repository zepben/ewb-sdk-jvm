/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.customers

import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableDocuments

class TableTariffs : TableDocuments() {

    override fun name(): String {
        return "tariffs"
    }

    override val tableClass: Class<TableTariffs> = this.javaClass
    override val tableClassInstance: TableTariffs = this

}
