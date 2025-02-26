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
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

class NetworkTraceStepPathProviderTest {

    private val pathProvider = NetworkTraceStepPathProvider(NetworkStateOperators.NORMAL)

    @Test
    fun `current external path steps internally`() {
        //
        //             2
        //  1--c0--2 1 j1
        //             3
        //
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
        //
        //  1 j0 21--c1--2
        //       1
        //       c2
        //       2
        //
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
        //
        //  1 j0 21--c1--2
        //       1
        //       c2 (not in service)
        //       2
        //
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
        //
        //            2 (A)
        //  1--c0--21 tx1 3 (B)
        //            4 (C)
        //
        val network = TestNetworkBuilder()
            .fromAcls() // c0
            .toPowerTransformer(listOf(PhaseCode.ABC, PhaseCode.A, PhaseCode.B, PhaseCode.C)) // tx1
            .network

        val c0: ConductingEquipment = network["c0"]!!
        val tx1: ConductingEquipment = network["tx1"]!!

        val currentPath = NetworkTraceStep.Path(c0.t2, tx1.t1, null, listOf(NominalPhasePath(SPK.A, SPK.A), NominalPhasePath(SPK.B, SPK.B)))
        val nextPaths = pathProvider.nextPaths(currentPath).toList()

        assertThat(
            nextPaths,
            containsInAnyOrder(
                NetworkTraceStep.Path(tx1.t1, tx1.t2, null, listOf(NominalPhasePath(SPK.A, SPK.A))),
                NetworkTraceStep.Path(tx1.t1, tx1.t3, null, listOf(NominalPhasePath(SPK.B, SPK.B)))
                // Should not contain tx1 terminal 4 because it's not in the phase paths
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
        assertThat(nextPaths, containsInAnyOrder(segment.t1..<clamp1.t1, segment.t1..<clamp2.t1, segment.t1..<segment.t2))
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
        assertThat(nextPaths, containsInAnyOrder(segment.t2..<clamp2.t1, segment.t2..<clamp1.t1, segment.t2..<segment.t1))
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
        assertThat(nextPaths, containsInAnyOrder(segment.t1..<clamp1.t1, segment.t1..<cut1.t1))
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
        assertThat(nextPaths, containsInAnyOrder(segment.t2..<clamp4.t1, segment.t2..<cut2.t2))
    }

    @Test
    fun `traverse step to cut t1 steps externally and across cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val cut1: Cut = network["cut1"]!!
        val c4: AcLineSegment = network["c4"]!!

        val currentPath = segment.t1..<cut1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(cut1.t1..cut1.t2, cut1.t1..c4.t1))
    }

    @Test
    fun `traverse step to cut t2 steps externally and across cut`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val cut2: Cut = network["cut2"]!!
        val c9: AcLineSegment = network["c9"]!!

        val currentPath = segment.t2..<cut2.t2
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
        assertThat(nextPaths, containsInAnyOrder(cut1.t1..<clamp1.t1, cut1.t1..<segment.t1, cut1.t1..cut1.t2))
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
        assertThat(nextPaths, containsInAnyOrder(cut2.t2..<clamp4.t1, cut2.t2..<segment.t2, cut2.t2..cut2.t1))
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
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..<segment.t1, clamp1.t1..<cut1.t1))
    }

    @Test
    fun `traverse step to clamp traces externally and does not traverse back along segment`() {
        val network = aclsWithClampsAndCutsNetwork()

        val segment: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val c3: AcLineSegment = network["c3"]!!

        val currentPath = segment.t1..<clamp1.t1
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
        assertThat(nextPaths, containsInAnyOrder(clamp2.t1..<cut1.t2, clamp2.t1..<clamp3.t1, clamp2.t1..<cut2.t1))
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
        assertThat(nextPaths, containsInAnyOrder(cut1.t2..<clamp2.t1, cut1.t2..<clamp3.t1, cut1.t2..<cut2.t1, cut1.t2..cut1.t1))
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
        assertThat(nextPaths, containsInAnyOrder(cut2.t1..<clamp3.t1, cut2.t1..<clamp2.t1, cut2.t1..<cut1.t2, cut2.t1..cut2.t2))
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
        assertThat(nextPaths, containsInAnyOrder(cut1.t2..<clamp2.t1, cut1.t2..<clamp3.t1, cut1.t2..<cut2.t1, cut1.t2..c5.t1))
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
        assertThat(nextPaths, containsInAnyOrder(cut2.t1..<clamp2.t1, cut2.t1..<clamp3.t1, cut2.t1..<cut1.t2, cut2.t1..c8.t1))
    }

    @Test
    fun `starting on clamp terminal flagged as traversed segment only steps externally`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c3: AcLineSegment = network["c3"]!!
        val clamp1: Clamp = network["clamp1"]!!

        val nextPaths = pathProvider.nextPaths(clamp1.t1..<clamp1.t1).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..c3.t1))
    }

    @Test
    fun `starting on clamp terminal that flagged as not traversed segment steps externally and traverses`() {
        val network = aclsWithClampsAndCutsNetwork()

        val c3: AcLineSegment = network["c3"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val cut1: Cut = network["cut1"]!!
        val c1: AcLineSegment = network["c1"]!!

        val nextPaths = pathProvider.nextPaths(clamp1.t1..clamp1.t1).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..c3.t1, clamp1.t1..<c1.t1, clamp1.t1..<cut1.t1))
    }

    @Test
    fun `traverse with cut with unknown length from t1 does not return clamp with known length from t1`() {
        //
        //  (Cut with null length is treated as at 0.0)
        //  1 b0 21*1 cut1 2*-c1-*-21 b2 2
        //                       1
        //                       Clamp1
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val c1: AcLineSegment = network["c1"]!!
        val b0: Breaker = network["b0"]!!
        val b2: Breaker = network["b2"]!!

        val clamp = c1.withClamp(network, 1.0)
        val cut = c1.withCut(network, null)

        "Traverse from T1 towards T2".run {
            val currentPath = b0.t2..c1.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t1..<cut.t1))
        }

        "Traverse from T2 towards T1".run {
            val currentPath = b2.t1..c1.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t2..<clamp.t1, c1.t2..<cut.t2))
        }
    }


    @Test
    fun `multiple cuts at same positions step to all cuts at that position`() {
        //
        //             *1 cut2 2*
        //  1 b0 21-c1-*1 cut1 2*-c1-21 b2 2
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val c1: AcLineSegment = network["c1"]!!
        val b0: Breaker = network["b0"]!!
        val b2: Breaker = network["b2"]!!

        val cut1 = c1.withCut(network, 1.0)
        val cut2 = c1.withCut(network, 1.0)

        "Traverse from T1 towards T2 should have both cuts t1".run {
            val currentPath = b0.t2..c1.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t1..<cut1.t1, c1.t1..<cut2.t1))
        }

        "Traverse from T2 towards T1 should have both cuts t2".run {
            val currentPath = b2.t1..c1.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t2..<cut1.t2, c1.t2..<cut2.t2))
        }

        "Internal step on cut1 t1 to t2 has cut2.t2 and traverses towards segment T2".run {
            val currentPath = cut1.t1..cut1.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t2..<c1.t2, cut1.t2..<cut2.t2))
        }

        "Internal step on cut1 t2 to t1 traverses towards segment T2".run {
            val currentPath = cut1.t2..cut1.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t1..<c1.t1, cut1.t1..<cut2.t1))
        }
    }

    @Test
    fun `cut and clamp without length only returns clamp on T1 side of cut`() {
        //
        //  1 b0 21*1 cut1 2*-c1-*-21 b2 2
        //         1
        //         Clamp1
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val c1: AcLineSegment = network["c1"]!!
        val b0: Breaker = network["b0"]!!
        val b2: Breaker = network["b2"]!!

        val clamp = c1.withClamp(network, null)
        val cut = c1.withCut(network, null)

        "Traverse from T1 towards T2".run {
            val currentPath = b0.t2..c1.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t1..<cut.t1, c1.t1..<clamp.t1))
        }

        "Traverse from T2 towards T1".run {
            val currentPath = b2.t1..c1.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t2..<cut.t2))
        }

        "Internally stepped on cut T1 to T2, traverse towards c1.t2".run {
            val currentPath = cut.t1..cut.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut.t2..<c1.t2))
        }

        "Internally stepped on cut T2 to T1, traverse towards c1.t1".run {
            val currentPath = cut.t2..cut.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut.t1..<c1.t1, cut.t1..<clamp.t1))
        }
    }

    @Test
    fun `cut and clamp at same length only returns clamp on T1 side of cut`() {
        //
        //  1 b0 21--*1 cut1 2*-c1-*-21 b2 2
        //           1
        //           Clamp1
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val c1: AcLineSegment = network["c1"]!!
        val b0: Breaker = network["b0"]!!
        val b2: Breaker = network["b2"]!!

        val clamp = c1.withClamp(network, 1.0)
        val cut = c1.withCut(network, 1.0)

        "Traverse from T1 towards T2".run {
            val currentPath = b0.t2..c1.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t1..<cut.t1, c1.t1..<clamp.t1))
        }

        "Traverse from T2 towards T1".run {
            val currentPath = b2.t1..c1.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t2..<cut.t2))
        }

        "Internally stepped on cut T1 to T2, traverse towards c1.t2".run {
            val currentPath = cut.t1..cut.t2
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut.t2..<c1.t2))
        }

        "Internally stepped on cut T2 to T1, traverse towards c1.t1".run {
            val currentPath = cut.t2..cut.t1
            val nextPaths = pathProvider.nextPaths(currentPath).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut.t1..<c1.t1, cut.t1..<clamp.t1))
        }
    }

    @Test
    fun `multiple clamps at same position does not return the other clamps more than once`() {
        //
        //  (Cut with null length is treated as at 0.0)
        //         Clamp2
        //         1
        //  1 b0 21*1 cut1 2*-c1-*-21 b2 2
        //         1
        //         Clamp1
        //
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .network

        val c1: AcLineSegment = network["c1"]!!

        val clamp1 = c1.withClamp(network, null)
        val clamp2 = c1.withClamp(network, null)
        val cut = c1.withCut(network, null)

        val currentPath = clamp1.t1..clamp1.t1
        val nextPaths = pathProvider.nextPaths(currentPath).toList()
        assertThat(nextPaths, containsInAnyOrder(clamp1.t1..<c1.t1, clamp1.t1..<clamp2.t1, clamp1.t1..<cut.t1))
    }

    @Test
    fun `unrealistic cuts and clamps network doesn't break the pathing algorithm`() {
        val network = aclsWithClampsAndCutsAtSamePositionNetwork()

        val b0: Breaker = network["b0"]!!
        val b2: Breaker = network["b2"]!!
        val c1: AcLineSegment = network["c1"]!!
        val clamp1: Clamp = network["clamp1"]!!
        val clamp2: Clamp = network["clamp2"]!!
        val clamp3: Clamp = network["clamp3"]!!
        val clamp4: Clamp = network["clamp4"]!!
        val clamp5: Clamp = network["clamp5"]!!
        val clamp6: Clamp = network["clamp6"]!!
        val cut1: Cut = network["cut1"]!!
        val cut2: Cut = network["cut2"]!!
        val cut3: Cut = network["cut3"]!!
        val cut4: Cut = network["cut4"]!!
        val cut5: Cut = network["cut5"]!!
        val cut6: Cut = network["cut6"]!!
        val cClamp1: AcLineSegment = network["c-clamp1"]!!
        val cCut1t1: AcLineSegment = network["c-cut1t1"]!!
        val cCut1t2: AcLineSegment = network["c-cut1t2"]!!
        val cClamp3: AcLineSegment = network["c-clamp3"]!!
        val cCut3t1: AcLineSegment = network["c-cut3t1"]!!
        val cCut3t2: AcLineSegment = network["c-cut3t2"]!!
        val cClamp5: AcLineSegment = network["c-clamp5"]!!
        val cCut5t1: AcLineSegment = network["c-cut5t1"]!!
        val cCut5t2: AcLineSegment = network["c-cut5t2"]!!

        "traverse from c1.t1 should get clamps at start and stop at both cuts at start".run {
            val nextPaths = pathProvider.nextPaths(b0.t2..c1.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t1..<clamp1.t1, c1.t1..<clamp2.t1, c1.t1..<cut1.t1, c1.t1..<cut2.t1))
        }

        "traverse from clamp1.t1 should traverse to other clamp at start, stop at both cuts at start and c1.t1".run {
            val nextPaths = pathProvider.nextPaths(cClamp1.t1..clamp1.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(clamp1.t1..<clamp2.t1, clamp1.t1..<c1.t1, clamp1.t1..<cut1.t1, clamp1.t1..<cut2.t1))
        }

        "traverse from cut1.t1 (external) should traverse to cut2.t1, clamps at start, c1.t1 and internally step to cut1.t2".run {
            val nextPaths = pathProvider.nextPaths(cCut1t1.t1..cut1.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t1..<cut2.t1, cut1.t1..<clamp1.t1, cut1.t1..<clamp2.t1, cut1.t1..<c1.t1, cut1.t1..cut1.t2))
        }

        "traverse from cut1.t1 (internal) should traverse to cut2.t1, clamps at start, c1.t1 and step to cCut1".run {
            val nextPaths = pathProvider.nextPaths(cut1.t2..cut1.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t1..<cut2.t1, cut1.t1..<clamp1.t1, cut1.t1..<clamp2.t1, cut1.t1..<c1.t1, cut1.t1..cCut1t1.t1))
        }

        "traverse from cut1.t2 (external) should traverse to cut2.t2, middle cuts, middle clamps, internally step to c1.t1".run {
            val nextPaths = pathProvider.nextPaths(cCut1t2.t1..cut1.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t2..<cut2.t2, cut1.t2..<clamp3.t1, cut1.t2..<clamp4.t1, cut1.t2..<cut3.t1, cut1.t2..<cut4.t1, cut1.t2..cut1.t1))
        }

        "traverse from cut1.t2 (internal) should traverse to cut2.t2, middle cuts, middle clamps and externally to cCut1t2".run {
            val nextPaths = pathProvider.nextPaths(cut1.t1..cut1.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut1.t2..<cut2.t2, cut1.t2..<clamp3.t1, cut1.t2..<clamp4.t1, cut1.t2..<cut3.t1, cut1.t2..<cut4.t1, cut1.t2..cCut1t2.t1))
        }

        "traverse from middle clamp (clamp3) should traverse to cuts at start, middle cuts, and other middle clamp".run {
            val nextPaths = pathProvider.nextPaths(cClamp3.t1..clamp3.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(clamp3.t1..<cut1.t2, clamp3.t1..<cut2.t2, clamp3.t1..<cut3.t1, clamp3.t1..<cut4.t1, clamp3.t1..<clamp4.t1))
        }

        "traverse from cut3.t1 (external) should traverse to cut4.t1, start cuts, middle clamps, and internally step to cut3.t2".run {
            val nextPaths = pathProvider.nextPaths(cCut3t1.t1..cut3.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut3.t1..<cut4.t1, cut3.t1..<cut1.t2, cut3.t1..<cut2.t2, cut3.t1..<clamp3.t1, cut3.t1..<clamp4.t1, cut3.t1..cut3.t2))
        }

        "traverse from cut3.t1 (internal) should traverse to cut2.t1, clamps at start, middle clamp and step to cCut3t1".run {
            val nextPaths = pathProvider.nextPaths(cut3.t2..cut3.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut3.t1..<cut4.t1, cut3.t1..<cut1.t2, cut3.t1..<cut2.t2, cut3.t1..<clamp3.t1, cut3.t1..<clamp4.t1, cut3.t1..cCut3t1.t1))
        }

        "traverse from cut3.t2 (external) should traverse to cut4.t2, end cuts, end clamps and internally step to cut3.t1".run {
            val nextPaths = pathProvider.nextPaths(cCut3t2.t1..cut3.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut3.t2..<cut4.t2, cut3.t2..<cut5.t1, cut3.t2..<cut6.t1, cut3.t2..<clamp5.t1, cut3.t2..<clamp6.t1, cut3.t2..cut3.t1))
        }

        "traverse from cut3.t2 (internal) should traverse to cut4.t2, end cuts, end clamps and externally to cut3t2".run {
            val nextPaths = pathProvider.nextPaths(cut3.t1..cut3.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut3.t2..<cut4.t2, cut3.t2..<cut5.t1, cut3.t2..<cut6.t1, cut3.t2..<clamp5.t1, cut3.t2..<clamp6.t1, cut3.t2..cCut3t2.t1))
        }

        "traverse from end clamp (clamp5) should traverse to middle cuts, end cuts and other end clamp".run {
            val nextPaths = pathProvider.nextPaths(cClamp5.t1..clamp5.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(clamp5.t1..<cut3.t2, clamp5.t1..<cut4.t2, clamp5.t1..<cut5.t1, clamp5.t1..<cut6.t1, clamp5.t1..<clamp6.t1))
        }

        "traverse from cut5.t1 (external) should traverse to cut6.t1, middle cuts, end clamps, and internally step to cut5.t2".run {
            val nextPaths = pathProvider.nextPaths(cCut5t1.t1..cut5.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut5.t1..<cut6.t1, cut5.t1..<cut3.t2, cut5.t1..<cut4.t2, cut5.t1..<clamp5.t1, cut5.t1..<clamp6.t1, cut5.t1..cut5.t2))
        }

        "traverse from cut5.t1 (internal) should traverse to cut6.t1, middle cuts, end clamps, and step to cCut5t1".run {
            val nextPaths = pathProvider.nextPaths(cut5.t2..cut5.t1).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut5.t1..<cut6.t1, cut5.t1..<cut3.t2, cut5.t1..<cut4.t2, cut5.t1..<clamp5.t1, cut5.t1..<clamp6.t1, cut5.t1..cCut5t1.t1))
        }

        "traverse from cut5.t2 (external) should traverse to cut6.t2, c1.t2, and internally step to cut5.t1".run {
            val nextPaths = pathProvider.nextPaths(cCut5t2.t1..cut5.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut5.t2..<cut6.t2, cut5.t2..<c1.t2, cut5.t2..cut5.t1))
        }

        "traverse from cut5.t2 (internal) should traverse to cut6.t2, c1.t2, end step externally to cCut5t2".run {
            val nextPaths = pathProvider.nextPaths(cut5.t1..cut5.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(cut5.t2..<cut6.t2, cut5.t2..<c1.t2, cut5.t2..cCut5t2.t1))
        }

        "traverse from c1.t2 should get cuts at end".run {
            val nextPaths = pathProvider.nextPaths(b2.t1..c1.t2).toList()
            assertThat(this, nextPaths, containsInAnyOrder(c1.t2..<cut5.t2, c1.t2..<cut6.t2))
        }
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
        //           1
        // 1 b0 21---*--c1--*---21 b2 2
        //                  1
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

    private fun aclsWithClampsAndCutsAtSamePositionNetwork(): NetworkService {
        // Drawing this is very messy, so it will be described in writing:
        // The network has 2 Breakers (b0, b2) with an AcLineSegment (c1) between them ( 1 b0 21--c1--21 b2 1 )
        // There is then 2 Clamps and 2 Cuts at the following position on c1
        // * At the start (0.0) (clamp1, clamp2, cut1, cut2)
        // * In the middle (length 1.0) (clamp3, clamp4, cut3, cut4)
        // * At the end (length 2.0) (clamp5, clamp6, cut5, cut6)
        // On each clamp terminal there is a separate AcLineSegment connected to it. (ids of c-clampX)
        // On each cut terminal (both 1 and 2) there is a separate AcLineSegment connected to it. (ids of c-cutXtN)
        val network = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls { length = 2.0 } // c1
            .toBreaker() // b2
            .fromAcls(mRID = "c-clamp1")
            .fromAcls(mRID = "c-clamp2")
            .fromAcls(mRID = "c-cut1t1")
            .fromAcls(mRID = "c-cut1t2")
            .fromAcls(mRID = "c-cut2t1")
            .fromAcls(mRID = "c-cut2t2")
            .fromAcls(mRID = "c-clamp3")
            .fromAcls(mRID = "c-clamp4")
            .fromAcls(mRID = "c-cut3t1")
            .fromAcls(mRID = "c-cut3t2")
            .fromAcls(mRID = "c-cut4t1")
            .fromAcls(mRID = "c-cut4t2")
            .fromAcls(mRID = "c-clamp5")
            .fromAcls(mRID = "c-clamp6")
            .fromAcls(mRID = "c-cut5t1")
            .fromAcls(mRID = "c-cut5t2")
            .fromAcls(mRID = "c-cut6t1")
            .fromAcls(mRID = "c-cut6t2")
            .network

        val segment: AcLineSegment = network["c1"]!!
        assertThat(segment.length, not(nullValue()))

        val clamp1 = segment.withClamp(network, 0.0)
        val clamp2 = segment.withClamp(network, null)
        val cut1 = segment.withCut(network, 0.0)
        val cut2 = segment.withCut(network, null)
        val clamp3 = segment.withClamp(network, 1.0)
        val clamp4 = segment.withClamp(network, 1.0)
        val cut3 = segment.withCut(network, 1.0)
        val cut4 = segment.withCut(network, 1.0)
        val clamp5 = segment.withClamp(network, segment.length)
        val clamp6 = segment.withClamp(network, segment.length)
        val cut5 = segment.withCut(network, segment.length)
        val cut6 = segment.withCut(network, segment.length)

        network.connect(clamp1.t1, network.get<ConductingEquipment>("c-clamp1")!!.t1)
        network.connect(clamp2.t1, network.get<ConductingEquipment>("c-clamp2")!!.t1)
        network.connect(cut1.t1, network.get<ConductingEquipment>("c-cut1t1")!!.t1)
        network.connect(cut1.t2, network.get<ConductingEquipment>("c-cut1t2")!!.t1)
        network.connect(cut2.t1, network.get<ConductingEquipment>("c-cut2t1")!!.t1)
        network.connect(cut2.t2, network.get<ConductingEquipment>("c-cut2t2")!!.t1)
        network.connect(clamp3.t1, network.get<ConductingEquipment>("c-clamp3")!!.t1)
        network.connect(clamp4.t1, network.get<ConductingEquipment>("c-clamp4")!!.t1)
        network.connect(cut3.t1, network.get<ConductingEquipment>("c-cut3t1")!!.t1)
        network.connect(cut3.t2, network.get<ConductingEquipment>("c-cut3t2")!!.t1)
        network.connect(cut4.t1, network.get<ConductingEquipment>("c-cut4t1")!!.t1)
        network.connect(cut4.t2, network.get<ConductingEquipment>("c-cut4t2")!!.t1)
        network.connect(clamp5.t1, network.get<ConductingEquipment>("c-clamp5")!!.t1)
        network.connect(clamp6.t1, network.get<ConductingEquipment>("c-clamp6")!!.t1)
        network.connect(cut5.t1, network.get<ConductingEquipment>("c-cut5t1")!!.t1)
        network.connect(cut5.t2, network.get<ConductingEquipment>("c-cut5t2")!!.t1)
        network.connect(cut6.t1, network.get<ConductingEquipment>("c-cut6t1")!!.t1)
        network.connect(cut6.t2, network.get<ConductingEquipment>("c-cut6t2")!!.t1)

        return network
    }

    private fun AcLineSegment.withClamp(network: NetworkService, lengthFromTerminal1: Double?): Clamp {
        val clamp = Clamp("clamp${numClamps() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addClamp(clamp)
        network.add(clamp)

        return clamp
    }

    private fun AcLineSegment.withCut(network: NetworkService, lengthFromTerminal1: Double?): Cut {
        val cut = Cut("cut${numCuts() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            addTerminal(Terminal("$mRID-t2"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addCut(cut)
        network.add(cut)
        return cut
    }

    /**
     * Allows for shorthand notation to create a NetworkTraceStep.Path between 2 terminals. E.g. `j0.t2..c1.t1`
     */
    private operator fun Terminal.rangeTo(other: Terminal): NetworkTraceStep.Path = NetworkTraceStep.Path(this, other, null)

    /**
     * Allows for shorthand notation to create a NetworkTraceStep.Path that traversed an AcLineSegment between 2 terminals . E.g. `c1.t1..<clamp1.t1`
     */
    private operator fun Terminal.rangeUntil(other: Terminal): NetworkTraceStep.Path =
        NetworkTraceStep.Path(
            this,
            other,
            when (val ce = this.conductingEquipment) {
                is AcLineSegment -> ce
                is Clamp -> ce.acLineSegment
                is Cut -> ce.acLineSegment
                else -> error("Did not traverse")
            }
        )
}
