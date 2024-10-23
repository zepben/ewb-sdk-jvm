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
import com.zepben.evolve.cim.iec61970.base.protection.ProtectionRelayScheme
import com.zepben.evolve.cim.iec61970.base.protection.ProtectionRelaySystem
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.testdata.DownstreamFeederStartPointNetwork
import com.zepben.evolve.services.network.testdata.DroppedPhasesNetwork
import com.zepben.evolve.services.network.testdata.FeederStartPointBetweenConductorsNetwork
import com.zepben.evolve.services.network.testdata.FeederStartPointToOpenPointNetwork
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AssignToLvFeedersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c2")
    }

    @Test
    internal fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op")
        validateEquipment(lvFeeder.currentEquipment)
    }

    @Test
    internal fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        AssignToLvFeeders(NetworkStateOperators.CURRENT).run(network)

        validateEquipment(lvFeeder.equipment)
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    internal fun stopsAtLvFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    internal fun stopsAtLvFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp2", "c3")
    }

    @Test
    internal fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    internal fun stopsAtHvEquipment() {
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
        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)
        validateEquipment(lvFeeder.equipment, "b0", "c1")
    }

    @Test
    internal fun includesTransformers() {
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
        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)
        validateEquipment(lvFeeder.equipment, "b0", "c1", "tx2")
    }

    @Test
    internal fun onlyPoweredViaHeadEquipment() {
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

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)
        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        assertThat(feeder.normalEnergizedLvFeeders, empty())
        assertThat(lvFeeder.normalEnergizingFeeders, empty())
    }

    @Test
    internal fun singleFeederPowersMultipleLvFeeders() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addFeeder("b0") // fdr1
            .addLvFeeder("b0") // lvf2
            .addLvFeeder("b0") // lvf3
            .network

        val feeder: Feeder = network["fdr1"]!!
        val lvFeeder1: LvFeeder = network["lvf2"]!!
        val lvFeeder2: LvFeeder = network["lvf3"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)
        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        assertThat(feeder.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder1, lvFeeder2))
        assertThat(lvFeeder1.normalEnergizingFeeders, containsInAnyOrder(feeder))
        assertThat(lvFeeder2.normalEnergizingFeeders, containsInAnyOrder(feeder))
    }

    @Test
    internal fun multipleFeedersPowerSingleLvFeeder() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addFeeder("b0") // fdr1
            .addFeeder("b0") // fdr2
            .addLvFeeder("b0") // lvf3
            .network

        val feeder1: Feeder = network["fdr1"]!!
        val feeder2: Feeder = network["fdr2"]!!
        val lvFeeder: LvFeeder = network["lvf3"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)
        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        assertThat(feeder1.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(feeder2.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.normalEnergizingFeeders, containsInAnyOrder(feeder1, feeder2))
    }

    @Test
    internal fun `assigns AuxiliaryEquipment to LvFeeder`() {
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

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder.equipment, "b0", "c1", "a1", "a2")
    }

    @Test
    internal fun `assigns ProtectionRelaySystems to LvFeeder`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addLvFeeder("b0")
            .network
        val ps = network.get<ProtectedSwitch>("b0")!!
        val cr = CurrentRelay("cr1").apply {
            ps.addRelayFunction(this)
            addProtectedSwitch(ps)
        }
        val prs = ProtectionRelayScheme("prs2").apply {
            cr.addScheme(this)
            addFunction(cr)
        }
        val prsys = ProtectionRelaySystem("prsys3").apply {
            prs.system = this
            addScheme(prs)
        }
        network.add(cr)
        network.add(prs)
        network.add(prsys)

        val lvFeeder: LvFeeder = network["lvf1"]!!

        AssignToLvFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(lvFeeder.equipment, "b0", "prsys3")
    }

    @Test
    internal fun `assigns normal and current energising feeders based on state`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addLvFeeder("b0") // lvf1
            .network

        val normalFeeder = Feeder()
        val currentFeeder = Feeder()
        val breaker: Breaker = network["b0"]!!
        val lvFeeder: LvFeeder = network["lvf1"]!!

        breaker.addContainer(normalFeeder)
        breaker.addCurrentContainer(currentFeeder)

        Tracing.assignEquipmentToLvFeeders().run(network)

        assertThat(normalFeeder.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.normalEnergizingFeeders, containsInAnyOrder(normalFeeder))

        assertThat(currentFeeder.currentEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.currentEnergizingFeeders, containsInAnyOrder(currentFeeder))
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
