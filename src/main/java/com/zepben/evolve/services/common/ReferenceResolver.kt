/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

/**
 * This provides a way to resolve a reference relationship between properties on an instance of [T] to an instance of [R]
 * that may not be able to be resolved immediately.
 *
 * Used by the [BaseService] when attempting to resolve relationships between two
 * [com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject]s. Note that two instances of a reference resolver that
 * resolve the same reference relationship should always be equal so that unresolved references for an object can be
 * queried based on their resolver.
 */
interface ReferenceResolver<T, R> {
    val fromClass: Class<T>
    val toClass: Class<R>

    fun resolve(from: T, to: R)
}

