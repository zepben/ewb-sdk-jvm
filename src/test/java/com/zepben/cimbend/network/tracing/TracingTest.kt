/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.Breaker
import com.zepben.cimbend.testdata.TestNetworks
import com.zepben.traversals.BasicTraversal
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
        validate(Supplier<BasicTraversal<Any>> { Tracing.createBasicDepthTrace { _, _ -> } }, BasicTraversal::class.java)
        validate(Supplier<BasicTraversal<Any>> { Tracing.createBasicBreadthTrace { _, _ -> } }, BasicTraversal::class.java)
        validate(Supplier { Tracing.connectedEquipmentTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.connectedEquipmentBreadthTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.phaseTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.normalPhaseTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.currentPhaseTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.normalDownstreamTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.currentDownstreamTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.normalUpstreamTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.currentUpstreamTrace() }, BasicTraversal::class.java)
        validate(Supplier { Tracing.setPhases() }, SetPhases::class.java)
        validate(Supplier { Tracing.phaseInferrer() }, PhaseInferrer::class.java)
        validate(Supplier { Tracing.removePhases() }, RemovePhases::class.java)
        validate(Supplier { Tracing.assignEquipmentContainersToFeeders() }, AssignToFeeders::class.java)
        validate(Supplier { Tracing.normalDownstreamTree() }, DownstreamTree::class.java)
        validate(Supplier { Tracing.currentDownstreamTree() }, DownstreamTree::class.java)
        validate(Supplier { Tracing.findWithUsagePoints() }, FindWithUsagePoints::class.java)
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
