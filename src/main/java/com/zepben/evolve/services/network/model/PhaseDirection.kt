/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.model

/***
 * Enumeration of directions of phase at a terminal.
 *
 * @property NONE No flow.
 * @property IN Flow into the terminal out of the connectivity node.
 * @property OUT Flow out of the terminal into the connectivity node.
 * @property BOTH Flow in both directions from the terminal and the connectivity node.
 */
enum class PhaseDirection(private val value: Int) {

    NONE(0),
    IN(1),
    OUT(2),
    BOTH(3);

    companion object {
        private val directionsByValues: Array<PhaseDirection> = enumValues<PhaseDirection>().sortedBy { it.value }.toTypedArray()

        @JvmStatic
        fun from(value: Int): PhaseDirection {
            return if (value <= 0 || value > 3) NONE else directionsByValues[value]
        }
    }

    fun value(): Int {
        return value
    }

    fun has(other: PhaseDirection): Boolean {
        return if (this == BOTH) other != NONE else this == other
    }

    operator fun plus(rhs: PhaseDirection): PhaseDirection {
        return directionsByValues[value or rhs.value]
    }

    operator fun minus(rhs: PhaseDirection): PhaseDirection {
        return directionsByValues[value - (value and rhs.value)]
    }
}
