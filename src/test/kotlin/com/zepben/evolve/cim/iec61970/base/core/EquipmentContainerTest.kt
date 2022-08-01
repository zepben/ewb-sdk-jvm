/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.utils.PrivateCollectionValidator
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
    internal fun normalFeeders() {
        val fdr1 = Feeder()
        val fdr2 = Feeder()
        val fdr3 = Feeder()

        val eq1 = object : Equipment() {}.addContainer(fdr1).addContainer(fdr2)
        val eq2 = object : Equipment() {}.addContainer(fdr2).addContainer(fdr3)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.normalFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
        assertThat(equipmentContainer.currentFeeders(), empty())
    }

    @Test
    internal fun currentFeeders() {
        val fdr1 = Feeder()
        val fdr2 = Feeder()
        val fdr3 = Feeder()

        val eq1 = object : Equipment() {}.addCurrentContainer(fdr1).addCurrentContainer(fdr2)
        val eq2 = object : Equipment() {}.addCurrentContainer(fdr2).addCurrentContainer(fdr3)

        val equipmentContainer = object : EquipmentContainer() {}.addEquipment(eq1).addEquipment(eq2)

        assertThat(equipmentContainer.currentFeeders(), containsInAnyOrder(fdr1, fdr2, fdr3))
        assertThat(equipmentContainer.normalFeeders(), empty())
    }
}
