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
 * Represents a callout annotation attached to a [DataQualityIssue], following the
 * pattern established by ednar's Callout model.
 *
 * @property id Unique identifier for the callout.
 * @property dataQualityIssueId Identifier of the parent data quality issue.
 * @property longitude Geographic longitude where the callout stem points.
 * @property latitude Geographic latitude where the callout stem points.
 * @property positionX Screen x position of the callout box, as a percentage of screen width.
 * @property positionY Screen y position of the callout box, as a percentage of screen height.
 * @property width Width of the callout box in pixels.
 * @property height Height of the callout box in pixels.
 * @property label Short label for the callout, usually a single letter.
 * @property description Free text description of the callout.
 * @property colour Hex colour code for the callout.
 */
@Serializable
data class DataQualityIssueCallout(
    val id: String,
    val dataQualityIssueId: String,
    val longitude: Double,
    val latitude: Double,
    val positionX: Double,
    val positionY: Double,
    val width: Double,
    val height: Double,
    val label: String? = null,
    val description: String? = null,
    val colour: String,
)
