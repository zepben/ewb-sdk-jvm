/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections

/**
 * Lightweight interface for an indexable mutable collection that does not support
 * indexed assignment
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
 */
interface ArcList<T> : MutableCollection<T>, List<T>
