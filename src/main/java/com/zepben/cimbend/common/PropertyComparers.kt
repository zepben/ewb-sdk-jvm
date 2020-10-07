/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import kotlin.reflect.KProperty1

fun <T, R> KProperty1<in T, R?>.compareValues(source: T?, target: T?): ValueDifference? {
    val sVal = source?.let { this.get(source) }
    val tVal = target?.let { this.get(target) }
    return if (sVal == tVal) {
        null
    } else {
        ValueDifference(sVal, tVal)
    }
}

fun <T> KProperty1<in T, Double>.compareDoubles(source: T?, target: T): ValueDifference? {
    val sVal = source?.let { this.get(source) } ?: 0.0
    val tVal = target?.let { this.get(target) } ?: 0.0
    return if ((sVal == tVal) || sVal.isNaN() && tVal.isNaN()) {
        null
    } else {
        ValueDifference(sVal, tVal)
    }
}

fun <T, R : IdentifiedObject> KProperty1<in T, R?>.compareIdReference(source: T?, target: T?): ReferenceDifference? {
    val sRef: R? = source?.let { this.get(source) }
    val tRef: R? = target?.let { this.get(target) }

    return when {
        sRef == null && tRef == null -> null
        (sRef != null && tRef != null) && (sRef.mRID == tRef.mRID) -> null
        else -> ReferenceDifference(sRef, tRef)
    }
}

fun <T, R : IdentifiedObject> KProperty1<in T, Collection<R>>.compareIdReferenceCollection(source: T, target: T): CollectionDifference? {
    val differences = CollectionDifference()
    val sourceCollection = this.get(source)
    val targetCollection = this.get(target)

    val sourceMRIDs = mutableSetOf<String>()
    sourceCollection.forEach { sourceIdObj ->
        sourceMRIDs.add(sourceIdObj.mRID)
        val targetIdObj = targetCollection.find { it.mRID == sourceIdObj.mRID }
        if (targetIdObj == null) {
            differences.missingFromTarget.add(sourceIdObj)
        }
    }

    targetCollection.forEach {
        if (!sourceMRIDs.contains(it.mRID))
            differences.missingFromSource.add(it)
    }

    return differences.nullIfEmpty()
}

fun <T, R : IdentifiedObject> KProperty1<in T, List<R>>.compareIndexedIdReferenceCollection(
    source: T,
    target: T
): CollectionDifference? {
    val differences = CollectionDifference()
    val sourceList = this.get(source)
    val targetList = this.get(target)

    sourceList.forEachIndexed { index, sourceIdObj ->
        val targetIdObj = targetList.getOrNull(index)
        when {
            targetIdObj == null -> differences.missingFromTarget.add(IndexedDifference(index, ReferenceDifference(sourceIdObj, null)))
            targetIdObj.mRID != sourceIdObj.mRID -> differences.modifications.add(IndexedDifference(index, ReferenceDifference(sourceIdObj, targetIdObj)))
        }
    }

    for (index in sourceList.size until targetList.size) {
        differences.missingFromSource.add(IndexedDifference(index, ReferenceDifference(null, targetList[index])))
    }

    return differences.nullIfEmpty()
}

fun <T, R> KProperty1<in T, List<R>>.compareIndexedValueCollection(
    source: T,
    target: T
): CollectionDifference? {
    val differences = CollectionDifference()
    val sourceList = this.get(source)
    val targetList = this.get(target)

    sourceList.forEachIndexed { index, sourceValue ->
        val targetValue = targetList.getOrNull(index)
        when {
            targetValue == null -> differences.missingFromTarget.add(IndexedDifference(index, ValueDifference(sourceValue, null)))
            targetValue != sourceValue -> differences.modifications.add(IndexedDifference(index, ValueDifference(sourceValue, targetValue)))
        }
    }

    for (index in sourceList.size until targetList.size) {
        differences.missingFromSource.add(IndexedDifference(index, ValueDifference(null, targetList[index])))
    }

    return differences.nullIfEmpty()
}

private fun CollectionDifference.nullIfEmpty() =
    if (missingFromSource.isEmpty() && missingFromTarget.isEmpty() && modifications.isEmpty()) {
        null
    } else {
        this
    }
