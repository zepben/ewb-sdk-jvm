/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.network.NetworkDatabaseReader
import com.zepben.evolve.database.sqlite.network.NetworkServiceReader
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

internal class NetworkDatabaseReaderTest {

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
    private val networkServiceReader = mockk<NetworkServiceReader>().also { every { it.load() } returns true }
    private val setDirection = mockk<SetDirection>(relaxed = true)
    private val setPhases = mockk<SetPhases>(relaxed = true)
    private val phaseInferrer = mockk<PhaseInferrer>(relaxed = true)
    private val assignToFeeders = mockk<AssignToFeeders>(relaxed = true)
    private val assignToLvFeeders = mockk<AssignToLvFeeders>(relaxed = true)
    private val ns = NetworkService()

    private val reader = NetworkDatabaseReader(
        ns,
        metadataCollection,
        databaseFile,
        connectionProvider,
        statementProvider,
        upgradeRunner,
        metadataCollectionReader,
        networkServiceReader,
        setDirection,
        setPhases,
        phaseInferrer,
        assignToFeeders,
        assignToLvFeeders
    )

    @Test
    internal fun postProcesses() {
        assertThat("Should have loaded",reader.load())

        assertThat(systemErr.log, containsString("Applying feeder direction to network..."))
        verify(exactly = 1){setDirection.run(ns)}
        assertThat(systemErr.log, containsString("Feeder direction applied to network."))

        assertThat(systemErr.log, containsString("Applying phases to network..."))
        verify(exactly = 1){setPhases.run(ns)}
        verify(exactly = 1){phaseInferrer.run(ns)}
        assertThat(systemErr.log, containsString("Phasing applied to network."))

        assertThat(systemErr.log, containsString("Assigning equipment to feeders..."))
        verify(exactly = 1){assignToFeeders.run(ns)}
        assertThat(systemErr.log, containsString("Equipment assigned to feeders."))

        assertThat(systemErr.log, containsString("Assigning equipment to LV feeders..."))
        verify(exactly = 1){assignToLvFeeders.run(ns)}
        assertThat(systemErr.log, containsString("Equipment assigned to LV feeders."))
    }
}
