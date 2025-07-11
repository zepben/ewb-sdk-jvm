/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace

import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.Breaker
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.upstream
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class TracingTest {

    //
    //              1 b2 21--c3--21 ec4
    // 1 b0 21--c1--2
    //              1 b5 21--c6--21 ec7
    //
    private val network: NetworkService = TestNetworkBuilder()
        .fromBreaker() // b0
        .toAcls() // c1
        .toBreaker(isOpen = true) // b2
        .toAcls() // c3
        .toEnergyConsumer() // ec4
        .fromBreaker() // b5
        .connect("c1", "b5", 2, 1)
        .toAcls() // c6
        .toEnergyConsumer() // ec7
        .addFeeder("b0", 2) // fdr8
        .build()

    private val feederBreaker = network.get<Breaker>("b0") ?: error("network is not as expected")

    @Test
    fun `downstream normal trace`() {
        val visitedEquipmentIds = mutableListOf<String>()
        Tracing.networkTrace(NetworkStateOperators.NORMAL)
            .addCondition { downstream() }
            .addStepAction { step, _ -> visitedEquipmentIds.add(step.path.toEquipment.mRID) }
            .run(feederBreaker.t2)

        // Because b5 are both normally closed we expected both energy consumers
        assertThat(visitedEquipmentIds, hasItem("ec4"))
        assertThat(visitedEquipmentIds, hasItem("ec7"))
    }

    @Test
    fun `downstream current trace`() {
        val visitedEquipmentIds = mutableListOf<String>()
        Tracing.networkTrace(NetworkStateOperators.CURRENT)
            .addCondition { downstream() }
            .addStepAction { step, _ -> visitedEquipmentIds.add(step.path.toEquipment.mRID) }
            .run(feederBreaker.t2)

        // Because b2 is currently open, we expect it to trace all the way down to ec7, but not ec4 as it will not be downstream due to the open point.
        assertThat(visitedEquipmentIds, hasItem("ec7"))
        assertThat(visitedEquipmentIds, not(hasItem("ec4")))
    }

    @Test
    fun `upstream trace`() {
        val visitedEquipmentIds = mutableListOf<String>()
        Tracing.networkTrace() // Defaults to NetworkStateOperators.NORMAL
            .addCondition { upstream() }
            .addStepAction { step, _ -> visitedEquipmentIds.add(step.path.toEquipment.mRID) }
            .run(network.get<AcLineSegment>("c3")!!.t1)

        // Upstream trace should not go back down other branches
        assertThat(visitedEquipmentIds, hasItem("c1"))
        assertThat(visitedEquipmentIds, not(hasItem("b5")))
    }

    @Test
    fun `trace stopping at open points`() {
        val visitedEquipmentIds = mutableListOf<String>()
        Tracing.networkTrace(NetworkStateOperators.CURRENT)
            .addCondition { stopAtOpen() }
            .addStepAction { step, _ -> visitedEquipmentIds.add(step.path.toEquipment.mRID) }
            // We can also run from ConductingEquipment which will start a trace from each of its terminals.
            .run(network.get<AcLineSegment>("c3")!!)

        // This will trace in all directions from c6 but will stop at open points
        assertThat(visitedEquipmentIds, containsInAnyOrder("c3", "b2", "ec4"))
    }

    @Test
    fun `add custom stop condition`() {
        val stopEquipmentIds = mutableListOf<String>()
        Tracing.networkTrace() // Defaults to NetworkStateOperators.NORMAL
            .addStopCondition { step, _ -> step.path.toEquipment is Switch }
            // Convenience action function ifStopping will only be called when it is a step that meets a stop condition
            .ifStopping { step, _ -> stopEquipmentIds.add(step.path.toEquipment.mRID) }
            .run(network.get<AcLineSegment>("c1")!!)

        // This should not have traced past any of the breakers regardless of state because of our custom stop condition.
        assertThat(stopEquipmentIds, containsInAnyOrder("b0", "b2", "b5"))
    }
}
