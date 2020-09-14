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

package com.zepben.cimbend.testdata

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.tracing.Tracing
import com.zepben.cimbend.testdata.TestDataCreators.createPowerTransformerForConnecting
import com.zepben.cimbend.testdata.TestDataCreators.createSourceForConnecting

object SingleTransformerNetwork {

    //
    // s-tx
    //
    fun create(sequenceNumber: Int): NetworkService {
        val networkService = NetworkService()

        val source = createSourceForConnecting(networkService, "s", 1, PhaseCode.A)
        val transformer = createPowerTransformerForConnecting(networkService, "tx", 1, PhaseCode.A, 1, 0)

        source.terminals[0].sequenceNumber = sequenceNumber
        transformer.terminals[0].sequenceNumber = sequenceNumber

        networkService.connect(source.getTerminal(sequenceNumber)!!, transformer.getTerminal(sequenceNumber)!!)

        Tracing.setPhases().run(networkService)

        return networkService
    }

}
