/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.network.NetworkCIMWriter
import com.zepben.evolve.database.sqlite.network.NetworkServiceWriter
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TablePoles
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableStreetlights
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder.TableLvFeeders
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

internal class NetworkServiceWriterTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()



    private val testingDatabaseTables = object : DatabaseTables() {
        override val tables: Map<Class<out SqliteTable>, SqliteTable> = listOf(
            TableCircuits(),
            TableLoops(),
            TableLvFeeders(),
            TablePoles(),
            TableStreetlights(),
        ).associateBy { it::class.java }
    }
    private val savedCommonMRIDs = mutableSetOf<String>()
    private val networkService = NetworkService()
    private val mw = mockk<NetworkCIMWriter>(relaxed = true)
    private val networkServiceWriter = NetworkServiceWriter(
        networkService,
        mw,
        savedCommonMRIDs::contains,
        savedCommonMRIDs::add
    )
    private val resultSet = mockk<ResultSet>(relaxed = true)
    private val connection = mockk<Connection>(relaxed = true)
    private val preparedStatement = mockk<PreparedStatement>(relaxed = true).also { every { it.executeQuery(any()) } returns resultSet }
    private val preparedStatementProvider = spyk<(Connection, String) -> PreparedStatement>({ _, _ -> preparedStatement })

    @Test
    internal fun savesCircuits() {

        val circuit1 = Circuit().also { networkService.add(it) }

        testingDatabaseTables.prepareInsertStatements(connection, preparedStatementProvider)
        networkServiceWriter.save()

        //the save method will be called once but networkServiceWriter will error out due to fail to write
        verify(exactly = 1) { mw.save(circuit1) }

    }

    @Test
    internal fun savesLoops() {
        val loop1 = Loop().also { networkService.add(it) }

        networkServiceWriter.save()

        //the save method will be called once but networkServiceWriter will error out due to fail to write
        verify(exactly = 1) { mw.save(loop1) }
    }

    @Test
    internal fun savesLvFeeders() {
        val lvFeeder1 = LvFeeder().also { networkService.add(it) }

        networkServiceWriter.save()

        //the save method will be called once but networkServiceWriter will error out due to fail to write
        verify(exactly = 1) { mw.save(lvFeeder1) }
    }

    @Test
    internal fun `saves Poles`() {
        val pole1 = Pole().also { networkService.add(it) }

        networkServiceWriter.save()

        //the save method will be called once but networkServiceWriter will error out due to fail to write
        verify(exactly = 1) { mw.save(pole1) }
    }

    @Test
    internal fun `saves Streetlights`() {
        val streetlight1 = Streetlight().also { networkService.add(it) }

        networkServiceWriter.save()

        //the save method will be called once but networkServiceWriter will error out due to fail to write
        verify(exactly = 1) { mw.save(streetlight1) }
    }

}
