/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.protection.CurrentRelay
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.testdata.DownstreamFeederStartPointNetwork
import com.zepben.evolve.services.network.testdata.DroppedPhasesNetwork
import com.zepben.evolve.services.network.testdata.FeederStartPointBetweenConductorsNetwork
import com.zepben.evolve.services.network.testdata.FeederStartPointToOpenPointNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
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

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c2")
    }

    @Test
    fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op")
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op", "c2")
    }

    @Test
    fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op", "c2")
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    fun stopsAtLvFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    fun stopsAtLvFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp2", "c3")
    }

    @Test
    fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    fun stopsAtHvEquipment() {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = lvBaseVoltage } // b0
            .toAcls { baseVoltage = lvBaseVoltage } // c1
            .toAcls { baseVoltage = hvBaseVoltage } // c2
            .addLvFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }

        val lvFeeder: LvFeeder = network["lvf3"]!!
        Tracing.assignEquipmentToLvFeeders().run(network)
        validateEquipment(lvFeeder.equipment, "b0", "c1")
    }

    @Test
    fun includesTransformers() {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = lvBaseVoltage } // b0
            .toAcls { baseVoltage = lvBaseVoltage } // c1
            .toPowerTransformer(endActions = listOf({ baseVoltage = lvBaseVoltage }, { baseVoltage = hvBaseVoltage })) // tx2
            .toAcls { baseVoltage = hvBaseVoltage } // c3
            .addLvFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }

        val lvFeeder: LvFeeder = network["lvf4"]!!
        Tracing.assignEquipmentToLvFeeders().run(network)
        validateEquipment(lvFeeder.equipment, "b0", "c1", "tx2")
    }

    @Test
    fun onlyPoweredViaHeadEquipment() {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage } // b0
            .toAcls { baseVoltage = hvBaseVoltage } // c1
            .fromBreaker { baseVoltage = lvBaseVoltage } // b2
            .toAcls { baseVoltage = lvBaseVoltage } // b3
            .connect("c1", "c3", 2, 2)
            .addFeeder("b0")
            .addLvFeeder("b2")
            .network

        val feeder: Feeder = network["fdr4"]!!
        val lvFeeder: LvFeeder = network["lvf5"]!!

        Tracing.assignEquipmentToFeeders().run(network)
        Tracing.assignEquipmentToLvFeeders().run(network)

        assertThat(feeder.normalEnergizedLvFeeders, Matchers.empty())
        assertThat(lvFeeder.normalEnergizingFeeders, Matchers.empty())
    }

    @Test
    fun singleFeederPowersMultipleLvFeeders() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addFeeder("b0") // fdr1
            .addLvFeeder("b0") // lvf2
            .addLvFeeder("b0") // lvf3
            .network

        val feeder: Feeder = network["fdr1"]!!
        val lvFeeder1: LvFeeder = network["lvf2"]!!
        val lvFeeder2: LvFeeder = network["lvf3"]!!

        Tracing.assignEquipmentToFeeders().run(network)
        Tracing.assignEquipmentToLvFeeders().run(network)

        assertThat(feeder.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder1, lvFeeder2))
        assertThat(lvFeeder1.normalEnergizingFeeders, containsInAnyOrder(feeder))
        assertThat(lvFeeder2.normalEnergizingFeeders, containsInAnyOrder(feeder))
    }

    @Test
    fun multipleFeedersPowerSingleLvFeeder() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addFeeder("b0") // fdr1
            .addFeeder("b0") // fdr2
            .addLvFeeder("b0") // lvf3
            .network

        val feeder1: Feeder = network["fdr1"]!!
        val feeder2: Feeder = network["fdr2"]!!
        val lvFeeder: LvFeeder = network["lvf3"]!!

        Tracing.assignEquipmentToFeeders().run(network)
        Tracing.assignEquipmentToLvFeeders().run(network)

        assertThat(feeder1.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(feeder2.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.normalEnergizingFeeders, containsInAnyOrder(feeder1, feeder2))
    }

    @Test
    fun `assigns AuxiliaryEquipment to LvFeeder`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .addLvFeeder("b0")
            .network
            .apply {
                add(CurrentTransformer("a1").apply { terminal = get("c1-t1") })
                add(FaultIndicator("a2").apply { terminal = get("c1-t1") })
            }

        val lvFeeder: LvFeeder = network["lvf2"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "b0", "c1", "a1", "a2")
    }

    @Test
    fun `assigns ProtectionEquipment to LvFeeder`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addLvFeeder("b0")
            .network
            .apply {
                val ps = get<ProtectedSwitch>("b0")!!
                add(CurrentRelay("cr1").apply {
                    ps.addOperatedByProtectionEquipment(this)
                    this.addProtectedSwitch(ps)
                })
                add(CurrentRelay("cr2").apply {
                    ps.addOperatedByProtectionEquipment(this)
                    this.addProtectedSwitch(ps)
                })
            }

        val lvFeeder: LvFeeder = network["lvf1"]!!

        Tracing.assignEquipmentToLvFeeders().run(network)

        validateEquipment(lvFeeder.equipment, "b0", "cr1", "cr2")
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
