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
 * This provides a way to resolve a reference relationship between properties on an instance of [T] to an instance of [R]
 * that may not be able to be resolved immediately.
 *
 * Used by the [BaseService] when attempting to resolve relationships between two
 * [com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject]s. Note that two instances of a reference resolver that
 * resolve the same reference relationship should always be equal so that unresolved references for an object can be
 * queried based on their resolver.
 */
interface ReferenceResolver<T, R> {
    val fromClass: Class<T>
    val toClass: Class<R>

    fun resolve(from: T, to: R)
}

