/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.metrics

import com.zepben.ewb.metrics.IngestionJob
import com.zepben.ewb.metrics.variants.VariantMetrics

open class MetricsService {
    val ingestionJobs: MutableList<IngestionJob> = mutableListOf()
    val variantMetrics: MutableList<VariantMetrics> = mutableListOf() // TODO: should this be a map and/or is this identified/unique by the id's + baseModelVersion?
}
