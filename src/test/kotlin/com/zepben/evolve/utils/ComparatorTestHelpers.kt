/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.utils

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.services.common.*
import com.zepben.evolve.services.network.NetworkServiceComparatorOptions
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

class ServiceComparatorValidator<T : BaseService, C : BaseServiceComparator>(
    val newService: () -> T,
    val newComparator: (NetworkServiceComparatorOptions) -> C
) {

    fun <T : IdentifiedObject> validateServiceOf(
        source: T,
        target: T,
        expectModification: ObjectDifference<out IdentifiedObject>? = null,
        expectMissingFromTarget: T? = null,
        expectMissingFromSource: T? = null,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all()
    ) {
        val diff = newComparator(options).compare(serviceOf(source), serviceOf(target))

        if (expectModification == null)
            assertThat(diff.modifications(), equalTo(mapOf()))
        else {
            assertThat(diff.modifications(), aMapWithSize(1))
            assertThat(diff.modifications(), hasEntry(source.mRID, expectModification))
        }

        if (expectMissingFromTarget == null)
            assertThat(diff.missingFromTarget(), empty())
        else
            assertThat(diff.missingFromTarget(), containsInAnyOrder(expectMissingFromTarget.mRID))

        if (expectMissingFromSource == null)
            assertThat(diff.missingFromSource(), empty())
        else
            assertThat(diff.missingFromSource(), containsInAnyOrder(expectMissingFromSource.mRID))
    }

    fun validateNameTypes(
        source: NameType,
        target: NameType,
        expectModification: ObjectDifference<NameType>? = null,
        expectMissingFromTarget: NameType? = null,
        expectMissingFromSource: NameType? = null,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all()
    ) {
        val diff = newComparator(options).compare(
            newService().apply { addNameType(source) },
            newService().apply { addNameType(target) },
        )

        expectModification?.let { assertThat(diff.modifications().values, contains(it)) } ?: assertThat(diff.modifications().values, empty())
        expectMissingFromTarget?.let { assertThat(diff.missingFromTarget(), contains(it.name)) } ?: assertThat(diff.missingFromTarget(), empty())
        expectMissingFromSource?.let { assertThat(diff.missingFromSource(), contains(it.name)) } ?: assertThat(diff.missingFromSource(), empty())
    }

    fun <T : Any> validateCompare(
        source: T,
        target: T,
        expectModification: ObjectDifference<T> = ObjectDifference(source, target),
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val diff: ObjectDifference<T> = newComparator(NetworkServiceComparatorOptions.all()).compare(source, target)
        assertThat(diff, equalTo(expectModification))

        if (optionsStopCompare) {
            val noDiffExpected: ObjectDifference<T> = newComparator(options).compare(source, target)
            assertThat(noDiffExpected, equalTo(ObjectDifference(source, target)))
        }
    }

    fun <T : IdentifiedObject, R> validateProperty(
        property: KMutableProperty1<in T, R>,
        createIdObj: (String) -> T,
        createValue: (T) -> R,
        createOtherValue: (T) -> R,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val subject = createIdObj("mRID").apply { property.set(this, createValue(this)) }
        val matching = createIdObj("mRID").apply { property.set(this, createValue(this)) }
        validateCompare(subject, matching, options = options, optionsStopCompare = optionsStopCompare)

        val modified = createIdObj("mRID").apply { property.set(this, createOtherValue(this)) }

        ObjectDifference(subject, modified).apply {
            differences[property.name] = getValueOrReferenceDifference(property.get(subject), property.get(target))
        }.validateExpected(options, optionsStopCompare)
    }

    fun <T : IdentifiedObject, R> validateValProperty(
        property: KProperty1<in T, R>,
        createIdObj: (id: String) -> T,
        changeState: (idObj: T, currentPropertyValue: R) -> Unit,
        otherChangeState: (idObj: T, currentPropertyValue: R) -> Unit,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val subject = createIdObj("mRID").apply { changeState(this, property.get(this)) }
        val matching = createIdObj("mRID").apply { changeState(this, property.get(this)) }
        validateCompare(subject, matching, options = options, optionsStopCompare = optionsStopCompare)

        val modified = createIdObj("mRID").apply { otherChangeState(this, property.get(this)) }

        ObjectDifference(subject, modified).apply {
            differences[property.name] = getValueOrReferenceDifference(property.get(subject), property.get(target))
        }.validateExpected(options, optionsStopCompare)
    }

    fun <T : IdentifiedObject, R> validateCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply { addToCollection(this, createItem(this)) }
        val inTarget = createIdObj("mRID").apply { addToCollection(this, createItem(this)) }
        validateCompare(inSource, inTarget, options = options, optionsStopCompare = optionsStopCompare)

        ObjectDifference(inSource, targetEmpty).apply {
            val item = property.get(source).first()
            differences[property.name] = CollectionDifference(missingFromTarget = mutableListOf(item))
        }.validateExpected(options, optionsStopCompare)

        ObjectDifference(sourceEmpty, inTarget).apply {
            val item = property.get(target).first()
            differences[property.name] = CollectionDifference(missingFromSource = mutableListOf(item))
        }.validateExpected(options, optionsStopCompare)

        val inTargetDifference = createIdObj("mRID").apply { addToCollection(this, createOtherItem(this)) }
        ObjectDifference(inSource, inTargetDifference).apply {
            val inSourceItem = property.get(source).first()
            val inTargetItem = property.get(target).first()
            differences[property.name] = CollectionDifference(
                missingFromSource = mutableListOf(inTargetItem),
                missingFromTarget = mutableListOf(inSourceItem)
            )
        }.validateExpected(options, optionsStopCompare)
    }

    fun <T : IdentifiedObject, R> validateIndexedCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        setItemIdObj: (R, T) -> Unit = { _, _ -> },
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply {
            val item = createItem(this)
            setItemIdObj(item, this)
            addToCollection(this, item)
        }
        val inTarget = createIdObj("mRID").apply {
            val item = createItem(this)
            setItemIdObj(item, this)
            addToCollection(this, item)
        }
        validateCompare(inSource, inTarget, options = options, optionsStopCompare = optionsStopCompare)

        val getItem = { obj: T -> property.get(obj).firstOrNull() }

        ObjectDifference(inSource, targetEmpty).apply {
            val valOrRefDiff = getValueOrReferenceDifference(getItem(source), null)
            differences[property.name] = CollectionDifference().apply { missingFromTarget.add(IndexedDifference(0, valOrRefDiff)) }
        }.validateExpected(options, optionsStopCompare)

        ObjectDifference(sourceEmpty, inTarget).apply {
            val valOrRefDiff = getValueOrReferenceDifference(null, getItem(target))
            differences[property.name] = CollectionDifference().apply { missingFromSource.add(IndexedDifference(0, valOrRefDiff)) }
        }.validateExpected(options, optionsStopCompare)

        val targetDifferent = createIdObj("mRID").apply {
            val item = createOtherItem(this)
            setItemIdObj(item, this)
            addToCollection(this, item)
        }
        ObjectDifference(inSource, targetDifferent).apply {
            val valOrRefDiff = getValueOrReferenceDifference(getItem(source), getItem(target))
            differences[property.name] = CollectionDifference().apply { modifications.add(IndexedDifference(0, valOrRefDiff)) }
        }.validateExpected(options, optionsStopCompare)
    }

    private fun <T> getValueOrReferenceDifference(source: T?, target: T?): Difference {
        return if (source is IdentifiedObject? && target is IdentifiedObject?)
            ReferenceDifference(source, target)
        else
            ValueDifference(source, target)
    }

    private fun <T : IdentifiedObject> serviceOf(it: T) =
        newService().apply { tryAdd(it) }

    private fun <T : IdentifiedObject> ObjectDifference<T>.validateExpected(
        options: NetworkServiceComparatorOptions,
        optionsStopCompare: Boolean = false
    ) {
        validateCompare(source, target, expectModification = this, options = options, optionsStopCompare = optionsStopCompare)
    }
}
