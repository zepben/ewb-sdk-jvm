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
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.run
import java.util.*

/**
 * Convenience class that provides methods for finding downstream conducting equipment with attached usage points.
 * This class is backed by a [NetworkTrace].
 *
 * @property virtualUsagePointCondition Indicates how the search will handle virtual [UsagePoint] instances.
 */
// TODO [Review]: Move this out of the SDK and into the network routes as it is quite use case specific?
class FindWithUsagePoints(
    val stateOperators: NetworkStateOperators,
    private val virtualUsagePointCondition: VirtualUsagePointCondition = VirtualUsagePointCondition.LV_AGGREGATION_ONLY,
    private val lvThreshold: Int = 1000
) {

    fun run(from: ConductingEquipment, to: ConductingEquipment?): Result = run(listOf(from to to)).first()

    fun run(extents: List<Pair<ConductingEquipment, ConductingEquipment?>>): List<Result> {
        return extents.map { (from, to) ->
            if (from.mRID != to?.mRID)
                runTrace(from, to)
            else if (hasValidUsagePoints(from))
                Result(conductingEquipment = mapOf(from.mRID to from))
            else
                Result(conductingEquipment = emptyMap())
        }
    }

    private fun runTrace(from: ConductingEquipment, to: ConductingEquipment?): Result {
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

        val traversal = Tracing.networkTrace(stateOperators).addNetworkCondition { downstream() }
        traversal.addStopCondition { (path), _ -> extentIds.contains(path.toEquipment.mRID) }
        if ((virtualUsagePointCondition == VirtualUsagePointCondition.LV_AGGREGATION_ONLY) || (virtualUsagePointCondition == VirtualUsagePointCondition.ALL)) {
            traversal.addStopCondition { (path), _ -> shouldExcludeLv(path) }
        }

        traversal.addStepAction { (path), ctx ->
            if (ctx.isStopping)
                pathFound = pathFound || extentIds.contains(path.toEquipment.mRID)

            if (hasValidUsagePoints(path.toEquipment))
                withUsagePoints[path.toEquipment.mRID] = path.toEquipment
        }

        from.terminals.forEach {
            traversal.reset().run(it, it.phases, canStopOnStartItem = false)
        }

        if ((to != null) && !pathFound) {
            withUsagePoints.clear()
            to.terminals.forEach {
                traversal.reset().run(it, it.phases, canStopOnStartItem = false)
            }
        }

        return when {
            pathFound -> Result(conductingEquipment = withUsagePoints)
            else -> Result(status = Result.Status.NO_PATH)
        }
    }

    private fun shouldExcludeLv(stepPath: StepPath) =
        ((stepPath.toEquipment.baseVoltageValue <= lvThreshold) || (stepPath.toEquipment is EquivalentBranch))
            && stepPath.fromEquipment.usagePoints.any { it.connectionCategory == "LV_AGGREGATION" }

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
    enum class VirtualUsagePointCondition {

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
        val conductingEquipment: Map<String, ConductingEquipment> = emptyMap()
    ) {
        enum class Status {
            NO_ERROR, NO_PATH
        }
    }

}
