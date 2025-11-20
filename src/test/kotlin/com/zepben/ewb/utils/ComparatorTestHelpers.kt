/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.utils

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.*
import com.zepben.ewb.services.network.NetworkServiceComparatorOptions
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

typealias CreateCollectionDifference = (
    missingFromTarget: MutableList<in Any>,
    missingFromSource: MutableList<in Any>,
    modifications: MutableList<Difference>
) -> CollectionDifference

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
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet()
    ) {
        val diff: ObjectDifference<T> = newComparator(NetworkServiceComparatorOptions.all()).compare(source, target)
        assertThat(diff.source, equalTo(expectModification.source))
        assertThat(diff.target, equalTo(expectModification.target))
        assertThat(diff.differences.filterKeys { it !in expectedDifferences }, equalTo(expectModification.differences))

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
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet()
    ) {
        val subject = createIdObj("mRID").apply { property.set(this, createValue(this)) }
        val matching = createIdObj("mRID").apply { property.set(this, createValue(this)) }
        validateCompare(subject, matching, options = options, optionsStopCompare = optionsStopCompare)

        val modified = createIdObj("mRID").apply { property.set(this, createOtherValue(this)) }

        ObjectDifference(subject, modified).apply {
            differences[property.name] = getValueOrReferenceDifference(property.get(subject), property.get(target))
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
    }

    fun <T : IdentifiedObject, R> validateValProperty(
        property: KProperty1<in T, R>,
        createIdObj: (id: String) -> T,
        changeState: (idObj: T, currentPropertyValue: R) -> Unit,
        otherChangeState: (idObj: T, currentPropertyValue: R) -> Unit,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet()
    ) {
        val subject = createIdObj("mRID").apply { changeState(this, property.get(this)) }
        val matching = createIdObj("mRID").apply { changeState(this, property.get(this)) }
        validateCompare(subject, matching, options = options, optionsStopCompare = optionsStopCompare)

        val modified = createIdObj("mRID").apply { otherChangeState(this, property.get(this)) }

        ObjectDifference(subject, modified).apply {
            differences[property.name] = getValueOrReferenceDifference(property.get(subject), property.get(target))
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
    }

    fun <T : IdentifiedObject, R> validateCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        createCollectionDifference: CreateCollectionDifference,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet(),
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply { addToCollection(this, createItem(this)) }
        val inTarget = createIdObj("mRID").apply { addToCollection(this, createItem(this)) }
        validateCompare(inSource, inTarget, options = options, optionsStopCompare = optionsStopCompare)

        ObjectDifference(inSource, targetEmpty).apply {
            val item = property.get(source).first()
            differences[property.name] = createCollectionDifference(mutableListOf(item), mutableListOf(), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        ObjectDifference(sourceEmpty, inTarget).apply {
            val item = property.get(target).first()
            differences[property.name] = createCollectionDifference(mutableListOf(), mutableListOf(item), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        val inTargetDifference = createIdObj("mRID").apply { addToCollection(this, createOtherItem(this)) }
        ObjectDifference(inSource, inTargetDifference).apply {
            val inSourceItem = property.get(source).first()
            val inTargetItem = property.get(target).first()
            differences[property.name] = createCollectionDifference(
                mutableListOf(inSourceItem),
                mutableListOf(inTargetItem),
                mutableListOf(),
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
    }

    /**
     * A variation of the collection validation that passes two parameters (type [U] and [V]) to [addToCollection] that results in an object of type [R] being
     * added to the collection.
     *
     * This will validate if either of the [U] or [V] parameters are different, the resulting [R] is considered different.
     *
     * Example usage of this function is for validating the names collection of an [IdentifiedObject].
     */
    fun <T : IdentifiedObject, U, V, R> validateCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, U, V) -> T,
        createIdObj: (String) -> T,
        createItem1: (T) -> U,
        createOtherItem1: (T) -> U,
        createItem2: (T) -> V,
        createOtherItem2: (T) -> V,
        createCollectionDifference: CreateCollectionDifference,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet(),
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply { addToCollection(this, createItem1(this), createItem2(this)) }
        val inTarget = createIdObj("mRID").apply { addToCollection(this, createItem1(this), createItem2(this)) }
        validateCompare(inSource, inTarget, options = options, optionsStopCompare = optionsStopCompare)

        ObjectDifference(inSource, targetEmpty).apply {
            val item = property.get(source).first()
            differences[property.name] = createCollectionDifference(mutableListOf(item), mutableListOf(), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        ObjectDifference(sourceEmpty, inTarget).apply {
            val item = property.get(target).first()
            differences[property.name] = createCollectionDifference(mutableListOf(), mutableListOf(item), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        val inTargetDifference1 = createIdObj("mRID").apply { addToCollection(this, createOtherItem1(this), createItem2(this)) }
        ObjectDifference(inSource, inTargetDifference1).apply {
            val inSourceItem = property.get(source).first()
            val inTargetItem = property.get(target).first()
            differences[property.name] = createCollectionDifference(
                mutableListOf(inSourceItem),
                mutableListOf(inTargetItem),
                mutableListOf()
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        val inTargetDifference2 = createIdObj("mRID").apply { addToCollection(this, createItem1(this), createOtherItem2(this)) }
        ObjectDifference(inSource, inTargetDifference2).apply {
            val inSourceItem = property.get(source).first()
            val inTargetItem = property.get(target).first()
            differences[property.name] = createCollectionDifference(
                mutableListOf(inSourceItem),
                mutableListOf(inTargetItem),
                mutableListOf(),
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
    }

    fun <T : IdentifiedObject, R> validateIndexedCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        createCollectionDifference: CreateCollectionDifference,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet(),
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply {
            val item = createItem(this)
            addToCollection(this, item)
        }
        val inTarget = createIdObj("mRID").apply {
            val item = createItem(this)
            addToCollection(this, item)
        }
        validateCompare(inSource, inTarget, options = options, optionsStopCompare = optionsStopCompare)

        val getItem = { obj: T -> property.get(obj).firstOrNull() }

        ObjectDifference(inSource, targetEmpty).apply {
            val valOrRefDiff = getValueOrReferenceDifference(getItem(source), null)
            differences[property.name] = createCollectionDifference(mutableListOf(IndexedDifference(0, valOrRefDiff)), mutableListOf(), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        ObjectDifference(sourceEmpty, inTarget).apply {
            val valOrRefDiff = getValueOrReferenceDifference(null, getItem(target))
            differences[property.name] = createCollectionDifference(mutableListOf(), mutableListOf(IndexedDifference(0, valOrRefDiff)), mutableListOf())
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        val targetDifferent = createIdObj("mRID").apply {
            val item = createOtherItem(this)
            addToCollection(this, item)
        }
        ObjectDifference(inSource, targetDifferent).apply {
            val valOrRefDiff = getValueOrReferenceDifference(getItem(source), getItem(target))
            differences[property.name] = createCollectionDifference(mutableListOf(), mutableListOf(), mutableListOf(IndexedDifference(0, valOrRefDiff)))
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
    }

    /**
     * @param property The property we are checking.
     * @param addToCollection The function used to add the items to the object.
     * @param createIdObj Create the [IdentifiedObject] under test.
     * @param createItem1 Create the first item to add to the collection.
     * @param createItem2 Create the second item to add to the collection.
     * @param createDiffItem1 Create an item with the same key as the first item, but with different data.
     * @param options Optional comparator options. Defaults to `all`.
     * @param optionsStopCompare Indicates if the provided options result in the comparison detecting no differences.
     * @param expectedDifferences A set of differences expected between two items that should otherwise be equal. Only use if there are
     * expected runtime differences due to things outside your control. e.g. auto generated ID's etc.
     */
    fun <T : IdentifiedObject, R> validateUnorderedCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem1: (T) -> R,
        createItem2: (T) -> R,
        createDiffItem1: (T) -> R,
        createCollectionDifference: CreateCollectionDifference,
        options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all(),
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String> = emptySet(),
    ) {
        val sourceEmpty = createIdObj("mRID")
        val targetEmpty = createIdObj("mRID")
        validateCompare(sourceEmpty, targetEmpty, options = options, optionsStopCompare = optionsStopCompare)

        val inSource = createIdObj("mRID").apply {
            val item1 = createItem1(this)
            addToCollection(this, item1)

            val item2 = createItem2(this)
            addToCollection(this, item2)
        }
        val inTargetSameOrder = createIdObj("mRID").apply {
            val item1 = createItem1(this)
            addToCollection(this, item1)

            val item2 = createItem2(this)
            addToCollection(this, item2)
        }
        validateCompare(inSource, inTargetSameOrder, options = options, optionsStopCompare = optionsStopCompare)

        val inTargetDiffOrder = createIdObj("mRID").apply {
            val item2 = createItem2(this)
            addToCollection(this, item2)

            val item1 = createItem1(this)
            addToCollection(this, item1)
        }
        validateCompare(inSource, inTargetDiffOrder, options = options, optionsStopCompare = optionsStopCompare)

        val getItem1 = { obj: T -> property.get(obj).firstOrNull() }
        val getItem2 = { obj: T -> property.get(obj).lastOrNull() }

        ObjectDifference(inSource, targetEmpty).apply {
            differences[property.name] = createCollectionDifference(
                mutableListOf(
                    getValueOrReferenceDifference(getItem1(source), null),
                    getValueOrReferenceDifference(getItem2(source), null),
                ),
                mutableListOf(),
                mutableListOf(),
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        ObjectDifference(sourceEmpty, inTargetSameOrder).apply {
            differences[property.name] = createCollectionDifference(
                mutableListOf(),
                mutableListOf(
                    getValueOrReferenceDifference(null, getItem1(target)),
                    getValueOrReferenceDifference(null, getItem2(target)),
                ),
                mutableListOf(),
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)

        val targetDifferent = createIdObj("mRID").apply {
            val item1 = createDiffItem1(this)
            addToCollection(this, item1)

            val item2 = createItem2(this)
            addToCollection(this, item2)
        }
        ObjectDifference(inSource, targetDifferent).apply {
            differences[property.name] = createCollectionDifference(
                mutableListOf(),
                mutableListOf(),
                mutableListOf(getValueOrReferenceDifference(getItem1(source), getItem1(target))),
            )
        }.validateExpected(options, optionsStopCompare, expectedDifferences)
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
        optionsStopCompare: Boolean = false,
        expectedDifferences: Set<String>
    ) {
        validateCompare(source, target, expectModification = this, options = options, optionsStopCompare = optionsStopCompare, expectedDifferences)
    }
}
