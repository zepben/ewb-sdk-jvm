/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AcLineSegmentPhaseTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(AcLineSegmentPhase("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val acLineSegmentPhase = AcLineSegmentPhase(generateId())
        val acLineSegment = AcLineSegment(generateId())

        assertThat(acLineSegmentPhase.acLineSegment, nullValue())
        assertThat(acLineSegmentPhase.phase, equalTo(SinglePhaseKind.X))
        assertThat(acLineSegmentPhase.sequenceNumber, nullValue())
        assertThat(acLineSegmentPhase.assetInfo, nullValue())

        val wireInfo = OverheadWireInfo(generateId())
        acLineSegmentPhase.apply {
            this.acLineSegment = acLineSegment
            phase = SinglePhaseKind.A
            sequenceNumber = 1
            assetInfo = wireInfo
        }

        assertThat(acLineSegmentPhase.acLineSegment, equalTo(acLineSegment))
        assertThat(acLineSegmentPhase.phase, equalTo(SinglePhaseKind.A))
        assertThat(acLineSegmentPhase.sequenceNumber, equalTo(1))
        assertThat(acLineSegmentPhase.assetInfo, equalTo(wireInfo))
    }

}
