/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AssignToLvFeedersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c2")
    }

    @Test
    fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op")
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op", "c2")
    }

    @Test
    fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op", "c2")
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    fun stopsAtLvFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    fun stopsAtLvFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp2", "c3")
    }

    @Test
    fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentContainersToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    fun stopsAtHvEquipment() {
        val network = HvEquipmentBelowLvFeederHeadNetwork.create()
        val lvFeeder: LvFeeder = network["lvf11"]!!
        Tracing.assignEquipmentContainersToLvFeeders().run(network)
        validateEquipment(lvFeeder.equipment, "b0", "c1", "c3", "c4", "tx5", "c7", "tx8")
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
