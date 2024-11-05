/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

/**
 * A sealed interface representing a condition used in a traversal.
 * Implementations of this interface can influence the traversal process by determining
 * things such as the ability to queue items, stop at specific items, or apply other conditional logic during traversal.
 *
 * @param T The type of items being traversed.
 */
sealed interface TraversalCondition<T>
