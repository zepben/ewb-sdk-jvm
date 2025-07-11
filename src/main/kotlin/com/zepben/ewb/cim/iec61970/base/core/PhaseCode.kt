/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.services.common.extensions.asUnmodifiable
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
 */
@Suppress("EnumEntryName")
enum class PhaseCode(vararg singlePhases: SinglePhaseKind) {


    /**
     * No phases specified.
     */
    NONE(SinglePhaseKind.NONE),

    /**
     * Phase A.
     */
    A(SinglePhaseKind.A),

    /**
     * Phase B.
     */
    B(SinglePhaseKind.B),

    /**
     * Phase C.
     */
    C(SinglePhaseKind.C),

    /**
     * Neutral phase.
     */
    N(SinglePhaseKind.N),

    /**
     * Phases A and B.
     */
    AB(SinglePhaseKind.A, SinglePhaseKind.B),

    /**
     * Phases A and C.
     */
    AC(SinglePhaseKind.A, SinglePhaseKind.C),

    /**
     * Phases A and neutral.
     */
    AN(SinglePhaseKind.A, SinglePhaseKind.N),

    /**
     * Phases B and C.
     */
    BC(SinglePhaseKind.B, SinglePhaseKind.C),

    /**
     * Phases B and neutral.
     */
    BN(SinglePhaseKind.B, SinglePhaseKind.N),

    /**
     * Phases C and neutral.
     */
    CN(SinglePhaseKind.C, SinglePhaseKind.N),

    /**
     * Phases A, B, and C.
     */
    ABC(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C),

    /**
     * Phases A, B, and neutral.
     */
    ABN(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.N),

    /**
     * Phases A, C and neutral.
     */
    ACN(SinglePhaseKind.A, SinglePhaseKind.C, SinglePhaseKind.N),

    /**
     * Phases B, C, and neutral.
     */
    BCN(SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N),

    /**
     * Phases A, B, C, and N.
     */
    ABCN(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N),

    /**
     * Unknown non-neutral phase.
     */
    X(SinglePhaseKind.X),

    /**
     * Unknown non-neutral phase plus neutral.
     */
    XN(SinglePhaseKind.X, SinglePhaseKind.N),

    /**
     * Two unknown non-neutral phases.
     */
    XY(SinglePhaseKind.X, SinglePhaseKind.Y),

    /**
     * Two unknown non-neutral phases plus neutral.
     */
    XYN(SinglePhaseKind.X, SinglePhaseKind.Y, SinglePhaseKind.N),

    /**
     * [ZBEX] Unknown non-neutral phase.
     */
    @ZBEX
    Y(SinglePhaseKind.Y),

    /**
     * [ZBEX] Unknown non-neutral phase plus neutral.
     */
    @ZBEX
    YN(SinglePhaseKind.Y, SinglePhaseKind.N),

    /**
     * Secondary phase 1.
     */
    s1(SinglePhaseKind.s1),

    /**
     * Secondary phase 1 and neutral.
     */
    s1N(SinglePhaseKind.s1, SinglePhaseKind.N),

    /**
     * Secondary phase 1 and 2.
     */
    s12(SinglePhaseKind.s1, SinglePhaseKind.s2),

    /**
     * Secondary phases 1, 2, and neutral.
     */
    s12N(SinglePhaseKind.s1, SinglePhaseKind.s2, SinglePhaseKind.N),

    /**
     * Secondary phase 2.
     */
    s2(SinglePhaseKind.s2),

    /**
     * Secondary phase 2 and neutral.
     */
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
