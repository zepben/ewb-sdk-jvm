/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.network.tracing.phases.PhaseStep
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import java.util.*
import java.util.function.Supplier

/**
 * Convenience class that provides methods for finding conducting equipment with attached usage points.
 * This class is backed by a [BasicTraversal].
 *
 * @property virtualUsagePointCondition Indicates how the search will handle virtual [UsagePoint] instances.
 */
class FindWithUsagePoints(
    private val virtualUsagePointCondition: VirtualUsagePointCondition = VirtualUsagePointCondition.LV_AGGREGATION_ONLY,
    private val lvThreshold: Int = 1000
) {

    fun runNormal(from: ConductingEquipment, to: ConductingEquipment?): Result = runNormal(listOf(from), listOf(to))[0]
    fun runNormal(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>): List<Result> = run(froms, tos) { Tracing.normalDownstreamTrace() }

    fun runCurrent(from: ConductingEquipment, to: ConductingEquipment?): Result = runCurrent(listOf(from), listOf(to))[0]
    fun runCurrent(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>): List<Result> = run(froms, tos) { Tracing.currentDownstreamTrace() }

    private fun run(froms: List<ConductingEquipment>, tos: List<ConductingEquipment?>, traversalSupplier: Supplier<BasicTraversal<PhaseStep>>): List<Result> {
        if (froms.size != tos.size)
            return Collections.nCopies(froms.size.coerceAtLeast(tos.size), Result(status = Result.Status.MISMATCHED_FROM_TO))

        return froms.mapIndexed { index, from ->
            val to = tos[index]

            if (from.mRID != to?.mRID)
                runTrace(from, to, traversalSupplier)
            else if (hasValidUsagePoints(from))
                Result(conductingEquipment = mapOf(from.mRID to from))
            else
                Result(conductingEquipment = emptyMap())
        }
    }

    private fun runTrace(from: ConductingEquipment, to: ConductingEquipment?, traversalSupplier: Supplier<BasicTraversal<PhaseStep>>): Result {
        if (to?.numTerminals() == 0)
            return Result(status = Result.Status.NO_PATH)

        if (from.numTerminals() == 0) {
            return when {
                to != null -> Result(status = Result.Status.NO_PATH)
                from.numUsagePoints() != 0 -> Result(conductingEquipment = Collections.singletonMap(from.mRID, from))
                else -> Result(status = Result.Status.NO_ERROR)
            }
        }

        val extentIds = setOfNotNull(from.mRID, to?.mRID)
        var pathFound = to == null
        val withUsagePoints = mutableMapOf<String, ConductingEquipment>()

        val traversal = traversalSupplier.get()
        traversal.addStopCondition { extentIds.contains(it.conductingEquipment.mRID) }
        if ((virtualUsagePointCondition == VirtualUsagePointCondition.LV_AGGREGATION_ONLY) || (virtualUsagePointCondition == VirtualUsagePointCondition.ALL))
            traversal.addStopCondition { shouldExcludeLv(it) }

        traversal.addStepAction { ps, isStopping ->
            if (isStopping)
                pathFound = pathFound || extentIds.contains(ps.conductingEquipment.mRID)

            if (hasValidUsagePoints(ps.conductingEquipment))
                withUsagePoints[ps.conductingEquipment.mRID] = ps.conductingEquipment
        }

        traversal.reset().run(PhaseStep.startAt(from, from.terminals.first().phases), false)

        if ((to != null) && !pathFound) {
            withUsagePoints.clear()
            traversal.reset().run(PhaseStep.startAt(to, to.terminals.first().phases), false)
        }

        return when {
            pathFound -> Result(conductingEquipment = withUsagePoints)
            else -> Result(status = Result.Status.NO_PATH)
        }
    }

    private fun shouldExcludeLv(phaseStep: PhaseStep) =
        ((phaseStep.conductingEquipment.baseVoltageValue <= lvThreshold) || (phaseStep.conductingEquipment is EquivalentBranch))
            && phaseStep.previous?.usagePoints?.any { it.isVirtual && (it.connectionCategory == "LV_AGGREGATION") } ?: false

    private fun hasValidUsagePoints(conductingEquipment: ConductingEquipment): Boolean =
        conductingEquipment.usagePoints.any {
            !it.isVirtual || when (virtualUsagePointCondition) {
                VirtualUsagePointCondition.LV_AGGREGATION_ONLY -> it.connectionCategory == "LV_AGGREGATION"
                VirtualUsagePointCondition.NO_LV_AGGREGATION -> it.connectionCategory != "LV_AGGREGATION"
                VirtualUsagePointCondition.ALL -> true
                VirtualUsagePointCondition.NONE -> false

            }
        }

    /**
     * Controls how virtual [UsagePoint] instances are handled by the search.
     */
    enum

    class VirtualUsagePointCondition {

        /**
         * Only include virtual [UsagePoint] instances if they are also marked as LV aggregation. If an LV aggregation is found, any attached LV equipment
         * or [EquivalentBranch] instances will not be searched.
         */
        LV_AGGREGATION_ONLY,

        /**
         * Only include virtual [UsagePoint] instances if they are not marked as LV aggregation. A full search of any attached LV equipment or
         *  [EquivalentBranch] instances will be performed.
         */
        NO_LV_AGGREGATION,

        /**
         * Include all virtual [UsagePoint] instances. If an LV aggregation is found, any attached LV equipment or [EquivalentBranch] instances will not be
         * searched.
         */
        ALL,

        /**
         * Exclude all virtual [UsagePoint] instances. A full search of any attached LV equipment or [EquivalentBranch] instances will be performed.
         */
        NONE
    }

    class Result(
        val status: Status = Status.NO_ERROR,
        conductingEquipment: Map<String, ConductingEquipment> = emptyMap()
    ) {
        val conductingEquipment: Map<String, ConductingEquipment> = conductingEquipment.asUnmodifiable()

        enum class Status {
            NO_ERROR, NO_PATH, MISMATCHED_FROM_TO
        }

    }

}
