/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.variants

import java.util.*

/**
 * Variant metrics related to single variant
 */

data class VariantMetrics(
    val networkModelProjectId: UUID,
    val networkModelProjectStageId: UUID,
    val baseModelVersion: String,
    val metrics: List<VariantMetricEntry>
)
