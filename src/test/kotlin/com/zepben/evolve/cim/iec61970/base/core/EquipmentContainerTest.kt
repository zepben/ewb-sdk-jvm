/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.protobuf.cim.iec61970.base.wires.AcLineSegment
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.eq

internal class EquipmentContainerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : EquipmentContainer() {}.mRID, not(equalTo("")))
        assertThat(object : EquipmentContainer("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun equipment() {
        PrivateCollectionValidator.validate(
            { object : EquipmentContainer() {} },
            { id, _ -> object : Equipment(id) {} },
            EquipmentContainer::numEquipment,
            EquipmentContainer::getEquipment,
            EquipmentContainer::equipment,
            EquipmentContainer::addEquipment,
            EquipmentContainer::removeEquipment,
            EquipmentContainer::clearEquipment
        )
    }

    @Test
    internal fun currentEquipment() {
        PrivateCollectionValidator.validate(
            { object: EquipmentContainer() {} },
            { id, _ -> object : Equipment(id) {} },
            EquipmentContainer::numCurrentEquipment,
            EquipmentContainer::getCurrentEquipment,
            EquipmentContainer::currentEquipment,
            EquipmentContainer::addCurrentEquipment,
            EquipmentContainer::removeCurrentEquipment,
            EquipmentContainer::clearCurrentEquipment
        )
    }

    @Test
    internal fun currentEquipmentMirrorsNormalEquipment() {
        val ec = object : EquipmentContainer() {}
        val eq1 = object : Equipment("eq1") {}
        val eq2 = object : Equipment("eq2") {}

        ec.addEquipment(eq1)
        assertThat(ec.getCurrentEquipment("eq1"), equalTo(eq1))

        ec.addCurrentEquipment(eq2)
        assertThat(ec.getEquipment("eq2"), equalTo(eq2))
    }

    @Test
    internal fun normalFeeders() {
        val fdr1 = Feeder()
        val fdr2 = Feeder()
        val fdr3 = Feeder()
        val substation = Substation()
        val lvFdr = LvFeeder()

        val eq1 = object : Equipment() {}.addContainer(fdr1).addContainer(fdr2).addContainer(substation)
        val eq2 = object : Equipment() {}.addContainer(fdr2).addContainer(fdr3).addContainer(lvFdr)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
        assertThat(equipmentContainer.currentFeeders(), empty())
    }

    @Test
    internal fun currentFeeders() {
        val fdr1 = Feeder()
        val fdr2 = Feeder()
        val fdr3 = Feeder()
        val substation = Substation()
        val lvFdr = LvFeeder()

        val eq1 = object : Equipment() {}.addCurrentContainer(fdr1).addCurrentContainer(fdr2).addCurrentContainer(substation)
        val eq2 = object : Equipment() {}.addCurrentContainer(fdr2).addCurrentContainer(fdr3).addCurrentContainer(lvFdr)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalFeeders(), empty())
        assertThat(equipmentContainer.currentFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
    }

    @Test
    internal fun normalLvFeeders() {
        val lvFdr1 = LvFeeder()
        val lvFdr2 = LvFeeder()
        val lvFdr3 = LvFeeder()
        val substation = Substation()
        val fdr = Feeder()

        val eq1 = object : Equipment() {}.addContainer(lvFdr1).addContainer(lvFdr2).addContainer(substation)
        val eq2 = object : Equipment() {}.addContainer(lvFdr2).addContainer(lvFdr3).addContainer(fdr)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalLvFeeders(), containsInAnyOrder(lvFdr1, lvFdr2, lvFdr3))
        assertThat(equipmentContainer.currentLvFeeders(), empty())
    }

    @Test
    internal fun currentLvFeeders() {
        val lvFdr1 = LvFeeder()
        val lvFdr2 = LvFeeder()
        val lvFdr3 = LvFeeder()
        val substation = Substation()
        val fdr = Feeder()

        val eq1 = object : Equipment() {}.addCurrentContainer(lvFdr1).addCurrentContainer(lvFdr2).addCurrentContainer(substation)
        val eq2 = object : Equipment() {}.addCurrentContainer(lvFdr2).addCurrentContainer(lvFdr3).addCurrentContainer(fdr)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalLvFeeders(), empty())
        assertThat(equipmentContainer.currentLvFeeders(), containsInAnyOrder(lvFdr1, lvFdr2, lvFdr3))
    }

}
