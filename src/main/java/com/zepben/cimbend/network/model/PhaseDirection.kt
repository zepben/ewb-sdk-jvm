/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network.model

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
