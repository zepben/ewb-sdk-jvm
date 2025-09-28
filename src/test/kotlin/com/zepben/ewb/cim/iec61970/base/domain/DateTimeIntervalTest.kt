/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.domain

import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

class DateTimeIntervalTest {
    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(DateTimeInterval(start = Instant.now()), notNullValue())
        assertThat(DateTimeInterval(end = Instant.now()), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val interval = DateTimeInterval(start = Instant.ofEpochSecond(1), end = Instant.ofEpochSecond(2))

        assertThat(interval.start, equalTo(Instant.ofEpochSecond(1)))
        assertThat(interval.end, equalTo(Instant.ofEpochSecond(2)))
    }

    @Test
    internal fun `must have a valid start or end`() {
        expect { DateTimeInterval() }
            .toThrow<IllegalArgumentException>()
            .withMessage("You must provide a start or end time.")

        expect { DateTimeInterval(start = Instant.MAX, end = Instant.MIN) }
            .toThrow<IllegalArgumentException>()
            .withMessage("The start time must be before the end time.")
    }

}
