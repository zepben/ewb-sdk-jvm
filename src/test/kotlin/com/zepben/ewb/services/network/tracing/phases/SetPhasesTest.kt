/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:Suppress("PropertyName")

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.EnergySource
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class SetPhasesTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val setPhases = SetPhases(debugLogger = null)

    @Test
    internal fun setPhasesTest() {
        val n = PhaseSwapLoopNetwork.create()

        setPhases.run(n, NetworkStateOperators.NORMAL)
        setPhases.run(n, NetworkStateOperators.CURRENT)
        PhaseLogger.trace(n.listOf<EnergySource>())

        // Check various points to make sure phases have been applied during the trace.
        PhaseValidator.validatePhases(n.getT("acLineSegment0", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment0", 2), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment1", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment4", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j4", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j4", 2), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j4", 3), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j8", 1), listOf(SPK.A))
        PhaseValidator.validatePhases(n.getT("j5", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j5", 2), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j5", 3), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j9", 1), listOf(SPK.B))
        PhaseValidator.validatePhases(n.getT("j6", 1), listOf(SPK.A, SPK.B))
        PhaseValidator.validatePhases(n.getT("j6", 2), listOf(SPK.B, SPK.C))
        PhaseValidator.validatePhases(n.getT("acLineSegment2", 2), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment3", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
        PhaseValidator.validatePhases(n.getT("acLineSegment9", 2), listOf(SPK.B, SPK.C))
        PhaseValidator.validatePhases(n.getT("j2", 1), listOf(SPK.A, SPK.B, SPK.C, SPK.N))
    }

    @Test
    internal fun appliesPhasesFromSources() {
        //
        // s0 12--c1--21--c2--2
        //             1--c3--2
        //
        // 1--c4--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABCN) // s0
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
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABCN) // s0
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
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABCN) // s0
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
        val n = TestNetworkBuilder()
            .fromAcls(PhaseCode.ABCN) // c0
            .toAcls(PhaseCode.ABCN) // c1
            .toAcls(PhaseCode.ABCN) // c2
            .buildAndLog()

        n.getT("c1", 2).also {
            setPhases.run(it, it.phases, NetworkStateOperators.NORMAL)
            setPhases.run(it, it.phases, NetworkStateOperators.CURRENT)
        }

        PhaseValidator.validatePhases(n, "c0", PhaseCode.NONE, PhaseCode.NONE)
        PhaseValidator.validatePhases(n, "c1", PhaseCode.NONE, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun mustProvideTheCorrectNumberOfPhases() {
        //
        // 1--c0--21--c1--2
        //
        val n = TestNetworkBuilder()
            .fromAcls(PhaseCode.A) // c0
            .toAcls(PhaseCode.A) // c1
            .buildAndLog()

        expect {
            setPhases.run(n.getT("c0", 2), PhaseCode.AB, NetworkStateOperators.NORMAL)
            setPhases.run(n.getT("c0", 2), PhaseCode.AB, NetworkStateOperators.CURRENT)
        }.toThrow<IllegalArgumentException>()
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
        val n = TestNetworkBuilder()
            .fromAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.A } // c0
            .toAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.B } // c1
            .buildAndLog()

        val c1: AcLineSegment = n["c1"]!!

        expect {
            setPhases.run(n.getT("c0", 2), NetworkStateOperators.NORMAL)
            setPhases.run(n.getT("c0", 2), NetworkStateOperators.CURRENT)
        }.toThrow<IllegalStateException>()
            .withMessage(
                "Attempted to flow conflicting phase A onto B on nominal phase A. This occurred while flowing from " +
                    "${c1.terminals[0]} to ${c1.terminals[1]} through ${c1.typeNameAndMRID()}. This is often caused by missing open " +
                    "points, or incorrect phases in upstream equipment that should be corrected in the source data."
            )
    }

    @Test
    internal fun detectsCrossPhasingConnected() {
        //
        // 1--c0--21--c1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromAcls(PhaseCode.A) { terminals[1].normalPhases[SPK.A] = SPK.A } // c0
            .toAcls(PhaseCode.A) // c1
            .toAcls(PhaseCode.A) { terminals[0].normalPhases[SPK.A] = SPK.B } // c2
            .buildAndLog()

        val c1: AcLineSegment = n["c1"]!!
        val c2: AcLineSegment = n["c2"]!!

        expect {
            setPhases.run(n.getT("c0", 2), NetworkStateOperators.NORMAL)
            setPhases.run(n.getT("c0", 2), NetworkStateOperators.CURRENT)
        }.toThrow<IllegalStateException>()
            .withMessage(
                "Attempted to flow conflicting phase A onto B on nominal phase A. This occurred while flowing between " +
                    "${c1.terminals[1]} on ${c1.typeNameAndMRID()} and ${c2.terminals[0]} on ${c2.typeNameAndMRID()}. This is often caused by " +
                    "missing open points, or incorrect phases in upstream equipment that should be corrected in the source data."
            )
    }

    @Test
    internal fun addsNeutralThroughTransformers() {
        //
        // s0 11--tx1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toPowerTransformer(listOf(PhaseCode.ABC, PhaseCode.ABCN)) // tx1
            .toAcls(PhaseCode.ABCN) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.ABC, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
    }

    @Test
    internal fun appliesUnknownPhasesThroughTransformers() {
        //
        // s0 11--tx1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.BC) // s0
            .toPowerTransformer(listOf(PhaseCode.BC, PhaseCode.XN)) // tx1
            .toAcls(PhaseCode.XN) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.BC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.BC, PhaseCode.BN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.BN, PhaseCode.BN)
    }

    @Test
    internal fun `energises transformer phases straight`() {
        // Without neutral.
        validateTxPhases(PhaseCode.ABC, PhaseCode.ABC, PhaseCode.ABC, PhaseCode.ABC, PhaseCode.ABC)

        validateTxPhases(PhaseCode.AB, PhaseCode.AB, PhaseCode.AB, PhaseCode.AB, PhaseCode.AB)
        validateTxPhases(PhaseCode.BC, PhaseCode.BC, PhaseCode.BC, PhaseCode.BC, PhaseCode.BC)
        validateTxPhases(PhaseCode.AC, PhaseCode.AC, PhaseCode.AC, PhaseCode.AC, PhaseCode.AC)

        validateTxPhases(PhaseCode.AB, PhaseCode.AB, PhaseCode.XY, PhaseCode.AB, PhaseCode.AB)
        validateTxPhases(PhaseCode.BC, PhaseCode.BC, PhaseCode.XY, PhaseCode.BC, PhaseCode.BC)
        validateTxPhases(PhaseCode.AC, PhaseCode.AC, PhaseCode.XY, PhaseCode.AC, PhaseCode.AC)

        validateTxPhases(PhaseCode.AB, PhaseCode.XY, PhaseCode.XY, PhaseCode.AB, PhaseCode.AB)
        validateTxPhases(PhaseCode.BC, PhaseCode.XY, PhaseCode.XY, PhaseCode.BC, PhaseCode.BC)
        validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.XY, PhaseCode.AC, PhaseCode.AC)

        validateTxPhases(PhaseCode.A, PhaseCode.A, PhaseCode.A, PhaseCode.A, PhaseCode.A)
        validateTxPhases(PhaseCode.B, PhaseCode.B, PhaseCode.B, PhaseCode.B, PhaseCode.B)
        validateTxPhases(PhaseCode.C, PhaseCode.C, PhaseCode.C, PhaseCode.C, PhaseCode.C)

        validateTxPhases(PhaseCode.A, PhaseCode.A, PhaseCode.X, PhaseCode.A, PhaseCode.A)
        validateTxPhases(PhaseCode.B, PhaseCode.B, PhaseCode.X, PhaseCode.B, PhaseCode.B)
        validateTxPhases(PhaseCode.C, PhaseCode.C, PhaseCode.X, PhaseCode.C, PhaseCode.C)

        validateTxPhases(PhaseCode.A, PhaseCode.X, PhaseCode.X, PhaseCode.A, PhaseCode.A)
        validateTxPhases(PhaseCode.B, PhaseCode.X, PhaseCode.X, PhaseCode.B, PhaseCode.B)
        validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.X, PhaseCode.C, PhaseCode.C)

        // With neutral.
        validateTxPhases(PhaseCode.ABC, PhaseCode.ABC, PhaseCode.ABCN, PhaseCode.ABC, PhaseCode.ABCN)

        validateTxPhases(PhaseCode.AB, PhaseCode.AB, PhaseCode.ABN, PhaseCode.AB, PhaseCode.ABN)
        validateTxPhases(PhaseCode.BC, PhaseCode.BC, PhaseCode.BCN, PhaseCode.BC, PhaseCode.BCN)
        validateTxPhases(PhaseCode.AC, PhaseCode.AC, PhaseCode.ACN, PhaseCode.AC, PhaseCode.ACN)

        validateTxPhases(PhaseCode.AB, PhaseCode.AB, PhaseCode.XYN, PhaseCode.AB, PhaseCode.ABN)
        validateTxPhases(PhaseCode.BC, PhaseCode.BC, PhaseCode.XYN, PhaseCode.BC, PhaseCode.BCN)
        validateTxPhases(PhaseCode.AC, PhaseCode.AC, PhaseCode.XYN, PhaseCode.AC, PhaseCode.ACN)

        validateTxPhases(PhaseCode.AB, PhaseCode.XY, PhaseCode.XYN, PhaseCode.AB, PhaseCode.ABN)
        validateTxPhases(PhaseCode.BC, PhaseCode.XY, PhaseCode.XYN, PhaseCode.BC, PhaseCode.BCN)
        validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.XYN, PhaseCode.AC, PhaseCode.ACN)

        validateTxPhases(PhaseCode.A, PhaseCode.A, PhaseCode.AN, PhaseCode.A, PhaseCode.AN)
        validateTxPhases(PhaseCode.B, PhaseCode.B, PhaseCode.BN, PhaseCode.B, PhaseCode.BN)
        validateTxPhases(PhaseCode.C, PhaseCode.C, PhaseCode.CN, PhaseCode.C, PhaseCode.CN)

        validateTxPhases(PhaseCode.A, PhaseCode.A, PhaseCode.XN, PhaseCode.A, PhaseCode.AN)
        validateTxPhases(PhaseCode.B, PhaseCode.B, PhaseCode.XN, PhaseCode.B, PhaseCode.BN)
        validateTxPhases(PhaseCode.C, PhaseCode.C, PhaseCode.XN, PhaseCode.C, PhaseCode.CN)

        validateTxPhases(PhaseCode.A, PhaseCode.X, PhaseCode.XN, PhaseCode.A, PhaseCode.AN)
        validateTxPhases(PhaseCode.B, PhaseCode.X, PhaseCode.XN, PhaseCode.B, PhaseCode.BN)
        validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.XN, PhaseCode.C, PhaseCode.CN)
    }

    @Test
    internal fun `energises transformer phases added`() {
        //
        // NOTE: When adding a Y phase to an X -> XY transformer that is downstream of a C, the C phase will be spread on the X and the Y
        //       will be left de-energised.
        //
        //       You could rework it so this works as intended, but there are dramatic flow on effects making sure the XY (AC) is correctly
        //       connected at the other end to follow up equipment with non XY phases. Given this is only an issue where the phases of the
        //       transformer are unknown, and this is a SWER to split-phase transformer that happens to be on the end of a C phase SWER line, and
        //       you can resolve it by specifying the transformer phases explicitly (i.e. C -> ACN), it won't be fixed for now.
        //

        // Without neutral.
        validateTxPhases(PhaseCode.ABC, PhaseCode.A, PhaseCode.AB, PhaseCode.A, PhaseCode.AB)
        validateTxPhases(PhaseCode.ABC, PhaseCode.B, PhaseCode.BC, PhaseCode.B, PhaseCode.BC)
        validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.AC, PhaseCode.C, PhaseCode.AC)

        validateTxPhases(PhaseCode.ABC, PhaseCode.A, PhaseCode.XY, PhaseCode.A, PhaseCode.AB)
        validateTxPhases(PhaseCode.ABC, PhaseCode.B, PhaseCode.XY, PhaseCode.B, PhaseCode.BC)
        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.XY, PhaseCode.C, PhaseCode.AC)` and the single phase variant of
        // validateTxPhases would be removed.
        validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.XY, PhaseCode.C, listOf(SPK.C, SPK.NONE))

        validateTxPhases(PhaseCode.A, PhaseCode.X, PhaseCode.XY, PhaseCode.A, PhaseCode.AB)
        validateTxPhases(PhaseCode.B, PhaseCode.X, PhaseCode.XY, PhaseCode.B, PhaseCode.BC)

        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.XY, PhaseCode.C, PhaseCode.AC)` and the single phase variant of
        // validateTxPhases would be removed.
        validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.XY, PhaseCode.C, listOf(SPK.C, SPK.NONE))

        // With neutral.
        validateTxPhases(PhaseCode.ABC, PhaseCode.A, PhaseCode.ABN, PhaseCode.A, PhaseCode.ABN)
        validateTxPhases(PhaseCode.ABC, PhaseCode.B, PhaseCode.BCN, PhaseCode.B, PhaseCode.BCN)
        validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.ACN, PhaseCode.C, PhaseCode.ACN)

        validateTxPhases(PhaseCode.ABC, PhaseCode.A, PhaseCode.XYN, PhaseCode.A, PhaseCode.ABN)
        validateTxPhases(PhaseCode.ABC, PhaseCode.B, PhaseCode.XYN, PhaseCode.B, PhaseCode.BCN)
        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.XYN, PhaseCode.C, PhaseCode.ACN)` and the single phase variant of
        // validateTxPhases would be removed.
        validateTxPhases(PhaseCode.ABC, PhaseCode.C, PhaseCode.XYN, PhaseCode.C, listOf(SPK.C, SPK.NONE, SPK.N))

        validateTxPhases(PhaseCode.A, PhaseCode.X, PhaseCode.XYN, PhaseCode.A, PhaseCode.ABN)
        validateTxPhases(PhaseCode.B, PhaseCode.X, PhaseCode.XYN, PhaseCode.B, PhaseCode.BCN)

        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.XY, PhaseCode.C, PhaseCode.AC)` and the single phase variant of
        // validateTxPhases would be removed.
        validateTxPhases(PhaseCode.C, PhaseCode.X, PhaseCode.XYN, PhaseCode.C, listOf(SPK.C, SPK.NONE, SPK.N))
    }

    @Test
    internal fun `energises transformer phases dropped`() {
        //
        // NOTE: When dropping a Y phase to an XY -> X transformer that is downstream of an AC, the A phase will be spread on the X,
        //       and the C phase will be dropped.
        //
        //       You could rework it so this works as intended, but there are dramatic flow on effects making sure the XY (AC) is correctly
        //       connected at the other end to follow up equipment with non XY phases. Given this is only an issue where the phases of the
        //       transformer are unknown, and this is a split-phase to SWER transformer that happens to be on the end of an AC line, and
        //       you can resolve it by specifying the transformer phases explicitly (i.e. ACN -> C), it won't be fixed for now.
        //

        // Without neutral.
        validateTxPhases(PhaseCode.ABC, PhaseCode.AB, PhaseCode.A, PhaseCode.AB, PhaseCode.A)
        validateTxPhases(PhaseCode.ABC, PhaseCode.BC, PhaseCode.B, PhaseCode.BC, PhaseCode.B)
        validateTxPhases(PhaseCode.ABC, PhaseCode.AC, PhaseCode.C, PhaseCode.AC, PhaseCode.C)

        validateTxPhases(PhaseCode.AB, PhaseCode.XY, PhaseCode.A, PhaseCode.AB, PhaseCode.A)
        validateTxPhases(PhaseCode.BC, PhaseCode.XY, PhaseCode.B, PhaseCode.BC, PhaseCode.B)

        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.C, PhaseCode.AC, PhaseCode.C)`.
        validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.C, PhaseCode.AC, PhaseCode.A)

        validateTxPhases(PhaseCode.AB, PhaseCode.XY, PhaseCode.X, PhaseCode.AB, PhaseCode.A)
        validateTxPhases(PhaseCode.BC, PhaseCode.XY, PhaseCode.X, PhaseCode.BC, PhaseCode.B)

        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.X, PhaseCode.AC, PhaseCode.C)`.
        validateTxPhases(PhaseCode.AC, PhaseCode.XY, PhaseCode.X, PhaseCode.AC, PhaseCode.A)

        // With neutral.
        validateTxPhases(PhaseCode.ABCN, PhaseCode.ABN, PhaseCode.A, PhaseCode.ABN, PhaseCode.A)
        validateTxPhases(PhaseCode.ABCN, PhaseCode.BCN, PhaseCode.B, PhaseCode.BCN, PhaseCode.B)
        validateTxPhases(PhaseCode.ABCN, PhaseCode.ACN, PhaseCode.C, PhaseCode.ACN, PhaseCode.C)

        validateTxPhases(PhaseCode.ABN, PhaseCode.XYN, PhaseCode.A, PhaseCode.ABN, PhaseCode.A)
        validateTxPhases(PhaseCode.BCN, PhaseCode.XYN, PhaseCode.B, PhaseCode.BCN, PhaseCode.B)
        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.ACN, PhaseCode.XYN, PhaseCode.C, PhaseCode.ACN, PhaseCode.C)`.
        validateTxPhases(PhaseCode.ACN, PhaseCode.XYN, PhaseCode.C, PhaseCode.ACN, PhaseCode.A)

        validateTxPhases(PhaseCode.ABN, PhaseCode.XYN, PhaseCode.X, PhaseCode.ABN, PhaseCode.A)
        validateTxPhases(PhaseCode.BCN, PhaseCode.XYN, PhaseCode.X, PhaseCode.BCN, PhaseCode.B)

        // As per the note above, this is not ideal. Ideally the note above would be removed and the test below would be replaced with
        // `validateTxPhases(PhaseCode.ACN, PhaseCode.XYN, PhaseCode.X, PhaseCode.ACN, PhaseCode.C)`.
        validateTxPhases(PhaseCode.ACN, PhaseCode.XYN, PhaseCode.X, PhaseCode.ACN, PhaseCode.A)
    }

    @Test
    internal fun appliesPhasesToUnknownHv() {
        //
        // s0 11--c1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.BC) // s0
            .toAcls(PhaseCode.BC) // c1
            .toAcls(PhaseCode.XY) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.BC)
        PhaseValidator.validatePhases(n, "c1", PhaseCode.BC, PhaseCode.BC)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.BC, PhaseCode.BC)
    }

    @Test
    internal fun appliesPhasesToUnknownLv() {
        //
        // s0 11--c1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.CN) // s0
            .toAcls(PhaseCode.CN) // c1
            .toAcls(PhaseCode.XN) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.CN)
        PhaseValidator.validatePhases(n, "c1", PhaseCode.CN, PhaseCode.CN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.CN, PhaseCode.CN)
    }

    @Test
    internal fun appliesPhasesOntoSwer() {
        //
        // s0 11--tx1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.AC) // s0
            .toPowerTransformer(listOf(PhaseCode.AC, PhaseCode.X)) // tx1
            .toAcls(PhaseCode.X) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.AC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.AC, PhaseCode.C)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.C, PhaseCode.C)
    }

    @Test
    internal fun usesTransformerPaths() {
        //
        // s0 11--tx1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.AC) // s0
            .toPowerTransformer(listOf(PhaseCode.AC, PhaseCode.CN)) // tx1
            .toAcls(PhaseCode.CN) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.AC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.AC, PhaseCode.CN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.CN, PhaseCode.CN)
    }

    @Test
    internal fun doesNotRemovePhasesWhenApplyingSubsetOutOfLoop() {
        //
        // s0 12-----c5------1
        //    1              2
        //   tx1            tx4
        //    2              1
        //    1--c2--21--c3--2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toPowerTransformer(listOf(PhaseCode.ABC, PhaseCode.ABCN)) // tx1
            .toAcls(PhaseCode.ABCN) // c2
            .toAcls(PhaseCode.CN) // c3
            .toPowerTransformer(listOf(PhaseCode.CN, PhaseCode.AC)) // tx4
            .toAcls(PhaseCode.ABC) // c5
            .connect("c5", "s0", 2, 1)
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.ABC, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.ABCN, PhaseCode.ABCN)
        PhaseValidator.validatePhases(n, "c3", PhaseCode.CN, PhaseCode.CN)
        PhaseValidator.validatePhases(n, "tx4", PhaseCode.CN, PhaseCode.AC)
        PhaseValidator.validatePhases(n, "c5", PhaseCode.ABC, PhaseCode.ABC)
    }

    @Test
    internal fun canBackTraceThroughXnXyTransformerLoop() {
        //
        //    1 tx1 21--\
        // s0 1         c2
        //    2 tx3 12--/
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toPowerTransformer(listOf(PhaseCode.XY, PhaseCode.XN)) // tx1
            .toAcls(PhaseCode.XN) // c2
            .toPowerTransformer(listOf(PhaseCode.XN, PhaseCode.XY)) // tx3
            .connect("tx3", "s0", 2, 1)
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABC)
        //
        // NOTE: The phase paths don't currently look at energised phases, so the XY -> XN will spread the A from X onto the other side of the transformer
        //       even if CN would've been expected. If this is ever fixed the below tests should be replaced with:
        //        `PhaseValidator.validatePhases(n, "tx1", PhaseCode.AC, PhaseCode.CN)`
        //        `PhaseValidator.validatePhases(n, "c2", PhaseCode.CN, PhaseCode.CN)`
        //        `PhaseValidator.validatePhases(n, "tx3", PhaseCode.CN, PhaseCode.AC)`
        //
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.AC, PhaseCode.AN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.AN, PhaseCode.AN)
        PhaseValidator.validatePhases(n, "tx3", PhaseCode.AN, PhaseCode.AC)
    }

    @Test
    internal fun canBackTraceThroughXnXyTransformerSpur() {
        //
        // s0 11 tx1 21--c2--21 tx3 2
        //
        val n = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toPowerTransformer(listOf(PhaseCode.XY, PhaseCode.XN)) // tx1
            .toAcls(PhaseCode.XN) // c2
            .toPowerTransformer(listOf(PhaseCode.XN, PhaseCode.XY)) // tx3
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", PhaseCode.ABC)
        PhaseValidator.validatePhases(n, "tx1", PhaseCode.AC, PhaseCode.AN)
        PhaseValidator.validatePhases(n, "c2", PhaseCode.AN, PhaseCode.AN)
        //
        // NOTE: This is impacted on the XY -> X issue as described elsewhere. If this is fixed you should replace the following test with
        //       `PhaseValidator.validatePhases(n, "tx3", PhaseCode.AN, PhaseCode.AC)`
        //
        PhaseValidator.validatePhases(n, "tx3", PhaseCode.AN, PhaseCode.AB)
    }

    @Test
    internal fun `can set phases from an unknown nominal phase`() {
        //
        // 1--c0--21--c1--2
        //
        val n = TestNetworkBuilder()
            .fromAcls(PhaseCode.X) // c0
            .toAcls(PhaseCode.ABC) // c1
            .network

        val t = n.getT("c0", 2)
        t.normalPhases[SPK.X] = SPK.A
        t.currentPhases[SPK.X] = SPK.A
        setPhases.run(t, NetworkStateOperators.NORMAL)
        setPhases.run(t, NetworkStateOperators.CURRENT)

        PhaseValidator.validatePhases(n, "c0", PhaseCode.NONE, PhaseCode.A)
        PhaseValidator.validatePhases(n, "c1", listOf(SPK.A, SPK.NONE, SPK.NONE), listOf(SPK.A, SPK.NONE, SPK.NONE))
    }

    @Test
    internal fun `energises around dropped phase dual transformer loop`() {
        //
        // This was seen in PCOR data for a dual transformer site (BET006 - RHEOLA P58E) on a SWER line with an LV2 circuit.
        //
        //            21--c3--21 tx4 21--c5--21
        //            |                       |
        //            c2                      |
        //            |                       |
        //            1                       |
        // s0 11--c1--2                       c6
        //            1                       |
        //            |                       |
        //            c7                      |
        //            |                       |
        //            21--c8--21 tx9 21--c10-221--c11-2
        //
        val ns = TestNetworkBuilder()
            .fromSource(PhaseCode.A) // s0
            .toAcls(PhaseCode.A) // c1
            .toAcls(PhaseCode.A) // c2
            .toAcls(PhaseCode.A) // c3
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.AN)) // tx4
            .toAcls(PhaseCode.AN) // c5
            .toAcls(PhaseCode.AN) // c6
            .branchFrom("c1")
            .toAcls(PhaseCode.A) // c7
            .toAcls(PhaseCode.A) // c8
            .toPowerTransformer(listOf(PhaseCode.A, PhaseCode.ABN)) // tx9
            .toAcls(PhaseCode.ABN) // c10
            .connectTo("c6", 2)
            .toAcls(PhaseCode.ABN) // c11
            .buildAndLog()

        PhaseValidator.validatePhases(ns, "c1", PhaseCode.A, PhaseCode.A)
        PhaseValidator.validatePhases(ns, "c2", PhaseCode.A, PhaseCode.A)
        PhaseValidator.validatePhases(ns, "c3", PhaseCode.A, PhaseCode.A)
        PhaseValidator.validatePhases(ns, "tx4", PhaseCode.A, PhaseCode.AN)
        PhaseValidator.validatePhases(ns, "c5", PhaseCode.AN, PhaseCode.AN)
        PhaseValidator.validatePhases(ns, "c6", PhaseCode.AN, PhaseCode.AN)
        PhaseValidator.validatePhases(ns, "c7", PhaseCode.A, PhaseCode.A)
        PhaseValidator.validatePhases(ns, "c8", PhaseCode.A, PhaseCode.A)
        PhaseValidator.validatePhases(ns, "tx9", PhaseCode.A, PhaseCode.ABN)
        PhaseValidator.validatePhases(ns, "c10", PhaseCode.ABN, PhaseCode.ABN)
        PhaseValidator.validatePhases(ns, "c11", PhaseCode.ABN, PhaseCode.ABN)
    }

    private fun validateTxPhases(sourcePhases: PhaseCode, txPhase1: PhaseCode, txPhase2: PhaseCode, expectedPhases1: PhaseCode, expectedPhases2: PhaseCode) =
        validateTxPhases(sourcePhases, txPhase1, txPhase2, expectedPhases1, expectedPhases2.singlePhases)

    private fun validateTxPhases(sourcePhases: PhaseCode, txPhase1: PhaseCode, txPhase2: PhaseCode, expectedPhases1: PhaseCode, expectedPhases2: List<SPK>) {
        //
        // s0 11--tx1--21--c2--2
        //
        val n = TestNetworkBuilder()
            .fromSource(sourcePhases) // s0
            .toPowerTransformer(listOf(txPhase1, txPhase2)) // tx1
            .toAcls(txPhase2) // c2
            .buildAndLog()

        PhaseValidator.validatePhases(n, "s0", sourcePhases)
        PhaseValidator.validatePhases(n, "tx1", expectedPhases1.singlePhases, expectedPhases2)
        PhaseValidator.validatePhases(n, "c2", expectedPhases2, expectedPhases2)
    }

    private fun TestNetworkBuilder.buildAndLog() = build(debugLogger = LoggerFactory.getLogger(javaClass)).apply {
        PhaseLogger.trace(listOf<EnergySource>())
    }

    private fun NetworkService.getT(ce: String, t: Int): Terminal =
        get<ConductingEquipment>(ce)!!.getTerminal(t)!!

}
