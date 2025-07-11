/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

sealed class Difference

data class ValueDifference(val sourceValue: Any?, val targetValue: Any?) : Difference()

data class CollectionDifference @JvmOverloads constructor(
    val missingFromTarget: MutableList<in Any> = mutableListOf(),
    val missingFromSource: MutableList<in Any> = mutableListOf(),
    val modifications: MutableList<Difference> = mutableListOf()
) : Difference()

data class ObjectDifference<T> @JvmOverloads constructor(
    val source: T,
    val target: T,
    val differences: MutableMap<String, Difference> = mutableMapOf()
) : Difference()

data class ReferenceDifference(val source: IdentifiedObject?, val targetValue: IdentifiedObject?) : Difference()

data class IndexedDifference(val index: Int, val difference: Difference) : Difference()
