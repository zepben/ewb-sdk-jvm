/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversalV2.StepContext

fun interface ComputeNextT<T> {
    fun compute(currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath): T
}

fun interface ComputeNextTWithPaths<T> {
    fun compute(currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath, nextPaths: List<StepPath>): T
}
