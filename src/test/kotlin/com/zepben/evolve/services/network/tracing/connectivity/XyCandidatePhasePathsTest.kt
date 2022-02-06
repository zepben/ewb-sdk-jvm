/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class XyCandidatePhasePathsTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun usesKnownOverCandidates() {
        XyCandidatePhasePaths().apply {
            addKnown(X, A)
            addKnown(Y, B)

            addCandidates(X, listOf(B, B))
            addCandidates(Y, listOf(C, C))

            validatePaths(A, B)
        }
    }

    @Test
    internal fun handlesDuplicateKnown() {
        XyCandidatePhasePaths().apply {
            addKnown(X, B)
            addKnown(Y, B)

            validatePaths(B, NONE)
        }
    }

    @Test
    internal fun usesCandidatesIfUnknown() {
        XyCandidatePhasePaths().apply {
            addKnown(X, A)
            addCandidates(Y, listOf(B, B, C))

            validatePaths(A, B)
        }

        XyCandidatePhasePaths().apply {
            addKnown(X, B)
            addCandidates(Y, listOf(B, B, C))

            validatePaths(B, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A))
            addKnown(Y, B)

            validatePaths(A, B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A))
            addKnown(Y, A)

            validatePaths(NONE, A)
        }
    }

    @Test
    internal fun candidatesUseMostCommon() {
        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, A, A, B, B, C))
            addCandidates(Y, listOf(B, B, C, C, C))

            validatePaths(A, C)
        }
    }

    @Test
    internal fun candidatesUsePriorityWithDuplicateMostCommon() {
        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B, C, C))

            validatePaths(B, NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(Y, listOf(B, B, C, C))

            validatePaths(NONE, C)
        }
    }

    @Test
    internal fun handlesNoCandidates() {
        XyCandidatePhasePaths().apply {
            validatePaths(NONE, NONE)
        }

        XyCandidatePhasePaths().apply {
            addKnown(X, B)

            validatePaths(B, NONE)
        }

        XyCandidatePhasePaths().apply {
            addKnown(Y, B)

            validatePaths(NONE, B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(B))

            validatePaths(B, NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(Y, listOf(B))

            validatePaths(NONE, B)
        }
    }

    @Test
    internal fun usesMostCommonCandidate() {
        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B))
            addCandidates(Y, listOf(B, C, C))

            validatePaths(B, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B, B))
            addCandidates(Y, listOf(B, B, C))

            validatePaths(B, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, A, B, B, B))
            addCandidates(Y, listOf(B, B, B, B, C))

            validatePaths(A, B)
        }
    }

    @Test
    internal fun duplicateCandidateOccurrencesBetweenXyResolvedByPriority() {
        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, A, B, B, B))
            addCandidates(Y, listOf(B, B, B, C))

            validatePaths(A, B)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B, B, C, C))
            addCandidates(Y, listOf(B, B, B, C, C))

            validatePaths(B, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, C, C))
            addCandidates(Y, listOf(B, C, C))

            validatePaths(A, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(C, C))
            addCandidates(Y, listOf(B, C, C))

            validatePaths(C, NONE)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(B, B, C))
            addCandidates(Y, listOf(B, B, C))

            validatePaths(B, C)
        }

        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B))
            addCandidates(Y, listOf(B, B, C))

            validatePaths(B, C)
        }
    }

    @Test
    internal fun onlyCandidatesTakePriorityOverOccurrences() {
        XyCandidatePhasePaths().apply {
            addCandidates(X, listOf(A, B, B, B))
            addCandidates(Y, listOf(B, B))

            validatePaths(A, B)
        }
    }

    @Test
    internal fun onlyTracksXY() {
        XyCandidatePhasePaths().apply {
            SinglePhaseKind.values().forEach {
                if (it in PhaseCode.XY) {
                    addKnown(it, B)
                    addCandidates(it, listOf(B))
                } else {
                    expect { addKnown(it, B) }
                        .toThrow(IllegalArgumentException::class.java)
                        .withMessage("Unable to track phase $it, expected X or Y.")
                    expect { addCandidates(it, listOf(B)) }
                        .toThrow(IllegalArgumentException::class.java)
                        .withMessage("Unable to track phase $it, expected X or Y.")
                }
            }
        }
    }

    @Test
    internal fun validatesCandidatePhases() {
        XyCandidatePhasePaths().apply {
            SinglePhaseKind.values().forEach {
                when (it) {
                    A -> {
                        addCandidates(X, listOf(it))

                        expect { addCandidates(Y, listOf(it)) }
                            .toThrow(IllegalArgumentException::class.java)
                            .withMessage("Unable to use phase $it as a candidate, expected B or C.")
                    }
                    in PhaseCode.ABC -> {
                        addCandidates(X, listOf(it))
                        addCandidates(Y, listOf(it))
                    }
                    else -> {
                        expect { addCandidates(X, listOf(it)) }
                            .toThrow(IllegalArgumentException::class.java)
                            .withMessage("Unable to use phase $it as a candidate, expected A, B or C.")

                        expect { addCandidates(Y, listOf(it)) }
                            .toThrow(IllegalArgumentException::class.java)
                            .withMessage("Unable to use phase $it as a candidate, expected B or C.")
                    }
                }
            }
        }
    }

    private fun XyCandidatePhasePaths.validatePaths(expectedX: SinglePhaseKind, expectedY: SinglePhaseKind) {
        assertThat(calculatePaths().map { (k, v) -> k to v }, containsInAnyOrder(X to expectedX, Y to expectedY))
    }

}
