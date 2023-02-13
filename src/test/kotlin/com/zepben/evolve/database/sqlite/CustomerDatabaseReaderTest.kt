/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.customer.CustomerDatabaseReader
import com.zepben.evolve.database.sqlite.customer.CustomerServiceReader
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

internal class CustomerDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val databaseFile = "databaseFile"
    private val resultSet = mock<ResultSet>()
    private val connectionResult = mockk<UpgradeRunner.ConnectionResult>().also{
        every{it.connection} returns connection
        every{it.version} returns TableVersion().SUPPORTED_VERSION
    }
    private val upgradeRunner = mockk<UpgradeRunner>().also { every{it.connectAndUpgrade(any(), any())} returns connectionResult}
    private val metadataCollection = mock<MetadataCollection>()
    private val metadataCollectionReader = mockk<MetadataCollectionReader>().also { every { it.load() } returns true }
    private val statement = mockk<Statement>(relaxed = true).also { every { it.executeQuery(any()) } returns resultSet }
    private val connection = mockk<Connection>(relaxed = true)
    private val connectionProvider = spyk<(String) -> Connection>({ connection })
    private val statementProvider = spyk<(Connection) -> Statement>({ statement })
    private val customerServiceReader = mockk<CustomerServiceReader>().also { every { it.load() } returns true }
    private val cs = CustomerService()

    private val reader = CustomerDatabaseReader(
        cs,
        metadataCollection,
        databaseFile,
        connectionProvider,
        statementProvider,
        upgradeRunner,
        metadataCollectionReader,
        customerServiceReader
    )

    @Test
    internal fun checkLoad() {
        assertThat("Should have loaded",reader.load())
    }
}
