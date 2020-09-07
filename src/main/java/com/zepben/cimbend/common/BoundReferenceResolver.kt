/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common

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
