/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration

class NetworkTraceTest {

    @Test
    internal fun `Can run large branching traces`() {
        //
        // NOTE: This test was added when investigating an exponential run time on real world networks. It revealed an issue in the
        //       tracker for recursive branch traces which wasn't noticed in the other tests due to not being large/deep enough.
        //
        val builder = TestNetworkBuilder()
        val network = builder.network

        builder.fromJunction(numTerminals = 1)
            .toAcls()

        for (i in 1..1000) {
            builder.toJunction(mRID = "junc-$i", numTerminals = 3)
                .toAcls(mRID = "acls-$i-top")
                .fromAcls(mRID = "acls-$i-bottom")
                .connect("junc-$i", "acls-$i-bottom", 2, 1)
        }

        assertTimeoutPreemptively(
            Duration.ofSeconds(5),
            message = "If this test times out, you have managed to break things as described in the test note. Go fix it."
        ) {
            Tracing.networkTraceBranching().run(network.getT("j0", 1))
        }
    }

    private fun NetworkService.getT(id: String, terminalId: Int) =
        get<ConductingEquipment>(id)!!.getTerminal(terminalId)!!

}
