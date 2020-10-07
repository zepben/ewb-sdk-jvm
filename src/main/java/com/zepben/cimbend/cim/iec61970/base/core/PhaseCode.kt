/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode.*
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * An unordered enumeration of phase identifiers.  Allows designation of phases for both transmission and distribution equipment,
 * circuits and loads.   The enumeration, by itself, does not describe how the phases are connected together or connected to ground.
 * Ground is not explicitly denoted as a phase.
 * <p>
 * Residential and small commercial loads are often served from single-phase, or split-phase, secondary circuits. For example of s12N,
 * phases 1 and 2 refer to hot wires that are 180 degrees out of phase, while N refers to the neutral wire. Through single-phase
 * transformer connections, these secondary circuits may be served from one or two of the primary phases A, B, and C. For three-phase
 * loads, use the A, B, C phase codes instead of s12N.
 *
 * @property NONE No phases specified.
 * @property A Phase A.
 * @property B Phase B.
 * @property C Phase C.
 * @property N Neutral phase.
 * @property AB Phases A and B.
 * @property AC Phases A and C.
 * @property AN Phases A and neutral.
 * @property BC Phases B and C.
 * @property BN Phases B and neutral.
 * @property CN Phases C and neutral.
 * @property ABC Phases A, B, and C.
 * @property ABN Phases A, B, and neutral.
 * @property ACN Phases A, C and neutral.
 * @property BCN Phases B, C, and neutral.
 * @property ABCN Phases A, B, C, and N.
 * @property X Unknown non-neutral phase.
 * @property XN Unknown non-neutral phase plus neutral.
 * @property XY Two unknown non-neutral phases.
 * @property XYN Two unknown non-neutral phases plus neutral.
 * @property Y Unknown non-neutral phase.
 * @property YN Unknown non-neutral phase plus neutral.
 */
enum class PhaseCode(vararg singlePhases: SinglePhaseKind) {

    NONE(SinglePhaseKind.NONE),
    A(SinglePhaseKind.A),
    B(SinglePhaseKind.B),
    C(SinglePhaseKind.C),
    N(SinglePhaseKind.N),
    AB(SinglePhaseKind.A, SinglePhaseKind.B),
    AC(SinglePhaseKind.A, SinglePhaseKind.C),
    AN(SinglePhaseKind.A, SinglePhaseKind.N),
    BC(SinglePhaseKind.B, SinglePhaseKind.C),
    BN(SinglePhaseKind.B, SinglePhaseKind.N),
    CN(SinglePhaseKind.C, SinglePhaseKind.N),
    ABC(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C),
    ABN(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.N),
    ACN(SinglePhaseKind.A, SinglePhaseKind.C, SinglePhaseKind.N),
    BCN(SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N),
    ABCN(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N),
    X(SinglePhaseKind.X),
    XN(SinglePhaseKind.X, SinglePhaseKind.N),
    XY(SinglePhaseKind.X, SinglePhaseKind.Y),
    XYN(SinglePhaseKind.X, SinglePhaseKind.Y, SinglePhaseKind.N),
    Y(SinglePhaseKind.Y),
    YN(SinglePhaseKind.Y, SinglePhaseKind.N);

    private val singlePhases: List<SinglePhaseKind> = Collections.unmodifiableList(Stream.of(*singlePhases).collect(Collectors.toList()))

    fun numPhases(): Int {
        return when (this) {
            NONE -> 0
            else -> singlePhases.size
        }
    }

    fun singlePhases(): List<SinglePhaseKind> {
        return singlePhases
    }
}
