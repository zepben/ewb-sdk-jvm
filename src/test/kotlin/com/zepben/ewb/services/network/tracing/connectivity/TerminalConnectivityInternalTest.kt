/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.connectivity

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.LinearShuntCompensator
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.reflect.full.primaryConstructor

internal class TerminalConnectivityInternalTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun pathsThroughHv3Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABC to PhaseCode.ABC returns PhaseCode.ABC,
            PhaseCode.ABC to PhaseCode.ABCN returns PhaseCode.ABCN,
            PhaseCode.ABCN to PhaseCode.ABC returns PhaseCode.ABC,
        )
    }

    @Test
    internal fun pathsThroughHv1Hv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AB to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.BC to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.AC to PhaseCode.AC returns PhaseCode.AC,

            PhaseCode.AB to PhaseCode.XY returns PhaseCode.XY,
            PhaseCode.BC to PhaseCode.XY returns PhaseCode.XY,
            PhaseCode.AC to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.XY to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.XY to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.XY to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.XY to PhaseCode.XY returns PhaseCode.XY,
        )
    }

    @Test
    internal fun pathsThroughHv1Lv2Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AB to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.AB to PhaseCode.BCN returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.ACN returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.BC to PhaseCode.ABN returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.BC to PhaseCode.ACN returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.AC to PhaseCode.ABN returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.BCN returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.ACN returns PhaseCode.ACN,
            PhaseCode.AC to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.XY to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.XY to PhaseCode.ACN returns PhaseCode.ACN,
            PhaseCode.XY to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.XY to PhaseCode.XYN returns PhaseCode.XYN,
        )
    }

    @Test
    internal fun pathsThroughHv1Lv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AB to PhaseCode.AN returns PhaseCode.AN,
            PhaseCode.AB to PhaseCode.BN returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.CN returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.BC to PhaseCode.AN returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.BN returns PhaseCode.BN,
            PhaseCode.BC to PhaseCode.CN returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.AC to PhaseCode.AN returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.BN returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.CN returns PhaseCode.CN,
            PhaseCode.AC to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.XY to PhaseCode.AN returns PhaseCode.AN,
            PhaseCode.XY to PhaseCode.BN returns PhaseCode.BN,
            PhaseCode.XY to PhaseCode.CN returns PhaseCode.CN,
            PhaseCode.XY to PhaseCode.XN returns PhaseCode.XN,
        )
    }

    @Test
    internal fun pathsThroughLv2Lv2Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABN to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.BCN to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.ACN to PhaseCode.ACN returns PhaseCode.ACN,

            PhaseCode.ABN to PhaseCode.XYN returns PhaseCode.XYN,
            PhaseCode.BCN to PhaseCode.XYN returns PhaseCode.XYN,
            PhaseCode.ACN to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.XYN to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.XYN to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.XYN to PhaseCode.ACN returns PhaseCode.ACN,
            PhaseCode.XYN to PhaseCode.XYN returns PhaseCode.XYN,
        )
    }

    @Test
    internal fun pathsThroughLv2Hv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABN to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.ABN to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.BCN to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.BCN to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.ACN to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.ACN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.XYN to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.XYN to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.XYN to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.XYN to PhaseCode.XY returns PhaseCode.XY,
        )
    }

    @Test
    internal fun pathsThroughLv1Hv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AN to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.AN to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.AN to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.AN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.BN to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.BN to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.BN to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.BN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.CN to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.CN to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.CN to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.CN to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.XN to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.XN to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.XN to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.XN to PhaseCode.XY returns PhaseCode.XY,
        )
    }

    @Test
    internal fun pathsThroughHv1SwerTx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AB to PhaseCode.A returns PhaseCode.A,
            PhaseCode.AB to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.AB to PhaseCode.X returns PhaseCode.X,

            PhaseCode.BC to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.B returns PhaseCode.B,
            PhaseCode.BC to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.BC to PhaseCode.X returns PhaseCode.X,

            PhaseCode.AC to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.AC to PhaseCode.C returns PhaseCode.C,
            PhaseCode.AC to PhaseCode.X returns PhaseCode.X,

            PhaseCode.XY to PhaseCode.A returns PhaseCode.A,
            PhaseCode.XY to PhaseCode.B returns PhaseCode.B,
            PhaseCode.XY to PhaseCode.C returns PhaseCode.C,
            PhaseCode.XY to PhaseCode.X returns PhaseCode.X,
        )
    }

    @Test
    internal fun pathsThroughSwerHv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.A to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.A to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.B to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.B to PhaseCode.AC returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.C to PhaseCode.AB returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.BC returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.C to PhaseCode.XY returns PhaseCode.XY,

            PhaseCode.X to PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.X to PhaseCode.BC returns PhaseCode.BC,
            PhaseCode.X to PhaseCode.AC returns PhaseCode.AC,
            PhaseCode.X to PhaseCode.XY returns PhaseCode.XY,
        )
    }

    @Test
    internal fun pathsThroughSwerLv1Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.A to PhaseCode.AN returns PhaseCode.AN,
            PhaseCode.A to PhaseCode.BN returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.CN returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.B to PhaseCode.AN returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.BN returns PhaseCode.BN,
            PhaseCode.B to PhaseCode.CN returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.C to PhaseCode.AN returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.BN returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.CN returns PhaseCode.CN,
            PhaseCode.C to PhaseCode.XN returns PhaseCode.XN,

            PhaseCode.X to PhaseCode.AN returns PhaseCode.AN,
            PhaseCode.X to PhaseCode.BN returns PhaseCode.BN,
            PhaseCode.X to PhaseCode.CN returns PhaseCode.CN,
            PhaseCode.X to PhaseCode.XN returns PhaseCode.XN,
        )
    }

    @Test
    internal fun pathsThroughLv1SwerTx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.AN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.AN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.AN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.AN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.BN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.BN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.BN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.BN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.CN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.CN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.CN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.CN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.XN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.XN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.XN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.XN to PhaseCode.X returns PhaseCode.X,
        )
    }

    @Test
    internal fun pathsThroughSwerLv2Tx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.A to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.A to PhaseCode.BCN returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.ACN returns PhaseCode.NONE,
            PhaseCode.A to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.B to PhaseCode.ABN returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.B to PhaseCode.ACN returns PhaseCode.NONE,
            PhaseCode.B to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.C to PhaseCode.ABN returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.BCN returns PhaseCode.NONE,
            PhaseCode.C to PhaseCode.ACN returns PhaseCode.ACN,
            PhaseCode.C to PhaseCode.XYN returns PhaseCode.XYN,

            PhaseCode.X to PhaseCode.ABN returns PhaseCode.ABN,
            PhaseCode.X to PhaseCode.BCN returns PhaseCode.BCN,
            PhaseCode.X to PhaseCode.ACN returns PhaseCode.ACN,
            PhaseCode.X to PhaseCode.XYN returns PhaseCode.XYN,
        )
    }

    @Test
    internal fun pathsThroughLv2SwerTx() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.ABN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.BCN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.BCN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.ACN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.ACN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.XYN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.XYN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.XYN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.XYN to PhaseCode.X returns PhaseCode.X,
        )
    }

    @Test
    internal fun pathsThroughLv2SwerTx2() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.ABN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.ABN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.BCN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.BCN to PhaseCode.C returns PhaseCode.NONE,
            PhaseCode.BCN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.ACN to PhaseCode.A returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.B returns PhaseCode.NONE,
            PhaseCode.ACN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.ACN to PhaseCode.X returns PhaseCode.X,

            PhaseCode.XYN to PhaseCode.A returns PhaseCode.A,
            PhaseCode.XYN to PhaseCode.B returns PhaseCode.B,
            PhaseCode.XYN to PhaseCode.C returns PhaseCode.C,
            PhaseCode.XYN to PhaseCode.X returns PhaseCode.X,
        )
    }

    @Test
    internal fun `can filter transformer paths`() {
        validatePathsThrough<PowerTransformer>(
            PhaseCode.ABC to PhaseCode.ABC using PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.ABCN to PhaseCode.ABC using PhaseCode.AC returns PhaseCode.AC,

            // Neutral is picked up as it is added by the transformer, so not filtered out by the included phases.
            PhaseCode.ABC to PhaseCode.ABCN using PhaseCode.BC returns PhaseCode.BCN,
        )
    }

    @Test
    internal fun `check shunt compensator paths`() {
        validatePathsThrough<LinearShuntCompensator>(
            PhaseCode.ABC to PhaseCode.ABC returns PhaseCode.ABC,
            PhaseCode.ABC to PhaseCode.N returns PhaseCode.N,
            PhaseCode.N to PhaseCode.ABC returns PhaseCode.ABC,

            //
            // NOTE: When moving to/from the grounding terminal, all phases are added by the shunt compensator, so
            //       will not be impacted by the included phases.
            //
            PhaseCode.ABC to PhaseCode.ABC using PhaseCode.AB returns PhaseCode.AB,
            PhaseCode.ABC to PhaseCode.N using PhaseCode.AB returns PhaseCode.N,
            PhaseCode.N to PhaseCode.ABC using PhaseCode.N returns PhaseCode.ABC,
        ) {
            groundingTerminal = terminals.firstOrNull { it.phases == PhaseCode.N }
        }
    }

    @Test
    internal fun `check straight paths`() {
        validatePathsThrough<AcLineSegment>(
            PhaseCode.ABC to PhaseCode.ABC returns PhaseCode.ABC,
            PhaseCode.ABC to PhaseCode.ABC using PhaseCode.AB returns PhaseCode.AB,
        )
    }

    private inline fun <reified T : ConductingEquipment> validatePathsThrough(vararg paths: ExpectedPaths, additionalSetup: T.() -> Unit = {}) {
        val ce = T::class.primaryConstructor!!.call(generateId())
        val terminal = Terminal(generateId()).also { ce.addTerminal(it) }
        val otherTerminal = Terminal(generateId()).also { ce.addTerminal(it) }

        paths.forEach { (from, to, expected, included) ->
            terminal.phases = from
            otherTerminal.phases = to

            ce.additionalSetup()

            assertThat(
                "${T::class.simpleName} from $from to $to using $included should return $expected",
                findPathsBetween(terminal, otherTerminal, included).nominalPhasePaths.map { it.to },
                when (expected) {
                    PhaseCode.NONE -> empty()
                    else -> containsInAnyOrder(*expected.singlePhases.toTypedArray())
                }
            )
        }
    }

    private fun findPathsBetween(terminal: Terminal, otherTerminal: Terminal, included: PhaseCode): ConnectivityResult =
        TerminalConnectivityInternal.between(terminal, otherTerminal, included.singlePhases.toSet())

    private infix fun PhaseCode.to(to: PhaseCode): ExpectedPaths = ExpectedPaths(this, to)
    private infix fun ExpectedPaths.using(included: PhaseCode): ExpectedPaths = ExpectedPaths(from, to, expected, included)
    private infix fun ExpectedPaths.returns(expected: PhaseCode): ExpectedPaths = ExpectedPaths(from, to, expected, included)

    private data class ExpectedPaths(
        val from: PhaseCode,
        val to: PhaseCode,
        val expected: PhaseCode = to,
        val included: PhaseCode = from
    ) {
        init {
            require(from.singlePhases.containsAll(included.singlePhases)) { "`included` must only contain phases in `from`" }
        }
    }

}
