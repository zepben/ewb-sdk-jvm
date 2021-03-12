/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing

object SingleTransformerNetwork {

    //
    // s-tx
    //
    fun create(sequenceNumber: Int) = NetworkService().also { networkService ->
        val source = createSourceForConnecting(networkService, "s", 1, PhaseCode.A)
        val transformer = createPowerTransformerForConnecting(networkService, "tx", 1, PhaseCode.A, 1, 0)

        source.terminals[0].sequenceNumber = sequenceNumber
        transformer.terminals[0].sequenceNumber = sequenceNumber

        networkService.connect(source.getTerminal(sequenceNumber)!!, transformer.getTerminal(sequenceNumber)!!)

        Tracing.setPhases().run(networkService)
    }

}
