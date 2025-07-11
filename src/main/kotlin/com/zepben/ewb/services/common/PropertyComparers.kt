/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Name
import kotlin.reflect.KProperty1

fun <T, R> KProperty1<in T, R?>.compareValues(source: T?, target: T?): ValueDifference? {
    val sVal = source?.let { this.get(source) }
    val tVal = target?.let { this.get(target) }
    return if (sVal is Double) {
        if ((tVal is Double) && ((sVal == tVal) || (sVal.isNaN() && tVal.isNaN()))) {
            null
        } else {
            ValueDifference(sVal, tVal)
        }
    } else if (sVal is Float) {
        if ((tVal is Float) && ((sVal == tVal) || (sVal.isNaN() && tVal.isNaN()))) {
            null
        } else {
            ValueDifference(sVal, tVal)
        }
    } else if (sVal == tVal) {
        null
    } else {
        ValueDifference(sVal, tVal)
    }
}

fun <T, R, C> KProperty1<in T, R?>.compareValues(source: T?, target: T?, toComparable: (R) -> C): ValueDifference? {
    val sVal = source?.let { this.get(source) }
    val tVal = target?.let { this.get(target) }

    val sValComp = sVal?.let(toComparable)
    val tValComp = tVal?.let(toComparable)
    return if (sValComp == tValComp) {
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

fun <T : IdentifiedObject> KProperty1<in T, Collection<Name>>.compareNames(source: T, target: T): CollectionDifference? {
    data class NameTypeName(val nameType: String, val name: String)

    val differences = CollectionDifference()
    val sourceCollection = this.get(source)
    val targetCollection = this.get(target)

    val sourceNameTypeNames = mutableListOf<NameTypeName>()
    sourceCollection.forEach { sourceIdObj ->
        sourceNameTypeNames.add(NameTypeName(sourceIdObj.type.name, sourceIdObj.name))
        val targetIdObj = targetCollection.find { it.name == sourceIdObj.name && it.type.name == sourceIdObj.type.name }
        if (targetIdObj == null) {
            differences.missingFromTarget.add(sourceIdObj)
        }
    }

    targetCollection.forEach {
        if (sourceNameTypeNames.find { (nameType, otherName) -> it.name == otherName && it.type.name == nameType } == null)
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

fun <T, R, K : Comparable<K>> KProperty1<in T, List<R>>.compareUnorderedValueCollection(
    source: T,
    target: T,
    keySelector: (R) -> K
): CollectionDifference? {
    val differences = CollectionDifference()
    val sourceList = this.get(source).sortedBy { keySelector(it) }
    val targetList = this.get(target).sortedBy { keySelector(it) }

    var sourceIndex = 0
    var targetIndex = 0

    while ((sourceIndex < sourceList.size) && (targetIndex < targetList.size)) {
        val sourceItem = sourceList[sourceIndex]
        val targetItem = targetList[targetIndex]

        val sourceCheck = keySelector(sourceItem)
        val targetCheck = keySelector(targetItem)

        if (sourceCheck == targetCheck) {
            if (sourceItem != targetItem)
                differences.modifications.add(ValueDifference(sourceItem, targetItem))
            ++sourceIndex
            ++targetIndex
        } else if (sourceCheck < targetCheck) {
            differences.missingFromTarget.add(ValueDifference(sourceItem, null))
            ++sourceIndex
        } else { // if (targetCheck < sourceCheck)
            differences.missingFromSource.add(ValueDifference(null, targetItem))
            ++targetIndex
        }
    }

    while (targetIndex < targetList.size)
        differences.missingFromSource.add(ValueDifference(null, targetList[targetIndex++]))

    while (sourceIndex < sourceList.size)
        differences.missingFromTarget.add(ValueDifference(sourceList[sourceIndex++], null))

    return differences.nullIfEmpty()
}

internal fun CollectionDifference.nullIfEmpty() =
    if (missingFromSource.isEmpty() && missingFromTarget.isEmpty() && modifications.isEmpty()) {
        null
    } else {
        this
    }
