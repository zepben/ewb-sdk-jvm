/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing

import com.zepben.ewb.cim.iec61970.base.core.BaseVoltage
import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

internal class FindSwerEquipmentTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val stateOperators = spy(NetworkStateOperators.NORMAL)
    private val findSwerEquipment = FindSwerEquipment(debugLogger = null)

    @Test
    internal fun `processes all feeders in a network`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.AB, PhaseCode.A)) // tx0
            .fromPowerTransformer(listOf(PhaseCode.AB, PhaseCode.A)) // tx1
            .addFeeder("tx0") // fdr2
            .addFeeder("tx1") // fdr3
            .build()

        val findSwerEquipmentSpy = spy(findSwerEquipment)
        findSwerEquipmentSpy.find(ns, stateOperators)

        verify(findSwerEquipmentSpy).find(ns["fdr2"]!!, stateOperators)
        verify(stateOperators).getEquipment(ns["fdr2"]!!)
        verify(findSwerEquipmentSpy).find(ns["fdr3"]!!, stateOperators)
        verify(stateOperators).getEquipment(ns["fdr3"]!!)
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
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN, PhaseCode.AN)) // tx6
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c7
            .toBreaker(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // b8
            .branchFrom("tx6", 2)
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 11000 } } // c9
            .addFeeder("b0") // fdr10
            .build()

        assertThat(findSwerEquipment.find(ns["fdr10"]!!, stateOperators), ns.createContainsInAnyOrder("tx3", "c4", "c5", "tx6", "c7", "b8"))
    }

    @Test
    internal fun `does not run from SWER regulators`() {
        val ns = TestNetworkBuilder()
            .fromBreaker(PhaseCode.A) // b0
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.A)) // tx1
            .toAcls(PhaseCode.A) // c2
            .addFeeder("b0") // fdr3
            .build()

        assertThat(findSwerEquipment.find(ns["fdr3"]!!, stateOperators), empty())
    }

    @Test
    internal fun `does not trace through other transformers that will be traced`() {
        val ns = TestNetworkBuilder()
            .fromAcls(PhaseCode.AN) // c0
            .toPowerTransformer(listOf(PhaseCode.AN, PhaseCode.A)) // tx1
            .toAcls(PhaseCode.A) // c2
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.A)) // tx3
            .toAcls(PhaseCode.A) // c4
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx5
            .toAcls(PhaseCode.AN) // c6
            .addFeeder("c0") // fdr7
            .build()

        assertThat(findSwerEquipment.find(ns, stateOperators), ns.createContainsInAnyOrder("tx1", "c2", "tx3", "c4", "tx5"))
    }

    @Test
    internal fun `SWER includes open switches and stops at them`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.AN, PhaseCode.A)) // tx0
            .toBreaker(isNormallyOpen = true) // b1
            .toAcls() // c2
            .addFeeder("tx0") // fdr3
            .build()

        assertThat(findSwerEquipment.find(ns["fdr3"]!!, stateOperators), ns.createContainsInAnyOrder("tx0", "b1"))
        verify(stateOperators).isOpen(ns["b1"]!!)
    }

    @Test
    internal fun `LV includes open switches and stops at them`() {
        val ns = TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx0
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c1
            .toBreaker(PhaseCode.AN, isNormallyOpen = true) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // b2
            .toAcls(PhaseCode.AN) { baseVoltage = BaseVoltage().apply { nominalVoltage = 415 } } // c3
            .addFeeder("tx0") // fdr4
            .build()

        assertThat(findSwerEquipment.find(ns["fdr4"]!!, stateOperators), ns.createContainsInAnyOrder("tx0", "c1", "b2"))
        verify(stateOperators).isOpen(ns["b2"]!!)
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

        assertThat(findSwerEquipment.find(ns["fdr5"]!!, stateOperators), ns.createContainsInAnyOrder("tx0", "c1", "c2", "c3", "c4"))
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
        assertThat(findSwerEquipment.find(ns, stateOperators), ns.createContainsInAnyOrder("c2", "tx3", "c4", "tx5", "c6"))
    }

    private fun NetworkService.createContainsInAnyOrder(vararg mRIDs: String): Matcher<Iterable<ConductingEquipment>?>? =
        containsInAnyOrder(*mRIDs.map { get<ConductingEquipment>(it)!! }.toTypedArray())

}
