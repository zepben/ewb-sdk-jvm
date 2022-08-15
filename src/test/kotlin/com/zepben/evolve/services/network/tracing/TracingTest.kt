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
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.RemoveDirection
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.PhaseStep
import com.zepben.evolve.services.network.tracing.phases.RemovePhases
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.util.function.Supplier

class TracingTest {

    // Just trace all connected assets and make sure we actually visit every item.
    @Test
    internal fun basicAssetTrace() {
        val n = PhaseSwapLoopNetwork.create()
        val expected = n.setOf<ConductingEquipment>()
        val visited = mutableSetOf<ConductingEquipment>()

        Tracing.connectedEquipmentTrace().apply { addStepAction { (ce, _) -> visited.add(ce) } }
            .run(n["node0"]!!)

        assertThat(expected, equalTo(visited))
    }

    @Test
    internal fun coverage() {
        validate({ Tracing.createBasicDepthTrace { _: Any, _ -> } }, BasicTraversal::class.java)
        validate({ Tracing.createBasicBreadthTrace { _: Any, _ -> } }, BasicTraversal::class.java)
        validate({ Tracing.connectedEquipmentTrace() }, BasicTraversal::class.java)
        validate({ Tracing.connectedEquipmentBreadthTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalConnectedEquipmentTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentConnectedEquipmentTrace() }, BasicTraversal::class.java)
        validate({ Tracing.phaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalPhaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentPhaseTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalDownstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentDownstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.normalUpstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.currentUpstreamTrace() }, BasicTraversal::class.java)
        validate({ Tracing.setPhases() }, SetPhases::class.java)
        validate({ Tracing.setDirection() }, SetDirection::class.java)
        validate({ Tracing.phaseInferrer() }, PhaseInferrer::class.java)
        validate({ Tracing.removePhases() }, RemovePhases::class.java)
        validate({ Tracing.removeDirection() }, RemoveDirection::class.java)
        validate({ Tracing.assignEquipmentToFeeders() }, AssignToFeeders::class.java)
        validate({ Tracing.normalDownstreamTree() }, DownstreamTree::class.java)
        validate({ Tracing.currentDownstreamTree() }, DownstreamTree::class.java)
        validate({ Tracing.findWithUsagePoints() }, FindWithUsagePoints::class.java)
    }

    @Test
    internal fun downstreamTraceWithTooManyPhases() {
        val b1 = Breaker().apply { addTerminal(Terminal().apply { phases = PhaseCode.AB }) }

        Tracing.normalDownstreamTrace()
            .run(PhaseStep.startAt(b1, PhaseCode.ABCN))
    }

    private fun <T> validate(supplier: Supplier<T>, expectedClass: Class<*>) {
        assertThat("has the correct class type", supplier.get(), instanceOf(expectedClass))
        assertThat("returns a new instance", supplier.get(), not(equalTo(supplier.get())))
    }

}
