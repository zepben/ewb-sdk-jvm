/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.testing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TestNetworkBuilderTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun sampleNetworkStartingWithSource() {
        //
        // s0 11--c1--21 b2 21 s3
        //
        // s4 11--c5--2
        //
        TestNetworkBuilder
            .startWithSource(PhaseCode.ABC) // s0
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
        TestNetworkBuilder
            .startWithAcls(PhaseCode.ABC) // c0
            .toBreaker(PhaseCode.ABC, isNormallyOpen = true) // b1
            .toAcls(PhaseCode.AB) // c2
            .splitFrom("c0")
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
        // 1 b5*21--c6--2
        //
        TestNetworkBuilder
            .startWithBreaker(PhaseCode.ABC) // b0
            .toAcls(PhaseCode.ABC) // c1
            .toAcls(PhaseCode.ABC) // c2
            .addFeeder("b0") // fdr3
            .toAcls(PhaseCode.ABC) // c4
            .fromBreaker(PhaseCode.AB) // b5
            .toAcls(PhaseCode.AB) // c6
            .addFeeder("b5", 1) // fdr7
            .build()
            .apply {
                validateConnections("b0", emptyList(), listOf("c1-t1"))
                validateConnections("c1", listOf("b0-t2"), listOf("c2-t1"))
                validateConnections("c2", listOf("c1-t2"), listOf("c4-t1"))
                validateFeeder("fdr3", "b0-t2")
                validateConnections("c4", listOf("c2-t2"), emptyList())
                validateConnections("b5", emptyList(), listOf("c6-t1"))
                validateConnections("c6", listOf("b5-t2"), emptyList())
                validateFeeder("fdr7", "b5-t1")
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
        TestNetworkBuilder
            .startWithJunction(PhaseCode.ABC) // j0
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
    internal fun canStartWithOpenPoints() {
        //
        // 1 b0 2
        // 1 b1 2
        // 1 b2 2
        // 1 b3 2
        //
        TestNetworkBuilder
            .startWithBreaker(PhaseCode.A, isNormallyOpen = true, isOpen = false) // b0
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
    internal fun canSplitFromJunction() {
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
        TestNetworkBuilder
            .startWithJunction(PhaseCode.A, 4) // j0
            .toAcls(PhaseCode.A) // c1
            .splitFrom("j0", 1)
            .toAcls(PhaseCode.A) // c2
            .splitFrom("j0", 2)
            .toAcls(PhaseCode.A) // c3
            .splitFrom("j0", 3)
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
            TestNetworkBuilder
                .startWithSource(PhaseCode.XYN)
        }.toThrow(IllegalArgumentException::class.java)
            .withMessage("EnergySource phases must be a subset of ABCN")

        expect {
            TestNetworkBuilder
                .startWithSource(PhaseCode.ABC)
                .fromSource(PhaseCode.XYN)
        }.toThrow(IllegalArgumentException::class.java)
            .withMessage("EnergySource phases must be a subset of ABCN")
    }

    private fun NetworkService.validateConnections(mRID: String, vararg expectedTerms: List<String>) {
        assertThat(get<ConductingEquipment>(mRID)!!.numTerminals(), equalTo(expectedTerms.size))
        expectedTerms.forEachIndexed { index, expected ->
            validateConnections(get("$mRID-t${index + 1}")!!, expected)
        }
    }

    @Suppress("unused")
    private fun NetworkService.validateConnections(terminal: Terminal, expectedTerms: List<String>) {
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

}