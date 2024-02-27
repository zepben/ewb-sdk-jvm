/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.time.Instant

internal class ResultSetExtensionsTest {

    @Test
    internal fun `getNullableBoolean returns a bool`() {
        val rs = createMockResultSet {
            every { it.getBoolean(any<Int>()) } returns true
        }

        assertThat(rs.getNullableBoolean(1), equalTo(true))
    }

    @Test
    internal fun `getNullableBoolean returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getBoolean(any<Int>()) } returns true

        }

        assertThat(rs.getNullableBoolean(1), nullValue())
    }

    @Test
    internal fun `getNullableDouble returns a double`() {
        val rs = createMockResultSet {
            every { it.getDouble(any<Int>()) } returns 1.0
        }

        assertThat(rs.getNullableDouble(1), equalTo(1.0))
    }

    @Test
    internal fun `getNullableDouble returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getDouble(any<Int>()) } returns 0.0
        }

        assertThat(rs.getNullableDouble(1), nullValue())
    }

    @Test
    internal fun `getNullableDouble returns a NaN`() {
        val rs = createMockResultSet {
            every { it.getDouble(any<Int>()) } returns 0.0
            every { it.getString(any<Int>()) } returns "nan"
        }

        assertThat(rs.getNullableDouble(1), equalTo(Double.NaN))
    }

    @Test
    internal fun `getNullableDouble returns a 0`() {
        val rs = createMockResultSet {
            every { it.getDouble(any<Int>()) } returns 0.0
            every { it.getString(any<Int>()) } returns "0.0"
        }

        assertThat(rs.getNullableDouble(1), equalTo(0.0))
    }

    @Test
    internal fun `getNullableFloat returns a float`() {
        val rs = createMockResultSet {
            every { it.getFloat(any<Int>()) } returns 1.0f
        }

        assertThat(rs.getNullableFloat(1), equalTo(1.0f))
    }

    @Test
    internal fun `getNullableFloat returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getFloat(any<Int>()) } returns 0.0f
        }

        assertThat(rs.getNullableFloat(1), nullValue())
    }

    @Test
    internal fun `getNullableFloat returns a NaN`() {
        val rs = createMockResultSet {
            every { it.getFloat(any<Int>()) } returns 0.0f
            every { it.getString(any<Int>()) } returns "nan"
        }

        assertThat(rs.getNullableFloat(1), equalTo(Float.NaN))
    }

    @Test
    internal fun `getNullableFloat returns a 0f`() {
        val rs = createMockResultSet {
            every { it.getFloat(any<Int>()) } returns 0.0f
            every { it.getString(any<Int>()) } returns "0.0"
        }

        assertThat(rs.getNullableFloat(1), equalTo(0.0f))
    }


    @Test
    internal fun `getNullableInt returns a int`() {
        val rs = createMockResultSet {
            every { it.getInt(any<Int>()) } returns 1
        }

        assertThat(rs.getNullableInt(1), equalTo(1))
    }

    @Test
    internal fun `getNullableInt returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getInt(any<Int>()) } returns 0
        }

        assertThat(rs.getNullableInt(1), nullValue())
    }

    @Test
    internal fun `getNullableString returns a string`() {
        val rs = createMockResultSet {
            every { it.getString(any<Int>()) } returns "string"
        }

        assertThat(rs.getNullableString(1), equalTo("string"))
    }

    @Test
    internal fun `getNullableString returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getString(any<Int>()) } returns null
        }

        assertThat(rs.getNullableString(1), nullValue())
    }

    @Test
    internal fun `getNullableLong returns a long`() {
        val rs = createMockResultSet {
            every { it.getLong(any<Int>()) } returns 1L
        }

        assertThat(rs.getNullableLong(1), equalTo(1L))
    }

    @Test
    internal fun `getNullableLong returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getLong(any<Int>()) } returns 0L
        }

        assertThat(rs.getNullableLong(1), nullValue())
    }

    @Test
    internal fun `getInstant returns an instant`() {
        val rs = createMockResultSet {
            every { it.getString(any<Int>()) } returns "2020-01-01T00:00:00.000Z"
        }

        assertThat(rs.getInstant(1), equalTo(Instant.parse("2020-01-01T00:00:00.000Z")))
    }

    @Test
    internal fun `getInstant returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getString(any<Int>()) } returns ""
        }

        assertThat(rs.getInstant(1), nullValue())
    }

    @Test
    internal fun `getNullableRatio returns a ratio`() {
        val rs = createMockResultSet {
            every { it.getDouble(any<Int>()) } returns 1.0 andThen 2.0
        }

        assertThat(rs.getNullableRatio(1, 1), equalTo(Ratio(2.0, 1.0)))
    }

    @Test
    internal fun `getNullableRatio returns a null`() {
        val rs = createMockResultSet(true) {
            every { it.getDouble(any<Int>()) } returns 0.0
        }

        assertThat(rs.getNullableRatio(1, 1), nullValue())
    }

    private fun createMockResultSet(wasNull: Boolean = false, mock: (ResultSet) -> Unit): ResultSet =
        mockk {
            every { next() } returns true andThen false
            if (wasNull)
                every { wasNull() } returns true
            else
                every { wasNull() } returns false
            mock(this)
        }
}
