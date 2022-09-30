/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

object TransformerPhasePaths {

    private fun path(from: SPK, to: SPK) = NominalPhasePath(from, to)

    // THis is used to indicate that a transformer adds a neutral and it should be energised from the transformer.
    private val addNeutral = path(SPK.NONE, SPK.N)

    val lookup: Map<PhaseCode, Map<PhaseCode, List<NominalPhasePath>>> = mapOf(
        PhaseCode.ABCN to mapOf(
            PhaseCode.ABCN to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B), path(SPK.C, SPK.C), path(SPK.N, SPK.N)),
            PhaseCode.ABC to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B), path(SPK.C, SPK.C)),
        ),
        PhaseCode.AN to mapOf(
            PhaseCode.AN to listOf(path(SPK.A, SPK.A), path(SPK.N, SPK.N)),
            PhaseCode.XN to listOf(path(SPK.A, SPK.X), path(SPK.N, SPK.N)),
            PhaseCode.AB to listOf(path(SPK.A, SPK.A), path(SPK.NONE, SPK.B)),
            PhaseCode.XY to listOf(path(SPK.A, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.X to listOf(path(SPK.A, SPK.X)),
            PhaseCode.A to listOf(path(SPK.A, SPK.A)),
        ),
        PhaseCode.BN to mapOf(
            PhaseCode.BN to listOf(path(SPK.B, SPK.B), path(SPK.N, SPK.N)),
            PhaseCode.XN to listOf(path(SPK.B, SPK.X), path(SPK.N, SPK.N)),
            PhaseCode.BC to listOf(path(SPK.B, SPK.B), path(SPK.NONE, SPK.C)),
            PhaseCode.XY to listOf(path(SPK.B, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.B to listOf(path(SPK.B, SPK.B)),
            PhaseCode.X to listOf(path(SPK.B, SPK.X)),
        ),
        PhaseCode.CN to mapOf(
            PhaseCode.CN to listOf(path(SPK.C, SPK.C), path(SPK.N, SPK.N)),
            PhaseCode.XN to listOf(path(SPK.C, SPK.X), path(SPK.N, SPK.N)),
            PhaseCode.AC to listOf(path(SPK.C, SPK.C), path(SPK.NONE, SPK.A)),
            PhaseCode.XY to listOf(path(SPK.C, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.C to listOf(path(SPK.C, SPK.C)),
            PhaseCode.X to listOf(path(SPK.C, SPK.X)),
        ),
        PhaseCode.XN to mapOf(
            PhaseCode.AN to listOf(path(SPK.X, SPK.A), path(SPK.N, SPK.N)),
            PhaseCode.BN to listOf(path(SPK.X, SPK.B), path(SPK.N, SPK.N)),
            PhaseCode.CN to listOf(path(SPK.X, SPK.C), path(SPK.N, SPK.N)),
            PhaseCode.XN to listOf(path(SPK.X, SPK.X), path(SPK.N, SPK.N)),
            PhaseCode.AB to listOf(path(SPK.X, SPK.A), path(SPK.NONE, SPK.B)),
            PhaseCode.BC to listOf(path(SPK.X, SPK.B), path(SPK.NONE, SPK.C)),
            PhaseCode.AC to listOf(path(SPK.X, SPK.C), path(SPK.NONE, SPK.A)),
            PhaseCode.XY to listOf(path(SPK.X, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.A to listOf(path(SPK.X, SPK.A)),
            PhaseCode.B to listOf(path(SPK.X, SPK.B)),
            PhaseCode.C to listOf(path(SPK.X, SPK.C)),
            PhaseCode.X to listOf(path(SPK.X, SPK.X)),
        ),
        PhaseCode.ABC to mapOf(
            PhaseCode.ABCN to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B), path(SPK.C, SPK.C), addNeutral),
            PhaseCode.ABC to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B), path(SPK.C, SPK.C)),
        ),
        PhaseCode.AB to mapOf(
            PhaseCode.ABN to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B), addNeutral),
            PhaseCode.AN to listOf(path(SPK.A, SPK.A), addNeutral),
            PhaseCode.XN to listOf(path(SPK.A, SPK.X), addNeutral),
            PhaseCode.AB to listOf(path(SPK.A, SPK.A), path(SPK.B, SPK.B)),
            PhaseCode.XY to listOf(path(SPK.A, SPK.X), path(SPK.B, SPK.Y)),
            PhaseCode.A to listOf(path(SPK.A, SPK.A)),
            PhaseCode.X to listOf(path(SPK.A, SPK.X)),
        ),
        PhaseCode.BC to mapOf(
            PhaseCode.BCN to listOf(path(SPK.B, SPK.B), path(SPK.C, SPK.C), addNeutral),
            PhaseCode.BN to listOf(path(SPK.B, SPK.B), addNeutral),
            PhaseCode.XN to listOf(path(SPK.B, SPK.X), addNeutral),
            PhaseCode.BC to listOf(path(SPK.B, SPK.B), path(SPK.C, SPK.C)),
            PhaseCode.XY to listOf(path(SPK.B, SPK.X), path(SPK.C, SPK.Y)),
            PhaseCode.B to listOf(path(SPK.B, SPK.B)),
            PhaseCode.X to listOf(path(SPK.B, SPK.X)),
        ),
        PhaseCode.AC to mapOf(
            PhaseCode.ACN to listOf(path(SPK.A, SPK.A), path(SPK.C, SPK.C), addNeutral),
            PhaseCode.CN to listOf(path(SPK.C, SPK.C), addNeutral),
            PhaseCode.XN to listOf(path(SPK.C, SPK.X), addNeutral),
            PhaseCode.AC to listOf(path(SPK.A, SPK.A), path(SPK.C, SPK.C)),
            PhaseCode.XY to listOf(path(SPK.A, SPK.X), path(SPK.C, SPK.Y)),
            PhaseCode.C to listOf(path(SPK.C, SPK.C)),
            PhaseCode.X to listOf(path(SPK.C, SPK.X)),
        ),
        PhaseCode.XY to mapOf(
            PhaseCode.XYN to listOf(path(SPK.X, SPK.X), path(SPK.Y, SPK.Y), addNeutral),
            PhaseCode.AN to listOf(path(SPK.X, SPK.A), addNeutral),
            PhaseCode.BN to listOf(path(SPK.X, SPK.B), addNeutral),
            PhaseCode.CN to listOf(path(SPK.X, SPK.C), addNeutral),
            PhaseCode.XN to listOf(path(SPK.X, SPK.X), addNeutral),
            PhaseCode.AB to listOf(path(SPK.X, SPK.A), path(SPK.Y, SPK.B)),
            PhaseCode.BC to listOf(path(SPK.X, SPK.B), path(SPK.Y, SPK.C)),
            PhaseCode.AC to listOf(path(SPK.X, SPK.A), path(SPK.Y, SPK.C)),
            PhaseCode.XY to listOf(path(SPK.X, SPK.X), path(SPK.Y, SPK.Y)),
            PhaseCode.A to listOf(path(SPK.X, SPK.A)),
            PhaseCode.B to listOf(path(SPK.X, SPK.B)),
            PhaseCode.C to listOf(path(SPK.X, SPK.C)),
            PhaseCode.X to listOf(path(SPK.X, SPK.X)),
        ),
        PhaseCode.A to mapOf(
            PhaseCode.AN to listOf(path(SPK.A, SPK.A), addNeutral),
            PhaseCode.XN to listOf(path(SPK.A, SPK.X), addNeutral),
            PhaseCode.AB to listOf(path(SPK.A, SPK.A), path(SPK.NONE, SPK.B)),
            PhaseCode.XY to listOf(path(SPK.A, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.A to listOf(path(SPK.A, SPK.A)),
            PhaseCode.X to listOf(path(SPK.A, SPK.X)),
        ),
        PhaseCode.B to mapOf(
            PhaseCode.BN to listOf(path(SPK.B, SPK.B), addNeutral),
            PhaseCode.XN to listOf(path(SPK.B, SPK.X), addNeutral),
            PhaseCode.BC to listOf(path(SPK.B, SPK.B), path(SPK.NONE, SPK.C)),
            PhaseCode.XY to listOf(path(SPK.B, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.B to listOf(path(SPK.B, SPK.B)),
            PhaseCode.X to listOf(path(SPK.B, SPK.X)),
        ),
        PhaseCode.C to mapOf(
            PhaseCode.CN to listOf(path(SPK.C, SPK.C), addNeutral),
            PhaseCode.XN to listOf(path(SPK.C, SPK.X), addNeutral),
            PhaseCode.AC to listOf(path(SPK.C, SPK.C), path(SPK.NONE, SPK.A)),
            PhaseCode.XY to listOf(path(SPK.C, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.C to listOf(path(SPK.C, SPK.C)),
            PhaseCode.X to listOf(path(SPK.C, SPK.X)),
        ),
        PhaseCode.X to mapOf(
            PhaseCode.AN to listOf(path(SPK.X, SPK.A), addNeutral),
            PhaseCode.BN to listOf(path(SPK.X, SPK.B), addNeutral),
            PhaseCode.CN to listOf(path(SPK.X, SPK.C), addNeutral),
            PhaseCode.XN to listOf(path(SPK.X, SPK.X), addNeutral),
            PhaseCode.AB to listOf(path(SPK.X, SPK.A), path(SPK.NONE, SPK.B)),
            PhaseCode.BC to listOf(path(SPK.X, SPK.B), path(SPK.NONE, SPK.C)),
            PhaseCode.AC to listOf(path(SPK.X, SPK.C), path(SPK.NONE, SPK.A)),
            PhaseCode.XY to listOf(path(SPK.X, SPK.X), path(SPK.NONE, SPK.Y)),
            PhaseCode.A to listOf(path(SPK.X, SPK.A)),
            PhaseCode.B to listOf(path(SPK.X, SPK.B)),
            PhaseCode.C to listOf(path(SPK.X, SPK.C)),
            PhaseCode.X to listOf(path(SPK.X, SPK.X)),
        ),
    )

}
