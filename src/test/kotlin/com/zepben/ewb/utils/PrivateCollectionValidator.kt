/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.utils

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.util.regex.Pattern


internal class PrivateCollectionValidator {

    companion object {

        /**
         * Validate the internal collection for an associated [IdentifiedObject] that has no order significance.
         */
        internal fun <T : IdentifiedObject, U : IdentifiedObject> validateUnordered(
            createIt: (String) -> T,
            createOther: (String) -> U,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            getById: (T, String) -> U?,
            add: (T, U) -> T,
            remove: (T, U) -> Boolean,
            clear: (T) -> T
        ) {
            val it = createIt("it")
            val other1 = createOther("1")
            val other2 = createOther("2")
            val other3 = createOther("3")
            val otherDuplicateId = createOther("1")

            val expectedDuplicateErrors = mapOf(
                otherDuplicateId to "An? (current )?${getName(other1.javaClass)} with mRID ${other1.mRID} already exists in ${it.typeNameAndMRID()}."
            )

            validate(
                it,
                listOf(other1, other2, other3),
                getAll,
                num,
                add,
                remove,
                clear,
                validateCollection = ::assertUnordered,
                performDuplicateValidation = createDuplicatesThrowValidator(it, expectedDuplicateErrors, add),
                beforeRemovalValidation = {
                    assertThat(getById(it, "1"), equalTo(other1))
                    assertThat(getById(it, "2"), equalTo(other2))
                },
                afterRemovalValidation = {
                    assertThat(getById(it, "1"), nullValue())
                    assertThat(getById(it, "2"), equalTo(other2))
                },
                othersHaveOrder = false
            )
        }

        /**
         * Validate the internal collection for an associated object that is not an [IdentifiedObject] that has no order significance.
         */
        internal fun <T, U : Any, K : Any> validateUnordered(
            createIt: (String) -> T,
            createOther: (Int) -> U,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            getByKey: (T, K) -> U?,
            add: (T, U) -> T,
            remove: (T, U) -> Boolean,
            clear: (T) -> T,
            getKey: (U) -> K,
            duplicateBehaviour: DuplicateBehaviour,
        ) {
            val it = createIt("it")
            val other1 = createOther(1)
            val other2 = createOther(2)
            val other3 = createOther(3)
            val otherDuplicateKey = createOther(1)
            val others = listOf(other1, other2, other3)

            require(other1 !is IdentifiedObject) { "do not use this function with identified 'other', use one of the other variants instead." }

            // Just check that the duplicate key is in the error message.
            val expectedDuplicateErrors = mapOf(otherDuplicateKey to ".*${getKey(otherDuplicateKey)}.*")

            validate(
                it,
                listOf(other1, other2, other3),
                getAll,
                num,
                add,
                remove,
                clear,
                validateCollection = ::assertUnordered,
                performDuplicateValidation = when (duplicateBehaviour) {
                    DuplicateBehaviour.THROWS -> createDuplicatesThrowValidator(it, expectedDuplicateErrors, add)
                    DuplicateBehaviour.SUPPORTED -> createDuplicatesSupportedValidator(it, others, getAll, num, add, remove, ::assertUnordered)
                    DuplicateBehaviour.IGNORED -> createDuplicatesIgnoredValidator(it, others, getAll, num, add, ::assertUnordered)
                },
                beforeRemovalValidation = {
                    assertThat(getByKey(it, getKey(other1)), equalTo(other1))
                    assertThat(getByKey(it, getKey(other2)), equalTo(other2))
                },
                afterRemovalValidation = {
                    assertThat(getByKey(it, getKey(other1)), nullValue())
                    assertThat(getByKey(it, getKey(other2)), equalTo(other2))
                },
                othersHaveOrder = false
            )
        }

        /**
         * Validate the internal collection for an associated [IdentifiedObject] that has order significance, baked into the object itself, not just
         * the placement in the collection.
         *
         * NOTE: Baked in index is expected to be 1-based, not 0-based.
         */
        internal fun <T : IdentifiedObject, U : IdentifiedObject> validateOrdered(
            createIt: (String) -> T,
            createOther: (String, Int) -> U,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            getById: (T, String) -> U?,
            getByIndex: (T, Int) -> U?,
            add: (T, U) -> T,
            remove: (T, U) -> Boolean,
            clear: (T) -> T,
            indexOf: (U) -> Int
        ) {
            val it = createIt("it")
            val other1 = createOther("1", 1)
            val other2 = createOther("2", 2)
            val otherAuto = createOther("3", 0)
            val otherDuplicateId = createOther("1", 4)
            val otherDuplicateIndex = createOther("4", 1)

            // Make sure all the objects have the indexes we provided so our checks will work.
            assertThat(listOf(other1, other2, otherAuto, otherDuplicateId, otherDuplicateIndex).map(indexOf), contains(1, 2, 0, 4, 1))

            val expectedDuplicateErrors = mapOf(
                otherDuplicateId to "An? (current )?${getName(other1.javaClass)} with mRID 1 already exists in ${it.typeNameAndMRID()}.",
                otherDuplicateIndex to "Unable to add ${otherDuplicateIndex.typeNameAndMRID()} to ${it.typeNameAndMRID()}. A ${other1.typeNameAndMRID()} already exists with \\w+ 1."
            )

            validate(
                it,
                listOf(other1, other2, otherAuto),
                getAll,
                num,
                add,
                remove,
                clear,
                validateCollection = ::assertOrdered,
                performDuplicateValidation = createDuplicatesThrowValidator(it, expectedDuplicateErrors, add),
                beforeRemovalValidation = {
                    assertThat(getById(it, other1.mRID), equalTo(other1))
                    assertThat(getById(it, other2.mRID), equalTo(other2))

                    // Adding the auto indexed object should have set its index, which was 0 above.
                    assertThat(indexOf(otherAuto), equalTo(3))

                    // We should be able to get each item by its index, and nulls for invalid indexes.
                    assertThat(getByIndex(it, 0), nullValue())
                    assertThat(getByIndex(it, 1), equalTo(other1))
                    assertThat(getByIndex(it, 2), equalTo(other2))
                    assertThat(getByIndex(it, 3), equalTo(otherAuto))
                    assertThat(getByIndex(it, 4), nullValue())
                },
                afterRemovalValidation = {
                    assertThat(getById(it, other1.mRID), nullValue())
                    assertThat(getById(it, other2.mRID), equalTo(other2))

                    assertThat(getByIndex(it, 1), nullValue())
                    assertThat(getByIndex(it, 2), equalTo(other2))
                },
                othersHaveOrder = true
            )
        }

        /**
         * Validate the internal collection for an associated object that is not an [IdentifiedObject] that has order significance based on its index
         * in the collection.
         *
         * NOTE: Positional index is expected to be 0-based, not 1-based.
         */
        internal fun <T : IdentifiedObject, U : Any> validateOrdered(
            createIt: (String) -> T,
            createOther: (Int) -> U,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            getByIndex: (T, Int) -> U?,
            forEach: (T, (Int, U) -> Unit) -> Unit,
            add: (T, U) -> T,
            addWithIndex: ((T, U, Int) -> T),
            remove: (T, U) -> Boolean,
            removeAtIndex: ((T, Int) -> U?),
            clear: (T) -> T
        ) {
            val it = createIt("it")
            val other1 = createOther(1)
            val other2 = createOther(2)
            val other3 = createOther(3)
            val others = listOf(other1, other2, other3)

            require(other1 !is IdentifiedObject) { "do not use this function with identified 'other', use one of the other variants instead." }

            validate(
                it,
                others,
                getAll,
                num,
                add,
                remove,
                clear,
                validateCollection = ::assertOrdered,
                performDuplicateValidation = createDuplicatesSupportedValidator(it, others, getAll, num, add, remove, ::assertOrdered),
                beforeRemovalValidation = {
                    val looped = mutableListOf<U>()
                    forEach(it) { index, item ->
                        assertThat(index, equalTo(looped.size))
                        looped.add(item)
                    }
                    assertThat(looped, contains(other1, other2, other3))

                    val other4 = createOther(4)
                    addWithIndex(it, other4, 1)

                    assertThat(num(it), equalTo(4))
                    assertThat(getAll(it), contains(other1, other4, other2, other3))
                    assertThat(getByIndex(it, 1), equalTo(other4))

                    // Put the collection back to how it was before we added the duplicate for future tests.
                    assertThat(removeAtIndex(it, 1), equalTo(other4))

                    // Adding to an invalid index is not valid.
                    expect { addWithIndex(it, other4, 5) }
                        .toThrow<IllegalArgumentException>()
                        .withMessage(
                            Pattern.compile(
                                "Unable to add ${other4.javaClass.simpleName} to ${it.typeNameAndMRID()}. " +
                                    "\\w* number 5 is invalid. Expected a value between 0 and ${num(it)}. " +
                                    "Make sure you are adding the items in order and there are no gaps in the numbering."
                            )
                        )

                    assertThat(getByIndex(it, -1), nullValue())
                    assertThat(getByIndex(it, 0), equalTo(other1))
                    assertThat(getByIndex(it, 1), equalTo(other2))
                    assertThat(getByIndex(it, 2), equalTo(other3))
                    assertThat(getByIndex(it, 3), nullValue())

                    // Removing an invalid index returns null, not an IndexOutOfBounds exception.
                    assertThat(removeAtIndex(it, 5), nullValue())
                },
                afterRemovalValidation = {
                    assertThat(getByIndex(it, -1), nullValue())
                    assertThat(getByIndex(it, 0), equalTo(other2))
                    assertThat(getByIndex(it, 1), equalTo(other3))
                    assertThat(getByIndex(it, 2), nullValue())
                },
                othersHaveOrder = false // Order comes from insertion order, not the other objects.
            )
        }

        private fun <T, U> validate(
            it: T,
            others: List<U>,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            add: (T, U) -> T,
            remove: (T, U) -> Boolean,
            clear: (T) -> T,
            validateCollection: (Collection<U>, List<U>) -> Unit,
            performDuplicateValidation: () -> Unit,
            beforeRemovalValidation: () -> Unit,
            afterRemovalValidation: () -> Unit,
            othersHaveOrder: Boolean
        ) {
            // Make sure all the objects are not equal.
            assertThat(others.toSet(), hasSize(others.size))

            // Make sure the item under test is empty to begin with, so nothing messes with our tests.
            assertThat(num(it), equalTo(0))
            assertThat(getAll(it), empty())

            val (other1, other2, other3) = others
            add(it, other1)
            add(it, other2)
            add(it, other3)

            assertThat(num(it), equalTo(3))
            validateCollection(getAll(it), listOf(other1, other2, other3))

            performDuplicateValidation()

            // Ensure there are no changes to our collection after the duplicate testing, otherwise it may provide false positives below.
            assertThat(num(it), equalTo(3))
            validateCollection(getAll(it), listOf(other1, other2, other3))

            beforeRemovalValidation()

            assertThat("remove should return true for previously-added object", remove(it, other1))
            assertThat("remove should return false for already-removed object", !remove(it, other1))

            // Make sure the items is fully removed.
            assertThat(num(it), equalTo(2))
            validateCollection(getAll(it), listOf(other2, other3))

            afterRemovalValidation()

            // Make sure we can add the item back in, and it ends up in the correct spot in the collection.
            add(it, other1)
            assertThat(num(it), equalTo(3))
            if (othersHaveOrder)
                validateCollection(getAll(it), listOf(other1, other2, other3))
            else
                validateCollection(getAll(it), listOf(other2, other3, other1))

            clear(it)
            assertThat(num(it), equalTo(0))
            assertThat(getAll(it), empty())

            // Make sure you can call remove on an empty list.
            remove(it, other3)
            assertThat(num(it), equalTo(0))
            assertThat(getAll(it), empty())

            // Make sure you can add an item back after it has been cleared
            add(it, other1)
            assertThat(num(it), equalTo(1))
            validateCollection(getAll(it), listOf(other1))
        }

        private fun getName(clazz: Class<*>?): String {
            return clazz?.let {
                if (it.simpleName.isNullOrBlank())
                    getName(it.superclass)
                else
                    it.simpleName
            } ?: ""
        }

        private fun <U> assertUnordered(actual: Collection<U>, expected: List<U>) {
            // We don't want to make everything inlined/reified just to compare a list, we map to the matchers ourselves since we can't spread the
            // list, and if we change expected to an Array, you can't create it.
            assertThat(actual, containsInAnyOrder(*expected.map { equalTo(it) }.toTypedArray()))
        }

        private fun <U> assertOrdered(actual: Collection<U>, expected: List<U>) {
            // We don't want to make everything inlined/reified just to compare a list, we map to the matchers ourselves since we can't spread the
            // list, and if we change expected to an Array, you can't create it.
            assertThat(actual, contains(*expected.map { equalTo(it) }.toTypedArray()))
        }

        private fun <T, U> createDuplicatesThrowValidator(it: T, expectedDuplicateErrors: Map<U, String>, add: (T, U) -> T): () -> Unit = {
            expectedDuplicateErrors.forEach { (otherDuplicate, expectedError) ->
                expect { add(it, otherDuplicate) }
                    .toThrow<IllegalArgumentException>()
                    .withMessage(Pattern.compile(expectedError))
            }
        }

        private fun <T, U> createDuplicatesSupportedValidator(
            it: T,
            others: List<U>,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            add: (T, U) -> T,
            remove: (T, U) -> Boolean,
            validateCollection: (Collection<U>, List<U>) -> Unit,
        ): () -> Unit = {
            //
            // NOTE: We add all the items a second time to allow us to clean it up, as the remove below will take the first instance out, changing
            //       the order of the final collection if we only use a single duplicate.
            //
            others.forEach { duplicate -> add(it, duplicate) }

            assertThat(num(it), equalTo(others.size * 2))
            validateCollection(getAll(it), others + others)

            // Put the collection back to how it was before we added the duplicate for future tests.
            others.forEach { duplicate -> assertThat("Should be able to remove the duplicate", remove(it, duplicate)) }
        }

        private fun <T, U> createDuplicatesIgnoredValidator(
            it: T,
            others: List<U>,
            getAll: (T) -> Collection<U>,
            num: (T) -> Int,
            add: (T, U) -> T,
            validateCollection: (Collection<U>, List<U>) -> Unit,
        ): () -> Unit = {
            //
            // NOTE: Adding duplicate data classes should have no effect.
            //
            others.forEach { duplicate -> add(it, duplicate) }

            assertThat(num(it), equalTo(others.size))
            validateCollection(getAll(it), others)
        }

    }

    /**
     * How the private collection handles duplicates.
     */
    internal enum class DuplicateBehaviour {
        /**
         * Different objects with common IDs are expected to throw.
         */
        THROWS,

        /**
         * The collection has no concept of IDs, or duplicates, and just accepts the new value.
         */
        SUPPORTED,

        /**
         * Any attempt to add a duplicates is detected and just ignored.
         */
        IGNORED
    }

}
