/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*

/**
 * Used to track the candidate and know paths for XY phase connectivity.
 */
class XyCandidatePhasePaths {

    private val knownTracking = mutableMapOf<SinglePhaseKind, SinglePhaseKind>()
    private val candidateTracking = mutableMapOf<SinglePhaseKind, MutableList<SinglePhaseKind>>()

    private val xPriority = listOf(A, B, C)
    private val yPriority = listOf(C, B)


    /**
     * Add a [knownPhase] for the specified [xyPhase]. If there is already a [knownPhase] the new value will be ignored.
     *
     * @param xyPhase The phase that is being tracked.
     * @param knownPhase The phase that should be allocated to the tracked phase.
     */
    fun addKnown(xyPhase: SinglePhaseKind, knownPhase: SinglePhaseKind) {
        knownTracking.getOrPut(xyPhase.validateForTracking()) { knownPhase }
    }

    /**
     * Add [candidatePhases] for the specified [xyPhase]. If the same candidate has been found from more than
     * one path it should be added multiple times
     *
     * @param xyPhase The phase that is being tracked.
     * @param candidatePhases The phases that could be allocated to the tracked phase.
     */
    fun addCandidates(xyPhase: SinglePhaseKind, candidatePhases: Iterable<SinglePhaseKind>) {
        candidateTracking.getOrPut(xyPhase.validateForTracking()) { mutableListOf() }.addAll(candidatePhases.filter { it.isValidCandidate(xyPhase) })
    }

    /**
     * Calculate the paths for the tracked phases taking into account the following:
     * 1. Known phases take preference.
     * 2. X is always a "lower" phase than Y.
     * 3. If multiple candidates are valid then the one with the most occurrences will be chosen.
     * 4. If multiple candidates are valid and are equally common, the phases will be chosen with the following priority maintaining the above rules:
     *    X: A, B then C
     *    Y: C then B
     */
    fun calculatePaths(): Map<SinglePhaseKind, SinglePhaseKind> {
        val paths = mutableMapOf<SinglePhaseKind, SinglePhaseKind>()

        val knownX = knownTracking[X]?.also { paths[X] = it }
        val knownY = knownTracking[Y]?.takeUnless { it == knownX }?.also { paths[Y] = it }

        if ((knownX != null) && (knownY != null))
            return paths

        val candidatePhaseCounts = candidateTracking.mapValues { (_, values) -> values.groupingBy { it }.eachCount() }
        if (knownX != null) {
            paths[Y] = candidatePhaseCounts[Y]?.let {
                findCandidate(it, priority = yPriority, after = knownX)
            } ?: NONE
        } else if (knownY != null)
            paths[X] = candidatePhaseCounts[X]?.let {
                findCandidate(it, priority = xPriority, before = knownY)
            } ?: NONE
        else {
            val (xCandidate, yCandidate) = processCandidates(candidatePhaseCounts)
            paths[X] = xCandidate
            paths[Y] = yCandidate
        }

        return paths
    }

    private fun processCandidates(candidatePhaseCounts: Map<SinglePhaseKind, Map<SinglePhaseKind, Int>>): Pair<SinglePhaseKind, SinglePhaseKind> {
        val candidateXCounts = candidatePhaseCounts[X] ?: emptyMap()
        val candidateYCounts = candidatePhaseCounts[Y] ?: emptyMap()

        return when {
            candidateXCounts.isEmpty() ->
                NONE to findCandidate(candidateYCounts, priority = yPriority)
            candidateXCounts.size == 1 ->
                candidateXCounts.keys.first() to findCandidate(candidateYCounts, priority = yPriority, after = candidateXCounts.keys.first())
            candidateYCounts.isEmpty() ->
                findCandidate(candidateXCounts, priority = xPriority) to NONE
            candidateYCounts.size == 1 ->
                findCandidate(candidateXCounts, priority = xPriority, before = candidateYCounts.keys.first()) to candidateYCounts.keys.first()
            else -> {
                val xCandidate = findCandidate(candidateXCounts, priority = xPriority)
                val yCandidate = findCandidate(candidateYCounts, priority = yPriority)

                if (xCandidate.isBefore(yCandidate))
                    xCandidate to yCandidate
                else if (candidateXCounts[xCandidate]!! > candidateYCounts[yCandidate]!!)
                    xCandidate to findCandidate(candidateYCounts, priority = yPriority, after = xCandidate)
                else if (candidateYCounts[yCandidate]!! > candidateXCounts[xCandidate]!!)
                    findCandidate(candidateXCounts, priority = xPriority, before = yCandidate) to yCandidate
                else {
                    val xCandidate2 = findCandidate(candidateXCounts, priority = xPriority, before = yCandidate)
                    val yCandidate2 = findCandidate(candidateYCounts, priority = yPriority, after = xCandidate)

                    if (xCandidate2 == NONE)
                        xCandidate to yCandidate2
                    else if (yCandidate2 == NONE)
                        xCandidate2 to yCandidate
                    else if (candidateXCounts[xCandidate2]!! > candidateYCounts[yCandidate2]!!)
                        xCandidate2 to yCandidate
                    else if (candidateYCounts[yCandidate2]!! > candidateXCounts[xCandidate2]!!)
                        xCandidate to yCandidate2
                    else
                        xCandidate to yCandidate2
                }
            }
        }
    }

    private fun findCandidate(
        candidateCounts: Map<SinglePhaseKind, Int>,
        priority: List<SinglePhaseKind>,
        before: SinglePhaseKind? = null,
        after: SinglePhaseKind? = null
    ): SinglePhaseKind {
        candidateCounts.filterKeys { it.isBefore(before) && it.isAfter(after) }.apply {
            if (isEmpty())
                return NONE
            else if (size == 1)
                return keys.first()
            else {
                val maxCount = values.maxOrNull()
                val candidates = filterValues { it == maxCount }
                if (candidates.size == 1)
                    return candidates.keys.first()
                else {
                    priority.forEach {
                        if (candidates.containsKey(it))
                            return it
                    }
                }
                throw IllegalStateException("INTERNAL ERROR: If you get here it means you did not limit the candidates to only valid phases, go fix that!")
            }
        }
    }

    private fun SinglePhaseKind.validateForTracking(): SinglePhaseKind =
        when (this) {
            X, Y -> this
            else -> throw IllegalArgumentException("Unable to track phase $this, expected X or Y.")
        }

    private fun SinglePhaseKind.isValidCandidate(xyPhase: SinglePhaseKind): Boolean =
        if (xyPhase == X)
            isValidCandidateX()
        else
            isValidCandidateY()

    private fun SinglePhaseKind.isValidCandidateX(): Boolean =
        when (this) {
            A, B, C -> true
            else -> throw IllegalArgumentException("Unable to use phase $this as a candidate, expected A, B or C.")
        }

    private fun SinglePhaseKind.isValidCandidateY(): Boolean =
        when (this) {
            B, C -> true
            else -> throw IllegalArgumentException("Unable to use phase $this as a candidate, expected B or C.")
        }

    private fun SinglePhaseKind.isBefore(before: SinglePhaseKind?): Boolean = when (before) {
        null, NONE -> true
        A -> false
        B -> this == A
        C -> (this == A) || (this == B)
        else -> throw IllegalStateException("INTERNAL ERROR: isBefore should only ever be checking against valid Y phases. If you get this message you need to ask the dev team to go put the planets back into alignment as they stuffed something up!")
    }

    private fun SinglePhaseKind.isAfter(after: SinglePhaseKind?): Boolean = when (after) {
        null, NONE -> true
        C -> false
        B -> this == C
        A -> (this == C) || (this == B)
        else -> throw IllegalStateException("INTERNAL ERROR: isAfter should only ever be checking against valid X phases. If you get this message you need to ask the dev team to go put the planets back into alignment as they stuffed something up!")
    }

}
