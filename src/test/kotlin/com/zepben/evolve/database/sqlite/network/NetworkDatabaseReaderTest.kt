/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.EwbDatabaseType
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.Paths
import java.sql.Connection

internal class NetworkDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val databaseFile = "databaseFile"
    private val service = NetworkService()

    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.load() } returns true }
    private val networkServiceReader = mockk<NetworkServiceReader>().also { every { it.load() } returns true }

    private val connection = mockk<Connection> { justRun { close() } }
    private val connectionResult = mockk<UpgradeRunner.ConnectionResult>().also {
        every { it.connection } returns connection
        every { it.version } returns TableVersion().SUPPORTED_VERSION
    }
    private val upgradeRunner = mockk<UpgradeRunner>().also { every { it.connectAndUpgrade(any(), any(), any()) } returns connectionResult }

    private val setDirection = mockk<SetDirection> { justRun { run(service) } }
    private val setPhases = mockk<SetPhases> { justRun { run(service) } }
    private val phaseInferrer = mockk<PhaseInferrer> { justRun { run(service) } }
    private val assignToFeeders = mockk<AssignToFeeders> { justRun { run(service) } }
    private val assignToLvFeeders = mockk<AssignToLvFeeders> { justRun { run(service) } }

    private val reader = NetworkDatabaseReader(
        databaseFile,
        mockk(), // Metadata is unused if we provide a metadataReader.
        service,
        mockk(), // tables should not be used if we provide the rest of the parameters, so provide a mockk that will throw if used.
        { metadataReader },
        { networkServiceReader },
        upgradeRunner,
        setDirection,
        setPhases,
        phaseInferrer,
        assignToFeeders,
        assignToLvFeeders
    )

    //
    // NOTE: We don't do an exhaustive test of reading objects as this is done via the schema test.
    //

    @Test
    internal fun `calls expected processors, including post processes`() {
        assertThat("Should have loaded", reader.load())

        assertThat(systemErr.log, containsString("Applying feeder direction to network..."))
        assertThat(systemErr.log, containsString("Feeder direction applied to network."))

        assertThat(systemErr.log, containsString("Applying phases to network..."))
        assertThat(systemErr.log, containsString("Phasing applied to network."))

        assertThat(systemErr.log, containsString("Assigning equipment to feeders..."))
        assertThat(systemErr.log, containsString("Equipment assigned to feeders."))

        assertThat(systemErr.log, containsString("Assigning equipment to LV feeders..."))
        assertThat(systemErr.log, containsString("Equipment assigned to LV feeders."))

        verifySequence {
            upgradeRunner.connectAndUpgrade(match { it.contains(databaseFile) }, Paths.get(databaseFile), EwbDatabaseType.NETWORK)
            metadataReader.load()
            networkServiceReader.load()
            service.unresolvedReferences()
            setDirection.run(service)
            setPhases.run(service)
            phaseInferrer.run(service)
            assignToFeeders.run(service)
            assignToLvFeeders.run(service)
            connection.close()
        }
    }

}
