/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TerminalConnectivityInternalTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val connectivity = TerminalConnectivityInternal()

    @Test
    internal fun pathsThroughHv3Tx() {
        validateTxPaths(PhaseCode.ABC, PhaseCode.ABC)
        validateTxPaths(PhaseCode.ABC, PhaseCode.ABCN)
        validateTxPaths(PhaseCode.ABCN, PhaseCode.ABC)
    }

    @Test
    internal fun pathsThroughHv1Hv1Tx() {
        validateTxPaths(PhaseCode.AB, PhaseCode.AB)
        validateTxPaths(PhaseCode.BC, PhaseCode.BC)
        validateTxPaths(PhaseCode.AC, PhaseCode.AC)

        validateTxPaths(PhaseCode.AB, PhaseCode.XY)
        validateTxPaths(PhaseCode.BC, PhaseCode.XY)
        validateTxPaths(PhaseCode.AC, PhaseCode.XY)

        validateTxPaths(PhaseCode.XY, PhaseCode.AB)
        validateTxPaths(PhaseCode.XY, PhaseCode.BC)
        validateTxPaths(PhaseCode.XY, PhaseCode.AC)
        validateTxPaths(PhaseCode.XY, PhaseCode.XY)
    }

    @Test
    internal fun pathsThroughHv1Lv2Tx() {
        validateTxPaths(PhaseCode.AB, PhaseCode.ABN)
        validateTxPaths(PhaseCode.AB, PhaseCode.BCN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.ACN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.XYN)

        validateTxPaths(PhaseCode.BC, PhaseCode.ABN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.BCN)
        validateTxPaths(PhaseCode.BC, PhaseCode.ACN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.XYN)

        validateTxPaths(PhaseCode.AC, PhaseCode.ABN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.BCN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.ACN)
        validateTxPaths(PhaseCode.AC, PhaseCode.XYN)

        validateTxPaths(PhaseCode.XY, PhaseCode.ABN)
        validateTxPaths(PhaseCode.XY, PhaseCode.ACN)
        validateTxPaths(PhaseCode.XY, PhaseCode.BCN)
        validateTxPaths(PhaseCode.XY, PhaseCode.XYN)
    }

    @Test
    internal fun pathsThroughHv1Lv1Tx() {
        validateTxPaths(PhaseCode.AB, PhaseCode.AN)
        validateTxPaths(PhaseCode.AB, PhaseCode.BN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.CN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.XN)

        validateTxPaths(PhaseCode.BC, PhaseCode.AN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.BN)
        validateTxPaths(PhaseCode.BC, PhaseCode.CN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.XN)

        validateTxPaths(PhaseCode.AC, PhaseCode.AN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.BN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.CN)
        validateTxPaths(PhaseCode.AC, PhaseCode.XN)

        validateTxPaths(PhaseCode.XY, PhaseCode.AN)
        validateTxPaths(PhaseCode.XY, PhaseCode.BN)
        validateTxPaths(PhaseCode.XY, PhaseCode.CN)
        validateTxPaths(PhaseCode.XY, PhaseCode.XN)
    }

    @Test
    internal fun pathsThroughLv2Lv2Tx() {
        validateTxPaths(PhaseCode.ABN, PhaseCode.ABN)
        validateTxPaths(PhaseCode.BCN, PhaseCode.BCN)
        validateTxPaths(PhaseCode.ACN, PhaseCode.ACN)

        validateTxPaths(PhaseCode.ABN, PhaseCode.XYN)
        validateTxPaths(PhaseCode.BCN, PhaseCode.XYN)
        validateTxPaths(PhaseCode.ACN, PhaseCode.XYN)

        validateTxPaths(PhaseCode.XYN, PhaseCode.ABN)
        validateTxPaths(PhaseCode.XYN, PhaseCode.BCN)
        validateTxPaths(PhaseCode.XYN, PhaseCode.ACN)
        validateTxPaths(PhaseCode.XYN, PhaseCode.XYN)
    }

    @Test
    internal fun pathsThroughLv2Hv1Tx() {
        validateTxPaths(PhaseCode.ABN, PhaseCode.AB)
        validateTxPaths(PhaseCode.ABN, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.ABN, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.ABN, PhaseCode.XY)

        validateTxPaths(PhaseCode.BCN, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BCN, PhaseCode.BC)
        validateTxPaths(PhaseCode.BCN, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BCN, PhaseCode.XY)

        validateTxPaths(PhaseCode.ACN, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.ACN, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.ACN, PhaseCode.AC)
        validateTxPaths(PhaseCode.ACN, PhaseCode.XY)

        validateTxPaths(PhaseCode.XYN, PhaseCode.AB)
        validateTxPaths(PhaseCode.XYN, PhaseCode.BC)
        validateTxPaths(PhaseCode.XYN, PhaseCode.AC)
        validateTxPaths(PhaseCode.XYN, PhaseCode.XY)
    }

    @Test
    internal fun pathsThroughLv1Hv1Tx() {
        validateTxPaths(PhaseCode.AN, PhaseCode.AB)
        validateTxPaths(PhaseCode.AN, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AN, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AN, PhaseCode.XY)

        validateTxPaths(PhaseCode.BN, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BN, PhaseCode.BC)
        validateTxPaths(PhaseCode.BN, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BN, PhaseCode.XY)

        validateTxPaths(PhaseCode.CN, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.CN, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.CN, PhaseCode.AC)
        validateTxPaths(PhaseCode.CN, PhaseCode.XY)

        validateTxPaths(PhaseCode.XN, PhaseCode.AB)
        validateTxPaths(PhaseCode.XN, PhaseCode.BC)
        validateTxPaths(PhaseCode.XN, PhaseCode.AC)
        validateTxPaths(PhaseCode.XN, PhaseCode.XY)
    }

    @Test
    internal fun pathsThroughHv1SwerTx() {
        validateTxPaths(PhaseCode.AB, PhaseCode.A)
        validateTxPaths(PhaseCode.AB, PhaseCode.B, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.C, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AB, PhaseCode.X)

        validateTxPaths(PhaseCode.BC, PhaseCode.A, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.B)
        validateTxPaths(PhaseCode.BC, PhaseCode.C, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BC, PhaseCode.X)

        validateTxPaths(PhaseCode.AC, PhaseCode.A, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.B, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AC, PhaseCode.C)
        validateTxPaths(PhaseCode.AC, PhaseCode.X)

        validateTxPaths(PhaseCode.XY, PhaseCode.A)
        validateTxPaths(PhaseCode.XY, PhaseCode.B)
        validateTxPaths(PhaseCode.XY, PhaseCode.C)
        validateTxPaths(PhaseCode.XY, PhaseCode.X)
    }

    @Test
    internal fun pathsThroughSwerHv1Tx() {
        validateTxPaths(PhaseCode.A, PhaseCode.AB)
        validateTxPaths(PhaseCode.A, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.A, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.A, PhaseCode.XY)

        validateTxPaths(PhaseCode.B, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.B, PhaseCode.BC)
        validateTxPaths(PhaseCode.B, PhaseCode.AC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.B, PhaseCode.XY)

        validateTxPaths(PhaseCode.C, PhaseCode.AB, PhaseCode.NONE)
        validateTxPaths(PhaseCode.C, PhaseCode.BC, PhaseCode.NONE)
        validateTxPaths(PhaseCode.C, PhaseCode.AC)
        validateTxPaths(PhaseCode.C, PhaseCode.XY)

        validateTxPaths(PhaseCode.X, PhaseCode.AB)
        validateTxPaths(PhaseCode.X, PhaseCode.BC)
        validateTxPaths(PhaseCode.X, PhaseCode.AC)
        validateTxPaths(PhaseCode.X, PhaseCode.XY)
    }

    @Test
    internal fun pathsThroughSwerLv1Tx() {
        validateTxPaths(PhaseCode.A, PhaseCode.AN)
        validateTxPaths(PhaseCode.A, PhaseCode.BN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.A, PhaseCode.CN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.A, PhaseCode.XN)

        validateTxPaths(PhaseCode.B, PhaseCode.AN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.B, PhaseCode.BN)
        validateTxPaths(PhaseCode.B, PhaseCode.CN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.B, PhaseCode.XN)

        validateTxPaths(PhaseCode.C, PhaseCode.AN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.C, PhaseCode.BN, PhaseCode.NONE)
        validateTxPaths(PhaseCode.C, PhaseCode.CN)
        validateTxPaths(PhaseCode.C, PhaseCode.XN)

        validateTxPaths(PhaseCode.X, PhaseCode.AN)
        validateTxPaths(PhaseCode.X, PhaseCode.BN)
        validateTxPaths(PhaseCode.X, PhaseCode.CN)
        validateTxPaths(PhaseCode.X, PhaseCode.XN)
    }

    @Test
    internal fun pathsThroughLv1SwerTx() {
        validateTxPaths(PhaseCode.AN, PhaseCode.A)
        validateTxPaths(PhaseCode.AN, PhaseCode.B, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AN, PhaseCode.C, PhaseCode.NONE)
        validateTxPaths(PhaseCode.AN, PhaseCode.X)

        validateTxPaths(PhaseCode.BN, PhaseCode.A, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BN, PhaseCode.B)
        validateTxPaths(PhaseCode.BN, PhaseCode.C, PhaseCode.NONE)
        validateTxPaths(PhaseCode.BN, PhaseCode.X)

        validateTxPaths(PhaseCode.CN, PhaseCode.A, PhaseCode.NONE)
        validateTxPaths(PhaseCode.CN, PhaseCode.B, PhaseCode.NONE)
        validateTxPaths(PhaseCode.CN, PhaseCode.C)
        validateTxPaths(PhaseCode.CN, PhaseCode.X)

        validateTxPaths(PhaseCode.XN, PhaseCode.A)
        validateTxPaths(PhaseCode.XN, PhaseCode.B)
        validateTxPaths(PhaseCode.XN, PhaseCode.C)
        validateTxPaths(PhaseCode.XN, PhaseCode.X)
    }

    private fun validateTxPaths(primary: PhaseCode, secondary: PhaseCode, traced: PhaseCode = secondary) {
        val tx = PowerTransformer()
        val primaryTerminal = Terminal().apply { phases = primary }.also { tx.addTerminal(it) }
        val secondaryTerminal = Terminal().apply { phases = secondary }.also { tx.addTerminal(it) }

        if (traced != PhaseCode.NONE) {
            assertThat(
                connectivity.between(primaryTerminal, secondaryTerminal).nominalPhasePaths.map { it.to },
                containsInAnyOrder(*traced.singlePhases.toTypedArray())
            )
        } else {
            assertThat(
                connectivity.between(primaryTerminal, secondaryTerminal).nominalPhasePaths,
                empty()
            )
        }
    }
}
