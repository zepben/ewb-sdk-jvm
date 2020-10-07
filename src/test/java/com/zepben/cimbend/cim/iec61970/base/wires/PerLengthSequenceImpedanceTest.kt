/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PerLengthSequenceImpedanceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PerLengthSequenceImpedance().mRID, not(equalTo("")))
        assertThat(PerLengthSequenceImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val perLengthSequenceImpedance = PerLengthSequenceImpedance()

        assertThat(perLengthSequenceImpedance.r, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.x, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.bch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.gch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.r0, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.x0, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.b0ch, equalTo(0.0))
        assertThat(perLengthSequenceImpedance.g0ch, equalTo(0.0))

        perLengthSequenceImpedance.r = 1.0
        perLengthSequenceImpedance.x = 2.0
        perLengthSequenceImpedance.bch = 3.0
        perLengthSequenceImpedance.gch = 4.0
        perLengthSequenceImpedance.r0 = 5.0
        perLengthSequenceImpedance.x0 = 6.0
        perLengthSequenceImpedance.b0ch = 7.0
        perLengthSequenceImpedance.g0ch = 8.0

        assertThat(perLengthSequenceImpedance.r, equalTo(1.0))
        assertThat(perLengthSequenceImpedance.x, equalTo(2.0))
        assertThat(perLengthSequenceImpedance.bch, equalTo(3.0))
        assertThat(perLengthSequenceImpedance.gch, equalTo(4.0))
        assertThat(perLengthSequenceImpedance.r0, equalTo(5.0))
        assertThat(perLengthSequenceImpedance.x0, equalTo(6.0))
        assertThat(perLengthSequenceImpedance.b0ch, equalTo(7.0))
        assertThat(perLengthSequenceImpedance.g0ch, equalTo(8.0))
    }
}
