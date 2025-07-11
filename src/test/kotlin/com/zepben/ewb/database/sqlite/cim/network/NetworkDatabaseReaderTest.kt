/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.wires.EnergySource
import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.database.sqlite.cim.metadata.MetadataCollectionReader
import com.zepben.ewb.database.sqlite.common.SqliteTableVersion
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.feeder.AssignToFeeders
import com.zepben.ewb.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.ewb.services.network.tracing.feeder.SetDirection
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.phases.PhaseInferrer
import com.zepben.ewb.services.network.tracing.phases.SetPhases
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.Connection

internal class NetworkDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val databaseFile = "databaseFile"
    private val service = spyk(NetworkService())

    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.read(service.metadata) } returns true }
    private val networkServiceReader = mockk<NetworkServiceReader>().also { every { it.read(service) } returns true }

    private val connection = mockk<Connection> {
        justRun { close() }
    }

    private val tableVersion = mockk<SqliteTableVersion> {
        every { getVersion(any()) } returns 1
        every { supportedVersion } returns 1
    }

    private val setFeederDirections = mockk<SetDirection> {
        justRun { run(service, NetworkStateOperators.NORMAL) }
        justRun { run(service, NetworkStateOperators.CURRENT) }
    }
    private val setPhases = mockk<SetPhases> {
        justRun { run(service, NetworkStateOperators.NORMAL) }
        justRun { run(service, NetworkStateOperators.CURRENT) }
    }
    private val phaseInferrer = mockk<PhaseInferrer>()
    private val assignToFeeders = mockk<AssignToFeeders> {
        justRun { run(service, NetworkStateOperators.NORMAL) }
        justRun { run(service, NetworkStateOperators.CURRENT) }
    }
    private val assignToLvFeeders = mockk<AssignToLvFeeders> {
        justRun { run(service, NetworkStateOperators.NORMAL) }
        justRun { run(service, NetworkStateOperators.CURRENT) }
    }

    private fun reader(inferPhases: Boolean) = NetworkDatabaseReader(
        connection,
        databaseFile,
        mockk<NetworkDatabaseTables>().also {
            every { it.tables } returns mapOf(SqliteTableVersion::class to tableVersion)
        },
        { _, _ -> metadataReader },
        { _, _ -> networkServiceReader },
        inferPhases = inferPhases,
        setFeederDirections,
        setPhases,
        phaseInferrer,
        assignToFeeders,
        assignToLvFeeders,
    )

    //
    // NOTE: We don't do an exhaustive test of reading objects as this is done via the schema test.
    //

    @Test
    internal fun `calls expected processors, including post processes`() {
        every { phaseInferrer.run(service, NetworkStateOperators.NORMAL) } returns emptyList()
        every { phaseInferrer.run(service, NetworkStateOperators.CURRENT) } returns emptyList()

        assertThat("Should have read", reader(true).read(service))

        assertThat(systemErr.log, containsString("Applying feeder direction to network..."))
        assertThat(systemErr.log, containsString("Feeder direction applied to network."))

        assertThat(systemErr.log, containsString("Applying phases to network..."))
        assertThat(systemErr.log, containsString("Phasing applied to network."))

        assertThat(systemErr.log, containsString("Assigning equipment to feeders..."))
        assertThat(systemErr.log, containsString("Equipment assigned to feeders."))

        assertThat(systemErr.log, containsString("Assigning equipment to LV feeders..."))
        assertThat(systemErr.log, containsString("Equipment assigned to LV feeders."))

        assertThat(systemErr.log, containsString("Validating that each equipment is assigned to a container..."))
        assertThat(systemErr.log, containsString("Equipment containers validated."))

        assertThat(systemErr.log, containsString("Validating primary sources vs feeders..."))
        assertThat(systemErr.log, containsString("Sources vs feeders validated."))

        verifySequence {
            tableVersion.supportedVersion
            tableVersion.getVersion(connection)

            metadataReader.read(service.metadata)
            networkServiceReader.read(service)
            service.unresolvedReferences()

            setFeederDirections.run(service, NetworkStateOperators.NORMAL)
            setFeederDirections.run(service, NetworkStateOperators.CURRENT)

            setPhases.run(service, NetworkStateOperators.NORMAL)
            setPhases.run(service, NetworkStateOperators.CURRENT)

            phaseInferrer.run(service, NetworkStateOperators.NORMAL)
            phaseInferrer.run(service, NetworkStateOperators.CURRENT)

            assignToFeeders.run(service, NetworkStateOperators.NORMAL)
            assignToFeeders.run(service, NetworkStateOperators.CURRENT)

            assignToLvFeeders.run(service, NetworkStateOperators.NORMAL)
            assignToLvFeeders.run(service, NetworkStateOperators.CURRENT)

            // calls for _validate_equipment_containers(), including the sequenceOf which is called inside listOf.
            service.listOf<Equipment>(any<(Equipment) -> Boolean>())
            service.sequenceOf<Equipment>()

            // calls for _validate_sources()
            service.sequenceOf<Feeder>()
            service.sequenceOf<EnergySource>()
        }
    }

    @Test
    internal fun `logs inferred phases`() {
        val j1 = Junction("j1").apply { name = "j1 name" }
        val j2 = Junction("j1").apply { name = "j1 name" }
        val j3 = Junction("j1").apply { name = "j1 name" }
        every { phaseInferrer.run(service, NetworkStateOperators.NORMAL) } returns listOf(
            PhaseInferrer.InferredPhase(j1, false),
            PhaseInferrer.InferredPhase(j1, true)
        )
        every { phaseInferrer.run(service, NetworkStateOperators.CURRENT) } returns listOf(
            PhaseInferrer.InferredPhase(j2, false),
            PhaseInferrer.InferredPhase(j3, true)
        )

        reader(true).read(service)

        fun correctMessage(idObj: IdentifiedObject) =
            "*** Action Required *** Inferred missing phase for '${idObj.name}' [${idObj.mRID}] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

        fun suspectMessage(idObj: IdentifiedObject) =
            "*** Action Required *** Inferred missing phases for '${idObj.name}' [${idObj.mRID}] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

        assertThat(systemErr.log, containsString(correctMessage(j1)))
        assertThat(systemErr.log, containsString(suspectMessage(j2)))
        assertThat(systemErr.log, containsString(suspectMessage(j3)))
    }

    @Test
    fun `does not infer phases when inferPhases is false`() {
        reader(false).read(service)

        verify { phaseInferrer wasNot called }
        verify { phaseInferrer wasNot called }
    }

}
