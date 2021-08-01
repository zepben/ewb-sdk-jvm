/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity


import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.ConnectedEquipmentNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConnectivityTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val visited = mutableSetOf<String>()

    @Test
    internal fun connectivityTraceIgnoresOpenState() {
        validateRun(Tracing.connectivityTrace(), "s1", "s2", "n1", "s3", "s4", "n2")
        validateRun(Tracing.connectivityBreadthTrace(), "s1", "s2", "n1", "s3", "s4", "n2")
    }

    @Test
    internal fun normalConnectedEquipmentTraceUsesOpenState() {
        validateRun(Tracing.normalConnectivityTrace(), "s1", "s3", "s4")
    }

    @Test
    internal fun currentConnectivityTraceUsesOpenState() {
        validateRun(Tracing.currentConnectivityTrace(), "s1", "s2", "s3")
    }

    private fun validateRun(t: BasicTraversal<ConnectivityResult>, vararg expected: String) {
        NetworkService.connectedEquipment(ConnectedEquipmentNetwork.create()["start"]!!).forEach { t.queue.add(it) }

        t.addStepAction { ce, _ -> visited.add(ce.to!!.mRID) }
            .run()

        assertThat(visited, containsInAnyOrder(*expected))
    }

}
