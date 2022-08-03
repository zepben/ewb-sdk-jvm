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
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConnectedEquipmentTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val network = ConnectedEquipmentNetwork.create()

    @Test
    internal fun connectedEquipmentTraceIgnoresOpenState() {
        Tracing.connectedEquipmentTrace().validateRun("start", "s1", "s2", "n1", "s3", "s4", "n2")
    }

    @Test
    internal fun normalConnectedEquipmentTraceUsesOpenState() {
        Tracing.normalConnectedEquipmentTrace().validateRun("start", "s1", "s3", "s4")
    }

    @Test
    internal fun currentConnectedEquipmentTraceUsesOpenState() {
        Tracing.currentConnectedEquipmentTrace().validateRun("start", "s1", "s2", "s3")
    }

    @Test
    internal fun limitedTraceCoverage() {
        // These traces are implemented and tested in a separate class, so just do a simple type check coverage test.
        assertThat(Tracing.normalLimitedConnectedEquipmentTrace(), instanceOf(LimitedConnectedEquipmentTrace::class.java))
        assertThat(Tracing.currentLimitedConnectedEquipmentTrace(), instanceOf(LimitedConnectedEquipmentTrace::class.java))
    }

    @Test
    internal fun canStartOnOpenSwitch() {
        Tracing.normalConnectedEquipmentTrace().validateRun("s2", "n1", "s1")
        Tracing.currentConnectedEquipmentTrace().validateRun("s4", "s3", "n2")
    }

    private fun BasicTraversal<ConductingEquipmentStep>.validateRun(start: String, vararg expected: String) {
        val visited = mutableSetOf<String>()

        addStepAction { (ce, _), _ -> visited.add(ce.mRID) }
            .run(ConductingEquipmentStep(network[start]!!))

        assertThat(visited, containsInAnyOrder(start, *expected))
    }

}
