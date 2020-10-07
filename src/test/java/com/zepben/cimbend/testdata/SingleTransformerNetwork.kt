/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
