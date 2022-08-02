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
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AssignToFeedersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create()
        val feeder: Feeder = network["f"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder.equipment, "fsp", "c2")
    }

    @Test
    fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false)
        val feeder: Feeder = network["f"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder.equipment, "fsp", "c1", "op")
        validateEquipment(feeder.currentEquipment, "fsp", "c1", "op", "c2")
    }

    @Test
    fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true)
        val feeder: Feeder = network["f"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder.equipment, "fsp", "c1", "op", "c2")
        validateEquipment(feeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    fun stopsAtSubstationTransformers() {
        val network = FeederToSubstationTransformerNetwork.create()
        val feeder: Feeder = network["f"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder.equipment, "fsp", "c1")
    }

    @Test
    fun stopsAtFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    fun stopsAtFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp2", "c3")
    }

    @Test
    fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create()
        val feeder: Feeder = network["f"]!!

        Tracing.assignEquipmentContainersToFeeders().run(network)

        validateEquipment(feeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    fun stopsAtLvEquipment() {
        val network = HvLvNetwork.create()
        val feeder: Feeder = network["fdr12"]!!
        Tracing.assignEquipmentContainersToFeeders().run(network)
        validateEquipment(feeder.equipment, "b0", "c1", "j2", "c4", "j5", "c6", "tx7", "c9", "tx10")
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
