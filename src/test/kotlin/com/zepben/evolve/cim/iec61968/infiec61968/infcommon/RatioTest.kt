/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infcommon

import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RatioTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun accessorCoverage() {
        val ratio = Ratio(9.0, 6.0)

        assertThat(ratio.numerator, equalTo(9.0))
        assertThat(ratio.denominator, equalTo(6.0))
        assertThat(ratio.quotient, equalTo(1.5))
    }

    @Test
    internal fun throwsExceptionOnZeroDenominator() {
        val invalidRatio = Ratio(42.0, 0.0)
        expect { invalidRatio.quotient }
            .toThrow<IllegalArgumentException>()
            .withMessage("Cannot calculate the quotient of a Ratio with a denominator of zero.")
    }

}
