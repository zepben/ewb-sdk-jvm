/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.generators.SqlGenerator
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class VariantDatabaseTablesTest {

    @Test
    internal fun `contains base tables`() {
        val tables = VariantDatabaseTables().tables.keys
        val baseTables = (object : BaseDatabaseTables() {
            override val sqlGenerator: SqlGenerator
                get() = mockk()
        }).tables.keys

        assertThat("should contain all base tables", tables.containsAll(baseTables))
    }

}
