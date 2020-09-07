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
package com.zepben.cimbend.utils

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.cimbend.common.*
import com.zepben.cimbend.network.NetworkServiceCompatatorOptions
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

class ServiceComparatorValidator<T : BaseService, C : BaseServiceComparator>(
    val newService: () -> T,
    val newComparator: (NetworkServiceCompatatorOptions) -> C
) {

    fun <T : IdentifiedObject> validateServiceOf(
        source: T,
        target: T,
        expectModification: ObjectDifference<out IdentifiedObject>? = null,
        expectMissingFromTarget: T? = null,
        expectMissingFromSource: T? = null,
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all()
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

    fun <T : IdentifiedObject> validateCompare(
        source: T,
        target: T,
        expectModification: ObjectDifference<out IdentifiedObject> = ObjectDifference(source, target),
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all(),
        optionsStopCompare: Boolean = false
    ) {
        val diff: ObjectDifference<T> = newComparator(NetworkServiceCompatatorOptions.all()).compare(source, target)
        assertThat(diff, equalTo(expectModification))

        if (optionsStopCompare) {
            val noDiffExpecited: ObjectDifference<T> = newComparator(options).compare(source, target)
            assertThat(noDiffExpecited, equalTo(ObjectDifference(source, target)))
        }
    }

    fun <T : IdentifiedObject, R> validateProperty(
        property: KMutableProperty1<in T, R>,
        createIdObj: (String) -> T,
        createValue: (T) -> R,
        createOtherValue: (T) -> R,
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all(),
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
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all(),
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

    fun <T : IdentifiedObject, R : IdentifiedObject> validateIdObjCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all(),
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

        val inTargetDifference = createIdObj("mRID").apply { addToCollection(this, createOtherItem(this) ) }
        ObjectDifference(inSource, inTargetDifference).apply {
            val inSourceItem = property.get(source).first()
            val inTargetItem = property.get(target).first()
            differences[property.name] = CollectionDifference(
                missingFromSource = mutableListOf(inTargetItem),
                missingFromTarget = mutableListOf(inSourceItem))
        }.validateExpected(options, optionsStopCompare)
    }

    fun <T : IdentifiedObject, R> validateIndexedCollection(
        property: KProperty1<in T, Collection<R>>,
        addToCollection: (T, R) -> Unit,
        createIdObj: (String) -> T,
        createItem: (T) -> R,
        createOtherItem: (T) -> R,
        setItemIdObj: (R, T) -> Unit = {_, _ -> },
        options: NetworkServiceCompatatorOptions = NetworkServiceCompatatorOptions.all(),
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
        options: NetworkServiceCompatatorOptions,
        optionsStopCompare: Boolean = false
    ) {
        validateCompare(source, target, expectModification = this, options = options, optionsStopCompare = optionsStopCompare)
    }
}
