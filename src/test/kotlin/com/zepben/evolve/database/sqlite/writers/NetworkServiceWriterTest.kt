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
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mockito.*

internal class NetworkServiceWriterTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val savedCommonMRIDs = mutableSetOf<String>()
    private val networkServiceWriter = NetworkServiceWriter(savedCommonMRIDs::contains, savedCommonMRIDs::add)
    private val networkCIMWriter = mock(NetworkCIMWriter::class.java)

    @Test
    internal fun savesCircuits() {
        val circuit1 = Circuit()
        val circuit2 = Circuit()

        networkServiceWriter.save(serviceOf(circuit1, circuit2), networkCIMWriter)

        verify(networkCIMWriter, times(1)).save(circuit1)
        verify(networkCIMWriter, times(1)).save(circuit2)
    }

    @Test
    internal fun savesLoops() {
        val loop1 = Loop()
        val loop2 = Loop()

        networkServiceWriter.save(serviceOf(loop1, loop2), networkCIMWriter)

        verify(networkCIMWriter, times(1)).save(loop1)
        verify(networkCIMWriter, times(1)).save(loop2)
    }

    @Test
    internal fun `saves Poles`() {
        val pole1 = Pole()
        val pole2 = Pole()

        networkServiceWriter.save(serviceOf(pole1, pole2), networkCIMWriter)

        verify(networkCIMWriter, times(1)).save(pole1)
        verify(networkCIMWriter, times(1)).save(pole2)
    }

    @Test
    internal fun `saves Streetlights`() {
        val streetlight1 = Streetlight()
        val streetlight2 = Streetlight()

        networkServiceWriter.save(serviceOf(streetlight1, streetlight2), networkCIMWriter)

        verify(networkCIMWriter, times(1)).save(streetlight1)
        verify(networkCIMWriter, times(1)).save(streetlight2)
    }

    private fun serviceOf(io1: IdentifiedObject, io2: IdentifiedObject): NetworkService {
        val networkService = NetworkService()

        networkService.tryAdd(io1)
        networkService.tryAdd(io2)

        return networkService
    }
}
