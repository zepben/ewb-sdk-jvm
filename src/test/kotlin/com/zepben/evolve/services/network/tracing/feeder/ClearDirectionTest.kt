/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.getT
import com.zepben.evolve.services.network.tracing.feeder.DirectionValidator.validateDirection
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ClearDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val clearDirection = ClearDirection(debugLogger = null)
    private val stateOperators = NetworkStateOperators.NORMAL

    @Test
    fun `simple clear direction`() {
        //
        //              1--c2--2
        //   b0 11--c1--2
        //              1--c3--2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toAcls() // c2
            .fromAcls() // c3
            .connect("c1", "c3", 2, 1)
            .addFeeder("b0")
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), stateOperators)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2)))

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.NONE)
        n.getT("c1", 1).validateDirection(FeederDirection.NONE)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE)
        n.getT("c2", 1).validateDirection(FeederDirection.NONE)
        n.getT("c2", 2).validateDirection(FeederDirection.NONE)
        n.getT("c3", 1).validateDirection(FeederDirection.NONE)
        n.getT("c3", 2).validateDirection(FeederDirection.NONE)
    }

    @Test
    fun `only clears given state`() {
        //
        //
        //   b0 11--c1--2
        //
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .addFeeder("b0")
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), NetworkStateOperators.NORMAL)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2)))

        n.getT("b0", 2).validateDirection(FeederDirection.NONE, NetworkStateOperators.NORMAL)
        n.getT("c1", 1).validateDirection(FeederDirection.NONE, NetworkStateOperators.NORMAL)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE, NetworkStateOperators.NORMAL)
        n.getT("b0", 2).validateDirection(FeederDirection.DOWNSTREAM, NetworkStateOperators.CURRENT)
        n.getT("c1", 1).validateDirection(FeederDirection.UPSTREAM, NetworkStateOperators.CURRENT)
        n.getT("c1", 2).validateDirection(FeederDirection.DOWNSTREAM, NetworkStateOperators.CURRENT)
    }

    @Test
    fun `can clear from any terminal and only steps externally`() {
        //
        //              1--c2--2
        //   b0 11--c1--2
        //              1--c3--2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toAcls() // c2
            .fromAcls() // c3
            .connect("c1", "c3", 2, 1)
            .addFeeder("b0")
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("c1", 2), stateOperators)
        assertThat(headTerminals, empty())

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("c1", 1).validateDirection(FeederDirection.UPSTREAM)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE)
        n.getT("c2", 1).validateDirection(FeederDirection.NONE)
        n.getT("c2", 2).validateDirection(FeederDirection.NONE)
        n.getT("c3", 1).validateDirection(FeederDirection.NONE)
        n.getT("c3", 2).validateDirection(FeederDirection.NONE)
    }

    @Test
    fun `clears loops`() {
        //
        //              1--c2--2
        //   b0 11--c1--2      1--c3--2
        //              1--c4--2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toAcls() // c2
            .toAcls() // c3
            .fromAcls() // c4
            .connect("c4", "c1", 1, 2)
            .connect("c4", "c3", 2, 1)
            .addFeeder("b0")
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), stateOperators)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2)))

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.NONE)
        n.getT("c1", 1).validateDirection(FeederDirection.NONE)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE)
        n.getT("c2", 1).validateDirection(FeederDirection.NONE)
        n.getT("c2", 2).validateDirection(FeederDirection.NONE)
        n.getT("c3", 1).validateDirection(FeederDirection.NONE)
        n.getT("c3", 2).validateDirection(FeederDirection.NONE)
        n.getT("c4", 1).validateDirection(FeederDirection.NONE)
        n.getT("c4", 2).validateDirection(FeederDirection.NONE)
    }

    @Test
    fun `stops at open points`() {
        //
        //   b0 11--c1--21 b2 21--c3--21 b4 2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .toBreaker() // b4
            .addFeeder("b0")
            .addFeeder("b4", 1)
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), stateOperators)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2)))

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.NONE)
        n.getT("c1", 1).validateDirection(FeederDirection.NONE)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE)
        n.getT("b2", 1).validateDirection(FeederDirection.NONE)
        n.getT("b2", 2).validateDirection(FeederDirection.UPSTREAM)
        n.getT("c3", 1).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("c3", 2).validateDirection(FeederDirection.UPSTREAM)
        n.getT("b4", 1).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("b4", 2).validateDirection(FeederDirection.NONE)
    }

    @Test
    fun `returns all encountered feeder head terminals`() {
        //
        //   b0 11--c1--21 b2 21--c3--21 b4 2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .toAcls() // c3
            .toBreaker() // b4
            .addFeeder("b0")
            .addFeeder("b4", 1)
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), stateOperators)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2), n.getT("b4", 1)))

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.NONE)
        n.getT("c1", 1).validateDirection(FeederDirection.NONE)
        n.getT("c1", 2).validateDirection(FeederDirection.NONE)
        n.getT("b2", 1).validateDirection(FeederDirection.NONE)
        n.getT("b2", 2).validateDirection(FeederDirection.NONE)
        n.getT("c3", 1).validateDirection(FeederDirection.NONE)
        n.getT("c3", 2).validateDirection(FeederDirection.NONE)
        n.getT("b4", 1).validateDirection(FeederDirection.NONE)
        n.getT("b4", 2).validateDirection(FeederDirection.NONE)
    }

    @Test
    fun `supports clearing with BusbarSection`() {
        //
        //        1--c3--2
        //   b0 1 1 o1
        //        1--c2--2
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toOther<BusbarSection>(numTerminals = 1) // o1
            .toAcls() // c2
            .fromAcls() // c3
            .connect("o1", "c3", 1, 1)
            .addFeeder("b0")
            .buildAndLog("b0")

        val headTerminals = clearDirection.run(n.getT("b0", 2), stateOperators)
        assertThat(headTerminals, containsInAnyOrder(n.getT("b0", 2)))

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.NONE)
        n.getT("o1", 1).validateDirection(FeederDirection.NONE)
        n.getT("c2", 1).validateDirection(FeederDirection.NONE)
        n.getT("c2", 2).validateDirection(FeederDirection.NONE)
        n.getT("c3", 1).validateDirection(FeederDirection.NONE)
        n.getT("c3", 2).validateDirection(FeederDirection.NONE)
    }


    @Test
    internal fun `can reapply directions after cleaning with a dual fed loop`() {
        // Special case test that if someone tries to be clever and just remove directions rather than clear and reapply,
        // they need to make sure the directions in their clever change still pass the final assertions in this test.
        //
        //              1--c2--2
        //   b0 11--c1--2      1--c3--21 b4
        //              1--c5--2
        //
        val n = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toAcls() // c2
            .toAcls() // c3
            .toBreaker() // b4
            .fromAcls() // c5
            .connect("c5", "c1", 1, 2)
            .connect("c5", "c3", 2, 1)
            .addFeeder("b0")
            .addFeeder("b4", 1)
            .buildAndLog("b0")

        val breaker = n.get<Breaker>("b4")!!
        stateOperators.setOpen(breaker, true)

        val headTerminals = clearDirection.run(n.getT("b4", 1), stateOperators)
        DirectionLogger.trace(n["b0"])

        headTerminals
            .filter { !stateOperators.isOpen(it.conductingEquipment!!) }
            .forEach { Tracing.setDirection().run(it, stateOperators) }

        DirectionLogger.trace(n["b0"])

        n.getT("b0", 1).validateDirection(FeederDirection.NONE)
        n.getT("b0", 2).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("c1", 1).validateDirection(FeederDirection.UPSTREAM)
        n.getT("c1", 2).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("c2", 1).validateDirection(FeederDirection.BOTH)
        n.getT("c2", 2).validateDirection(FeederDirection.BOTH)
        n.getT("c3", 1).validateDirection(FeederDirection.UPSTREAM)
        n.getT("c3", 2).validateDirection(FeederDirection.DOWNSTREAM)
        n.getT("b4", 1).validateDirection(FeederDirection.UPSTREAM)
        n.getT("b4", 2).validateDirection(FeederDirection.NONE)
        n.getT("c5", 1).validateDirection(FeederDirection.BOTH)
        n.getT("c5", 2).validateDirection(FeederDirection.BOTH)
    }

    private fun Terminal.validateDirection(feederDirection: FeederDirection) = validateDirection(feederDirection, stateOperators)

    private fun TestNetworkBuilder.buildAndLog(vararg logFrom: String) = build().apply {
        DirectionLogger.trace(logFrom.map { get(it) })
    }

}
