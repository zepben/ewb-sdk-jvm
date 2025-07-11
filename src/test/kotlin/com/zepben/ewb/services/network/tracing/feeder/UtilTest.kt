/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.services.network.getT
import com.zepben.ewb.services.network.lvFeederStartPoints
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test

class UtilTest {

    @Test
    fun `findLvFeeders excludes open switches`() {
        //
        // tx0 21 b1(lvf5) 21--c2--2
        //     21 b3(lvf6) 21--c4--2
        val site = Site()
        val network = TestNetworkBuilder()
            .fromPowerTransformer { addToSite(site) } // tx0
            .toBreaker(isNormallyOpen = true, isOpen = true) { addToSite(site) }// b1
            .toAcls() // c2
            .branchFrom("tx0")
            .toBreaker(isNormallyOpen = false, isOpen = false) { addToSite(site) }//b3
            .toAcls() // c4
            .addLvFeeder("b1") // lvf5
            .addLvFeeder("b3") // lvf6
            .network
        val assignToLvFeeders = AssignToLvFeeders(debugLogger = null)

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL, network.getT("b1", 2))
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL, network.getT("b3", 2))
        assignToLvFeeders.run(network, NetworkStateOperators.CURRENT, network.getT("b1", 2))
        assignToLvFeeders.run(network, NetworkStateOperators.CURRENT, network.getT("b3", 2))

        val lvf6 = network.get<LvFeeder>("lvf6")
        val normalLvFeeders = setOf(site).findLvFeeders(network.lvFeederStartPoints, NetworkStateOperators.NORMAL)
        assertThat(normalLvFeeders, containsInAnyOrder(lvf6))

        val currentLvFeeders = setOf(site).findLvFeeders(network.lvFeederStartPoints, NetworkStateOperators.CURRENT)
        assertThat(currentLvFeeders, containsInAnyOrder(lvf6))
    }

    private fun Equipment.addToSite(site: Site) {
        site.addEquipment(this)
        addContainer(site)
        site.addCurrentEquipment(this)
        addCurrentContainer(site)
    }
}
