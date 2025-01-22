/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.annotations.ZepbenExperimental
import com.zepben.evolve.services.network.tracing.traversal.StepContext

/**
 * Functional interface used to compute contextual data stored on a [NetworkTraceStep].
 */
fun interface ComputeData<T> {
    /**
     * Called for each new [NetworkTraceStep] in a [NetworkTrace]. The value returned from this function will be stored against the next step within [NetworkTraceStep.data].
     *
     * @param currentStep The current step of the trace.
     * @param currentContext The context of teh current step in the trace.
     * @param nextPath The next path of the next [NetworkTraceStep] that the data will be associated with.
     *
     * @return The data to associate with the next [NetworkTraceStep].
     */
    fun computeNext(currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: NetworkTraceStep.Path): T
}

/**
 * Functional interface used to compute contextual data stored on a [NetworkTraceStep]. This can be used when the contextual data can only be computed
 * by knowing all the next paths that can be stepped to from a given step.
 */
@ZepbenExperimental
fun interface ComputeDataWithPaths<T> {
    /**
     * Called for each new [NetworkTraceStep] in a [NetworkTrace]. The value returned from this function will be stored against the next step within [NetworkTraceStep.data].
     *
     * @param currentStep The current step of the trace.
     * @param currentContext The context of teh current step in the trace.
     * @param nextPath The next path of the next [NetworkTraceStep] that the data will be associated with.
     * @param nextPaths A list of all the next paths that the current step can trace to.
     *
     * @return The data to associate with the next [NetworkTraceStep].
     */
    fun computeNext(currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: NetworkTraceStep.Path, nextPaths: List<NetworkTraceStep.Path>): T
}
