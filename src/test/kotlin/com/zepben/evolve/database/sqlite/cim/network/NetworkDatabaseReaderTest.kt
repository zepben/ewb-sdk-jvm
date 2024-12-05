/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.network

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.database.sqlite.cim.metadata.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
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
    private val service = mockk<NetworkService>(relaxed = true)

    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.load() } returns true }
    private val networkServiceReader = mockk<NetworkServiceReader>().also { every { it.load() } returns true }

    private val connection = mockk<Connection> {
        justRun { close() }
    }

    private val tableVersion = mockk<TableVersion> {
        every { getVersion(any()) } returns 1
        every { this@mockk.supportedVersion } returns 1
    }

    private val normalSetFeederDirections = mockk<SetDirection> { justRun { run(service) } }
    private val currentSetFeederDirections = mockk<SetDirection> { justRun { run(service) } }
    private val normalSetPhases = mockk<SetPhases> { justRun { run(service) } }
    private val currentSetPhases = mockk<SetPhases> { justRun { run(service) } }
    private val normalPhaseInferrer = mockk<PhaseInferrer>()
    private val currentPhaseInferrer = mockk<PhaseInferrer>()
    private val normalAssignToFeeders = mockk<AssignToFeeders> { justRun { run(service) } }
    private val currentAssignToFeeders = mockk<AssignToFeeders> { justRun { run(service) } }
    private val normalAssignToLvFeeders = mockk<AssignToLvFeeders> { justRun { run(service) } }
    private val currentAssignToLvFeeders = mockk<AssignToLvFeeders> { justRun { run(service) } }

    private fun reader(inferPhases: Boolean) = NetworkDatabaseReader(
        connection,
        service,
        databaseFile,
        inferPhases,
        metadataReader,
        networkServiceReader,
        tableVersion,
        normalSetFeederDirections,
        currentSetFeederDirections,
        normalSetPhases,
        currentSetPhases,
        normalPhaseInferrer,
        currentPhaseInferrer,
        normalAssignToFeeders,
        currentAssignToFeeders,
        normalAssignToLvFeeders,
        currentAssignToLvFeeders,
    )

    //
    // NOTE: We don't do an exhaustive test of reading objects as this is done via the schema test.
    //

    @Test
    internal fun `calls expected processors, including post processes`() {
        every { normalPhaseInferrer.run(service) } returns emptyList()
        every { currentPhaseInferrer.run(service) } returns emptyList()

        assertThat("Should have loaded", reader(true).load())

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

            metadataReader.load()
            networkServiceReader.load()
            service.unresolvedReferences()

            normalSetFeederDirections.run(service)
            currentSetFeederDirections.run(service)

            normalSetPhases.run(service)
            currentSetPhases.run(service)

            normalPhaseInferrer.run(service)
            currentPhaseInferrer.run(service)

            normalAssignToFeeders.run(service)
            currentAssignToFeeders.run(service)

            normalAssignToLvFeeders.run(service)
            currentAssignToLvFeeders.run(service)

            // calls for _validate_equipment_containers()
            service.listOf<Equipment>(any<(Equipment) -> Boolean>())

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
        every { normalPhaseInferrer.run(service) } returns listOf(PhaseInferrer.InferredPhase(j1, false), PhaseInferrer.InferredPhase(j1, true))
        every { currentPhaseInferrer.run(service) } returns listOf(PhaseInferrer.InferredPhase(j2, false), PhaseInferrer.InferredPhase(j3, true))

        reader(true).load()

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
        reader(false).load()

        verify { normalPhaseInferrer wasNot called }
        verify { currentPhaseInferrer wasNot called }
    }
}
