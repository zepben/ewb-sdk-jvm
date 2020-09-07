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
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.test.util.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.util.regex.Pattern


internal class PrivateCollectionValidator {

    companion object {

        /**
         * Validate the internal collection for an associated [IdentifiedObject] that has no order significance.
         */
        internal inline fun <reified T : IdentifiedObject, reified U : IdentifiedObject> validate(
            crossinline createIt: () -> T,
            crossinline createOther: (String, T) -> U,
            crossinline num: (T) -> Int,
            crossinline get: (T, String) -> U?,
            crossinline getAll: (T) -> Collection<U>,
            crossinline add: (T, U) -> T,
            crossinline remove: (T, U?) -> Boolean,
            crossinline clear: (T) -> T
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
                .toThrow(IllegalArgumentException::class.java)
                .withMessage(Pattern.compile("An? (current )?${getName(other1.javaClass)} with mRID ${other1.mRID} already exists in ${it.typeNameAndMRID()}."))

            assertThat(num(it), equalTo(3))

            assertThat(remove(it, other1), equalTo(true))
            assertThat(remove(it, other1), equalTo(false))
            assertThat(remove(it, null), equalTo(false))
            assertThat(num(it), equalTo(2))

            assertThat(get(it, other2.mRID), equalTo(other2))
            
            assertThat(getAll(it), containsInAnyOrder(other2, other3))

            clear(it)
            assertThat(num(it), equalTo(0))

            // Make sure you can add an item back after it has been removed
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
                .toThrow(IllegalArgumentException::class.java)
                .withMessage(Pattern.compile("An? (current )?${getName(other1.javaClass)} with mRID ${other1.mRID} already exists in ${it.typeNameAndMRID()}."))

            assertThat(num(it), equalTo(2))

            assertThat(remove(it, other1), equalTo(true))
            assertThat(remove(it, other1), equalTo(false))
            assertThat(remove(it, null), equalTo(false))
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
                .toThrow()
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
            crossinline add: (T, U) -> T,
            crossinline addWithIndex: (T, U, Int) -> T,
            remove: (T, U?) -> Boolean,
            clear: (T) -> T
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

            // Non-identified objects can be added more than once
            add(it, other1)
            addWithIndex(it, other1, 1)
            assertThat(num(it), equalTo(5))

            assertThat(remove(it, other2), equalTo(true))
            assertThat(remove(it, other2), equalTo(false))
            assertThat(remove(it, null), equalTo(false))
            assertThat(num(it), equalTo(4))

            assertThat(get(it, 2), equalTo(other3))

            val list = mutableListOf<U>()
            forEach(it, list::add)
            assertThat(list, contains(other1, other1, other3, other1))

            clear(it)
            assertThat(num(it), equalTo(0))

            // Make sure you can add an item back after it has been removed
            add(it, other2)
            assertThat(num(it), equalTo(1))

            expect { addWithIndex(it, other3, 20) }
                .toThrow()
                .withMessage(
                    Pattern.compile(
                        "Unable to add ${other3.javaClass.simpleName} to ${it.typeNameAndMRID()}. " +
                            "\\w* number 20 is invalid. Expected a value between 0 and ${num(it)}. " +
                            "Make sure you are adding the items in order and there are no gaps in the numbering."
                    )
                )

            remove(it, other2)
            assertThat(num(it), equalTo(0))

            // Make sure you can call remove on an empty list.
            remove(it, other2)
            assertThat(num(it), equalTo(0))
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
