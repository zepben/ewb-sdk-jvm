/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

internal class DatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val resultSet = mock<ResultSet>()
    private val connection = mock<Connection>()
    private val statement = mock<Statement>().also { doReturn(resultSet).`when`(it).executeQuery(any()) }
    private val connectionResult = UpgradeRunner.ConnectionResult(connection, TableVersion().SUPPORTED_VERSION)
    private val upgradeRunner = mock<UpgradeRunner>().also { doReturn(connectionResult).`when`(it).connectAndUpgrade(any(), any()) }
    private val setDirection = mock<SetDirection>()
    private val setPhases = mock<SetPhases>()
    private val phaseInferrer = mock<PhaseInferrer>()
    private val assignToFeeders = mock<AssignToFeeders>()

    @Test
    internal fun postProcesses() {
        val ns = NetworkService()
        DatabaseReader(
            "filename",
            { filename -> connection.also { assertThat(filename, equalTo("filename")) } },
            { con -> statement.also { assertThat(con, equalTo(connection)) } },
            upgradeRunner,
            setDirection,
            setPhases,
            phaseInferrer,
            assignToFeeders
        ).load(MetadataCollection(), ns, DiagramService(), CustomerService())

        verify(upgradeRunner).connectAndUpgrade(any(), any())
        verify(setDirection).run(ns)
        verify(setPhases).run(ns)
        verify(phaseInferrer).run(ns)
        verify(assignToFeeders).run(ns)
    }

}
