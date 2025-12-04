/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.services.common.testdata.generateId
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

}
