/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.testing.TestNetworkBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

class NetworkTraceStepPathProviderTest {

    private val pathProvider = NetworkTraceStepPathProvider(NetworkStateOperators.NORMAL)

    @Test
    fun `current external path steps internally`() {
        val network = TestNetworkBuilder()
            .fromAcls() // c0
            .toJunction(numTerminals = 3) // j1
            .network

        val c0: ConductingEquipment = network["c0"]!!
        val j1: ConductingEquipment = network["j1"]!!

        val currentPath = c0.t2..j1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        assertThat(nextPaths, containsInAnyOrder(j1.t1..j1.t2, j1.t1..j1.t3))
    }

    @Test
    fun `current internal path steps externally`() {
        val network = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .fromAcls() // c2
            .connect("j0", "c2", 2, 1)
            .network

        val j0: ConductingEquipment = network["j0"]!!
        val c1: ConductingEquipment = network["c1"]!!
        val c2: ConductingEquipment = network["c2"]!!

        val currentPath = j0.t1..j0.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        assertThat(nextPaths, containsInAnyOrder(j0.t2..c1.t1, j0.t2..c2.t1))
    }

    @Test
    fun `only steps to in service equipment`() {
        val network = TestNetworkBuilder()
            .fromJunction() // j0
            .toAcls() // c1
            .fromAcls { normallyInService = false } // c2
            .connect("j0", "c2", 2, 1)
            .network

        val j0: ConductingEquipment = network["j0"]!!
        val c1: ConductingEquipment = network["c1"]!!

        val currentPath = j0.t1..j0.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        // Should not step to c2 because it is not in service
        assertThat(nextPaths, containsInAnyOrder(j0.t2..c1.t1))
    }

    @Test
    fun `only includes followed phases`() {
        val network = TestNetworkBuilder()
            .fromAcls() // c0
            .toPowerTransformer(listOf(PhaseCode.ABC, PhaseCode.A, PhaseCode.B, PhaseCode.C)) // tx1
            .network

        val c0: ConductingEquipment = network["c0"]!!
        val tx1: ConductingEquipment = network["tx1"]!!

        val currentPath = NetworkTraceStep.Path(c0.t2, tx1.t1, listOf(NominalPhasePath(SPK.A, SPK.A), NominalPhasePath(SPK.B, SPK.B)))
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        assertThat(
            nextPaths,
            containsInAnyOrder(
                NetworkTraceStep.Path(tx1.t1, tx1.t2, listOf(NominalPhasePath(SPK.A, SPK.A))),
                NetworkTraceStep.Path(tx1.t1, tx1.t3, listOf(NominalPhasePath(SPK.B, SPK.B)))
            )
        )
    }

    @Test
    fun `stepping externally to connectivity node with busbars only goes to busbars`() {
        val network = busbarNetwork()

        val b0: ConductingEquipment = network["b0"]!!
        val bbs1: ConductingEquipment = network["bbs1"]!!
        val bbs2: ConductingEquipment = network["bbs2"]!!

        val currentPath = b0.t1..b0.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        // Should only contain steps to busbars
        assertThat(nextPaths, containsInAnyOrder(b0.t2..bbs1.t1, b0.t2..bbs2.t1))
    }

    @Test
    fun `stepping externally from busbars does not step to busbars or original from terminal`() {
        val network = busbarNetwork()

        val bbs1: ConductingEquipment = network["bbs1"]!!
        val b0: ConductingEquipment = network["b0"]!!
        val b3: ConductingEquipment = network["b3"]!!
        val b4: ConductingEquipment = network["b4"]!!
        val b5: ConductingEquipment = network["b5"]!!
        val b6: ConductingEquipment = network["b6"]!!

        val currentPath = b0.t2..bbs1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        // Should not contain bbs2 and should not step back to b0
        assertThat(nextPaths, containsInAnyOrder(bbs1.t1..b3.t1, bbs1.t1..b4.t1, bbs1.t1..b5.t1, bbs1.t1..b6.t1))
    }

    private fun busbarNetwork(): NetworkService {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toBusbarSection() // bbs1
            .branchFrom("b0", 2)
            .toBusbarSection() // bbs2
            .branchFrom("bbs1", 1)
            .toBreaker() // b3
            .branchFrom("bbs1", 1)
            .toBreaker() // b4
            .branchFrom("bbs2", 1)
            .toBreaker() // b5
            .branchFrom("bbs2", 1)
            .toBreaker() // b6
            .network

        val bbs1: ConductingEquipment = network["bbs1"]!!
        val bbs2: ConductingEquipment = network["bbs2"]!!
        val b0: ConductingEquipment = network["b0"]!!
        val b3: ConductingEquipment = network["b3"]!!
        val b4: ConductingEquipment = network["b4"]!!
        val b5: ConductingEquipment = network["b5"]!!
        val b6: ConductingEquipment = network["b6"]!!

        // Make sure all the terminals that should be considered in the next paths are connected to the same connectivity node
        assertThat(b0.t2.connectivityNode?.terminals, containsInAnyOrder(b0.t2, bbs1.t1, bbs2.t1, b3.t1, b4.t1, b5.t1, b6.t1))

        return network
    }

    private operator fun Terminal.rangeTo(other: Terminal): NetworkTraceStep.Path = NetworkTraceStep.Path(this, other)
}
