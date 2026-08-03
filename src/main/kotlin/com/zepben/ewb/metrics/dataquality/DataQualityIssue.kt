/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.dataquality

import kotlinx.serialization.Serializable

/**
 * Represents a data quality issue raised against a network model.
 *
 * @property id Unique identifier for the issue.
 * @property status Current lifecycle status of the issue.
 * @property createdAt Timestamp when the issue was created.
 * @property createdBy Identifier of the user who created the issue.
 * @property updatedAt Timestamp when the issue was last updated.
 * @property updatedBy Identifier of the user who last updated the issue.
 * @property networkModelCreatedAgainst Identifier of the network model the issue was raised against.
 * @property networkModelResolvedAgainst Identifier of the network model the issue was resolved against, if any.
 * @property name Short name or title for the issue.
 * @property description Detailed description of the issue.
 * @property suggestedResolution Optional suggested steps to resolve the issue.
 * @property associatedAssets List of asset mRIDs associated with the issue.
 * @property annotationGeoJson GeoJSON string representing the geographic annotation for the issue.
 * @property categoryId Identifier of the category this issue belongs to.
 * @property externalReference Optional external reference such as a ticket ID.
 * @property severity Numeric severity level of the issue.
 * @property priority Optional numeric priority of the issue.
 * @property callouts List of callout IDs associated with the issue, or null if none.
 */
@Serializable
data class DataQualityIssue(
    val id: String,
    val status: DataQualityIssueStatus,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: String,
    val updatedBy: String,
    val networkModelCreatedAgainst: String,
    val networkModelResolvedAgainst: String? = null,
    val name: String,
    val description: String,
    val suggestedResolution: String? = null,
    val associatedAssets: List<String>,
    val annotationGeoJson: String,
    val categoryId: String,
    val externalReference: String? = null,
    val severity: Int,
    val priority: Int,
    val callouts: List<String>? = null,
)
