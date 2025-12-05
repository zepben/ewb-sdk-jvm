/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AcLineSegmentTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(AcLineSegment("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val acLineSegment = AcLineSegment(generateId())
        val perLengthSequenceImpedance = PerLengthSequenceImpedance(generateId())
        val perLengthPhaseImpedance = PerLengthPhaseImpedance(generateId())

        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())
        assertThat(acLineSegment.perLengthImpedance, nullValue())

        acLineSegment.perLengthSequenceImpedance = perLengthSequenceImpedance

        assertThat(acLineSegment.perLengthSequenceImpedance, equalTo(perLengthSequenceImpedance))
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())
        assertThat(acLineSegment.perLengthImpedance, equalTo(perLengthSequenceImpedance))

        acLineSegment.perLengthPhaseImpedance = perLengthPhaseImpedance

        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())
        assertThat(acLineSegment.perLengthPhaseImpedance, equalTo(perLengthPhaseImpedance))
        assertThat(acLineSegment.perLengthImpedance, equalTo(perLengthPhaseImpedance))

        acLineSegment.perLengthImpedance = null

        assertThat(acLineSegment.perLengthSequenceImpedance, nullValue())
        assertThat(acLineSegment.perLengthPhaseImpedance, nullValue())
        assertThat(acLineSegment.perLengthImpedance, nullValue())
    }

    @Test
    internal fun cuts() {
        PrivateCollectionValidator.validateUnordered(
            { id -> AcLineSegment(id) },
            { id -> Cut(id) },
            AcLineSegment::cuts,
            AcLineSegment::numCuts,
            AcLineSegment::getCut,
            AcLineSegment::addCut,
            AcLineSegment::removeCut,
            AcLineSegment::clearCuts,
        )
    }

    @Test
    internal fun clamps() {
        PrivateCollectionValidator.validateUnordered(
            { id -> AcLineSegment(id) },
            { id -> Clamp(id) },
            AcLineSegment::clamps,
            AcLineSegment::numClamps,
            AcLineSegment::getClamp,
            AcLineSegment::addClamp,
            AcLineSegment::removeClamp,
            AcLineSegment::clearClamps,
        )
    }

}
