/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.services.network.NetworkService

object SingleTransformerNetwork {

    //
    // s-tx
    //
    fun create(sequenceNumber: Int): NetworkService = NetworkService().also { networkService ->
        val source = createSourceForConnecting(networkService, "s", 1)
        val transformer = createPowerTransformerForConnecting(networkService, "tx", 1, 1, 0)

        source.terminals[0].sequenceNumber = sequenceNumber
        transformer.terminals[0].sequenceNumber = sequenceNumber

        networkService.connect(source.getTerminal(sequenceNumber)!!, transformer.getTerminal(sequenceNumber)!!)

        networkService.setPhases()
        source.addFeederDirections(sequenceNumber)
    }

}
