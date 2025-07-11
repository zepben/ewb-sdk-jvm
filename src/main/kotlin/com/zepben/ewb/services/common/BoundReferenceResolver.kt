/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

/**
 * Holds a pair of reciprocal [ReferenceResolver]s where [resolver] can resolve the relationship from [T] to [R],
 * [reverseResolver] can resolve the relationship from [R] to [T] and [from] is the object that is initiating the
 * reference resolution.
 *
 * If there is no bidirectional relationship between [T] and [R] the [reverseResolver] should be null.
 */
data class BoundReferenceResolver<T, R>(
    val from: T,
    val resolver: ReferenceResolver<T, R>,
    val reverseResolver: ReferenceResolver<R, T>?
)
