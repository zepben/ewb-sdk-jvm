/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversalV2.QueueConditionWithContextValue
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext

internal class PhaseCondition<T>(
    val phases: PhaseCode
) : QueueConditionWithContextValue<NetworkTraceStep<T>, ConnectivityResult> {

    private val terminalConnectivity = TerminalConnectivityConnected()
    private var precalculatedResult: ConnectivityResult? = null

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean {
        val connectivity = currentContext.terminalConnectivity()
        val path = nextItem.path
        val cr = terminalConnectivity.terminalConnectivity(path.fromTerminal, path.toTerminal, connectivity.toNominalPhases.toSet())
        return if (cr.nominalPhasePaths.isNotEmpty()) {
            precalculatedResult = cr
            true
        } else {
            precalculatedResult = null
            false
        }
    }

    override fun computeInitialValue(nextItem: NetworkTraceStep<T>): ConnectivityResult {
        val path = nextItem.path
        return terminalConnectivity.terminalConnectivity(path.toTerminal, path.toTerminal, phases.singlePhases.toSet())

    }

    override fun computeNextValueTyped(nextItem: NetworkTraceStep<T>, currentValue: ConnectivityResult): ConnectivityResult {
        val result = precalculatedResult ?: error("INTERNAL ERROR: value should have been stored as part of queuing")
        // Clear the result as this function should only ever be called once by the traversal for each call to shouldQueue.
        // If that ever changes the assumption made by this class means this class needs to change and by making this null
        // it should be easier to detect a problem rather than returning a stale result and causing weird things to happen.
        precalculatedResult = null
        return result
    }

    override val key: String
        get() = contextKey

    companion object {
        internal const val contextKey = "sdk:phase"
    }
}

// TODO: Is this a suitable function name?
fun StepContext.terminalConnectivity(): ConnectivityResult {
    return this.getValue(PhaseCondition.contextKey) ?: error("Your trace needs to have PhaseCondition added to access this")
}
