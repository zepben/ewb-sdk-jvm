/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversalV2

fun interface QueueCondition<T> : TraversalCondition<T> {
    // TODO [Review]: Should this have the current step too?
    //  Use case would be when queuing item you may have stored state in the current T that impacts if you should queue the next item or not.
    //  Currently working around this by using the context map
    fun shouldQueue(nextItem: T, currentContext: StepContext): Boolean
}

interface QueueConditionWithContextValue<T, U> : QueueCondition<T>, TypedContextValueComputer<T, U>
