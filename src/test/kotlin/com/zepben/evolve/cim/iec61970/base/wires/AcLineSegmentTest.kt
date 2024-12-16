/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AcLineSegmentTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(AcLineSegment().mRID, not(equalTo("")))
        assertThat(AcLineSegment("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val acLineSegment = AcLineSegment()
        val perLengthSequenceImpedance = PerLengthSequenceImpedance()
        val perLengthPhaseImpedance = PerLengthPhaseImpedance()

        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())
        assertThat(acLineSegment.perLengthImpedance, nullValue())

        acLineSegment.perLengthSequenceImpedance = perLengthSequenceImpedance

        assertThat(acLineSegment.perLengthSequenceImpedance, equalTo(perLengthSequenceImpedance))
        assertThat(acLineSegment.perLengthImpedance, equalTo(perLengthSequenceImpedance))
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())

        acLineSegment.perLengthPhaseImpedance = perLengthPhaseImpedance

        assertThat(acLineSegment.perLengthPhaseImpedance, equalTo(perLengthPhaseImpedance))
        assertThat(acLineSegment.perLengthImpedance, equalTo(perLengthPhaseImpedance))
        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())

        acLineSegment.perLengthPhaseImpedance = null

        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())
        assertThat(acLineSegment.perLengthImpedance, nullValue())
    }
}
