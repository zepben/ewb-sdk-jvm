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
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.Cut
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

    @Test
    fun `traversing segment with clamps from t1 includes all clamp steps`() {
        val network = aclsWithClampsNetwork()

        val breaker: Breaker = network["b0"]!!
        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val clamp2: Clamp = network["clamp2"]!!

        val currentPath = breaker.t2..segment.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(segment.t1..clamp1.t1, segment.t1..clamp2.t1, segment.t1..segment.t2))
    }

    @Test
    fun `traversing segment with clamps from t2 includes all clamps steps`() {
        val network = aclsWithClampsNetwork()

        val breaker: Breaker = network["b2"]!!
        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val clamp2: Clamp = network["clamp2"]!!

        val currentPath = breaker.t1..segment.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(segment.t2..clamp2.t1, segment.t2..clamp1.t1, segment.t2..segment.t1))
    }

    @Test
    fun `non traverse step to segment t1 traverses towards t2 stopping at cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val b0: Breaker = network["b0"]!!
        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val cut1: Cut = network["cut1"]!!

        val currentPath = b0.t2..segment.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(segment.t1..clamp1.t1, segment.t1..cut1.t1))
    }

    @Test
    fun `non traverse step to segment t2 traverses towards t1 stopping at cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val b2: Breaker = network["b2"]!!
        val segment: AcLineSegment = network["c1"]!!
        val clamp4: Clamp = network["clamp4"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = b2.t1..segment.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(segment.t2..clamp4.t1, segment.t2..cut2.t2))
    }

    @Test
    fun `traverse step to cut t1 steps externally and across cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val cut1: Cut = network["cut1"]!!
        val c4: AcLineSegment = network["c4"]!!

        val currentPath = segment.t1..cut1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut1.t1..cut1.t2, cut1.t1..c4.t1))
    }

    @Test
    fun `traverse step to cut t2 steps externally and across cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val cut2: Cut = network["cut2"]!!
        val c9: AcLineSegment = network["c9"]!!

        val currentPath = segment.t2..cut2.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut2.t2..cut2.t1, cut2.t2..c9.t1))
    }

    @Test
    fun `non traverse step to cut t1 traverses segment towards t1 and internally through cut to t2`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val cut1: Cut = network["cut1"]!!
        val c4: AcLineSegment = network["c4"]!!

        val currentPath = c4.t1..cut1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut1.t1..clamp1.t1, cut1.t1..segment.t1, cut1.t1..cut1.t2))
    }

    @Test
    fun `non traverse step to cut t2 traverses segment towards t2 and internally through cut to t1`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val clamp4: Clamp = network["clamp4"]!!
        val cut2: Cut = network["cut2"]!!
        val c9: AcLineSegment = network["c9"]!!

        val currentPath = c9.t1..cut2.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut2.t2..clamp4.t1, cut2.t2..segment.t2, cut2.t2..cut2.t1))
    }

    @Test
    fun `non traverse step to clamp traverses segment in both directions`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val cut1: Cut = network["cut1"]!!
        val c3: AcLineSegment = network["c3"]!!

        val currentPath = c3.t1..clamp1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..segment.t1, clamp1.t1..cut1.t1))
    }

    @Test
    fun `traverse step to clamp traces externally and does not traverse back along segment`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val c3: AcLineSegment = network["c3"]!!

        val currentPath = segment.t1..clamp1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..c3.t1))
    }

    @Test
    fun `non traverse step to clamp between cuts traverses segment both ways stopping at cuts`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c6: AcLineSegment = network["c6"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = c6.t1..clamp2.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp2.t1..cut1.t2, clamp2.t1..clamp3.t1, clamp2.t1..cut2.t1))
    }

    @Test
    fun `non traverse external step to cut t2 between cuts traverses segment towards t2 stopping at next cut and steps internally to cut t1`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c5: AcLineSegment = network["c5"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = c5.t1..cut1.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut1.t2..clamp2.t1, cut1.t2..clamp3.t1, cut1.t2..cut2.t1, cut1.t2..cut1.t1))
    }

    @Test
    fun `non traverse external step to cut t1 between cuts traverses segment towards t1 stopping at next cut and steps internally to cut t2`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c8: AcLineSegment = network["c8"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = c8.t1..cut2.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut2.t1..clamp3.t1, cut2.t1..clamp2.t1, cut2.t1..cut1.t2, cut2.t1..cut2.t2))
    }

    @Test
    fun `internal step to cut t2 between cuts steps externally and traverses segment towards t2 stopping at next cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c5: AcLineSegment = network["c5"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = cut1.t1..cut1.t2
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut1.t2..clamp2.t1, cut1.t2..clamp3.t1, cut1.t2..cut2.t1, cut1.t2..c5.t1))
    }

    @Test
    fun `internal step to cut t1 between cuts steps externally and traverses segment towards t1 stopping at next cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c8: AcLineSegment = network["c8"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!

        val currentPath = cut2.t2..cut2.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut2.t1..clamp2.t1, cut2.t1..clamp3.t1, cut2.t1..cut1.t2, cut2.t1..c8.t1))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `supports legacy AcLineSegment with multiple terminals`() {
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .network

        val b0: Breaker = network["b0"]!!
        val c1: AcLineSegment = network["c1"]!!
        c1.midSpanTerminalsEnabled = true
        c1.addTerminal(Terminal())

        val currentPath = b0.t2..c1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        assertThat(nextPaths, containsInAnyOrder(c1.t1..c1.t2, c1.t1..c1.t3))
    }

    private fun busbarNetwork(): NetworkService {
        //         1
        //         b0
        //  bbs1 1-2-1 bbs2
        //  -----|   |-----
        //  1    1   1    1
        //  b3   b4  b5   b6
        //  2    2   2    2
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

    private fun aclsWithClampsNetwork(): NetworkService {
        //
        //           clamp1
        //           |
        // 1 b0 21---*--c1--*---21 b2 2
        //                  |
        //                  clamp2
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val segment: AcLineSegment = network["c1"]!!

        segment.withClamp(network, 1.0)
        segment.withClamp(network, 2.0)

        return network
    }

    private fun aclsWithClampsAndCutsNetwork(): NetworkService {
        //
        //          2                     2
        //          c3          2         c7          2
        //          1           c5        1           c9
        //          1 clamp1    1         1 clamp3    1
        //          |           |         |           |
        // 1 b0 21--*--*1 cut1 2*--*--c1--*--*1 cut2 2*--*--21 b2 2
        //             |           |         |           |
        //             1           1 clamp2  1           1 clamp4
        //             c4          1         c8          1
        //             2           c6        2           c10
        //                         2                     2
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .fromAcls() // c3
            .fromAcls() // c4
            .fromAcls() // c5
            .fromAcls() // c6
            .fromAcls() // c7
            .fromAcls() // c8
            .fromAcls() // c9
            .fromAcls() // c10
            .network

        val segment: AcLineSegment = network["c1"]!!

        val clamp1 = segment.withClamp(network, 1.0)
        val cut1 = segment.withCut(network, 2.0)
        val clamp2 = segment.withClamp(network, 3.0)
        val clamp3 = segment.withClamp(network, 4.0)
        val cut2 = segment.withCut(network, 5.0)
        val clamp4 = segment.withClamp(network, 6.0)

        network.connect(clamp1.t1, network.get<ConductingEquipment>("c3")!!.t1)
        network.connect(cut1.t1, network.get<ConductingEquipment>("c4")!!.t1)
        network.connect(cut1.t2, network.get<ConductingEquipment>("c5")!!.t1)
        network.connect(clamp2.t1, network.get<ConductingEquipment>("c6")!!.t1)
        network.connect(clamp3.t1, network.get<ConductingEquipment>("c7")!!.t1)
        network.connect(cut2.t1, network.get<ConductingEquipment>("c8")!!.t1)
        network.connect(cut2.t2, network.get<ConductingEquipment>("c9")!!.t1)
        network.connect(clamp4.t1, network.get<ConductingEquipment>("c10")!!.t1)

        return network
    }

    private fun AcLineSegment.withClamp(network: NetworkService, lengthFromTerminal1: Double): Clamp {
        val clamp = Clamp("clamp${numClamps() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addClamp(clamp)
        network.add(clamp)

        return clamp
    }

    private fun AcLineSegment.withCut(network: NetworkService, lengthFromTerminal1: Double): Cut {
        val cut = Cut("cut${numCuts() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            addTerminal(Terminal("$mRID-t2"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addCut(cut)
        network.add(cut)
        return cut
    }

    private operator fun Terminal.rangeTo(other: Terminal): NetworkTraceStep.Path = NetworkTraceStep.Path(this, other)
}
