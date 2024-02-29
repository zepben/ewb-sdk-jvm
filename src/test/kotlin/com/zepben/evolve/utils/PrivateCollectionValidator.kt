/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.utils

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.util.regex.Pattern


internal class PrivateCollectionValidator {

    companion object {

        /**
         * Validate the internal collection for an associated [IdentifiedObject] that has no order significance.
         */
        internal inline fun <reified T : IdentifiedObject, reified U : IdentifiedObject> validate(
            createIt: () -> T,
            createOther: (String, T) -> U,
            num: (T) -> Int,
            get: (T, String) -> U?,
            getAll: (T) -> Collection<U>,
            crossinline add: (T, U) -> T,
            remove: (T, U?) -> Boolean,
            clear: (T) -> T
        ) {
            val it = createIt()
            val other1 = createOther("1", it)
            val other2 = createOther("2", it)
            val other3 = createOther("3", it)
            val duplicate1 = createOther("1", it)

            assertThat(other1, not(equalTo(other2)))
            assertThat(other1, not(equalTo(other3)))
            assertThat(other2, not(equalTo(other3)))

            assertThat(num(it), equalTo(0))

            add(it, other1)
            add(it, other2)
            add(it, other3)
            assertThat(num(it), equalTo(3))
            expect { add(it, duplicate1) }
                .toThrow<IllegalArgumentException>()
                .withMessage(Pattern.compile("An? (current )?${getName(other1.javaClass)} with mRID ${other1.mRID} already exists in ${it.typeNameAndMRID()}."))

            assertThat(num(it), equalTo(3))

            assertThat("remove should return true for previously-added object", remove(it, other1))
            assertThat("remove should return false for already-removed object", !remove(it, other1))
            assertThat("remove should return false for null", !remove(it, null))
            assertThat(num(it), equalTo(2))

            assertThat(get(it, other2.mRID), equalTo(other2))

            assertThat(getAll(it), containsInAnyOrder(other2, other3))

            clear(it)
            assertThat(num(it), equalTo(0))

            // Make sure you can add an item back after it has been removed
            add(it, other1)
            assertThat(num(it), equalTo(1))

            // Make sure adding an already-added item does not change the collection
            add(it, other1)
            assertThat(num(it), equalTo(1))

            remove(it, other1)
            assertThat(num(it), equalTo(0))

            // Make sure you can call remove on an empty list.
            remove(it, other2)
            assertThat(num(it), equalTo(0))
        }

        /**
         * Validate the internal collection for an associated [IdentifiedObject] that has order significance.
         */
        internal inline fun <reified T : IdentifiedObject, reified U : IdentifiedObject> validate(
            createIt: () -> T,
            createOther: (String, T, Int?) -> U,
            num: (T) -> Int,
            getById: (T, String) -> U?,
            getByIndex: (T, Int) -> U?,
            getAll: (T) -> Collection<U>,
            crossinline add: (T, U) -> T,
            remove: (T, U?) -> Boolean,
            clear: (T) -> T
        ) {
            val it = createIt()
            val other1 = createOther("1", it, 1)
            val other2 = createOther("2", it, 1)
            val other3 = createOther("3", it, 3)
            val other4 = createOther("3", it, null)
            val duplicate1 = createOther("1", it, 1)

            assertThat(other1, not(equalTo(other2)))
            assertThat(other1, not(equalTo(other3)))
            assertThat(other2, not(equalTo(other3)))

            assertThat(num(it), equalTo(0))

            add(it, other1)
            add(it, other3)
            assertThat(num(it), equalTo(2))

            expect { add(it, duplicate1) }
                .toThrow<IllegalArgumentException>()
                .withMessage(Pattern.compile("An? (current )?${getName(other1.javaClass)} with mRID ${other1.mRID} already exists in ${it.typeNameAndMRID()}."))

            assertThat(num(it), equalTo(2))

            assertThat("remove should return true for previously-added object", remove(it, other1))
            assertThat("remove should return false for already-removed object", !remove(it, other1))
            assertThat("remove should return false for null", !remove(it, null))
            assertThat(num(it), equalTo(1))

            assertThat(getById(it, other3.mRID), equalTo(other3))
            assertThat(getByIndex(it, 0), nullValue())
            assertThat(getByIndex(it, 3), equalTo(other3))

            add(it, other1)
            val list = mutableListOf<U>()
            getAll(it).forEach(list::add)
            assertThat(list, containsInRelativeOrder(other1, other3))

            clear(it)
            assertThat(num(it), equalTo(0))

            // Make sure you can add an item back after it has been removed
            add(it, other1)
            assertThat(num(it), equalTo(1))

            expect { add(it, other2) }
                .toThrowAny()
                .withMessage(
                    Pattern.compile(
                        "Unable to add ${other2.typeNameAndMRID()} to ${it.typeNameAndMRID()}. A ${other1.typeNameAndMRID()} already exists with \\w+ \\d+."
                    )
                )
            add(it, other4)
            assertThat(num(it), equalTo(2))
            assertThat(getAll(it).last(), equalTo(other4))

            remove(it, other1)
            assertThat(num(it), equalTo(1))

            // Make sure you can call remove on an empty list.
            remove(it, other4)
            assertThat(num(it), equalTo(0))
        }

        /**
         * Validate the internal collection for an associated object that is not an [IdentifiedObject] that has order significance.
         */
        internal inline fun <reified T : IdentifiedObject, reified U : Any> validate(
            createIt: () -> T,
            createOther: (T) -> U,
            num: (T) -> Int,
            get: (T, Int) -> U?,
            forEach: (T, (Int, U) -> Unit) -> Unit,
            add: (T, U) -> T,
            noinline addWithIndex: ((T, U, Int) -> T)?,
            remove: (T, U?) -> Boolean,
            noinline removeAtIndex: ((T, Int) -> U?)?,
            clear: (T) -> T,
            supportsDuplicates: Boolean = true,
        ) {
            require(U::class !is IdentifiedObject) { "do not use this function with identified 'other', use one of the other variants instead." }

            val it = createIt()
            val other1 = createOther(it)
            val other2 = createOther(it)
            val other3 = createOther(it)

            assertThat(other1, not(equalTo(other2)))
            assertThat(other1, not(equalTo(other3)))
            assertThat(other2, not(equalTo(other3)))

            assertThat(num(it), equalTo(0))

            add(it, other1)
            add(it, other2)
            add(it, other3)
            assertThat(num(it), equalTo(3))
            assertThat(get(it, 2), equalTo(other3))

            var numObjects = 3
            if (supportsDuplicates) {
                // objects can be added more than once
                add(it, other1)
                assertThat(num(it), equalTo(++numObjects))
            }

            assertThat("remove should return true for previously-added object", remove(it, other2))
            assertThat("remove should return false for already-removed object", !remove(it, other2))
            assertThat("remove should return false for null", !remove(it, null))
            assertThat(num(it), equalTo(--numObjects))

            val list = mutableListOf<U>()
            forEach(it, list::add)
            if (supportsDuplicates)
                assertThat(list, contains(other1, other3, other1))
            else
                assertThat(list, containsInAnyOrder(other1, other3))

            addWithIndex?.let { awi ->
                awi(it, other2, 1)
                assertThat(num(it), equalTo(++numObjects))
                assertThat(get(it, 1), equalTo(other2))
            }

            clear(it)
            assertThat(num(it), equalTo(0))

            // Make sure you can add an item back after it has been removed
            add(it, other2)
            assertThat(num(it), equalTo(1))

            addWithIndex?.also { awi ->
                expect { awi(it, other3, 20) }
                    .toThrowAny()
                    .withMessage(
                        Pattern.compile(
                            "Unable to add ${other3.javaClass.simpleName} to ${it.typeNameAndMRID()}. " +
                                "\\w* number 20 is invalid. Expected a value between 0 and ${num(it)}. " +
                                "Make sure you are adding the items in order and there are no gaps in the numbering."
                        )
                    )
            }

            remove(it, other2)
            assertThat(num(it), equalTo(0))

            // Make sure you can call remove on an empty list.
            remove(it, other2)
            assertThat(num(it), equalTo(0))

            removeAtIndex?.let { rai ->
                add(it, other1)
                add(it, other2)
                add(it, other3)
                assertThat(rai(it, 1), equalTo(other2))
                assertThat(num(it), equalTo(2))
                assertThat(get(it, 1), equalTo(other3))
                assertThat(rai(it, 2), nullValue())
            }
        }

        private fun getName(clazz: Class<*>?): String {
            return clazz?.let {
                if (it.simpleName.isNullOrBlank())
                    getName(it.superclass)
                else
                    it.simpleName
            } ?: ""
        }

    }
}
