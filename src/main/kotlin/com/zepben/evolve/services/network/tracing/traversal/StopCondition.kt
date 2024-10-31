/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

fun interface StopCondition<T> : TraversalCondition<T> {
    fun shouldStop(item: T, context: StepContext): Boolean
}

abstract class StopConditionWithContextValue<T, U> : StopCondition<T>, TypedContextValueComputer<T, U>
