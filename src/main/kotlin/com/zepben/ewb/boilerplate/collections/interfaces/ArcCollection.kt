/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections.interfaces

/**
 * Lightweight interface for a partly mutable collection that does not support
 * bulk mutation (addAll, removeAll, retainAll).
 *
 * Arc stands for Add, Remove, Clear.
 *
 * ```kotlin
 * fun use(items: ArcList<String>) {
 *     // Following works:
 *     items.add("first")
 *     val first = items[0]
 *     items.remove(first)
 *     items.clear()
 *
 *     // Throws an error (indexed assignment is not exposed):
 *     items[0] = "replacement"
 * }
 * ```
 *
 * NOTE: This could implement MutableCollection, but error behaviour on methods such as addAll would be ambiguous.
 * There is no issue with implementing addAll downstream, but it is left up to the user to make things more transparent.
 */
interface ArcCollection<T> : Collection<T> {

    fun add(element: T): Boolean

    fun remove(element: T): Boolean

    fun clear()

}
