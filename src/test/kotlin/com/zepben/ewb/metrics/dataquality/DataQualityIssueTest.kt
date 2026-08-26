/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.dataquality

import com.zepben.testutils.junit.SystemLogExtension
import kotlin.time.Instant
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*

internal class DataQualityIssueTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val uuid = UUID.randomUUID()
    private val categoryId = UUID.randomUUID()

    @Test
    internal fun constructorCoverage() {
        val epoch = Instant.fromEpochMilliseconds(0)
        val issue = DataQualityIssue(
            id = uuid.toString(),
            status = DataQualityIssueStatus.CREATED,
            createdAt = epoch,
            createdBy = String(),
            updatedAt = epoch,
            updatedBy = String(),
            networkModelCreatedAgainst = String(),
            name = "Bad connectivity",
            description = "Disconnected segment found",
            associatedAssets = listOf("asset-001"),
            annotationGeoJson = """{"type":"Point"}""",
            categoryId = categoryId.toString(),
            severity = 2,
            priority = 1
        )

        assertThat(issue.id, equalTo(uuid.toString()))
        assertThat(issue.status, equalTo(DataQualityIssueStatus.CREATED))
        assertThat(issue.createdAt, equalTo(epoch))
        assertThat(issue.createdBy, equalTo(String()))
        assertThat(issue.updatedAt, equalTo(epoch))
        assertThat(issue.updatedBy, equalTo(String()))
        assertThat(issue.networkModelCreatedAgainst, equalTo(String()))
        assertThat(issue.networkModelResolvedAgainst, nullValue())
        assertThat(issue.name, equalTo("Bad connectivity"))
        assertThat(issue.description, equalTo("Disconnected segment found"))
        assertThat(issue.suggestedResolution, nullValue())
        assertThat(issue.associatedAssets, equalTo(listOf("asset-001")))
        assertThat(issue.annotationGeoJson, equalTo("""{"type":"Point"}"""))
        assertThat(issue.categoryId, equalTo(categoryId.toString()))
        assertThat(issue.externalReference, nullValue())
        assertThat(issue.severity, equalTo(2))
        assertThat(issue.priority, equalTo(1))
        assertThat(issue.callouts, equalTo(emptyList()))
    }

    @Test
    internal fun `optional fields can be set`() {
        val epoch = Instant.fromEpochMilliseconds(0)
        val issue = DataQualityIssue(
            id = uuid.toString(),
            status = DataQualityIssueStatus.RESOLVED,
            createdAt = epoch,
            createdBy = String(),
            updatedAt = epoch,
            updatedBy = String(),
            networkModelCreatedAgainst = String(),
            networkModelResolvedAgainst = "2026-01-02",
            name = "Fixed issue",
            description = "Was missing data",
            suggestedResolution = "Re-import from source",
            associatedAssets = listOf("asset-003"),
            annotationGeoJson = """{"type":"Point"}""",
            categoryId = categoryId.toString(),
            externalReference = "NAR-100",
            severity = 3,
            priority = 2,
            callouts = listOf(
                DataQualityIssueCallout(
                    id = UUID.randomUUID().toString(), dataQualityIssueId = uuid.toString(),
                    longitude = 144.9, latitude = -37.8, positionX = 50.0, positionY = 25.0,
                    width = 200.0, height = 100.0, colour = "#FF0000"
                )
            )
        )

        assertThat(issue.networkModelResolvedAgainst, equalTo("2026-01-02"))
        assertThat(issue.suggestedResolution, equalTo("Re-import from source"))
        assertThat(issue.externalReference, equalTo("NAR-100"))
        assertThat(issue.callouts, hasSize(1))
    }
}
