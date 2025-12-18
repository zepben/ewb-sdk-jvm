/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class EquipmentTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Equipment("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val equipment = object : Equipment(generateId()) {}

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
            { id -> object : Equipment(id) {} },
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
            { id -> object : Equipment(id) {} },
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
            { id -> object : Equipment(id) {} },
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
            { id -> object : Equipment(id) {} },
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
        val equipment = object : Equipment(generateId()) {}
        val site1 = Site(generateId())
        val site2 = Site(generateId())
        val feeder1 = Feeder(generateId())
        val feeder2 = Feeder(generateId())
        val feeder3 = Feeder(generateId())
        val feeder4 = Feeder(generateId())
        val lvFeeder1 = LvFeeder(generateId())
        val lvFeeder2 = LvFeeder(generateId())
        val lvFeeder3 = LvFeeder(generateId())
        val lvFeeder4 = LvFeeder(generateId())
        val substation1 = Substation(generateId())
        val substation2 = Substation(generateId())
        val lvSub1 = LvSubstation(generateId())
        val lvSub2 = LvSubstation(generateId())

        equipment.addContainer(site1)
        equipment.addContainer(site2)
        equipment.addContainer(feeder1)
        equipment.addContainer(feeder2)
        equipment.addContainer(lvFeeder1)
        equipment.addContainer(lvFeeder2)
        equipment.addContainer(substation1)
        equipment.addContainer(substation2)
        equipment.addContainer(lvSub1)
        equipment.addContainer(lvSub2)

        equipment.addCurrentContainer(feeder3)
        equipment.addCurrentContainer(feeder4)
        equipment.addCurrentContainer(lvFeeder3)
        equipment.addCurrentContainer(lvFeeder4)
        equipment.addCurrentContainer(lvSub1)
        equipment.addCurrentContainer(lvSub2)

        assertThat(equipment.sites, containsInAnyOrder(site1, site2))
        assertThat(equipment.normalFeeders, containsInAnyOrder(feeder1, feeder2))
        assertThat(equipment.currentFeeders, containsInAnyOrder(feeder3, feeder4))
        assertThat(equipment.normalLvFeeders, containsInAnyOrder(lvFeeder1, lvFeeder2))
        assertThat(equipment.currentLvFeeders, containsInAnyOrder(lvFeeder3, lvFeeder4))
        assertThat(equipment.substations, containsInAnyOrder(substation1, substation2))
        assertThat(equipment.normalLvSubstations, containsInAnyOrder(lvSub1, lvSub2))
        assertThat(equipment.currentLvSubstations, containsInAnyOrder(lvSub1, lvSub2))
    }

}
