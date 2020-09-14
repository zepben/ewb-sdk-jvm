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
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.traversals.BasicTraversal
import java.util.*
import java.util.function.Supplier

/**
 * Convenience class that provides methods for finding conducting equipment with attached usage points.
 * This class is backed by a [BasicTraversal].
 */
class FindWithUsagePoints {
    fun runNormal(from: ConductingEquipment, to: ConductingEquipment?): Result {
        return runNormal(listOf(from), listOf(to))[0]
    }

    fun runNormal(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>): List<Result> {
        return run(froms, tos) { PhaseTrace.newNormalDownstreamTrace() }
    }

    fun runCurrent(from: ConductingEquipment, to: ConductingEquipment?): Result {
        return runCurrent(listOf(from), listOf(to))[0]
    }

    fun runCurrent(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>): List<Result> {
        return run(froms, tos) { PhaseTrace.newCurrentDownstreamTrace() }
    }

    private fun run(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>, traversalSupplier: Supplier<BasicTraversal<PhaseStep>>): List<Result> {
        if (froms.size != tos.size)
            return Collections.nCopies(froms.size.coerceAtLeast(tos.size), result().withStatus(Result.Status.MISMATCHED_FROM_TO))

        val results: MutableList<Result> = ArrayList()
        for (index in froms.indices) {
            val from = froms[index]
            val to = tos[index]

            if (to != null && from.mRID == to.mRID) {
                val withUsagePoints: MutableMap<String, ConductingEquipment> = mutableMapOf()

                if (from.numUsagePoints() != 0)
                    withUsagePoints[from.mRID] = from

                results.add(result().withConductingEquipment(withUsagePoints))
            } else
                results.add(runTrace(from, to, traversalSupplier))
        }

        return results
    }

    private fun runTrace(from: ConductingEquipment, to: ConductingEquipment?, traversalSupplier: Supplier<BasicTraversal<PhaseStep>>): Result {
        if (from.numTerminals() == 0) {
            return when {
                to != null -> result().withStatus(Result.Status.NO_PATH)
                from.numUsagePoints() != 0 -> result().withConductingEquipment(Collections.singletonMap(from.mRID, from))
                else -> result().withStatus(Result.Status.NO_ERROR)
            }
        }

        val extentIds = listOfNotNull(from.mRID, to?.mRID).toSet()
        var pathFound = to == null
        val withUsagePoints: MutableMap<String, ConductingEquipment> = mutableMapOf()

        val traversal = traversalSupplier.get()
        traversal.addStopCondition { ce2c: PhaseStep -> extentIds.contains(ce2c.conductingEquipment().mRID) }
        traversal.addStepAction { ce2c: PhaseStep, isStopping: Boolean ->
            if (isStopping)
                pathFound = true

            if (ce2c.conductingEquipment().numUsagePoints() != 0)
                withUsagePoints[ce2c.conductingEquipment().mRID] = ce2c.conductingEquipment()
        }

        traversal.reset().run(PhaseStep.startAt(from, from.terminals.first().phases), false)

        if ((to != null) && !pathFound) {
            if (to.numTerminals() == 0)
                return result().withStatus(Result.Status.NO_PATH)

            withUsagePoints.clear()
            traversal.reset().run(PhaseStep.startAt(to, to.terminals.first().phases), false)
        }

        return when {
            pathFound -> result().withConductingEquipment(withUsagePoints)
            else -> result().withStatus(Result.Status.NO_PATH)
        }
    }

    private fun result(): Result {
        return Result()
    }

    class Result {

        enum class Status {
            NO_ERROR, NO_PATH, MISMATCHED_FROM_TO
        }

        private var status = Status.NO_ERROR
        private var conductingEquipment: Map<String, ConductingEquipment> = emptyMap()

        fun status(): Status {
            return status
        }

        fun conductingEquipment(): Map<String, ConductingEquipment> {
            return Collections.unmodifiableMap(conductingEquipment)
        }

        fun withStatus(status: Status): Result {
            this.status = status
            return this
        }

        fun withConductingEquipment(conductingEquipment: Map<String, ConductingEquipment>): Result {
            this.conductingEquipment = conductingEquipment
            return this
        }

    }

}
