/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * The kind of regulation model. For example regulating voltage, reactive power, active power, etc.
 */
@Suppress("EnumEntryName")
enum class RegulatingControlModeKind {

    /**
     * Default, unknown.
     */
    UNKNOWN,

    /**
     * Voltage is specified.
     */
    voltage,

    /**
     * Active power is specified.
     */
    activePower,

    /**
     * Reactive power is specified.
     */
    reactivePower,

    /**
     * Current flow is specified.
     */
    currentFlow,

    /**
     * Admittance is specified.
     */
    admittance,

    /**
     * Control switches on/off by time of day. The times may change on the weekend, or in different seasons.
     */
    timeScheduled,

    /**
     * Control switches on/off based on the local temperature (i.e., a thermostat).
     */
    temperature,

    /**
     * Power factor is specified.
     */
    powerFactor

}
