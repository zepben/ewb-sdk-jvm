/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.services.network.testdata.ConnectedEquipmentNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConnectedEquipmentTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val visited = mutableSetOf<String>()

    @Test
    internal fun connectedEquipmentTraceIgnoresOpenState() {
        validateRun(Tracing.connectedEquipmentTrace(), "start", "s1", "s2", "n1", "s3", "s4", "n2")
    }

    @Test
    internal fun normalConnectedEquipmentTraceUsesOpenState() {
        validateRun(Tracing.normalConnectedEquipmentTrace(), "start", "s1", "s3", "s4")
    }

    @Test
    internal fun currentConnectedEquipmentTraceUsesOpenState() {
        validateRun(Tracing.currentConnectedEquipmentTrace(), "start", "s1", "s2", "s3")
    }

    private fun validateRun(t: BasicTraversal<ConductingEquipmentStep>, vararg expected: String) {
        t.addStepAction { (ce, _), _ -> visited.add(ce.mRID) }
            .run(ConductingEquipmentStep(ConnectedEquipmentNetwork.create()["start"]!!))

        assertThat(visited, containsInAnyOrder(*expected))
    }

}
