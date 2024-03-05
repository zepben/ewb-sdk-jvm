/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode.*
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import java.util.function.Consumer

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
 * @property s1 Secondary phase 1.
 * @property s1N Secondary phase 1 and neutral.
 * @property s12 Secondary phase 1 and 2.
 * @property s12N Secondary phases 1, 2, and neutral.
 * @property s2 Secondary phase 2.
 * @property s2N Secondary phase 2 and neutral.
 */
@Suppress("EnumEntryName")
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
    YN(SinglePhaseKind.Y, SinglePhaseKind.N),
    s1(SinglePhaseKind.s1),
    s1N(SinglePhaseKind.s1, SinglePhaseKind.N),
    s12(SinglePhaseKind.s1, SinglePhaseKind.s2),
    s12N(SinglePhaseKind.s1, SinglePhaseKind.s2, SinglePhaseKind.N),
    s2(SinglePhaseKind.s2),
    s2N(SinglePhaseKind.s2, SinglePhaseKind.N);

    val singlePhases: List<SinglePhaseKind> = singlePhases.asList().asUnmodifiable()

    fun numPhases(): Int {
        return when (this) {
            NONE -> 0
            else -> singlePhases.size
        }
    }

    fun withoutNeutral(): PhaseCode {
        return if (!singlePhases.contains(SinglePhaseKind.N))
            this
        else
            fromSinglePhases(singlePhases.filter { it != SinglePhaseKind.N })
    }

    operator fun contains(phase: SinglePhaseKind): Boolean = singlePhases.contains(phase)
    operator fun plus(phase: SinglePhaseKind): PhaseCode = fromSinglePhases(singlePhases + phase)
    operator fun plus(phaseCode: PhaseCode): PhaseCode = fromSinglePhases(singlePhases + phaseCode.singlePhases)
    operator fun minus(phase: SinglePhaseKind): PhaseCode = fromSinglePhases(singlePhases - phase)
    operator fun minus(phaseCode: PhaseCode): PhaseCode = fromSinglePhases(singlePhases - phaseCode.singlePhases.toSet())

    fun <R> map(transform: (SinglePhaseKind) -> R): List<R> = singlePhases.map(transform)
    fun any(predicate: (SinglePhaseKind) -> Boolean): Boolean = singlePhases.any(predicate)
    fun all(predicate: (SinglePhaseKind) -> Boolean): Boolean = singlePhases.all(predicate)

    fun forEach(action: (SinglePhaseKind) -> Unit) {
        for (element in singlePhases) action(element)
    }

    /**
     * Java interop version to prevent a `return null` or `return Unit.INSTANCE`
     */
    fun forEach(action: Consumer<SinglePhaseKind>) {
        for (element in singlePhases) action.accept(element)
    }

    init {
        byPhases[singlePhases.toSet()] = this
    }

    companion object {

        fun fromSinglePhases(singlePhases: Collection<SinglePhaseKind>): PhaseCode {
            return if (singlePhases is Set)
                byPhases[singlePhases] ?: NONE
            else
                byPhases[singlePhases.toSet()] ?: NONE
        }

    }

}

private val byPhases by lazy { mutableMapOf<Set<SinglePhaseKind>, PhaseCode>() }
