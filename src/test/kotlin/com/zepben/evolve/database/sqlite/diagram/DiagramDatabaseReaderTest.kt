/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.EwbDatabaseType
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.Paths
import java.sql.Connection

internal class DiagramDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val databaseFile = "databaseFile"
    private val service = DiagramService()

    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.load() } returns true }
    private val diagramServiceReader = mockk<DiagramServiceReader>().also { every { it.load() } returns true }

    private val connection = mockk<Connection> { justRun { close() } }
    private val connectionResult = mockk<UpgradeRunner.ConnectionResult>().also {
        every { it.connection } returns connection
        every { it.version } returns TableVersion().SUPPORTED_VERSION
    }
    private val upgradeRunner = mockk<UpgradeRunner>().also { every { it.connectAndUpgrade(any(), any(), any()) } returns connectionResult }

    private val reader = DiagramDatabaseReader(
        databaseFile,
        mockk(), // The metadata is unused if we provide a metadataReader.
        service,
        mockk(), // tables should not be used if we provide the rest of the parameters, so provide a mockk that will throw if used.
        { metadataReader },
        { diagramServiceReader },
        upgradeRunner
    )

    //
    // NOTE: We don't do an exhaustive test of reading objects as this is done via the schema test.
    //

    @Test
    internal fun `calls expected processors, including post processes`() {
        assertThat("Should have loaded", reader.load())

        verifySequence {
            upgradeRunner.connectAndUpgrade(match { it.contains(databaseFile) }, Paths.get(databaseFile), EwbDatabaseType.DIAGRAM)
            metadataReader.load()
            diagramServiceReader.load()
            service.unresolvedReferences()
            connection.close()
        }
    }

}
