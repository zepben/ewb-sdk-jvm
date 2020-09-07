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

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject

sealed class Difference

data class ValueDifference(val sourceValue: Any?, val targetValue: Any?) : Difference()

data class CollectionDifference @JvmOverloads constructor(
    val missingFromTarget: MutableList<in Any> = mutableListOf(),
    val missingFromSource: MutableList<in Any> = mutableListOf(),
    val modifications: MutableList<Difference> = mutableListOf()
) : Difference()

data class ObjectDifference<T : IdentifiedObject> @JvmOverloads constructor(
    val source: T,
    val target: T,
    val differences: MutableMap<String, Difference> = mutableMapOf()
) : Difference()

data class ReferenceDifference(val source: IdentifiedObject?, val targetValue: IdentifiedObject?) : Difference()

data class IndexedDifference(val index: Int, val difference: Difference) : Difference()
