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
 */
data class Ratio(

    /**
     * The part of a fraction that is below the line and that functions as the divisor of the numerator.
     */
    val numerator: Double,

    /**
     * The part of a fraction that is above the line and signifies the number to be divided by the denominator.
     */
    val denominator: Double
)
