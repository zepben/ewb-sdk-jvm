/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

/***
 * Enumeration of directions along a feeder at a terminal.
 *
 * @property NONE The terminal is not on a feeder.
 * @property UPSTREAM The terminal can be used to trace upstream towards the feeder head.
 * @property DOWNSTREAM The terminal can be used to trace downstream away from the feeder head.
 * @property BOTH The terminal is part of a loop on the feeder and tracing in either direction will allow you
 *                to trace upstream towards the feeder head, or downstream away from the feeder head.
 * @property CONNECTOR The terminal belongs to a Connector that is modelled with only a single terminal.
 *                     CONNECTOR will match direction UPSTREAM, DOWNSTREAM, and BOTH, however it exists to
 *                     differentiate it from BOTH which is used to indicate loops on the feeder. This however
 *                     means you can't tell if a terminal with CONNECTOR is part of a loop directly, you need
 *                     to check its connected terminals and check for BOTH to determine if it is in a loop.
 */
enum class FeederDirection(private val value: Int) {

    NONE(0),
    UPSTREAM(1),
    DOWNSTREAM(2),
    BOTH(3),
    CONNECTOR(4);

    companion object {
        private val directionsByValues: Array<FeederDirection> = enumValues<FeederDirection>().sortedBy { it.value }.toTypedArray()

        @JvmStatic
        fun from(value: Int): FeederDirection = if (value <= 0 || value > 3) NONE else directionsByValues[value]
    }

    fun value(): Int = value

    operator fun contains(other: FeederDirection): Boolean = if (this == BOTH || this == CONNECTOR) other != NONE else this == other

    operator fun plus(rhs: FeederDirection): FeederDirection = if (this == CONNECTOR || rhs == CONNECTOR) CONNECTOR else directionsByValues[value or rhs.value]

    operator fun minus(rhs: FeederDirection): FeederDirection = directionsByValues[value - (value and rhs.value)]

    operator fun not(): FeederDirection =
        when (this) {
            UPSTREAM -> DOWNSTREAM
            DOWNSTREAM -> UPSTREAM
            BOTH, CONNECTOR -> NONE
            NONE -> BOTH
        }

}
