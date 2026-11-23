/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.ewb.services.network.tracing.networktrace.actions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTrace
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.networktrace.operators.OpenStateOperators
import com.zepben.ewb.services.network.tracing.traversal.StepAction
import com.zepben.ewb.services.network.tracing.traversal.StepContext

/**
 * A [StepAction] that can be added to a [NetworkTrace] to collect the tie points of the area covered by the trace.
 *
 * A tie point is any open [Switch] in the trace results, where at least one connected piece of [ConductingEquipment] is outside the trace results.
 *
 * @param openStateOperators The operators used to determine if a [Switch] is open. This should match the network state the trace is run
 *   against, e.g. [OpenStateOperators.CURRENT] when tracing with `NetworkStateOperators.CURRENT`. Defaults to [OpenStateOperators.NORMAL].
 */
class TiePointCollector @JvmOverloads constructor(
    private val openStateOperators: OpenStateOperators = OpenStateOperators.NORMAL,
) : StepAction<NetworkTraceStep<*>> {

    private val _visited: MutableSet<ConductingEquipment> = mutableSetOf()

    /**
     * All [ConductingEquipment] stepped on by the trace(s) this collector has been added to.
     */
    val visited: Set<ConductingEquipment> get() = _visited

    override fun apply(item: NetworkTraceStep<*>, context: StepContext) {
        _visited.add(item.path.toEquipment)
    }

    /**
     * Calculate the tie points of the traced area. This should be called after the trace has completed.
     *
     * @return Every open [Switch] in the trace results with at least one connected piece of [ConductingEquipment] outside the trace results.
     */
    fun calculateTiePoints(): Set<Switch> = calculateTiePoints(_visited, openStateOperators)

    /**
     * Clear all collected equipment so this collector can be reused for another trace.
     */
    fun clear() {
        _visited.clear()
    }

    companion object {

        /**
         * Calculate the tie points of an existing set of trace results. Use this when the results are not simply the equipment stepped on by a
         * single [NetworkTrace], e.g. they have been post-processed or combined from several traces. Otherwise, prefer adding a [TiePointCollector]
         * to the trace.
         *
         * @param traceResults The trace results.
         * @param openStateOperators The operators used to determine if a [Switch] is open.
         * @return Every open [Switch] in [traceResults] with at least one connected piece of [ConductingEquipment] outside [traceResults].
         */
        @JvmStatic
        @JvmOverloads
        fun calculateTiePoints(
            traceResults: Set<ConductingEquipment>,
            openStateOperators: OpenStateOperators = OpenStateOperators.NORMAL,
        ): Set<Switch> =
            traceResults.asSequence()
                .filterIsInstance<Switch>()
                .filter { openStateOperators.isOpen(it) }
                .filter { switch -> switch.connectedEquipment().any { it !in traceResults } }
                .toSet()

        private fun Switch.connectedEquipment(): Sequence<ConductingEquipment> =
            terminals.asSequence()
                .flatMap { it.connectedTerminals() }
                .mapNotNull { it.conductingEquipment }

    }

}
