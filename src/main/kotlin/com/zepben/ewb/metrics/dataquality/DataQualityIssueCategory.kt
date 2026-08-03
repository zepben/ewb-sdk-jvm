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
 * Represents a category used to classify data quality issues.
 *
 * @property id Unique identifier for the category.
 * @property name Name of the category.
 * @property description Optional description providing additional details about the category.
 */
@Serializable
data class DataQualityIssueCategory(
    val id: String,
    val name: String,
    val description: String? = null,
)
