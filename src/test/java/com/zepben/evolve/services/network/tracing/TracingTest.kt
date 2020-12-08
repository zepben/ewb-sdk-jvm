/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.services.network.testdata.TestNetworks
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.util.*
import java.util.function.Supplier

class TracingTest {
    // Just trace all connected assets and make sure we actually visit every item.
    @Test
    internal fun basicAssetTrace() {
        val n = TestNetworks.getNetwork(1)
        val expected = n.setOf<ConductingEquipment>()
        val visited: MutableSet<ConductingEquipment> = HashSet()
        val start = n.get<ConductingEquipment>("node0")!!
        val trace = Tracing.connectedEquipmentTrace().addStepAction { ce, _ -> visited.add(ce) }
        trace.run(start)
        Assert.assertEquals(expected, visited)
    }

    @Test
    internal fun coverage() {
        validate<BasicTraversal<Any>>({ Tracing.createBasicDepthTrace { _, _ -> } }, BasicTraversal::class.java)
        validate<BasicTraversal<Any>>({ Tracing.createBasicBreadthTrace { _, _ -> } }, BasicTraversal::class.java)
        validate({ Tracing.connectedEquipmentTrace() }, BasicTraversal::class.java)
        validate({ Tracing.connectedEquipmentBreadthTrace() }, BasicTraversal::class.java)
        validate({ Tracing.phaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalPhaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentPhaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalDownstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentDownstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalUpstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentUpstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.setPhases() }, SetPhases::class.java)
        validate({ Tracing.phaseInferrer() }, PhaseInferrer::class.java)
        validate({ Tracing.removePhases() }, RemovePhases::class.java)
        validate({ Tracing.assignEquipmentContainersToFeeders() }, AssignToFeeders::class.java)
        validate({ Tracing.normalDownstreamTree() }, DownstreamTree::class.java)
        validate({ Tracing.currentDownstreamTree() }, DownstreamTree::class.java)
        validate({ Tracing.findWithUsagePoints() }, FindWithUsagePoints::class.java)
    }

    @Test
    internal fun downstreamTraceWithTooManyPhases() {
        val b1 = Breaker().apply {
            addTerminal(Terminal().apply {
                phases = PhaseCode.AB
            }.also {
                it.conductingEquipment = this
            })
        }

        Tracing.normalDownstreamTrace()
            .run(PhaseStep.startAt(b1, PhaseCode.ABCN))
    }

    private fun <T> validate(supplier: Supplier<T>, expectedClass: Class<*>) {
        MatcherAssert.assertThat("has the correct class type", supplier.get(), Matchers.instanceOf(expectedClass))
        MatcherAssert.assertThat("returns a new instance", supplier.get(), Matchers.not(Matchers.equalTo(supplier.get())))
    }
}
