/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class XyCandidatePhasePathsTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun usesKnownOverCandidates() {
        XyCandidatePhasePaths().apply {
            addKnown(SPK.X, SPK.A)
            addKnown(SPK.Y, SPK.B)

            addCandidates(SPK.X, listOf(SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.C, SPK.C))

            validatePaths(SPK.A, SPK.B)
        }
    }

    @Test
    internal fun handlesDuplicateKnown() {
        XyCandidatePhasePaths().apply {
            addKnown(SPK.X, SPK.B)
            addKnown(SPK.Y, SPK.B)

            validatePaths(SPK.B, SPK.NONE)
        }
    }

    @Test
    internal fun usesCandidatesIfUnknown() {
        XyCandidatePhasePaths().apply {
            addKnown(SPK.X, SPK.A)
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.A, SPK.B)
        }

        XyCandidatePhasePaths().apply {
            addKnown(SPK.X, SPK.B)
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A))
            addKnown(SPK.Y, SPK.B)

            validatePaths(SPK.A, SPK.B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A))
            addKnown(SPK.Y, SPK.A)

            validatePaths(SPK.NONE, SPK.A)
        }
    }

    @Test
    internal fun candidatesUseMostCommon() {
        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.A, SPK.A, SPK.B, SPK.B, SPK.C))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C, SPK.C, SPK.C))

            validatePaths(SPK.A, SPK.C)
        }
    }

    @Test
    internal fun candidatesUsePriorityWithDuplicateMostCommon() {
        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.B, SPK.NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.NONE, SPK.C)
        }
    }

    @Test
    internal fun handlesNoCandidates() {
        XyCandidatePhasePaths().apply {
            validatePaths(SPK.NONE, SPK.NONE)
        }

        XyCandidatePhasePaths().apply {
            addKnown(SPK.X, SPK.B)

            validatePaths(SPK.B, SPK.NONE)
        }

        XyCandidatePhasePaths().apply {
            addKnown(SPK.Y, SPK.B)

            validatePaths(SPK.NONE, SPK.B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.B))

            validatePaths(SPK.B, SPK.NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.Y, listOf(SPK.B))

            validatePaths(SPK.NONE, SPK.B)
        }
    }

    @Test
    internal fun usesMostCommonCandidate() {
        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.A, SPK.B, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.A, SPK.B)
        }
    }

    @Test
    internal fun duplicateCandidateOccurrencesBetweenXyResolvedByPriority() {
        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.A, SPK.B, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.A, SPK.B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B, SPK.B, SPK.C, SPK.C))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.C, SPK.C))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.A, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.C, SPK.C))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.C, SPK.C))

            validatePaths(SPK.C, SPK.NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.B, SPK.B, SPK.C))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B, SPK.C))

            validatePaths(SPK.B, SPK.C)
        }
    }

    @Test
    internal fun onlyCandidatesTakePriorityOverOccurrences() {
        XyCandidatePhasePaths().apply {
            addCandidates(SPK.X, listOf(SPK.A, SPK.B, SPK.B, SPK.B))
            addCandidates(SPK.Y, listOf(SPK.B, SPK.B))

            validatePaths(SPK.A, SPK.B)
        }
    }

    @Test
    internal fun onlyTracksXY() {
        XyCandidatePhasePaths().apply {
            SPK.values().forEach {
                if (it in PhaseCode.XY) {
                    addKnown(it, SPK.B)
                    addCandidates(it, listOf(SPK.B))
                } else {
                    expect { addKnown(it, SPK.B) }
                        .toThrow<IllegalArgumentException>()
                        .withMessage("Unable to track phase $it, expected X or Y.")
                    expect { addCandidates(it, listOf(SPK.B)) }
                        .toThrow<IllegalArgumentException>()
                        .withMessage("Unable to track phase $it, expected X or Y.")
                }
            }
        }
    }

    @Test
    internal fun validatesCandidatePhases() {
        XyCandidatePhasePaths().apply {
            SPK.values().forEach {
                when (it) {
                    SPK.A -> {
                        addCandidates(SPK.X, listOf(it))

                        expect { addCandidates(SPK.Y, listOf(it)) }
                            .toThrow<IllegalArgumentException>()
                            .withMessage("Unable to use phase $it as a candidate, expected B or C.")
                    }
                    in PhaseCode.ABC -> {
                        addCandidates(SPK.X, listOf(it))
                        addCandidates(SPK.Y, listOf(it))
                    }
                    else -> {
                        expect { addCandidates(SPK.X, listOf(it)) }
                            .toThrow<IllegalArgumentException>()
                            .withMessage("Unable to use phase $it as a candidate, expected A, B or C.")

                        expect { addCandidates(SPK.Y, listOf(it)) }
                            .toThrow<IllegalArgumentException>()
                            .withMessage("Unable to use phase $it as a candidate, expected B or C.")
                    }
                }
            }
        }
    }

    private fun XyCandidatePhasePaths.validatePaths(expectedX: SPK, expectedY: SPK) {
        assertThat(calculatePaths().map { (k, v) -> k to v }, containsInAnyOrder(SPK.X to expectedX, SPK.Y to expectedY))
    }

}
