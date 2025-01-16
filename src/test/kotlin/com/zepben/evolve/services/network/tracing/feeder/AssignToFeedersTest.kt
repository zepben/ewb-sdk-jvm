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
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AssignToFeedersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
    val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

    private val assignToFeeders = AssignToFeeders()

    @Test
    internal fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create()
        val feeder: Feeder = network["f"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "fsp", "c2")
    }

    @Test
    internal fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false)
        val feeder: Feeder = network["f"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "fsp", "c1", "op")
        validateEquipment(feeder.currentEquipment)
    }

    @Test
    internal fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true)
        val feeder: Feeder = network["f"]!!

        assignToFeeders.run(network, NetworkStateOperators.CURRENT)

        validateEquipment(feeder.equipment)
        validateEquipment(feeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    internal fun stopsAtSubstationTransformers() {
        val network = FeederToSubstationTransformerNetwork.create()
        val feeder: Feeder = network["f"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "fsp", "c1")
    }

    @Test
    internal fun stopsAtFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    internal fun stopsAtFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp2", "c3")
    }

    @Test
    internal fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create()
        val feeder: Feeder = network["f"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    internal fun stopsAtLvEquipment() {
        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage } // b0
            .toAcls { baseVoltage = hvBaseVoltage } // c1
            .toAcls { baseVoltage = lvBaseVoltage } // c2
            .addFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }

        val feeder: Feeder = network["fdr3"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "b0", "c1")
    }

    @Test
    internal fun includesTransformers() {
        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage } // b0
            .toAcls { baseVoltage = hvBaseVoltage } // c1
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage })) // tx2
            .toAcls { baseVoltage = lvBaseVoltage } // c3
            .addFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }

        val feeder: Feeder = network["fdr4"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "b0", "c1", "tx2")
    }

    @Test
    internal fun `assigns AuxiliaryEquipment to Feeder`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .addFeeder("b0")
            .network
            .apply {
                add(CurrentTransformer("a1").apply { terminal = get("c1-t1") })
                add(FaultIndicator("a2").apply { terminal = get("c1-t1") })
            }

        val feeder: Feeder = network["fdr2"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "b0", "c1", "a1", "a2")
    }

    @Test
    internal fun `assigns ProtectionEquipment to Feeder`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addFeeder("b0")
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

        val feeder: Feeder = network["fdr1"]!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "b0", "prsys3")
    }

    @Test
    internal fun `can be run from a single terminal`() {
        //
        // 1 b0 21--c1--2 j2 31--c3--21--c4--2
        //                2
        //                1
        //                |
        //                c5
        //                |
        //                21--c6--2
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toJunction(numTerminals = 3) // j2
            .toAcls() // c3
            .toAcls() // c4
            .fromAcls() // c5
            .toAcls() // c6
            .connect("j2", "c5", 2, 1)
            .addFeeder("b0") // fdr7
            .network

        val feeder = network.get<Feeder>("fdr7")!!
        val junction = network.get<Junction>("j2")!!.also {
            feeder.addEquipment(it)
            it.addContainer(feeder)
        }

        assignToFeeders.run(network, NetworkStateOperators.NORMAL, startTerminal = junction.terminals.last())

        // b0 is included from the network builder.
        // j2 was added to allow us to test the terminal based assignment.
        // c3 and c4 should have been added via the trace.
        // c1, c5 and c6 shouldn't have been added if the assignment only went out t3 of j2.
        validateEquipment(feeder.equipment, "b0", "j2", "c3", "c4")
    }

    @Test
    internal fun `energizes all LV feeders for a dist TX site that is energized`() {
        //
        //                              1--c4--21 b5 2
        // 1 b0 21--c1--21 tx2 21--c3--2
        //                              1--c6--21 b7 2
        //
        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage } // b0
            .toAcls { baseVoltage = hvBaseVoltage } // c1
            .toPowerTransformer(endActions = listOf({ ratedU = hvBaseVoltage.nominalVoltage }, { ratedU = lvBaseVoltage.nominalVoltage })) // tx2
            .toAcls { baseVoltage = lvBaseVoltage } // c3
            .toAcls { baseVoltage = lvBaseVoltage } // c4
            .toBreaker { baseVoltage = lvBaseVoltage } // b5
            .fromAcls { baseVoltage = lvBaseVoltage } // c6
            .toBreaker { baseVoltage = lvBaseVoltage } // b7
            .connect("c3", "c6", 2, 1)
            .addFeeder("b0") // fdr8
            .addLvFeeder("tx2") // lvf9
            .addLvFeeder("b5") // lvf10
            .addLvFeeder("b7") // lvf11
            .addSite("tx2", "c3", "c4", "b5", "c6", "b7") // site12
            .network

        val feeder = network.get<Feeder>("fdr8")!!

        assignToFeeders.run(network, NetworkStateOperators.NORMAL)

        // We ensure the HV trace stopped at the transformer, but the additional LV feeders from b5 and b7 are still
        // marked as energized through the dist substation site.
        validateEquipment(feeder.equipment, "b0", "c1", "tx2")
        assertThat(feeder.normalEnergizedLvFeeders.map { it.mRID }, containsInAnyOrder("lvf9", "lvf10", "lvf11"))
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
