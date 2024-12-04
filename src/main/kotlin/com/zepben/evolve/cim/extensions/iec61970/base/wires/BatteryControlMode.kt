/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.extensions.iec61970.base.wires

import com.zepben.evolve.cim.extensions.ZBEX

/**
 * Mode of operation for the dispatch (charging/discharging) function of BatteryControl.
 */
@ZBEX
@Suppress("EnumEntryName")
enum class BatteryControlMode {
    /**
     * Unknown control mode.
     */
    UNKNOWN,

    /**
     * This mode directs the BatteryUnit to discharge as needed to maintain the power level of the monitored element within a defined range
     * (specified by targetDeadband) or to keep it at or below the value specified by maxAllowedTargetValue. This mode helps prevent power spikes by discharging
     * the BatteryUnit to manage peak demand effectively.
     */
    peakShaveDischarge,

    /**
     * This mode directs the BatteryUnit to discharge as needed to maintain the current (in amps) at a monitored element below a specified target. Similar
     * to peakShaveDischarge, this mode aims to reduce demand peaks, focusing specifically on current levels to manage capacity and enhance system stability where
     * current control is essential.
     */
    currentPeakShaveDischarge,

    /**
     * The control is triggered by time and resets the targetValue property to the present monitored element power.
     */
    following,

    /**
     * This is essentially the opposite of peakShave modes. The fleet is dispatched to keep the power in the monitored terminal at or above targetValue.
     */
    support,

    /**
     * In Schedule mode, a trapezoidal-shaped discharge schedule is specified through Tup (up ramp duration), TFlat (at duration),
     * and Tdn (down ramp duration) properties.
     */
    schedule,

    /**
     * This mode directs the BatteryUnit to initiate charging when the power level at a monitored element falls below a specified threshold (minAllowedTargetValue).
     * This mode supports demand leveling by charging the BatteryUnit during low-demand periods, optimizing overall power management.
     */
    peakShaveCharge,

    /**
     * This mode initiates charging when the current (in Amps) at a monitored element falls below a specified threshold (minAllowedTargetValue).
     * Similar to peakShaveCharge, this mode aims to manage demand effectively, but it operates based on current levels rather than power, providing finer
     * control in systems where current management is prioritized.
     */
    currentPeakShaveCharge,

    /**
     * In Time mode all storage elements are set to discharge when in the course of simulation the time of day passes the specified hour of day by the
     * TimeDisChargeTrigger property (hour is a decimal value, e.g., 10.5 = 1030)
     */
    time,

    /**
     * In this mode both discharging and charging precisely follow a per-unit curve.
     */
    profile,

}
