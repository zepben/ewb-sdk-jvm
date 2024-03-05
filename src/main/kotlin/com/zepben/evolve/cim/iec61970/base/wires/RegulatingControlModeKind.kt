/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * The kind of regulation model. For example regulating voltage, reactive power, active power, etc.
 *
 * @property UNKNOWN_CONTROL_MODE Default, unknown.
 * @property voltage Voltage is specified.
 * @property activePower Active power is specified.
 * @property reactivePower Reactive power is specified.
 * @property currentFlow Current flow is specified.
 * @property admittance Admittance is specified.
 * @property timeScheduled Control switches on/off by time of day. The times may change on the weekend, or in different seasons.
 * @property temperature Control switches on/off based on the local temperature (i.e., a thermostat).
 * @property powerFactor Power factor is specified.
 */
@Suppress("EnumEntryName")
enum class RegulatingControlModeKind {

    UNKNOWN_CONTROL_MODE,
    voltage,
    activePower,
    reactivePower,
    currentFlow,
    admittance,
    timeScheduled,
    temperature,
    powerFactor

}
