/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.diagram

import com.zepben.ewb.database.sqlite.cim.CimDatabaseTables
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class DiagramDatabaseTablesTest {

    @Test
    internal fun `contains base tables`() {
        val tables = DiagramDatabaseTables().tables.keys
        val baseTables = (object : CimDatabaseTables() {}).tables.keys

        assertThat("should contain all base tables", tables.containsAll(baseTables))
    }

}
