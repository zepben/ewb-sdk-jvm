/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

/**
 * Battery state.
 */
@Suppress("EnumEntryName")
enum class BatteryStateKind {

    /**
     * Unknown.
     */
    UNKNOWN,

    /**
     * Stored energy is decreasing.
     */
    discharging,

    /**
     * Unable to charge, and not discharging.
     */
    full,

    /**
     * Neither charging nor discharging, but able to do so.
     */
    waiting,

    /**
     * Stored energy is increasing.
     */
    charging,

    /**
     * Unable to discharge, and not charging.
     */
    empty

}
