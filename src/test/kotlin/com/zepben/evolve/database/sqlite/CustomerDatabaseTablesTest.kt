/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.customer.customerDatabaseTables
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class CustomerDatabaseTablesTest {

    @Test
    fun `assert that CustomerDatabaseTable is a map to the classes of their table`() {

        customerDatabaseTables.forEachTable { sqlTable ->
            val table = customerDatabaseTables.tables[sqlTable::class.java]
            assertThat(table, instanceOf(sqlTable::class.java))
        }
    }

}
