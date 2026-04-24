/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramDatabaseTablesTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `contains base tables`() {
        val tables = DiagramDatabaseTables().tables.keys
        val baseTables = (object : CimDatabaseTables() {
            override val sqlGenerator: SqlGenerator
                // Unused by CimDatabaseTables internally, so just return a mockk with no configuration that will break if it is actually used.
                get() = mockk()
        }).tables.keys

        assertThat("should contain all base tables", tables.containsAll(baseTables))
    }

}
