/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.variants

import kotlinx.serialization.Serializable

/**
 * Variant metrics related to single variant
 */

@Serializable
data class VariantMetrics(
    val networkModelProjectId: String,
    val networkModelProjectStageId: String,
    val baseModelVersion: String,
    val changeSetId: String,
    val metrics: List<VariantMetricEntry>
)
