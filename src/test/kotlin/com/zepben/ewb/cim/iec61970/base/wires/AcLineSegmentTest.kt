/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

    @Test
    internal fun rejectsDuplicatePhase() {
        val acls = AcLineSegment(generateId())
        val phase1 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.A }
        val phase2 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.A }

        acls.addPhase(phase1)
        expect { acls.addPhase(phase2) }
            .toThrow<IllegalArgumentException>()
            .withMessage("Could not add ${phase2.typeNameAndMRID()} to ${acls.typeNameAndMRID()} as ${phase2.phase} was already present in the phases collection for this conductor. Ensure you are not adding duplicate phases.")
    }

    @Test
    internal fun supportsDifferentPhase() {
        val acls = AcLineSegment(generateId())
        val phase1 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.A }
        val phase2 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.B }
        val phase3 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.C }
        val phase4 = AcLineSegmentPhase(generateId()).apply { phase = SinglePhaseKind.N }

        acls.addPhase(phase1)
        acls.addPhase(phase2)
        acls.addPhase(phase3)
        acls.addPhase(phase4)

        assertThat(acls.numPhases(), equalTo(4))
        assertThat(acls.phases, contains(phase1, phase2, phase3, phase4))
    }

    @Test
    internal fun assignsAcLineSegmentToPhaseIfMissing() {
        val acls = AcLineSegment(generateId())
        val phase = AcLineSegmentPhase(generateId())

        acls.addPhase(phase)
        assertThat(phase.acLineSegment, equalTo(acls))
    }

    @Test
    internal fun rejectsAcLineSegmentPhaseWithWrongAcLineSegment() {
        val acls1 = AcLineSegment(generateId())
        val acls2 = AcLineSegment(generateId())
        val phase = AcLineSegmentPhase(generateId()).apply { acLineSegment = acls2 }

        expect { acls1.addPhase(phase) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${phase.typeNameAndMRID()} `acLineSegment` property references ${acls2.typeNameAndMRID()}, expected ${acls1.typeNameAndMRID()}.")
    }

    @Test
    internal fun acLineSegmentPhases() {
        PrivateCollectionValidator.validateUnordered(
            ::AcLineSegment,
            ::AcLineSegmentPhase,
            AcLineSegment::phases,
            AcLineSegment::numPhases,
            AcLineSegment::getPhase,
            AcLineSegment::addPhase,
            AcLineSegment::removePhase,
            AcLineSegment::clearPhases
        )
    }


}
