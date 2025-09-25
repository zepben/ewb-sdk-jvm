/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.testing

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.services.network.NetworkService
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TestNetworkBuilderTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun sampleNetworkStartingWithSource() {
        //
        // s0 11--c1--21 b2 21 s3
        //
        // s4 11--c5--2
        //
        TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toAcls(PhaseCode.ABC) // c1
            .toBreaker(PhaseCode.ABC) // b2
            .toSource(PhaseCode.ABC) // s3
            .fromSource(PhaseCode.AB) // s4
            .toAcls(PhaseCode.AB) // c5
            .build()
            .apply {
                validateConnections("s0", listOf("c1-t1"))
                validateConnections("c1", listOf("s0-t1"), listOf("b2-t1"))
                validateConnections("b2", listOf("c1-t2"), listOf("s3-t1"))
                validateConnections("s3", listOf("b2-t2"))
                validateConnections("s4", listOf("c5-t1"))
                validateConnections("c5", listOf("s4-t1"), emptyList())
            }
    }

    @Test
    internal fun sampleNetworkStartingWithAcls() {
        //
        // 1--c0--21 b1 21--c2--2
        //         1 b3 21--c4--2
        //
        // 1--c5--21--c6--2
        //
        TestNetworkBuilder()
            .fromAcls(PhaseCode.ABC) // c0
            .toBreaker(PhaseCode.ABC, isNormallyOpen = true) // b1
            .toAcls(PhaseCode.AB) // c2
            .branchFrom("c0")
            .toBreaker(PhaseCode.ABC, isOpen = true) // b3
            .toAcls(PhaseCode.AB) // c4
            .fromAcls(PhaseCode.AB) // c5
            .toAcls(PhaseCode.AB) // c6
            .connect("c2", "c4", 2, 2)
            .build()
            .apply {
                validateConnections("c0", emptyList(), listOf("b1-t1", "b3-t1"))
                validateConnections("b1", listOf("c0-t2", "b3-t1"), listOf("c2-t1"))
                validateConnections("c2", listOf("b1-t2"), listOf("c4-t2"))
                validateConnections("b3", listOf("c0-t2", "b1-t1"), listOf("c4-t1"))
                validateConnections("c4", listOf("b3-t2"), listOf("c2-t2"))
                validateConnections("c5", emptyList(), listOf("c6-t1"))
                validateConnections("c6", listOf("c5-t2"), emptyList())
            }
    }

    @Test
    internal fun sampleNetworkStartingWithBreaker() {
        //
        // 1 b0*21--c1--21--c2--21--c4--2
        //
        // 1 b5*21--c6--21 tx7+21--c8--2
        //
        TestNetworkBuilder()
            .fromBreaker(PhaseCode.ABC) // b0
            .toAcls(PhaseCode.ABC) // c1
            .toAcls(PhaseCode.ABC) // c2
            .addFeeder("b0") // fdr3
            .toAcls(PhaseCode.ABC) // c4
            .fromBreaker(PhaseCode.AB) // b5
            .toAcls(PhaseCode.AB) // c6
            .toPowerTransformer(listOf(PhaseCode.AB, PhaseCode.A)) // tx7
            .toAcls(PhaseCode.A) // c8
            .addFeeder("b5", 1) // fdr9
            .addLvFeeder("tx7") // lvf10
            .build()
            .apply {
                validateConnections("b0", emptyList(), listOf("c1-t1"))
                validateConnections("c1", listOf("b0-t2"), listOf("c2-t1"))
                validateConnections("c2", listOf("c1-t2"), listOf("c4-t1"))
                validateFeeder("fdr3", "b0-t2")
                validateConnections("c4", listOf("c2-t2"), emptyList())
                validateConnections("b5", emptyList(), listOf("c6-t1"))
                validateConnections("c6", listOf("b5-t2"), listOf("tx7-t1"))
                validateConnections("tx7", listOf("c6-t2"), listOf("c8-t1"))
                validateConnections("c8", listOf("tx7-t2"), emptyList())
                validateFeeder("fdr9", "b5-t1")
                validateLvFeeder("lvf10", "tx7-t2")
            }
    }

    @Test
    internal fun sampleNetworkStartingWithJunction() {
        //
        // 1 j0 21--c1--21 j2 2
        //
        // 1 j3 31--c4--2
        //   2
        //
        TestNetworkBuilder()
            .fromJunction(PhaseCode.ABC) // j0
            .toAcls(PhaseCode.ABC) // c1
            .toJunction(PhaseCode.ABC) // j2
            .fromJunction(PhaseCode.AB, 3) // j3
            .toAcls(PhaseCode.AB) // c4
            .build()
            .apply {
                validateConnections("j0", emptyList(), listOf("c1-t1"))
                validateConnections("c1", listOf("j0-t2"), listOf("j2-t1"))
                validateConnections("j2", listOf("c1-t2"), emptyList())
                validateConnections("j3", emptyList(), emptyList(), listOf("c4-t1"))
                validateConnections("c4", listOf("j3-t3"), emptyList())
            }
    }

    @Test
    internal fun sampleNetworkStartingWithPowerTransformer() {
        //
        // 1 tx0 21--c1--21 tx2 2
        //
        // 1 tx3 31--c4--2
        //    2
        //
        TestNetworkBuilder()
            .fromPowerTransformer() // tx0
            .toAcls(PhaseCode.ABC) // c1
            .toPowerTransformer(listOf(PhaseCode.ABC)) // tx2
            .fromPowerTransformer(listOf(PhaseCode.AB, PhaseCode.AB, PhaseCode.AN)) // tx3
            .toAcls(PhaseCode.AN) // c4
            .build()
            .apply {
                validateConnections("tx0", emptyList(), listOf("c1-t1"))
                validateConnections("c1", listOf("tx0-t2"), listOf("tx2-t1"))
                validateConnections("tx2", listOf("c1-t2"))
                validateConnections("tx3", emptyList(), emptyList(), listOf("c4-t1"))
                validateConnections("c4", listOf("tx3-t3"), emptyList())

                validateEnds("tx0", listOf(PhaseCode.ABC, PhaseCode.ABC))
                validateEnds("tx2", listOf(PhaseCode.ABC))
                validateEnds("tx3", listOf(PhaseCode.AB, PhaseCode.AB, PhaseCode.AN))
            }
    }

    @Test
    internal fun sampleNetworkStartingWithBusbarSections() {
        //
        // bbs0 11--c1--21 bbs2
        //
        TestNetworkBuilder()
            .fromBusbarSection(PhaseCode.ABC) // bbs0
            .toAcls(PhaseCode.ABC) // c1
            .toBusbarSection(PhaseCode.ABC) // bbs2
            .build()
            .apply {
                validateConnections("bbs0", listOf("c1-t1"))
                validateConnections("c1", listOf("bbs0-t1"), listOf("bbs2-t1"))
                validateConnections("bbs2", listOf("c1-t2"))
            }
    }

    @Test
    internal fun `can override ids`() {
        TestNetworkBuilder()
            .fromSource(mRID = "my source 1")
            .toSource(mRID = "my source 2")
            .fromAcls(mRID = "my acls 1")
            .toAcls(mRID = "my acls 2")
            .fromBreaker(mRID = "my breaker 1")
            .toBreaker(mRID = "my breaker 2")
            .fromJunction(mRID = "my junction 1")
            .toJunction(mRID = "my junction 2")
            .toPowerElectronicsConnection(mRID = "my pec 1")
            .fromPowerTransformer(mRID = "my tx 1")
            .toPowerTransformer(mRID = "my tx 2")
            .toEnergyConsumer(mRID = "my ec 1")
            .fromOther(::Fuse, mRID = "my other 1")
            .toOther(::Fuse, mRID = "my other 2")
            .build()
            .apply {
                assertThat(
                    listOf<ConductingEquipment>().map { it.mRID },
                    containsInAnyOrder(
                        "my source 1",
                        "my source 2",
                        "my acls 1",
                        "my acls 2",
                        "my breaker 1",
                        "my breaker 2",
                        "my junction 1",
                        "my junction 2",
                        "my pec 1",
                        "my tx 1",
                        "my tx 2",
                        "my ec 1",
                        "my other 1",
                        "my other 2"
                    )
                )
            }
    }

    @Test
    internal fun `can override other prefix`() {
        TestNetworkBuilder()
            .fromOther(::Fuse, defaultMridPrefix = "abc")
            .toOther(::Fuse, defaultMridPrefix = "def")
            .build()
            .apply {
                assertThat(listOf<ConductingEquipment>().map { it.mRID }, containsInAnyOrder("abc0", "def1"))
            }
    }

    @Test
    internal fun canStartWithOpenPoints() {
        //
        // 1 b0 2
        // 1 b1 2
        // 1 b2 2
        // 1 b3 2
        //
        TestNetworkBuilder()
            .fromBreaker(PhaseCode.A, isNormallyOpen = true, isOpen = false) // b0
            .fromBreaker(PhaseCode.B, isNormallyOpen = true, isOpen = false) // b1
            .fromBreaker(PhaseCode.B) // b2
            .fromBreaker(PhaseCode.B, isNormallyOpen = true) // b3
            .build()
            .apply {
                validateOpenStates("b0", expectedIsNormallyOpen = true, expectedIsOpen = false)
                validateOpenStates("b1", expectedIsNormallyOpen = true, expectedIsOpen = false)
                validateOpenStates("b2", expectedIsNormallyOpen = false, expectedIsOpen = false)
                validateOpenStates("b3", expectedIsNormallyOpen = true, expectedIsOpen = true)
            }
    }

    @Test
    internal fun canBranchFromJunction() {
        //
        //           2
        //           |
        //           c2
        //           |
        //           1
        //           1
        // 2--c1--14 j0 31--c4--21--c5--2
        //           2
        //           1
        //           |
        //           c3
        //           |
        //           2
        //
        TestNetworkBuilder()
            .fromJunction(PhaseCode.A, 4) // j0
            .toAcls(PhaseCode.A) // c1
            .branchFrom("j0", 1)
            .toAcls(PhaseCode.A) // c2
            .branchFrom("j0", 2)
            .toAcls(PhaseCode.A) // c3
            .branchFrom("j0", 3)
            .toAcls(PhaseCode.A) // c4
            .toAcls(PhaseCode.A) // c5
            .build()
            .apply {
                validateConnections("j0", listOf("c2-t1"), listOf("c3-t1"), listOf("c4-t1"), listOf("c1-t1"))
                validateConnections("c1", listOf("j0-t4"), emptyList())
                validateConnections("c2", listOf("j0-t1"), emptyList())
                validateConnections("c3", listOf("j0-t2"), emptyList())
                validateConnections("c4", listOf("j0-t3"), listOf("c5-t1"))
                validateConnections("c5", listOf("c4-t2"), emptyList())
            }
    }

    @Test
    internal fun mustUseValidSourcePhases() {
        expect {
            TestNetworkBuilder()
                .fromSource(PhaseCode.XYN)
        }.toThrow<IllegalArgumentException>()
            .withMessage("EnergySource phases must be a subset of ABCN")

        expect {
            TestNetworkBuilder()
                .fromSource(PhaseCode.ABC)
                .fromSource(PhaseCode.XYN)
        }.toThrow<IllegalArgumentException>()
            .withMessage("EnergySource phases must be a subset of ABCN")
    }

    @Test
    internal fun canInitialiseEnds() {
        //
        // 1 tx0 21 tx1
        //
        // 1 tx3 3
        //    2
        //
        TestNetworkBuilder()
            .fromPowerTransformer(listOf(PhaseCode.ABC, PhaseCode.ABC), listOf({ ratedU = 1 }, { ratedU = 2 })) // tx0
            .toPowerTransformer(listOf(PhaseCode.ABC), listOf { addRating(3, TransformerCoolingType.UNKNOWN) }) // tx1
            .fromPowerTransformer(listOf(PhaseCode.AB, PhaseCode.AB, PhaseCode.AN), listOf({ b = 4.0 }, { b = 5.0 }, { b = 6.0 })) // tx2
            .build()
            .apply {
                assertThat(get<PowerTransformerEnd>("tx0-e1")!!.ratedU, equalTo(1))
                assertThat(get<PowerTransformerEnd>("tx0-e2")!!.ratedU, equalTo(2))
                assertThat(get<PowerTransformerEnd>("tx1-e1")!!.ratedS, equalTo(3))
                assertThat(get<PowerTransformerEnd>("tx2-e1")!!.b, equalTo(4.0))
                assertThat(get<PowerTransformerEnd>("tx2-e2")!!.b, equalTo(5.0))
                assertThat(get<PowerTransformerEnd>("tx2-e3")!!.b, equalTo(6.0))
            }
    }

    @Test
    internal fun canCreateOtherTypes() {
        //
        // o0 11 my-id[o1]
        //
        // 1 o2 21 o3 2
        //
        TestNetworkBuilder()
            .fromOther(::Fuse, numTerminals = 1) // o0
            .toOther(
                {
                    assertThat(it, equalTo("o1"))
                    Fuse("my-id")
                }, numTerminals = 1
            ) // o1
            .fromOther(::Fuse, PhaseCode.AB) // o2
            .toOther(::Fuse, PhaseCode.AB) // o3
            .build()
            .apply {
                validateConnections("o0", listOf("my-id-t1"))
                validateConnections("my-id", listOf("o0-t1"))
                validateConnections("o2", emptyList(), listOf("o3-t1"))
                validateConnections("o3", listOf("o2-t2"), emptyList())
            }
    }

    @Test
    internal fun canCreateOtherTypesKotlin() {
        //
        // o0 11 my1 2
        //
        // 1 my2 21 o1
        //
        TestNetworkBuilder()
            .fromOther<Fuse>(numTerminals = 1) // o0
            .toOther<LoadBreakSwitch>(mRID = "my1")
            .fromOther<Recloser>(nominalPhases = PhaseCode.AB, mRID = "my2")
            .toOther<Recloser>(nominalPhases = PhaseCode.AB, numTerminals = 1) // o1
            .build()
            .apply {
                validateConnections("o0", listOf("my1-t1"))
                validateConnections("my1", listOf("o0-t1"), emptyList())
                validateConnections("my2", emptyList(), listOf("o1-t1"))
                validateConnections("o1", listOf("my2-t2"))
            }
    }

    @Test
    internal fun `can choose the connectivity node id`() {
        validateConnectivityNodeOverride { mRID, cnMrid -> toBreaker(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toJunction(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toAcls(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toPowerTransformer(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toPowerElectronicsConnection(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toEnergyConsumer(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toSource(mRID = mRID, connectivityNodeMrid = cnMrid) }
        validateConnectivityNodeOverride { mRID, cnMrid -> toOther<Fuse>(mRID = mRID, connectivityNodeMrid = cnMrid) }
    }

    @Test
    internal fun `can add sites`() {
        //
        // 1--c0--21 b1 21--c2--21 b2 21--c3--1
        //
        TestNetworkBuilder()
            .fromAcls() // c0
            .toBreaker() // b1
            .toAcls() // c2
            .toBreaker() // b3
            .toAcls() // c4
            .addSite("b1", "c2", "b3") // site5
            .addSite("c0", "b1", mRID = "customSiteId")
            .network
            .apply {
                validateSite("site5", equipment = listOf("b1", "c2", "b3"))
                validateSite("customSiteId", equipment = listOf("c0", "b1"))
            }
    }

    private fun NetworkService.validateConnections(mRID: String, vararg expectedTerms: List<String>) {
        assertThat(get<ConductingEquipment>(mRID)!!.numTerminals(), equalTo(expectedTerms.size))
        expectedTerms.forEachIndexed { index, expected ->
            validateConnections(get("$mRID-t${index + 1}")!!, expected)
        }
    }

    private fun validateConnections(terminal: Terminal, expectedTerms: List<String>) {
        if (expectedTerms.isNotEmpty())
            assertThat(NetworkService.connectedTerminals(terminal).map { it.toTerminal.mRID }, containsInAnyOrder(*expectedTerms.toTypedArray()))
        else
            assertThat(NetworkService.connectedTerminals(terminal), empty())
    }

    private fun NetworkService.validateOpenStates(mRID: String, expectedIsNormallyOpen: Boolean, expectedIsOpen: Boolean) {
        assertThat(get<Breaker>(mRID)!!.isNormallyOpen(), equalTo(expectedIsNormallyOpen))
        assertThat(get<Breaker>(mRID)!!.isOpen(), equalTo(expectedIsOpen))
    }

    private fun NetworkService.validateFeeder(mRID: String, headTerminal: String) {
        assertThat(get<Feeder>(mRID)!!.normalHeadTerminal, equalTo(get(headTerminal)))
    }

    private fun NetworkService.validateLvFeeder(mRID: String, headTerminal: String) {
        assertThat(get<LvFeeder>(mRID)!!.normalHeadTerminal, equalTo(get(headTerminal)))
    }

    private fun NetworkService.validateEnds(mRID: String, expectedEnds: List<PhaseCode>) {
        get<PowerTransformer>(mRID)!!.apply {
            assertThat(numTerminals(), equalTo(expectedEnds.size))
            assertThat(numEnds(), equalTo(expectedEnds.size))

            ends.forEachIndexed { index, end ->
                assertThat(end.terminal, equalTo(terminals[index]))
            }
        }
    }

    private fun validateConnectivityNodeOverride(addWithConnectivityNode: TestNetworkBuilder.(mRID: String, cnMrid: String) -> Unit) {
        val ns = TestNetworkBuilder()
            .fromSource() // s0
            // Connect using a specific connectivity node
            .apply { addWithConnectivityNode("my1", "specified-cn") }
            .fromAcls() // c1
            // Reuse the specific connectivity node, which should connect all 4 items.
            .apply { addWithConnectivityNode("my2", "specified-cn") }
            .fromAcls() // c2
            .fromAcls() // c3
            // Force connect to the specific connectivity node, which should connect the additional 2 items.
            .connect("c2", "c3", 2, 1, "specified-cn")
            .fromAcls() // c4
            // Force connect using a different connectivity node, which should be overridden due to the `to` terminal being connected.
            .connect("c2", "c4", 2, 1, "different-cn")
            .fromAcls() // c5
            // Force connect using a different connectivity node, which should be overridden due to the `from` terminal being connected.
            .connect("c5", "c4", 2, 1, "different-cn")
            .network

        assertThat(
            ns.get<ConnectivityNode>("specified-cn")!!.terminals.map { it.mRID },
            containsInAnyOrder("s0-t1", "my1-t1", "c1-t2", "my2-t1", "c2-t2", "c3-t1", "c4-t1", "c5-t2")
        )
        // Make sure our overridden connectivity node was not created.
        assertThat(ns.get<ConnectivityNode>("different-cn"), nullValue())
    }

    private fun NetworkService.validateSite(mRID: String, equipment: List<String>) {
        val site = get<Site>(mRID)!!
        assertThat(site.equipment.map { it.mRID }, containsInAnyOrder(*equipment.toTypedArray()))
        equipment.map { get<Equipment>(it)!! }.forEach { ce -> assertThat(ce.sites, hasItem(site)) }
    }

}
