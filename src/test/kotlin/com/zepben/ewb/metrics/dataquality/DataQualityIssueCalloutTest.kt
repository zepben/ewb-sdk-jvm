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

internal class DataQualityIssueCalloutTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val uuid = UUID.randomUUID()
    private val issueId = UUID.randomUUID()

    @Test
    internal fun constructorCoverage() {
        val callout = DataQualityIssueCallout(
            id = uuid.toString(),
            dataQualityIssueId = issueId.toString(),
            longitude = 144.9,
            latitude = -37.8,
            positionX = 50.0,
            positionY = 25.0,
            width = 200.0,
            height = 100.0,
            colour = "#FF0000"
        )

        assertThat(callout.id, equalTo(uuid.toString()))
        assertThat(callout.dataQualityIssueId, equalTo(issueId.toString()))
        assertThat(callout.longitude, equalTo(144.9))
        assertThat(callout.latitude, equalTo(-37.8))
        assertThat(callout.positionX, equalTo(50.0))
        assertThat(callout.positionY, equalTo(25.0))
        assertThat(callout.width, equalTo(200.0))
        assertThat(callout.height, equalTo(100.0))
        assertThat(callout.label, nullValue())
        assertThat(callout.description, nullValue())
        assertThat(callout.colour, equalTo("#FF0000"))
    }

    @Test
    internal fun `optional fields can be set`() {
        val callout = DataQualityIssueCallout(
            id = uuid.toString(),
            dataQualityIssueId = issueId.toString(),
            longitude = 145.0,
            latitude = -37.7,
            positionX = 10.0,
            positionY = 20.0,
            width = 150.0,
            height = 80.0,
            label = "A",
            description = "Check this area",
            colour = "#00FF00"
        )

        assertThat(callout.label, equalTo("A"))
        assertThat(callout.description, equalTo("Check this area"))
    }
}
