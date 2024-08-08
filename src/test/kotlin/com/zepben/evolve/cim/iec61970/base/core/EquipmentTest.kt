/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class EquipmentTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Equipment() {}.mRID, not(equalTo("")))
        assertThat(object : Equipment("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val equipment = object : Equipment() {}

        assertThat(equipment.inService, equalTo(true))
        assertThat(equipment.normallyInService, equalTo(true))
        assertThat(equipment.commissionedDate, nullValue())

        equipment.fillFields(NetworkService())

        assertThat(equipment.inService, equalTo(false))
        assertThat(equipment.normallyInService, equalTo(false))
        assertThat(equipment.commissionedDate, equalTo(Instant.MIN))
    }

    @Test
    internal fun equipmentContainers() {
        PrivateCollectionValidator.validateUnordered(
            { object : Equipment() {} },
            { id -> object : EquipmentContainer(id) {} },
            Equipment::containers,
            Equipment::numContainers,
            Equipment::getContainer,
            Equipment::addContainer,
            Equipment::removeContainer,
            Equipment::clearContainers
        )
    }

    @Test
    internal fun usagePoints() {
        PrivateCollectionValidator.validateUnordered(
            { object : Equipment() {} },
            ::UsagePoint,
            Equipment::usagePoints,
            Equipment::numUsagePoints,
            Equipment::getUsagePoint,
            Equipment::addUsagePoint,
            Equipment::removeUsagePoint,
            Equipment::clearUsagePoints
        )
    }

    @Test
    internal fun operationalRestrictions() {
        PrivateCollectionValidator.validateUnordered(
            { object : Equipment() {} },
            ::OperationalRestriction,
            Equipment::operationalRestrictions,
            Equipment::numOperationalRestrictions,
            Equipment::getOperationalRestriction,
            Equipment::addOperationalRestriction,
            Equipment::removeOperationalRestriction,
            Equipment::clearOperationalRestrictions
        )
    }

    @Test
    internal fun currentContainers() {
        PrivateCollectionValidator.validateUnordered(
            { object : Equipment() {} },
            { id -> object : EquipmentContainer(id) {} },
            Equipment::currentContainers,
            Equipment::numCurrentContainers,
            Equipment::getCurrentContainer,
            Equipment::addCurrentContainer,
            Equipment::removeCurrentContainer,
            Equipment::clearCurrentContainers
        )
    }

    @Test
    internal fun equipmentContainerFilters() {
        val equipment = object : Equipment() {}
        val site1 = Site()
        val site2 = Site()
        val feeder1 = Feeder()
        val feeder2 = Feeder()
        val feeder3 = Feeder()
        val feeder4 = Feeder()
        val lvFeeder1 = LvFeeder()
        val lvFeeder2 = LvFeeder()
        val lvFeeder3 = LvFeeder()
        val lvFeeder4 = LvFeeder()
        val substation1 = Substation()
        val substation2 = Substation()

        equipment.addContainer(site1)
        equipment.addContainer(site2)
        equipment.addContainer(feeder1)
        equipment.addContainer(feeder2)
        equipment.addContainer(lvFeeder1)
        equipment.addContainer(lvFeeder2)
        equipment.addContainer(substation1)
        equipment.addContainer(substation2)

        equipment.addCurrentContainer(feeder3)
        equipment.addCurrentContainer(feeder4)
        equipment.addCurrentContainer(lvFeeder3)
        equipment.addCurrentContainer(lvFeeder4)

        assertThat(equipment.sites, containsInAnyOrder(site1, site2))
        assertThat(equipment.normalFeeders, containsInAnyOrder(feeder1, feeder2))
        assertThat(equipment.currentFeeders, containsInAnyOrder(feeder3, feeder4))
        assertThat(equipment.normalLvFeeders, containsInAnyOrder(lvFeeder1, lvFeeder2))
        assertThat(equipment.currentLvFeeders, containsInAnyOrder(lvFeeder3, lvFeeder4))
        assertThat(equipment.substations, containsInAnyOrder(substation1, substation2))
    }
}
