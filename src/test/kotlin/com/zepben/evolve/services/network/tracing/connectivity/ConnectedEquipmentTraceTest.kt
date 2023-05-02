/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.services.network.testdata.ConnectedEquipmentNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.fail

internal class ConnectedEquipmentTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val straightNetwork = ConnectedEquipmentNetwork.createStraight()
    private val branchedNetwork = ConnectedEquipmentNetwork.createBranched()

    @Test
    internal fun `connectedEquipmentTrace checks open state`() {
        Tracing.connectedEquipmentTrace().validateRun("start", "s1", "s2", "n1", "s3", "s4", "n2")
        Tracing.normalConnectedEquipmentTrace().validateRun("start", "s1", "s3", "s4")
        Tracing.currentConnectedEquipmentTrace().validateRun("start", "s1", "s2", "s3")
    }

    @Test
    internal fun limitedTraceCoverage() {
        // These traces are implemented and tested in a separate class, so just do a simple type check coverage test.
        assertThat(Tracing.normalLimitedConnectedEquipmentTrace(), instanceOf(LimitedConnectedEquipmentTrace::class.java))
        assertThat(Tracing.currentLimitedConnectedEquipmentTrace(), instanceOf(LimitedConnectedEquipmentTrace::class.java))
    }

    @Test
    internal fun `connectedEquipmentTrace can start on an open switch`() {
        Tracing.normalConnectedEquipmentTrace().validateRun("s2", "n1", "s1")
        Tracing.currentConnectedEquipmentTrace().validateRun("s4", "s3", "n2")
    }

    @Test
    internal fun `direction based trace respects direction and state`() {
        validateTrace(ConnectedEquipmentTrace::newNormalDownstreamEquipmentTrace, "c0", "b1", "b2", "b4", "c5")
        validateTrace(ConnectedEquipmentTrace::newNormalDownstreamEquipmentTrace, "b2")
        validateTrace(ConnectedEquipmentTrace::newNormalDownstreamEquipmentTrace, "b4", "c5")

        validateTrace(ConnectedEquipmentTrace::newCurrentDownstreamEquipmentTrace, "c0", "b1", "b2", "c3", "b4")
        validateTrace(ConnectedEquipmentTrace::newCurrentDownstreamEquipmentTrace, "b2", "c3")
        validateTrace(ConnectedEquipmentTrace::newCurrentDownstreamEquipmentTrace, "b4")

        validateTrace(ConnectedEquipmentTrace::newNormalUpstreamEquipmentTrace, "b1", "c0")
        validateTrace(ConnectedEquipmentTrace::newNormalUpstreamEquipmentTrace, "c3")
        validateTrace(ConnectedEquipmentTrace::newNormalUpstreamEquipmentTrace, "c5", "b4", "b1", "c0")

        validateTrace(ConnectedEquipmentTrace::newCurrentUpstreamEquipmentTrace, "b1", "c0")
        validateTrace(ConnectedEquipmentTrace::newCurrentUpstreamEquipmentTrace, "c3", "b2", "b1", "c0")
        validateTrace(ConnectedEquipmentTrace::newCurrentUpstreamEquipmentTrace, "c5")
    }

    @Test
    internal fun `direction based trace ignores phase connectivity`() {
        branchedNetwork.get<ConductingEquipment>("b4")!!.terminals.forEach { it.phases = PhaseCode.A }
        branchedNetwork.get<ConductingEquipment>("c5")!!.terminals.forEach { it.phases = PhaseCode.B }

        validateTrace(ConnectedEquipmentTrace::newNormalDownstreamEquipmentTrace, "b4", "c5")
    }

    @Test
    internal fun `does not queue from single terminals after the first`() {
        val junctions = TestNetworkBuilder()
            .fromJunction(numTerminals = 1)
            .toJunction(numTerminals = 1)
            .toJunction(numTerminals = 1)
            .toJunction(numTerminals = 1)
            .network
            .listOf<Junction>()

        junctions.forEach { start ->
            val steppedOn = mutableListOf<ConductingEquipmentStep>()

            // We clear the tracker on every step to allow it to queue things multiple times to ensure it does even try.
            val trace = ConnectedEquipmentTrace.newConnectedEquipmentTrace().apply {
                addStepAction { tracker.clear() }
                addStepAction { steppedOn.add(it).also { if (steppedOn.size > 4) fail("should not have stepped on more than 4 things") } }
            }

            trace.run(start)

            assertThat(steppedOn, containsInAnyOrder(*junctions.map { ConductingEquipmentStep(it, if (it == start) 0 else 1) }.toTypedArray()))
        }
    }

    private fun BasicTraversal<ConductingEquipmentStep>.validateRun(start: String, vararg expected: String) {
        val visited = mutableSetOf<String>()

        addStepAction { (ce, _), _ -> visited.add(ce.mRID) }
            .run(ConductingEquipmentStep(straightNetwork[start]!!))

        assertThat(visited, containsInAnyOrder(start, *expected))
    }

    private fun validateTrace(createTrace: () -> BasicTraversal<ConductingEquipment>, start: String, vararg expected: String) {
        val visited = mutableListOf<String?>()
        val trace = createTrace().apply { addStepAction { visited.add(it.mRID) } }

        trace.run(branchedNetwork[start]!!)

        assertThat(visited, containsInAnyOrder(start, *expected))
    }

}
