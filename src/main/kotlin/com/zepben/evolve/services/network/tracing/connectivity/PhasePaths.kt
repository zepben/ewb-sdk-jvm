/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

object PhasePaths {

    private val straightPhasePaths = mapOf(
        SPK.A to NominalPhasePath(SPK.A, SPK.A),
        SPK.B to NominalPhasePath(SPK.B, SPK.B),
        SPK.C to NominalPhasePath(SPK.C, SPK.C),
        SPK.N to NominalPhasePath(SPK.N, SPK.N),
        SPK.X to NominalPhasePath(SPK.X, SPK.X),
        SPK.Y to NominalPhasePath(SPK.Y, SPK.Y),
    )

    private val knownPhaseCodes = PhaseCode.entries.filter { pc -> pc.singlePhases.any { it in PhaseCode.ABC.singlePhases } || (pc == PhaseCode.N) }
    private val unknownPhaseCodes = PhaseCode.entries.filter { pc -> pc.singlePhases.any { it in PhaseCode.XY.singlePhases } }

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

    val viableInferredPhaseConnectivity: Map<PhaseCode, Map<PhaseCode, Map<SPK, List<SPK>>>> = mapOf(
        PhaseCode.XY to mapOf(
            PhaseCode.ABC to mapOf(SPK.X to listOf(SPK.A, SPK.B, SPK.C), SPK.Y to listOf(SPK.B, SPK.C)),
            PhaseCode.AB to mapOf(SPK.X to listOf(SPK.A, SPK.B), SPK.Y to listOf(SPK.B)),
            PhaseCode.AC to mapOf(SPK.X to listOf(SPK.A, SPK.C), SPK.Y to listOf(SPK.C)),
            PhaseCode.BC to mapOf(SPK.X to listOf(SPK.B, SPK.C), SPK.Y to listOf(SPK.B, SPK.C)),
            PhaseCode.A to mapOf(SPK.X to listOf(SPK.A)),
            PhaseCode.B to mapOf(SPK.X to listOf(SPK.B), SPK.Y to listOf(SPK.B)),
            PhaseCode.C to mapOf(SPK.X to listOf(SPK.C), SPK.Y to listOf(SPK.C))
        ),
        PhaseCode.X to mapOf(
            PhaseCode.ABC to mapOf(SPK.X to listOf(SPK.A, SPK.B, SPK.C)),
            PhaseCode.AB to mapOf(SPK.X to listOf(SPK.A, SPK.B)),
            PhaseCode.AC to mapOf(SPK.X to listOf(SPK.A, SPK.C)),
            PhaseCode.BC to mapOf(SPK.X to listOf(SPK.B, SPK.C)),
            PhaseCode.A to mapOf(SPK.X to listOf(SPK.A)),
            PhaseCode.B to mapOf(SPK.X to listOf(SPK.B)),
            PhaseCode.C to mapOf(SPK.X to listOf(SPK.C))
        ),
        PhaseCode.Y to mapOf(
            PhaseCode.ABC to mapOf(SPK.Y to listOf(SPK.B, SPK.C)),
            PhaseCode.AB to mapOf(SPK.Y to listOf(SPK.B)),
            PhaseCode.AC to mapOf(SPK.Y to listOf(SPK.C)),
            PhaseCode.BC to mapOf(SPK.Y to listOf(SPK.B, SPK.C)),
            PhaseCode.A to mapOf(),
            PhaseCode.B to mapOf(SPK.Y to listOf(SPK.B)),
            PhaseCode.C to mapOf(SPK.Y to listOf(SPK.C))
        )
    )

}
