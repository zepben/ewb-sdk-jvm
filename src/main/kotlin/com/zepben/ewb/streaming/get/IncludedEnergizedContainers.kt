/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

/**
 * Indicates which energized contains should be included when fetching a container.
 */
enum class IncludedEnergizedContainers {

    /**
     * All energized containers should be excluded.
     */
    NONE,

    /**
     * Energized HV feeders should be included.
     */
    FEEDERS,

    /**
     * Energized HV feeders, LV substations, and LV feeders should be included.
     */
    LV_FEEDERS,

    /**
     * Energized HV feeders and LV substations should be included.
     */
    LV_SUBSTATIONS
}
