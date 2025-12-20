/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayScheme
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelaySystem
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.ewb.cim.iec61970.base.core.BaseVoltage
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.wires.Breaker
import com.zepben.ewb.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.getT
import com.zepben.ewb.services.network.lvFeederStartPoints
import com.zepben.ewb.services.network.testdata.DownstreamFeederStartPointNetwork
import com.zepben.ewb.services.network.testdata.DroppedPhasesNetwork
import com.zepben.ewb.services.network.testdata.FeederStartPointBetweenConductorsNetwork
import com.zepben.ewb.services.network.testdata.FeederStartPointToOpenPointNetwork
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AssignToLvFeedersTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    val hvBaseVoltage = BaseVoltage(generateId()).apply { nominalVoltage = 11000 }
    val lvBaseVoltage = BaseVoltage(generateId()).apply { nominalVoltage = 400 }

    private val assignToLvFeeders = AssignToLvFeeders(debugLogger = null)

    @Test
    internal fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder.equipment, "fsp", "c2")
    }

    @Test
    internal fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder.equipment, "fsp", "c1", "op")
        validateEquipment(lvFeeder.currentEquipment)
    }

    @Test
    internal fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true, makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.CURRENT)

        validateEquipment(lvFeeder.equipment)
        validateEquipment(lvFeeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    internal fun stopsAtLvFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    internal fun stopsAtLvFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2, makeFeedersLv = true)
        val lvFeeder1: LvFeeder = network["f1"]!!
        val lvFeeder2: LvFeeder = network["f2"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(lvFeeder2.equipment, "fsp2", "c3")
    }

    @Test
    internal fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create(makeFeederLv = true)
        val lvFeeder: LvFeeder = network["f"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    internal fun stopsAtHvEquipment() {
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
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)
        validateEquipment(lvFeeder.equipment, "b0", "c1")
    }

    @Test
    internal fun includesTransformers() {
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
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)
        validateEquipment(lvFeeder.equipment, "b0", "c1", "tx2")
    }

    @Test
    internal fun onlyPoweredViaHeadEquipment() {
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

        AssignToFeeders(debugLogger = null).run(network, NetworkStateOperators.NORMAL)
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

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

        AssignToFeeders(debugLogger = null).run(network, NetworkStateOperators.NORMAL)
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

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

        AssignToFeeders(debugLogger = null).run(network, NetworkStateOperators.NORMAL)
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

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

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

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

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(lvFeeder.equipment, "b0", "prsys3")
    }

    @Test
    internal fun `assigns PowerElectronicUnits to Feeder`() {
        val peu1 = PhotoVoltaicUnit("peu1")

        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toPowerElectronicsConnection { addUnit(peu1); peu1.powerElectronicsConnection = this }  // pec1
            .addLvFeeder("b0")
            .network

        network.add(peu1)

        val feeder: LvFeeder = network["lvf2"]!!

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        validateEquipment(feeder.equipment, "b0", "pec1", "peu1")
    }

    @Test
    internal fun `lv feeders detect back feeds for energizing feeders`() {
        //
        // 1 b0 21 tx1 21--c2--21--c3--21 tx4 21 b5 2
        //
        // NOTE: Transformer is deliberately set to use the hv voltage as their base voltage to ensure they are still processed.
        //
        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage } // b0
            .toPowerTransformer { baseVoltage = hvBaseVoltage }// tx1
            .toAcls { baseVoltage = lvBaseVoltage } // c2
            .toAcls { baseVoltage = lvBaseVoltage } // c3
            .toPowerTransformer { baseVoltage = hvBaseVoltage } // tx4
            .toBreaker { baseVoltage = hvBaseVoltage } // b5
            .addFeeder("b0") // fdr6
            .addLvFeeder("tx1") // lvf7
            .addLvFeeder("tx4", 1) // lvf8
            .addFeeder("b5", 1) // fdr9
            .network

        val feeder6: Feeder = network["fdr6"]!!
        val feeder9: Feeder = network["fdr9"]!!
        val lvFeeder7: LvFeeder = network["lvf7"]!!
        val lvFeeder8: LvFeeder = network["lvf8"]!!

        AssignToFeeders(debugLogger = null).run(network, NetworkStateOperators.NORMAL)
        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)

        assertThat(feeder6.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder7, lvFeeder8))
        assertThat(feeder9.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder7, lvFeeder8))
        assertThat(lvFeeder7.normalEnergizingFeeders, containsInAnyOrder(feeder6, feeder9))
        assertThat(lvFeeder8.normalEnergizingFeeders, containsInAnyOrder(feeder6, feeder9))
    }

    @Test
    internal fun `lv feeders detect back feeds for dist substation sites`() {
        fun runWithOperators(operators: NetworkStateOperators) {
            //
            //                1--c2--21 b3 2
            // 1 tx0 21--c1--2
            //                1--c4--21 b5 21--c6--21 b7 2 Feeder
            //
            val network = TestNetworkBuilder()
                .fromPowerTransformer(endActions = listOf({ ratedU = hvBaseVoltage.nominalVoltage }, { ratedU = lvBaseVoltage.nominalVoltage })) // tx0
                .toAcls { baseVoltage = lvBaseVoltage } // c1
                .toAcls { baseVoltage = lvBaseVoltage } // c2
                .toBreaker { baseVoltage = lvBaseVoltage } // b3
                .fromAcls { baseVoltage = lvBaseVoltage } // c4
                .toBreaker { baseVoltage = lvBaseVoltage } // b5
                .toAcls { baseVoltage = lvBaseVoltage } // c6
                .toBreaker { baseVoltage = lvBaseVoltage } // b7
                .connect("c1", "c4", 2, 1)
                .addLvFeeder("tx0") // lvf8
                .addLvFeeder("b3") // lvf9
                .addLvFeeder("b5") // lvf10
                .addLvFeeder("b7", 1) // lvf11
                .addSite("tx0", "c1", "c2", "b3", "c4", "b5") // site12
                .network

            val b7 = network.get<Breaker>("b7")!!

            val feeder = Feeder(generateId())
            val lvFeeder8 = network.get<LvFeeder>("lvf8")!!.also { operators.associateEnergizingFeeder(feeder, it) }
            val lvFeeder9 = network.get<LvFeeder>("lvf9")!!.also { operators.associateEnergizingFeeder(feeder, it) }
            val lvFeeder10 = network.get<LvFeeder>("lvf10")!!.also { operators.associateEnergizingFeeder(feeder, it) }

            // We create an LV feeder to assign from b7 with its associated energizing feeder, which we will test is assigned to all LV feeders
            // in the dist substation site, not just the one on b5.
            val backFeed = Feeder(generateId())
            val lvFeeder = LvFeeder(generateId()).also { operators.associateEnergizingFeeder(backFeed, it) }

            assignToLvFeeders.run(
                b7.terminals.first(),
                network.lvFeederStartPoints,
                terminalToAuxEquipment = emptyMap(),
                listOf(lvFeeder),
                operators
            )

            // Make sure the LV feeder traced stopped at the first LV feeder head.
            assertThat(operators.getEquipment(lvFeeder).map { it.mRID }, containsInAnyOrder("b7", "c6", "b5"))

            // Make sure both feeders are now considered to be energizing all LV feeders.
            assertThat(operators.getEnergizedLvFeeders(feeder), containsInAnyOrder(lvFeeder, lvFeeder8, lvFeeder9, lvFeeder10))
            assertThat(operators.getEnergizedLvFeeders(backFeed), containsInAnyOrder(lvFeeder, lvFeeder8, lvFeeder9, lvFeeder10))

            // Make sure all LV feeders are now considered to be energized by both feeders.
            assertThat(operators.getEnergizingFeeders(lvFeeder), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder8), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder9), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder10), containsInAnyOrder(feeder, backFeed))
        }

        runWithOperators(NetworkStateOperators.NORMAL)
        runWithOperators(NetworkStateOperators.CURRENT)
    }

    @Test
    internal fun `lv feeders detect back feeds for lv substations`() {
        fun runWithOperators(operators: NetworkStateOperators) {
            //
            //                1--c2--21 b3 2
            // 1 tx0 21--c1--2
            //                1--c4--21 b5 21--c6--21 b7 2
            //
            val network = TestNetworkBuilder()
                .fromPowerTransformer(endActions = listOf({ ratedU = hvBaseVoltage.nominalVoltage }, { ratedU = lvBaseVoltage.nominalVoltage })) // tx0
                .toAcls { baseVoltage = lvBaseVoltage } // c1
                .toAcls { baseVoltage = lvBaseVoltage } // c2
                .toBreaker { baseVoltage = lvBaseVoltage } // b3
                .fromAcls { baseVoltage = lvBaseVoltage } // c4
                .toBreaker { baseVoltage = lvBaseVoltage } // b5
                .toAcls { baseVoltage = lvBaseVoltage } // c6
                .toBreaker { baseVoltage = lvBaseVoltage } // b7
                .connect("c1", "c4", 2, 1)
                .addLvFeeder("tx0") // lvf8
                .addLvFeeder("b3") // lvf9
                .addLvFeeder("b5") // lvf10
                .addLvFeeder("b7", 1) // lvf11
                .addLvSubstation("tx0", "c1", "c2", "b3", "c4", "b5") // lvs12
                .network

            val b7 = network.get<Breaker>("b7")!!

            val feeder = Feeder(generateId())
            val lvSub = network.get<LvSubstation>("lvs12")!!.also { operators.associateEnergizingFeeder(feeder, it) }
            val lvFeeder8 = network.get<LvFeeder>("lvf8")!!.also { operators.associateEnergizingFeeder(feeder, it); it.normalEnergizingLvSubstation = lvSub }
            val lvFeeder9 = network.get<LvFeeder>("lvf9")!!.also { operators.associateEnergizingFeeder(feeder, it); it.normalEnergizingLvSubstation = lvSub }
            val lvFeeder10 = network.get<LvFeeder>("lvf10")!!.also { operators.associateEnergizingFeeder(feeder, it); it.normalEnergizingLvSubstation = lvSub }

            // We create an LV feeder to assign from b7 with its associated energizing feeder, which we will test is assigned to all LV feeders
            // in the dist substation site, not just the one on b5.
            val backFeed = Feeder(generateId())
            val lvFeeder = LvFeeder(generateId()).also { operators.associateEnergizingFeeder(backFeed, it) }

            assignToLvFeeders.run(
                b7.terminals.first(),
                network.lvFeederStartPoints,
                terminalToAuxEquipment = emptyMap(),
                listOf(lvFeeder),
                operators
            )

            // Make sure the LV feeder traced stopped at the first LV feeder head.
            assertThat(operators.getEquipment(lvFeeder).map { it.mRID }, containsInAnyOrder("b7", "c6", "b5"))

            // Make sure both feeders are now considered to be energizing all LV feeders.
            assertThat(operators.getEnergizedLvFeeders(feeder), containsInAnyOrder(lvFeeder, lvFeeder8, lvFeeder9, lvFeeder10))
            assertThat(operators.getEnergizedLvFeeders(backFeed), containsInAnyOrder(lvFeeder, lvFeeder8, lvFeeder9, lvFeeder10))

            // Make sure all LV feeders are now considered to be energized by both feeders.
            assertThat(operators.getEnergizingFeeders(lvFeeder), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder8), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder9), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvFeeder10), containsInAnyOrder(feeder, backFeed))
            assertThat(operators.getEnergizingFeeders(lvSub), containsInAnyOrder(feeder, backFeed))
        }

        runWithOperators(NetworkStateOperators.NORMAL)
        runWithOperators(NetworkStateOperators.CURRENT)
    }

    @Test
    internal fun `assigns normal and current energising feeders based on state`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .addLvFeeder("b0") // lvf1
            .network

        val normalFeeder = Feeder(generateId())
        val currentFeeder = Feeder(generateId())
        val breaker: Breaker = network["b0"]!!
        val lvFeeder: LvFeeder = network["lvf1"]!!

        breaker.addContainer(normalFeeder)
        breaker.addCurrentContainer(currentFeeder)

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL)
        assignToLvFeeders.run(network, NetworkStateOperators.CURRENT)

        assertThat(normalFeeder.normalEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.normalEnergizingFeeders, containsInAnyOrder(normalFeeder))

        assertThat(currentFeeder.currentEnergizedLvFeeders, containsInAnyOrder(lvFeeder))
        assertThat(lvFeeder.currentEnergizingFeeders, containsInAnyOrder(currentFeeder))
    }

    @Test
    internal fun `does not trace out from terminal belonging to open switch`() {
        //
        // 1 b0 21--c1--2
        //
        val network = TestNetworkBuilder()
            .fromBreaker(isNormallyOpen = true) // b0
            .toAcls() // c1
            .addLvFeeder("b0") // lvf2
            .network

        assignToLvFeeders.run(network, NetworkStateOperators.NORMAL, network.getT("b0", 2))

        val feeder = network.get<LvFeeder>("lvf2")!!
        validateEquipment(feeder.equipment, "b0")
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
