/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.assets.Streetlight
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.network.NetworkService
import com.zepben.test.util.junit.SystemLogExtension
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
