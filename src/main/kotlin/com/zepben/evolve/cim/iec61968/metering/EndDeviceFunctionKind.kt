/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.metering

/**
 * Kind of end device function.
 */
@Suppress("EnumEntryName")
enum class EndDeviceFunctionKind {

    /**
     * Unknown function kind.
     */
    UNKNOWN,

    /**
     * Autonomous application of daylight saving time (DST).
     */
    autonomousDst,

    /**
     * Demand response functions.
     */
    demandResponse,

    /**
     * Electricity metering.
     */
    electricMetering,

    /**
     * Presentation of metered values to a user or another system (always a function of a meter, but might not be supported by a load control unit).
     */
    metrology,

    /**
     * On-request reads.
     */
    onRequestRead,

    /**
     * Reporting historical power interruption data.
     */
    outageHistory,

    /**
     * Support for one or more relays that may be programmable in the meter (and tied to TOU, time pulse, load control or other functions).
     */
    relaysProgramming,

    /**
     * Detection and monitoring of reverse flow.
     */
    reverseFlow

}
