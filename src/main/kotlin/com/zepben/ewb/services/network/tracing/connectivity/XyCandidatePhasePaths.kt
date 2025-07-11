/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.connectivity

import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind as SPK

/**
 * Used to track the candidate and know paths for XY phase connectivity.
 */
class XyCandidatePhasePaths {

    private val knownTracking = mutableMapOf<SPK, SPK>()
    private val candidateTracking = mutableMapOf<SPK, MutableList<SPK>>()

    /**
     * Add a [knownPhase] for the specified [xyPhase]. If there is already a [knownPhase] the new value will be ignored.
     *
     * @param xyPhase The phase that is being tracked.
     * @param knownPhase The phase that should be allocated to the tracked phase.
     */
    fun addKnown(xyPhase: SPK, knownPhase: SPK) {
        knownTracking.getOrPut(xyPhase.validateForTracking()) { knownPhase }
    }

    /**
     * Add [candidatePhases] for the specified [xyPhase]. If the same candidate has been found from more than
     * one path it should be added multiple times
     *
     * @param xyPhase The phase that is being tracked.
     * @param candidatePhases The phases that could be allocated to the tracked phase.
     */
    fun addCandidates(xyPhase: SPK, candidatePhases: Collection<SPK>) {
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
    fun calculatePaths(): Map<SPK, SPK> {
        val paths = mutableMapOf<SPK, SPK>()

        val knownX = knownTracking[SPK.X]?.also { paths[SPK.X] = it }
        val knownY = knownTracking[SPK.Y]?.takeUnless { it == knownX }?.also { paths[SPK.Y] = it }

        if ((knownX != null) && (knownY != null))
            return paths

        val candidatePhaseCounts = candidateTracking.mapValues { (_, values) -> values.groupingBy { it }.eachCount() }
        if (knownX != null) {
            paths[SPK.Y] = candidatePhaseCounts[SPK.Y]?.let {
                findCandidate(it, priority = yPriority, after = knownX)
            } ?: SPK.NONE
        } else if (knownY != null)
            paths[SPK.X] = candidatePhaseCounts[SPK.X]?.let {
                findCandidate(it, priority = xPriority, before = knownY)
            } ?: SPK.NONE
        else {
            val (xCandidate, yCandidate) = processCandidates(candidatePhaseCounts)
            paths[SPK.X] = xCandidate
            paths[SPK.Y] = yCandidate
        }

        return paths
    }

    private fun processCandidates(candidatePhaseCounts: Map<SPK, Map<SPK, Int>>): Pair<SPK, SPK> {
        val candidateXCounts = candidatePhaseCounts[SPK.X] ?: emptyMap()
        val candidateYCounts = candidatePhaseCounts[SPK.Y] ?: emptyMap()

        return when {
            candidateXCounts.isEmpty() ->
                SPK.NONE to findCandidate(candidateYCounts, priority = yPriority)

            candidateXCounts.size == 1 ->
                candidateXCounts.keys.first() to findCandidate(candidateYCounts, priority = yPriority, after = candidateXCounts.keys.first())

            candidateYCounts.isEmpty() ->
                findCandidate(candidateXCounts, priority = xPriority) to SPK.NONE

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

                    if (xCandidate2 == SPK.NONE)
                        xCandidate to yCandidate2
                    else if (yCandidate2 == SPK.NONE)
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
        candidateCounts: Map<SPK, Int>,
        priority: List<SPK>,
        before: SPK? = null,
        after: SPK? = null
    ): SPK {
        candidateCounts.filterKeys { it.isBefore(before) && it.isAfter(after) }.apply {
            if (isEmpty())
                return SPK.NONE
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

    private fun SPK.validateForTracking(): SPK =
        when (this) {
            SPK.X, SPK.Y -> this
            else -> throw IllegalArgumentException("Unable to track phase $this, expected X or Y.")
        }

    private fun SPK.isValidCandidate(xyPhase: SPK): Boolean =
        if (xyPhase == SPK.X)
            isValidCandidateX()
        else
            isValidCandidateY()

    private fun SPK.isValidCandidateX(): Boolean =
        when (this) {
            SPK.A, SPK.B, SPK.C -> true
            else -> throw IllegalArgumentException("Unable to use phase $this as a candidate, expected A, B or C.")
        }

    private fun SPK.isValidCandidateY(): Boolean =
        when (this) {
            SPK.B, SPK.C -> true
            else -> throw IllegalArgumentException("Unable to use phase $this as a candidate, expected B or C.")
        }

    companion object {

        /**
         * The pathing priority for nominal phase X
         */
        val xPriority: List<SinglePhaseKind> = listOf(SPK.A, SPK.B, SPK.C)

        /**
         * The pathing priority for nominal phase X
         */
        val yPriority: List<SinglePhaseKind> = listOf(SPK.C, SPK.B)

        fun SPK.isBefore(before: SPK?): Boolean = when (before) {
            null, SPK.NONE -> true
            SPK.A -> false
            SPK.B -> this == SPK.A
            SPK.C -> (this == SPK.A) || (this == SPK.B)
            else -> throw IllegalStateException("INTERNAL ERROR: isBefore should only ever be checking against valid Y phases. If you get this message you need to ask the dev team to go put the planets back into alignment as they stuffed something up!")
        }

        fun SPK.isAfter(after: SPK?): Boolean = when (after) {
            null, SPK.NONE -> true
            SPK.C -> false
            SPK.B -> this == SPK.C
            SPK.A -> (this == SPK.C) || (this == SPK.B)
            else -> throw IllegalStateException("INTERNAL ERROR: isAfter should only ever be checking against valid X phases. If you get this message you need to ask the dev team to go put the planets back into alignment as they stuffed something up!")
        }

    }

}
