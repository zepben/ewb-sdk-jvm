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
import com.zepben.cimbend.testdata.TestDataCreators.*

object SingleLineSwerNetwork {

    fun create(): NetworkService {
        val networkService = NetworkService()

        val source = createSourceForConnecting(networkService, "source", 1, PhaseCode.ABC)
        val line1 = createAcLineSegmentForConnecting(networkService, "line1", PhaseCode.ABC)
        val isoTx = createPowerTransformerForConnecting(networkService, "isoTx", 2, PhaseCode.ABC, 0, 0)
        val line2 = createAcLineSegmentForConnecting(networkService, "line2", PhaseCode.X)

        networkService.connect(source.getTerminal(1)!!, line1.getTerminal(1)!!)
        networkService.connect(isoTx.getTerminal(1)!!, line1.getTerminal(2)!!)
        networkService.connect(isoTx.getTerminal(2)!!, line2.getTerminal(1)!!)

        return networkService
    }

}
