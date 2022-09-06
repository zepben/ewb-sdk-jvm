/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.connectivity.ConductingEquipmentStep
import com.zepben.evolve.services.network.tracing.connectivity.ConnectedEquipmentTraversal
import com.zepben.evolve.services.network.tracing.traversals.Traversal
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*

internal class FindSwerEquipmentTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val trace1 = mock<ConnectedEquipmentTraversal>()
    private val trace2 = mock<ConnectedEquipmentTraversal>()
    private val createTrace = mock<() -> ConnectedEquipmentTraversal>().also { doReturn(trace1, trace2, null).`when`(it).invoke() }
    private val findSwerEquipment = FindSwerEquipment(createTrace)

    @Test
    internal fun `processes all feeders in a network`() {
        val networkService = NetworkService()
        val feeder1 = Feeder().also { networkService.add(it) }
        val feeder2 = Feeder().also { networkService.add(it) }
        val j1 = Junction().also { networkService.add(it) }
        val j2 = Junction().also { networkService.add(it) }
        val j3 = Junction().also { networkService.add(it) }

        val findSwerEquipment = spy<FindSwerEquipment>().also {
            doReturn(listOf(j1, j2)).`when`(it).find(feeder1)
            doReturn(listOf(j2, j3)).`when`(it).find(feeder2)
        }

        assertThat(findSwerEquipment.find(networkService), containsInAnyOrder(j1, j2, j3))

        verify(findSwerEquipment).find(feeder1)
        verify(findSwerEquipment).find(feeder2)
    }

    @Test
    internal fun `only runs trace from SWER transformers, and only runs non SWER from LV`() {
        val ns = TestNetworkBuilder()
            .fromBreaker() // b0
            .toPowerTransformer() // tx1
            .toAcls() // c2
            .toPowerTransformer(listOf(PhaseCode.AB, PhaseCode.A)) // tx3
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx6
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c7
            .addFeeder("b0") // fdr8
            .build()
        doReturn(trace1, trace2, trace1, trace2, null).`when`(createTrace).invoke()

        assertThat(findSwerEquipment.find(ns["fdr8"]!!), ns.createContainsInAnyOrder("tx3", "tx6"))

        verify(trace1, times(2)).run(any<ConductingEquipment>(), any())
        verify(trace1).run(ns["c4"]!!)
        verify(trace1).run(ns["c5"]!!)

        verify(trace2).run(any<ConductingEquipment>(), any())
        verify(trace2).run(ns["c7"]!!)
    }

    @Test
    internal fun `does not run from SWER regulators`() {
        val ns = TestNetworkBuilder()
            .fromBreaker(PhaseCode.A) // b0
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.A)) // tx1
            .toAcls(PhaseCode.A) // c2
            .addFeeder("b0") // fdr3
            .build()

        findSwerEquipment.find(ns["fdr3"]!!)

        verifyNoMoreInteractions(trace1)
        verifyNoMoreInteractions(trace2)
    }

    @Test
    internal fun `validate SWER trace stop conditions`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .fromPowerTransformer() // tx2
            .addFeeder("tx0") // fdr3
            .build()

        findSwerEquipment.find(ns["fdr3"]!!)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(trace1, times(2)).addStopCondition(stopConditionCaptor.capture())

        stopConditionCaptor.firstValue.also { stopCondition ->
            assertThat("Stops on equipment in swer collection", stopCondition(ConductingEquipmentStep(ns["tx0"]!!)))
            assertThat("Does not stop on equipment not in SWER collection", !stopCondition(ConductingEquipmentStep(ns["tx1"]!!)))
            assertThat("Does not stop on equipment not in SWER collection", !stopCondition(ConductingEquipmentStep(ns["tx2"]!!)))
        }

        stopConditionCaptor.secondValue.also { stopCondition ->
            assertThat("Does not stop on equipment with SWER terminal", !stopCondition(ConductingEquipmentStep(ns["tx0"]!!)))
            assertThat("Does not stop on equipment with SWER terminal", !stopCondition(ConductingEquipmentStep(ns["tx1"]!!)))
            assertThat("Stops on equipment without SWER terminals", stopCondition(ConductingEquipmentStep(ns["tx2"]!!)))
        }
    }

    @Test
    internal fun `validate SWER trace step action`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx1
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx2
            .fromBreaker() // b3
            .addFeeder("tx0") // fdr4
            .build()

        doAnswer {
            it.getArgument<Traversal.StepAction<ConductingEquipmentStep>>(0).also { stepAction ->
                stepAction.apply(ConductingEquipmentStep(ns["tx1"]!!), false)
                stepAction.apply(ConductingEquipmentStep(ns["tx2"]!!), true)
                stepAction.apply(ConductingEquipmentStep(ns["b3"]!!), true)
            }
            trace1
        }.`when`(trace1).addStepAction(any<Traversal.StepAction<ConductingEquipmentStep>>())

        // tx2 should not have been added as it was stopping. b3 should have been added even though it was stopping.
        assertThat(findSwerEquipment.find(ns["fdr4"]!!), ns.createContainsInAnyOrder("tx0", "tx1", "b3"))

        // This is here to make sure the above block is actually run.
        verify(trace1).addStepAction(any<Traversal.StepAction<ConductingEquipmentStep>>())
    }

    @Test
    internal fun `validate LV trace stop condition`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .addFeeder("tx0") // fdr2
            .build()

        findSwerEquipment.find(ns["fdr2"]!!)

        val stopConditionCaptor = argumentCaptor<(ConductingEquipmentStep) -> Boolean>()
        verify(trace2).addStopCondition(stopConditionCaptor.capture())

        stopConditionCaptor.firstValue.also { stopCondition ->
            assertThat("Stops on equipment in swer collection", stopCondition(ConductingEquipmentStep(ns["tx0"]!!)))
            assertThat("Does not stop on equipment not in SWER collection", !stopCondition(ConductingEquipmentStep(ns["tx1"]!!)))
        }
    }

    @Test
    internal fun `validate LV trace step action`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx1
            .addFeeder("tx0") // fdr2
            .build()

        doAnswer {
            it.getArgument<(ConductingEquipmentStep) -> Unit>(0).also { stepAction ->
                stepAction(ConductingEquipmentStep(ns["tx1"]!!))
            }
            trace2
        }.`when`(trace2).addStepAction(any<(ConductingEquipmentStep) -> Unit>())

        assertThat(findSwerEquipment.find(ns["fdr2"]!!), ns.createContainsInAnyOrder("tx0", "tx1"))

        // This is here to make sure the above block is actually run.
        verify(trace2).addStepAction(any<(ConductingEquipmentStep) -> Unit>())
    }

    @Test
    internal fun `runs off multiple terminals`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.A, PhaseCode.AN, PhaseCode.AN)) // tx0
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c1
            .branchFrom("tx0", 1)
            .toAcls(PhaseCode.A) // c2
            .branchFrom("tx0", 2)
            .toAcls(PhaseCode.A) // c3
            .branchFrom("tx0", 3)
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c4
            .addFeeder("tx0") // fdr5
            .build()

        FindSwerEquipment().find(ns["fdr5"]!!)
    }

    @Test
    internal fun `does not loop back out of swer from LV`() {
        val ns = TestNetworkBuilder()
            .fromJunction(numTerminals = 1) // j0
            .toAcls() // c1
            .toAcls(PhaseCode.A) // c2
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx3
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c4
            .toPowerTransformer(listOf(PhaseCode.AN, PhaseCode.A)) // tx5
            .toAcls(PhaseCode.A) // c6
            .connect("c6", "c1", 2, 1)
            .addFeeder("j0") // fdr7
            .build()

        // We need to run the actual trace rather than a mock to make sure it does not loop back through the LV.
        assertThat(FindSwerEquipment().find(ns), ns.createContainsInAnyOrder("c2", "tx3", "c4", "tx5", "c6"))
    }

    private fun NetworkService.createContainsInAnyOrder(vararg mRIDs: String): Matcher<Iterable<ConductingEquipment>?>? =
        containsInAnyOrder(*mRIDs.map { get<ConductingEquipment>(it) }.toTypedArray())

}
