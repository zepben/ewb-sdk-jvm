/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.dataquality

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*

internal class DataQualityIssueCategoryTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val uuid = UUID.randomUUID()

    @Test
    internal fun constructorCoverage() {
        val category = DataQualityIssueCategory(
            id = uuid.toString(),
            name = "Missing Data"
        )

        assertThat(category.id, equalTo(uuid.toString()))
        assertThat(category.name, equalTo("Missing Data"))
        assertThat(category.description, nullValue())
    }

    @Test
    internal fun `optional description can be set`() {
        val category = DataQualityIssueCategory(
            id = uuid.toString(),
            name = "Invalid Values",
            description = "Fields contain values outside expected ranges"
        )

        assertThat(category.description, equalTo("Fields contain values outside expected ranges"))
    }
}
