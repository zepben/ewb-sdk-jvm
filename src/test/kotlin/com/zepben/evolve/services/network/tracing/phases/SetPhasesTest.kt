/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:Suppress("PropertyName")

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

class SetPhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun setPhasesTest() {
        val n = PhaseSwapLoopNetwork.create()

        SetPhases().run(n)
        PhaseLogger.trace(n.listOf<EnergySource>())

        // Check various points to make sure phases have been applied during the trace.
        PhaseValidator.validatePhases(n.getT("acLineSegment0", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment0", 2), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment1", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment4", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node4", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node4", 2), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node4", 3), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node8", 1), listOf(SPK.A))
        PhaseValidator.validatePhases(n.getT("node5", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node5", 2), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node5", 3), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node9", 1), listOf(SPK.B))
        PhaseValidator.validatePhases(n.getT("node6", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("node6", 2), listOf(SPK.B, SPK.C))
        PhaseValidator.validatePhases(n.getT("acLineSegment2", 2), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment3", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment9", 2), listOf(SPK.B, SPK.C))
        PhaseValidator.validatePhases(n.getT("node2", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
    }

    @Test
    internal fun appliesPhasesFromSources() {
        //
        // s0 12--c1--21--c2--2
        //             1--c3--2
        //
        // 1--c4--2
        //
        val n = TestNetworkBuilder
            .startWithSource(PhaseCode.ABCN) // s0
            .toAcls(PhaseCode.ABCN) // c1
            .toAcls(PhaseCode.ABCN) // c2
            .branchFrom("c1")
            .toAcls(PhaseCode.AB) // c3
            .fromAcls(PhaseCode.ABCN) // c4
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c1", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c3", PhaseCode.AB, PhaseCode.AB)
        PhaseValidator.validatePhases(n, "c4", PhaseCode.NONE, PhaseCode.NONE)
    }

    @Test
    internal fun stopsAtOpenPoints() {
        //
        // s0 11 b1 21--c2--2
        //
        // s3 11 b4 21--c5--2
        //
        val n = TestNetworkBuilder
            .startWithSource(PhaseCode.ABCN) // s0
            .toBreaker(PhaseCode.ABCN, isNormallyOpen = true, isOpen = false) // b1
            .toAcls(PhaseCode.ABCN) // c2
            .fromSource(PhaseCode.ABCN) // s3
            .toBreaker(PhaseCode.ABCN, isOpen = true) // b4
            .toAcls(PhaseCode.ABCN) // c5
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0-t1", PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b1-t1", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b1-t2", PhaseCode.NONE, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2-t1", PhaseCode.NONE, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2-t2", PhaseCode.NONE, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "s3-t1", PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b4-t1", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b4-t2", PhaseCode.ABCN, PhaseCode.NONE)
        PhaseValidator.validatePhases(n, "c5-t1", PhaseCode.ABCN, PhaseCode.NONE)
        PhaseValidator.validatePhases(n, "c5-t2", PhaseCode.ABCN, PhaseCode.NONE)
    }

    @Test
    internal fun tracesUnganged() {
        //
        // s0 11 b1 21--c2--1
        //
        val n = TestNetworkBuilder
            .startWithSource(PhaseCode.ABCN) // s0
            .toBreaker(PhaseCode.ABCN) {
                setOpen(true, SPK.A)
                setNormallyOpen(true, SPK.B)
            }
            .toAcls(PhaseCode.ABCN) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b1-t1", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "b1-t2", listOf(SPK.A, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n, "c2-t1", listOf(SPK.A, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n, "c2-t2", listOf(SPK.A, SPK.NONE, SPK.C, SPK.N), listOf(SPK.NONE, SPK.B, SPK.C, SPK.N))
    }

    @Test
    internal fun canRunFromTerminal() {
        //
        // 1--c0--21--c1--21--c2--2
        //
        val n = TestNetworkBuilder
            .startWithAcls(PhaseCode.ABCN) // c0
            .toAcls(PhaseCode.ABCN) // c1
            .toAcls(PhaseCode.ABCN) // c2
            .buildAndLog()

        SetPhases().run(n.getT("c1", 2))

        PhaseValidator.validatePhases(n, "c0", PhaseCode.NONE, PhaseCode.NONE)
        PhaseValidator.validatePhases(n, "c1", PhaseCode.NONE, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun mustProvideTheCorrectNumberOfPhases() {
        //
        // 1--c0--21--c1--2
        //
        val n = TestNetworkBuilder
            .startWithAcls(PhaseCode.A) // c0
            .toAcls(PhaseCode.A) // c1
            .buildAndLog()

        expect {
            SetPhases().run(n.getT("c0", 2), PhaseCode.AB)
        }.toThrow(IllegalArgumentException::class.java)
            .withMessage(
                "Attempted to apply phases [A, B] to Terminal{id='c0-t2'} with nominal phases A. Number of phases to apply must match the number of " +
                    "nominal phases. Found 2, expected 1"
            )
    }

    @Test
    internal fun detectsCrossPhasingFlow() {
        //
        // 1--c0--21--c1--2
        //
        val n = TestNetworkBuilder
            .startWithAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.A } // c0
            .toAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.B } // c1
            .buildAndLog()

        val c1: AcLineSegment = n["c1"]!!

        expect {
            SetPhases().run(n.getT("c0", 2))
        }.toThrow(IllegalStateException::class.java)
            .withMessage(
                "Attempted to flow conflicting phase A onto B on nominal phase A. This occurred while flowing from " +
                    "${c1.terminals[0]} to ${c1.terminals[1]} through ${c1.typeNameAndMRID()}. This is caused by missing open " +
                    "points, or incorrect phases in upstream equipment that should be corrected in the source data."
            )
    }

    @Test
    internal fun detectsCrossPhasingConnected() {
        //
        // 1--c0--21--c1--21--c2--2
        //
        val n = TestNetworkBuilder
            .startWithAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.A } // c0
            .toAcls(PhaseCode.A) // c1
            .toAcls(PhaseCode.A) { terminals[0].normalPhases[SPK.A] = SPK.B } // c2
            .buildAndLog()

        val c1: AcLineSegment = n["c1"]!!
        val c2: AcLineSegment = n["c2"]!!

        expect {
            SetPhases().run(n.getT("c0", 2))
        }.toThrow(IllegalStateException::class.java)
            .withMessage(
                "Attempted to flow conflicting phase A onto B on nominal phase path A to A. This occurred while flowing between " +
                    "${c1.terminals[1]} on ${c1.typeNameAndMRID()} and ${c2.terminals[0]} on ${c2.typeNameAndMRID()}. This is caused by " +
                    "missing open points, or incorrect phases in upstream equipment that should be corrected in the source data."
            )
    }

    private fun TestNetworkBuilder.buildAndLog() = build().apply {
        PhaseLogger.trace(listOf<EnergySource>())
    }

    private fun NetworkService.getT(ce: String, t: Int): Terminal =
        get<ConductingEquipment>(ce)!!.getTerminal(t)!!

}
