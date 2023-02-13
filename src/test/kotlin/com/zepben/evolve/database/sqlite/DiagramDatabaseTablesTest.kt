/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.diagram.diagramDatabaseTables
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class DiagramDatabaseTablesTest {

    @Test
    fun `assert that DiagramDatabaseTable is a map to the classes of their table`() {

        diagramDatabaseTables.forEachTable { sqlTable ->
            val table = diagramDatabaseTables.tables[sqlTable::class.java]
            assertThat(table, instanceOf(sqlTable::class.java))
        }
    }

}
