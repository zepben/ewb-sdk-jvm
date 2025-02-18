/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.extensions

import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.evolve.utils.createMockResultSet
import io.mockk.every
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class ResultSetExtensionsTest {

    @Test
    internal fun `getNullableRatio returns a ratio`() {
        val rs = createMockResultSet {
            every { it.getDouble(any<Int>()) } returns 1.0 andThen 2.0
        }

        assertThat(rs.getNullableRatio(1, 1), equalTo(Ratio(2.0, 1.0)))
    }

    @Test
    internal fun `getNullableRatio returns a null`() {
        val rs = createMockResultSet(wasNull = true) {
            every { it.getDouble(any<Int>()) } returns 0.0
        }

        assertThat(rs.getNullableRatio(1, 1), nullValue())
    }

}
