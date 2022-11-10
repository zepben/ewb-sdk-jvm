/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infcommon

/**
 * Fraction specified explicitly with a numerator and denominator, which can be used to calculate the quotient.
 *
 * @property numerator The part of a fraction that is below the line and that functions as the divisor of the numerator.
 * @property denominator The part of a fraction that is above the line and signifies the number to be divided by the denominator.
 * @property quotient The result of dividing the numerator by the denominator. This is lazily evaluated and throws an [IllegalArgumentException]
 *                    upon evaluation if the denominator is zero.
 */
data class Ratio(
    val numerator: Double,
    val denominator: Double
) {

    @get:Throws(IllegalArgumentException::class)
    val quotient : Double by lazy {
        if (denominator == 0.0)
            throw IllegalArgumentException("Cannot calculate the quotient of a Ratio with a denominator of zero.")

        numerator / denominator
    }

}
