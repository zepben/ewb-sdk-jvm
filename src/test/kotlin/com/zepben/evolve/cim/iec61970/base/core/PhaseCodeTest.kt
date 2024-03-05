/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.validateEnum
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode

internal class PhaseCodeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun validateVsPb() {
        validateEnum(PhaseCode.entries, PBPhaseCode.entries)
    }

    @Test
    internal fun singlePhases() {
        PhaseCode.entries.forEach { phaseCode ->
            if (phaseCode === PhaseCode.NONE)
                assertThat(phaseCode.singlePhases, contains(SinglePhaseKind.NONE))
            else {
                // We need to strip the 's' off secondary phases for the following checks to work correctly.
                assertThat(phaseCode.singlePhases, hasSize(phaseCode.name.trimStart('s').length))

                val singlePhases = phaseCode.singlePhases
                    .asSequence()
                    .map { it.name.trimStart('s') }
                    .toSet()

                val namePhases = phaseCode.name.trimStart('s').toCharArray().map { it.toString() }.toSet()

                assertThat(singlePhases.containsAll(namePhases), equalTo(true))
            }
        }
    }

    @Test
    internal fun numPhases() {
        PhaseCode.entries.forEach { phaseCode ->
            if (phaseCode === PhaseCode.NONE)
                assertThat(phaseCode.numPhases(), equalTo(0))
            else
                assertThat(phaseCode.numPhases(), equalTo(phaseCode.singlePhases.size))
        }
    }

    @Test
    internal fun withoutNeutral() {
        assertThat(PhaseCode.ABCN.withoutNeutral(), equalTo(PhaseCode.ABC))
        assertThat(PhaseCode.ABC.withoutNeutral(), equalTo(PhaseCode.ABC))
        assertThat(PhaseCode.BCN.withoutNeutral(), equalTo(PhaseCode.BC))
        assertThat(PhaseCode.XYN.withoutNeutral(), equalTo(PhaseCode.XY))
        assertThat(PhaseCode.NONE.withoutNeutral(), equalTo(PhaseCode.NONE))
    }

    @Test
    internal fun fromSinglePhases() {
        PhaseCode.entries
            .asSequence()
            .forEach {
                assertThat(PhaseCode.fromSinglePhases(it.singlePhases), equalTo(it))
            }

        assertThat(PhaseCode.fromSinglePhases(listOf(SinglePhaseKind.A, SinglePhaseKind.B)), equalTo(PhaseCode.AB))
        assertThat(PhaseCode.fromSinglePhases(setOf(SinglePhaseKind.B, SinglePhaseKind.C)), equalTo(PhaseCode.BC))
    }

    @Test
    internal fun contains() {
        assertThat("Contains A", PhaseCode.ABCN.contains(SinglePhaseKind.A))
        assertThat("Contains B", PhaseCode.ABCN.contains(SinglePhaseKind.B))
        assertThat("Contains C", PhaseCode.ABCN.contains(SinglePhaseKind.C))
        assertThat("Contains N", PhaseCode.ABCN.contains(SinglePhaseKind.N))
        assertThat("Does not contain X", !PhaseCode.ABCN.contains(SinglePhaseKind.X))
        assertThat("Does not contain Y", !PhaseCode.ABCN.contains(SinglePhaseKind.Y))

        assertThat("Does not contain A", !PhaseCode.XY.contains(SinglePhaseKind.A))
        assertThat("Does not contain B", !PhaseCode.XY.contains(SinglePhaseKind.B))
        assertThat("Does not contain C", !PhaseCode.XY.contains(SinglePhaseKind.C))
        assertThat("Does not contain N", !PhaseCode.XY.contains(SinglePhaseKind.N))
        assertThat("Contains X", PhaseCode.XY.contains(SinglePhaseKind.X))
        assertThat("Contains Y", PhaseCode.XY.contains(SinglePhaseKind.Y))
    }

    @Test
    internal fun plus() {
        assertThat(PhaseCode.A + SinglePhaseKind.B, equalTo(PhaseCode.AB))
        assertThat(PhaseCode.BC + PhaseCode.AN, equalTo(PhaseCode.ABCN))
        assertThat(PhaseCode.X + SinglePhaseKind.Y, equalTo(PhaseCode.XY))
        assertThat(PhaseCode.N + PhaseCode.XY, equalTo(PhaseCode.XYN))

        // Can add existing phases.
        assertThat(PhaseCode.ABCN + SinglePhaseKind.A, equalTo(PhaseCode.ABCN))
        assertThat(PhaseCode.ABCN + SinglePhaseKind.B, equalTo(PhaseCode.ABCN))
        assertThat(PhaseCode.A + PhaseCode.ABCN, equalTo(PhaseCode.ABCN))

        // Returns NONE for invalid additions.
        assertThat(PhaseCode.ABCN + SinglePhaseKind.X, equalTo(PhaseCode.NONE))
        assertThat(PhaseCode.ABCN + PhaseCode.X, equalTo(PhaseCode.NONE))
    }

    @Test
    internal fun minus() {
        assertThat(PhaseCode.ABCN - SinglePhaseKind.B, equalTo(PhaseCode.ACN))
        assertThat(PhaseCode.ABCN - PhaseCode.AN, equalTo(PhaseCode.BC))
        assertThat(PhaseCode.BC - SinglePhaseKind.C, equalTo(PhaseCode.B))
        assertThat(PhaseCode.XY - PhaseCode.X, equalTo(PhaseCode.Y))

        assertThat(PhaseCode.X - SinglePhaseKind.Y, equalTo(PhaseCode.X))
        assertThat(PhaseCode.AB - PhaseCode.C, equalTo(PhaseCode.AB))

        assertThat(PhaseCode.ABCN - PhaseCode.ABCN, equalTo(PhaseCode.NONE))
    }

    @Test
    internal fun singlePhaseHelpers() {
        assertThat(PhaseCode.ABC.map { "$it-$it" }.toList(), contains("A-A", "B-B", "C-C"))

        assertThat("any uses single phases", PhaseCode.ABC.any { it == SinglePhaseKind.A })
        assertThat("any uses single phases", PhaseCode.ABC.any { it == SinglePhaseKind.B })
        assertThat("any uses single phases", PhaseCode.ABC.any { it == SinglePhaseKind.C })
        assertThat("any uses single phases", !PhaseCode.ABC.any { it == SinglePhaseKind.N })

        assertThat("all uses single phases", !PhaseCode.ABC.all { it != SinglePhaseKind.A })
        assertThat("all uses single phases", !PhaseCode.ABC.all { it != SinglePhaseKind.B })
        assertThat("all uses single phases", !PhaseCode.ABC.all { it != SinglePhaseKind.C })
        assertThat("all uses single phases", PhaseCode.ABC.all { it != SinglePhaseKind.N })

    }

    @Test
    internal fun forEach() {
        val phases = mutableListOf<SinglePhaseKind>()
        PhaseCode.ABCN.forEach { phases.add(it) }

        assertThat(phases, contains(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N))
    }

}
