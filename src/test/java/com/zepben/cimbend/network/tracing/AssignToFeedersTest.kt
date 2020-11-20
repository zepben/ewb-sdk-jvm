/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.Equipment
import com.zepben.cimbend.cim.iec61970.base.core.Feeder
import com.zepben.cimbend.testdata.DownstreamFeederStartPointNetwork
import com.zepben.cimbend.testdata.FeederStartPointBetweenConductorsNetwork
import com.zepben.cimbend.testdata.FeederStartPointToOpenPointNetwork
import com.zepben.cimbend.testdata.FeederToSubstationTransformerNetwork
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*
import java.util.function.Consumer

class AssignToFeedersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun stopsAtOpenPoints() {
        val network = FeederStartPointToOpenPointNetwork.create()
        Tracing.assignEquipmentContainersToFeeders().run(network)
        val feeder = network.get(Feeder::class.java, "f")!!
        val mRIDs: MutableList<String> = ArrayList()
        feeder.currentEquipment.forEach(Consumer { equipment: Equipment -> mRIDs.add(equipment.mRID) })
        MatcherAssert.assertThat<List<String>>(mRIDs, Matchers.containsInAnyOrder("fsp", "c2", "op"))
    }

    @Test
    fun appliesToEquipmentOnHeadTerminalSide() {
        val network = FeederStartPointBetweenConductorsNetwork.create()
        Tracing.assignEquipmentContainersToFeeders().run(network)
        val feeder = network.get(Feeder::class.java, "f")!!
        val mRIDs: MutableList<String> = ArrayList()
        feeder.currentEquipment.forEach(Consumer { equipment: Equipment -> mRIDs.add(equipment.mRID) })
        MatcherAssert.assertThat<List<String>>(mRIDs, Matchers.containsInAnyOrder("fsp", "c2"))
    }

    @Test
    fun stopsAtSubstationTransformers() {
        val network = FeederToSubstationTransformerNetwork.create()
        val feeder = network.get(Feeder::class.java, "f")!!
        val mRIDs: MutableList<String> = ArrayList()

        Tracing.assignEquipmentContainersToFeeders().run(network)

        feeder.currentEquipment.forEach(Consumer { equipment: Equipment -> mRIDs.add(equipment.mRID) })
        MatcherAssert.assertThat<List<String>>(mRIDs, Matchers.containsInAnyOrder("fsp", "c2"))
    }

    @Test
    fun stopsAtFeederStartPoints() {
        val network = DownstreamFeederStartPointNetwork.create()
        val feeder = network.get(Feeder::class.java, "f")!!
        val mRIDs: MutableList<String> = ArrayList()

        Tracing.assignEquipmentContainersToFeeders().run(network)

        feeder.currentEquipment.forEach(Consumer { equipment: Equipment -> mRIDs.add(equipment.mRID) })
        MatcherAssert.assertThat<List<String>>(mRIDs, Matchers.containsInAnyOrder("fsp", "c2", "cb"))
    }

}
