/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EquipmentContainerTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : EquipmentContainer("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun equipment() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : EquipmentContainer(id) {} },
            { id -> object : Equipment(id) {} },
            EquipmentContainer::equipment,
            EquipmentContainer::numEquipment,
            EquipmentContainer::getEquipment,
            EquipmentContainer::addEquipment,
            EquipmentContainer::removeEquipment,
            EquipmentContainer::clearEquipment
        )
    }

    @Test
    internal fun currentEquipment() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : EquipmentContainer(id) {} },
            { id -> object : Equipment(id) {} },
            EquipmentContainer::currentEquipment,
            EquipmentContainer::numCurrentEquipment,
            EquipmentContainer::getCurrentEquipment,
            EquipmentContainer::addCurrentEquipment,
            EquipmentContainer::removeCurrentEquipment,
            EquipmentContainer::clearCurrentEquipment
        )
    }

    @Test
    internal fun currentEquipmentMirrorsNormalEquipment() {
        val ec = object : EquipmentContainer(generateId()) {}
        val eq1 = object : Equipment("eq1") {}
        val eq2 = object : Equipment("eq2") {}

        ec.addEquipment(eq1)
        assertThat(ec.getCurrentEquipment("eq1"), equalTo(eq1))

        ec.addCurrentEquipment(eq2)
        assertThat(ec.getEquipment("eq2"), equalTo(eq2))
    }

    @Test
    internal fun normalFeeders() {
        val fdr1 = Feeder(generateId())
        val fdr2 = Feeder(generateId())
        val fdr3 = Feeder(generateId())
        val substation = Substation(generateId())
        val lvFdr = LvFeeder(generateId())

        val eq1 = object : Equipment(generateId()) {}.addContainer(fdr1).addContainer(fdr2).addContainer(substation)
        val eq2 = object : Equipment(generateId()) {}.addContainer(fdr2).addContainer(fdr3).addContainer(lvFdr)

        val equipmentContainer = object : EquipmentContainer(generateId()) {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
        assertThat(equipmentContainer.currentFeeders(), empty())
    }

    @Test
    internal fun currentFeeders() {
        val fdr1 = Feeder(generateId())
        val fdr2 = Feeder(generateId())
        val fdr3 = Feeder(generateId())
        val substation = Substation(generateId())
        val lvFdr = LvFeeder(generateId())

        val eq1 = object : Equipment(generateId()) {}.addCurrentContainer(fdr1).addCurrentContainer(fdr2).addCurrentContainer(substation)
        val eq2 = object : Equipment(generateId()) {}.addCurrentContainer(fdr2).addCurrentContainer(fdr3).addCurrentContainer(lvFdr)

        val equipmentContainer = object : EquipmentContainer(generateId()) {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalFeeders(), empty())
        assertThat(equipmentContainer.currentFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
    }

    @Test
    internal fun detectsEdgeTerminalsCorrectly() {
        val network = TestNetworkBuilder()
            .fromPowerTransformer()  // tx0
            .toBusbarSection()       // bbs1
            .toBreaker()             // b2  edge of substation, feeder
            .toAcls()                // c3
            .toPowerTransformer()    // tx4 edge of feeder, lv substation, tx4 lv feeder
            .toBusbarSection()       // bbs5
            .toBreaker()             // b6  edge of lv substation, tx4 lv feeder, b6 lv feeder
            .toAcls()                // c7
            .toEnergyConsumer()      // ec8
            .branchFrom("tx0")
            .toBreaker()             // b9  edge of substation
            .toAcls()                // c10
            .addFeeder("b2")    // fdr11
            .addLvFeeder("tx4") // lvf12
            .addLvFeeder("b6")  // lvf13
            .addLvSubstation("tx4", "bbs5", "b6")   // lvs14
            .addSubstation("tx0", "bbs1", "b2", "b9")     // sub15
            .build()

        val feeder = network.get<Feeder>("fdr11")!!
        val lvfTx = network.get<LvFeeder>("lvf12")!!
        val lvfB = network.get<LvFeeder>("lvf13")!!
        val lvSub = network.get<LvSubstation>("lvs14")!!
        val sub = network.get<Substation>("sub15")!!


        assertThat(edgeEquipMrids(feeder), contains("b2"))
        assertThat(edgeEquipMrids(lvfTx), contains("tx4", "b6"))
        assertThat(edgeEquipMrids(lvfB), contains("b6"))
        assertThat(edgeEquipMrids(lvSub), contains("tx4", "b6"))
        assertThat(edgeEquipMrids(sub), contains("b2", "b9"))

        assertThat(edgeEquipMrids(feeder, NetworkStateOperators.CURRENT), contains("b2"))
        assertThat(edgeEquipMrids(lvfTx, NetworkStateOperators.CURRENT), contains("tx4", "b6"))
        assertThat(edgeEquipMrids(lvfB, NetworkStateOperators.CURRENT), contains("b6"))
        assertThat(edgeEquipMrids(lvSub, NetworkStateOperators.CURRENT), contains("tx4", "b6"))
        assertThat(edgeEquipMrids(sub, NetworkStateOperators.CURRENT), contains("b2", "b9"))
    }

    @Test
    internal fun detectsEdgeTerminalsForOpenSwitch() {
        val network = TestNetworkBuilder()
            .fromPowerTransformer()  // tx0
            .toBusbarSection()       // bbs1
            .toBreaker()             // b2
            .toAcls()                // c3
            .branchFrom("tx0", 1)
            .toAcls()                // c4
            .toBreaker(isNormallyOpen = true, isOpen = true)  // b5
            .toAcls()               // c6
            .branchFrom("bbs1")
            .toAcls()               // c7
            .toBreaker()            // b8
            .toAcls()               // c9
            .addSubstation("tx0", "bbs1", "b2", "c4", "b5", "c7") // sub10
            .build()

        val sub = network.get<Substation>("sub10")!!

        assertThat(edgeEquipMrids(sub), containsInAnyOrder("b2", "b5", "c7"))
        assertThat(edgeEquipMrids(sub, NetworkStateOperators.CURRENT), containsInAnyOrder("b2", "b5", "c7"))
    }

    private fun edgeEquipMrids(ec: EquipmentContainer, stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) =
        ec.edgeTerminals(stateOperators).mapNotNull { it.conductingEquipment }.map { it.mRID }
}
