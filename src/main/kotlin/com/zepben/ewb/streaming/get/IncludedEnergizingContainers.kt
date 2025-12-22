/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

/**
 * Indicates which energizing contains should be included when fetching a container.
 */
enum class IncludedEnergizingContainers {

    /**
     * All energizing containers should be excluded.
     */
    NONE,

    /**
     * Energizing LV substations and feeders should be included.
     */
    FEEDERS,

    /**
     * Energizing LV substations, feeders and substations should be included.
     */
    SUBSTATIONS,

    /**
     * Energizing LV substations should be included.
     */
    LV_SUBSTATIONS

}
