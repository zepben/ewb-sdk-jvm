/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * Static VAr Compensator control mode.
 */
@Suppress("EnumEntryName")
enum class SVCControlMode {

    /**
     * [ZBEX] Unknown control.
     */
    @ZBEX
    UNKNOWN,

    /**
     * Reactive power control.
     */
    reactivePower,

    /**
     * Voltage control.
     */
    voltage

}
