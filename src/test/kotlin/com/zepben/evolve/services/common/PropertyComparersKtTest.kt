/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

class PropertyComparersKtTest {

    @Test
    internal fun `unordered list comparison`() {
        val source = UnorderedPropertyTest(listOf(1, 1, 2))
        val targetSame = UnorderedPropertyTest(listOf(1, 1, 2))
        val targetOrder = UnorderedPropertyTest(listOf(1, 2, 1))
        val targetDiffLess = UnorderedPropertyTest(listOf(2, 1))
        val targetDiffValues = UnorderedPropertyTest(listOf(2, 1, 2))
        val targetDiffMore = UnorderedPropertyTest(listOf(1, 1, 3, 4))

        assertThat(UnorderedPropertyTest<Int>::values.compareUnorderedValueCollection(source, targetSame) { it }, nullValue())
        assertThat(UnorderedPropertyTest<Int>::values.compareUnorderedValueCollection(source, targetOrder) { it }, nullValue())

        // Lazy check that we at least detect the error.
        assertThat(UnorderedPropertyTest<Int>::values.compareUnorderedValueCollection(source, targetDiffLess) { it }, notNullValue())
        assertThat(UnorderedPropertyTest<Int>::values.compareUnorderedValueCollection(source, targetDiffValues) { it }, notNullValue())
        assertThat(UnorderedPropertyTest<Int>::values.compareUnorderedValueCollection(source, targetDiffMore) { it }, notNullValue())
    }

    @Test
    internal fun `unordered list comparison with objects`() {
        val source = UnorderedPropertyTest(listOf(UnorderedCheck(1, 2), UnorderedCheck(2, 3)))
        val targetSame = UnorderedPropertyTest(listOf(UnorderedCheck(1, 2), UnorderedCheck(2, 3)))
        val targetOrder = UnorderedPropertyTest(listOf(UnorderedCheck(2, 3), UnorderedCheck(1, 2)))

        val targetDiffKeys = UnorderedPropertyTest(listOf(UnorderedCheck(1, 2), UnorderedCheck(3, 3)))
        val targetDiffValues = UnorderedPropertyTest(listOf(UnorderedCheck(1, 3), UnorderedCheck(2, 3)))

        assertThat(UnorderedPropertyTest<UnorderedCheck>::values.compareUnorderedValueCollection(source, targetSame) { it.key }, nullValue())
        assertThat(UnorderedPropertyTest<UnorderedCheck>::values.compareUnorderedValueCollection(source, targetOrder) { it.key }, nullValue())

        // Lazy check that we at least detect the error.
        assertThat(UnorderedPropertyTest<UnorderedCheck>::values.compareUnorderedValueCollection(source, targetDiffKeys) { it.key }, notNullValue())
        assertThat(UnorderedPropertyTest<UnorderedCheck>::values.compareUnorderedValueCollection(source, targetDiffValues) { it.key }, notNullValue())
    }

    private class UnorderedPropertyTest<T>(
        val values: List<T>
    )

    data class UnorderedCheck(
        val key: Int,
        val value: Int
    )

}
