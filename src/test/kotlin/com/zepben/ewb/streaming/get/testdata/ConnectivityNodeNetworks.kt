/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testdata

import com.zepben.ewb.cim.iec61970.base.core.ConnectivityNode
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.createTerminal


object ConnectivityNodeNetworks {
    fun createSimpleConnectivityNode(): NetworkService {
        val ns = NetworkService()

        createConnectivityNodeWithTerminals(ns, "cn1", PhaseCode.A, PhaseCode.B, PhaseCode.C)

        return ns
    }

    private fun createConnectivityNodeWithTerminals(ns: NetworkService, mRID: String, vararg terminalPhases: PhaseCode) =
        ConnectivityNode(mRID).apply {
            ns.add(this)

            terminalPhases.forEachIndexed { i, it ->
                val t = createTerminal(ns, null, it, i + 1)
                ns.connect(t, mRID)
            }
        }

}
