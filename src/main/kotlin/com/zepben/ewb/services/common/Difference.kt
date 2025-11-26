/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

/**
 * The base class of all differences detected when comparing objects or services.
 */
sealed class Difference

/**
 * A difference between two values.
 *
 * @property sourceValue The value in the source system.
 * @property targetValue The value in the target system.
 */
data class ValueDifference(val sourceValue: Any?, val targetValue: Any?) : Difference()

/**
 * The parent class of a difference between two collections.
 *
 * @property missingFromTarget A list of objects found in the source system, but missing from the target system.
 * @property missingFromSource A list of objects found in the target system, but missing from the source system.
 * @property modifications A list of difference in the objects that are common between the source and target systems.
 */
abstract class CollectionDifference : Difference() {
    abstract val missingFromTarget: MutableList<in Any>
    abstract val missingFromSource: MutableList<in Any>
    abstract val modifications: MutableList<Difference>
}

/**
 * A difference between two collections of values.
 *
 * @property missingFromTarget A list of values found in the source system, but missing from the target system.
 * @property missingFromSource A list of values found in the target system, but missing from the source system.
 * @property modifications A list of difference in the values that are common between the source and target systems.
 */
data class ValueCollectionDifference @JvmOverloads constructor(
    override val missingFromTarget: MutableList<in Any> = mutableListOf(),
    override val missingFromSource: MutableList<in Any> = mutableListOf(),
    override val modifications: MutableList<Difference> = mutableListOf()
) : CollectionDifference()

/**
 * A difference between two collections of identifiable objects.
 */
data class ObjectCollectionDifference @JvmOverloads constructor(
    override val missingFromTarget: MutableList<in Any> = mutableListOf(),
    override val missingFromSource: MutableList<in Any> = mutableListOf(),
    override val modifications: MutableList<Difference> = mutableListOf()
) : CollectionDifference()

/**
 * A difference between two objects.
 *
 * @property source The object from the source system.
 * @property target The object from the target system.
 * @property differences A map of differences between the properties of the objects, keyed by the property name.
 */
data class ObjectDifference<T> @JvmOverloads constructor(
    val source: T,
    val target: T,
    val differences: MutableMap<String, Difference> = mutableMapOf()
) : Difference()

/**
 * A difference between what is being referenced.
 *
 * @property source The object being referenced in the source system.
 * @property targetValue The object being referenced in the target system.
 */
data class ReferenceDifference(val source: IdentifiedObject?, val targetValue: IdentifiedObject?) : Difference()

/**
 * A difference in an indexed value.
 *
 * @property index The index being checked.
 * @property difference The difference between the items being checked.
 */
data class IndexedDifference(val index: Int, val difference: Difference) : Difference()
