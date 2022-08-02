/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.collection.IsMapWithSize.aMapWithSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*

internal class LimitedConnectedEquipmentTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val traversal = mock<ConnectedEquipmentTraversal>()
    private val getTerminalDirection = spy(Terminal::normalFeederDirection)

    private val trace = LimitedConnectedEquipmentTrace({ traversal }, getTerminalDirection)

    private val simpleNs = TestNetworkBuilder()
        .fromJunction(numTerminals = 1) // j0
        .toAcls() // c1
        .toBreaker() // b2
        .toAcls() // c3
        .addFeeder("j0")
        .build()

    @Test
    internal fun withoutDirectionAddsStopConditionAndStepAction() {
        trace.run(listOf(mock()))

        verify(traversal).addStopCondition(any())
        verify(traversal).addStepAction(any<(ConductingEquipmentStep) -> Unit>())
    }

    @Test
    internal fun withoutDirectionStopConditionChecksProvidedMaximumSteps() {
        trace.run(listOf(mock()), 2)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(traversal).addStopCondition(stopConditionCaptor.capture())

        val stopCondition = stopConditionCaptor.firstValue
        assertThat("Step 0 does not stop", !stopCondition(ConductingEquipmentStep(mock())))
        assertThat("Step 1 does not stop", !stopCondition(ConductingEquipmentStep(mock(), 1)))
        assertThat("Step 2 stops", stopCondition(ConductingEquipmentStep(mock(), 2)))
    }

    @Test
    internal fun withoutDirectionRunsTheTraceFromEachStartItem() {
        val j1 = Junction()
        val j2 = Junction()

        trace.run(listOf(j1, j2))

        verify(traversal, times(2)).run(any<ConductingEquipment>(), any())
        verify(traversal).run(j1, false)
        verify(traversal).run(j2, false)
    }

    @Test
    internal fun withoutDirectionStepActionAddsToResults() {
        val j = Junction()
        configureRunStepActions(ConductingEquipmentStep(j, 2))

        val results = trace.run(listOf(j), 2)

        assertThat(results, aMapWithSize(1))
        assertThat(results[j], equalTo(2))
    }

    @Test
    internal fun withDirectionAddsStopConditionAndStepAction() {
        trace.run(listOf(simpleNs["j0"]!!), feederDirection = FeederDirection.DOWNSTREAM)

        verify(traversal, times(3)).addStopCondition(any())
        verify(traversal).addStepAction(any<(ConductingEquipmentStep) -> Unit>())
    }

    @Test
    internal fun withDirectionFirstStopConditionChecksProvidedMaximumStepsMinusOne() {
        trace.run(listOf(simpleNs["j0"]!!), 2, FeederDirection.DOWNSTREAM)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(traversal, times(3)).addStopCondition(stopConditionCaptor.capture())

        val stopCondition = stopConditionCaptor.firstValue
        assertThat("Step 0 does not stop", !stopCondition(ConductingEquipmentStep(mock())))
        assertThat("Step 1 stops", stopCondition(ConductingEquipmentStep(mock(), 1)))
        assertThat("Step 2 stops", stopCondition(ConductingEquipmentStep(mock(), 2)))
    }

    @Test
    internal fun withDirectionSecondStopConditionChecksStartingEquipment() {
        trace.run(listOf(simpleNs["j0"]!!), feederDirection = FeederDirection.DOWNSTREAM)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(traversal, times(3)).addStopCondition(stopConditionCaptor.capture())

        val stopCondition = stopConditionCaptor.secondValue

        assertThat("Stops on start equipment", stopCondition(ConductingEquipmentStep(simpleNs["j0"]!!)))
        assertThat("Does not stop on other equipment", !stopCondition(ConductingEquipmentStep(Junction())))
    }

    @Test
    internal fun withDirectionThirdStopConditionChecksDirection() {
        val t1 = Terminal()
        val j = Junction().apply { addTerminal(t1) }

        trace.run(listOf(simpleNs["j0"]!!), feederDirection = FeederDirection.DOWNSTREAM)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(traversal, times(3)).addStopCondition(stopConditionCaptor.capture())

        val stopCondition = stopConditionCaptor.lastValue
        doReturn(FeederDirection.DOWNSTREAM, FeederDirection.BOTH, FeederDirection.UPSTREAM).`when`(getTerminalDirection).invoke(t1)

        assertThat("Does not stop with matching feeder direction", !stopCondition(ConductingEquipmentStep(j)))
        assertThat("Stops with partial match on feeder direction", stopCondition(ConductingEquipmentStep(j)))
        assertThat("Stops with mismatch on feeder direction", stopCondition(ConductingEquipmentStep(j)))
    }

    @Test
    internal fun withDirectionStartsFromConnectedAssetsDown() {
        trace.run(listOf(simpleNs["b2"]!!), 2, FeederDirection.DOWNSTREAM)

        verify(traversal).run(any<ConductingEquipment>(), any())
        verify(traversal).run(simpleNs["c3"]!!, false)
    }

    @Test
    internal fun withDirectionStartsFromConnectedAssetsUp() {
        trace.run(listOf(simpleNs["b2"]!!), 2, FeederDirection.UPSTREAM)

        verify(traversal).run(any<ConductingEquipment>(), any())
        verify(traversal).run(simpleNs["c1"]!!, false)
    }

    @Test
    internal fun withDirectionStartsFromConnectedAssetsBoth() {
        val ns = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toJunction(numTerminals = 1) // j4
            .branchFrom("j2", 2)
            .toAcls() // c5
            .toJunction(numTerminals = 1) // j6
            .addFeeder("j0")
            .addFeeder("j6")
            .build()

        trace.run(listOf(ns["j2"]!!), 2, FeederDirection.BOTH)

        verify(traversal, times(2)).run(any<ConductingEquipment>(), any())
        verify(traversal).run(ns["c1"]!!, false)
        verify(traversal).run(ns["c5"]!!, false)
    }

    @Test
    internal fun withDirectionStartsFromConnectedAssetsNone() {
        // We build the network halfway through to assign things to feeders before we add more network
        val ns = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction() // j2
            .toAcls() // c3
            .addFeeder("j0") // fdr4
            .apply {
                build()
                network.get<ConductingEquipment>("j2")!!.addTerminal(Terminal())
            }.branchFrom("j2")
            .toAcls() // c5
            .network

        trace.run(listOf(ns["j2"]!!), 2, FeederDirection.NONE)

        verify(traversal).run(any<ConductingEquipment>(), any())
        verify(traversal).run(ns["c5"]!!, false)
    }

    @Test
    internal fun withDirectionStepActionAddsNextStepToResults() {
        val j = Junction()
        configureRunStepActions(ConductingEquipmentStep(j, 2))

        val results = trace.run(listOf(simpleNs["j0"]!!), 2, FeederDirection.DOWNSTREAM)

        assertThat(results, aMapWithSize(2))
        assertThat(results[simpleNs["j0"]], equalTo(0))
        assertThat(results[j], equalTo(3))
    }

    @Test
    internal fun withDirectionResultsAreFilteredByValidDirectionBoth() {
        val ns = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 1) // j2
            .addFeeder("j0")
            .addFeeder("j2")
            .build()

        configureRunStepActions(ConductingEquipmentStep(ns["j0"]!!), ConductingEquipmentStep(ns["c1"]!!))
        doReturn(FeederDirection.BOTH).`when`(getTerminalDirection).invoke(ns["j0-t1"]!!)
        doReturn(FeederDirection.UPSTREAM).`when`(getTerminalDirection).invoke(ns["c1-t1"]!!)
        doReturn(FeederDirection.NONE).`when`(getTerminalDirection).invoke(ns["c1-t2"]!!)

        val results = trace.run(listOf(ns["j0"]!!), 2, FeederDirection.BOTH)

        assertThat(results, aMapWithSize(1))
        assertThat(results[ns["j0"]], equalTo(0))
    }

    @Test
    internal fun withDirectionResultsAreFilteredByValidDirectionNone() {
        val ns = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toJunction(numTerminals = 1) // j2
            .build()

        configureRunStepActions(ConductingEquipmentStep(ns["j0"]!!), ConductingEquipmentStep(ns["c1"]!!))
        doReturn(FeederDirection.NONE).`when`(getTerminalDirection).invoke(ns["j0-t1"]!!)
        doReturn(FeederDirection.UPSTREAM).`when`(getTerminalDirection).invoke(ns["c1-t1"]!!)
        doReturn(FeederDirection.BOTH).`when`(getTerminalDirection).invoke(ns["c1-t2"]!!)

        val results = trace.run(listOf(ns["j0"]!!), 2, FeederDirection.NONE)

        assertThat(results, aMapWithSize(1))
        assertThat(results[ns["j0"]], equalTo(0))
    }

    @Test
    internal fun resultsOnlyIncludeMinimumStepsGroupedByEquipment() {
        val j1 = Junction()
        val j2 = Junction()
        configureRunStepActions(
            ConductingEquipmentStep(j1, 2),
            ConductingEquipmentStep(j1, 1),
            ConductingEquipmentStep(j2, 0),
            ConductingEquipmentStep(j2, 2)
        )

        val results = trace.run(listOf(mock()), 2)

        assertThat(results, aMapWithSize(2))
        assertThat(results[j1], equalTo(1))
        assertThat(results[j2], equalTo(0))
    }

    private fun configureRunStepActions(vararg steps: ConductingEquipmentStep) {
        doAnswer {
            val stepActionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Unit>()
            verify(traversal).addStepAction(stepActionCaptor.capture())

            val stepAction = stepActionCaptor.firstValue

            steps.forEach(stepAction)
        }.`when`(traversal).run(any<ConductingEquipment>(), any())
    }
}
