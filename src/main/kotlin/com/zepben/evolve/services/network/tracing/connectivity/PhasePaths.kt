/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath

object PhasePaths {

    private val straightPhasePaths = mapOf(
        A to NominalPhasePath(A, A),
        B to NominalPhasePath(B, B),
        C to NominalPhasePath(C, C),
        N to NominalPhasePath(N, N),
        X to NominalPhasePath(X, X),
        Y to NominalPhasePath(Y, Y),
    )

    private val knownPhaseCodes = PhaseCode.values().filter { pc -> pc.singlePhases.any { it in PhaseCode.ABC.singlePhases } || (pc == PhaseCode.N) }
    private val unknownPhaseCodes = PhaseCode.values().filter { pc -> pc.singlePhases.any { it in PhaseCode.XY.singlePhases } }

    val straightPhaseConnectivity: Map<PhaseCode, Map<PhaseCode, List<NominalPhasePath>>> =
        knownPhaseCodes.associateWith { fromPhases ->
            knownPhaseCodes.associateWith { toPhases ->
                fromPhases.singlePhases.asSequence().filter { toPhases.singlePhases.contains(it) }.mapNotNull { straightPhasePaths[it] }.toList()
            }
        } + unknownPhaseCodes.associateWith { fromPhases ->
            unknownPhaseCodes.associateWith { toPhases ->
                fromPhases.singlePhases.asSequence().filter { toPhases.singlePhases.contains(it) }.mapNotNull { straightPhasePaths[it] }.toList()
            }
        }

    val viableInferredPhaseConnectivity: Map<PhaseCode, Map<PhaseCode, Map<SinglePhaseKind, List<SinglePhaseKind>>>> = mapOf(
        PhaseCode.XY to mapOf(
            PhaseCode.ABC to mapOf(X to listOf(A, B, C), Y to listOf(B, C)),
            PhaseCode.AB to mapOf(X to listOf(A, B), Y to listOf(B)),
            PhaseCode.AC to mapOf(X to listOf(A, C), Y to listOf(C)),
            PhaseCode.BC to mapOf(X to listOf(B, C), Y to listOf(B, C)),
            PhaseCode.A to mapOf(X to listOf(A)),
            PhaseCode.B to mapOf(X to listOf(B), Y to listOf(B)),
            PhaseCode.C to mapOf(X to listOf(C), Y to listOf(C))
        ),
        PhaseCode.X to mapOf(
            PhaseCode.ABC to mapOf(X to listOf(A, B, C)),
            PhaseCode.AB to mapOf(X to listOf(A, B)),
            PhaseCode.AC to mapOf(X to listOf(A, C)),
            PhaseCode.BC to mapOf(X to listOf(B, C)),
            PhaseCode.A to mapOf(X to listOf(A)),
            PhaseCode.B to mapOf(X to listOf(B)),
            PhaseCode.C to mapOf(X to listOf(C))
        ),
        PhaseCode.Y to mapOf(
            PhaseCode.ABC to mapOf(Y to listOf(B, C)),
            PhaseCode.AB to mapOf(Y to listOf(B)),
            PhaseCode.AC to mapOf(Y to listOf(C)),
            PhaseCode.BC to mapOf(Y to listOf(B, C)),
            PhaseCode.A to mapOf(),
            PhaseCode.B to mapOf(Y to listOf(B)),
            PhaseCode.C to mapOf(Y to listOf(C))
        )
    )

}
