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
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.networktrace.NetworkStateOperators
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

    @Test
    internal fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create()
        val feeder: Feeder = network["f"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "fsp", "c2")
    }

    @Test
    internal fun stopsAtNormallyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = true, currentlyOpen = false)
        val feeder: Feeder = network["f"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "fsp", "c1", "op")
        validateEquipment(feeder.currentEquipment)
    }

    @Test
    internal fun stopsAtCurrentlyOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create(normallyOpen = false, currentlyOpen = true)
        val feeder: Feeder = network["f"]!!

        AssignToFeeders(NetworkStateOperators.CURRENT).run(network)

        validateEquipment(feeder.equipment)
        validateEquipment(feeder.currentEquipment, "fsp", "c1", "op")
    }

    @Test
    internal fun stopsAtSubstationTransformers() {
        val network = FeederToSubstationTransformerNetwork.create()
        val feeder: Feeder = network["f"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "fsp", "c1")
    }

    @Test
    internal fun stopsAtFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create(1)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp1", "c2", "fsp2")
    }

    @Test
    internal fun stopsAtFeederStartPointsReversedHeadTerminal() {
        val network = DownstreamFeederStartPointNetwork.create(2)
        val feeder1: Feeder = network["f1"]!!
        val feeder2: Feeder = network["f2"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder1.equipment, "fsp1", "c2", "fsp2")
        validateEquipment(feeder2.equipment, "fsp2", "c3")
    }

    @Test
    internal fun handlesDroppedPhases() {
        val network = DroppedPhasesNetwork.create()
        val feeder: Feeder = network["f"]!!

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "fcb", "acls1", "acls2", "acls3", "iso", "acls4", "tx")
    }

    @Test
    internal fun stopsAtLvEquipment() {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

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

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "b0", "c1")
    }

    @Test
    internal fun includesTransformers() {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

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

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

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

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

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

        AssignToFeeders(NetworkStateOperators.NORMAL).run(network)

        validateEquipment(feeder.equipment, "b0", "prsys3")
    }

    private fun validateEquipment(equipment: Collection<Equipment>, vararg expectedMRIDs: String) {
        assertThat(equipment.map { it.mRID }.toList(), containsInAnyOrder(*expectedMRIDs))
    }

}
