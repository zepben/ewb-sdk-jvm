/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerTankInfo
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.evolve.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ResistanceReactanceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun readsOffEndIfAvailable() {
        val end = PowerTransformerEnd().apply {
            r = 1.1
            r0 = 1.2
            x = 1.3
            x0 = 1.4
        }

        validateResistanceReactance(end.resistanceReactance(), 1.1, 1.2, 1.3, 1.4)
    }

    @Test
    internal fun readsOffEndStarImpedanceIfAvailable() {
        val end = PowerTransformerEnd().apply {
            starImpedance = TransformerStarImpedance().apply {
                r = 2.1
                r0 = 2.2
                x = 2.3
                x0 = 2.4
            }
        }

        validateResistanceReactance(end.resistanceReactance(), 2.1, 2.2, 2.3, 2.4)
    }

    @Test
    internal fun readsOffEndInfoStarImpedanceIfAvailable() {
        val end = PowerTransformerEnd().apply {
            endNumber = 1
            powerTransformer = PowerTransformer().apply {
                assetInfo = PowerTransformerInfo().apply {
                    addTransformerTankInfo(TransformerTankInfo().apply {
                        addTransformerEndInfo(TransformerEndInfo().apply {
                            endNumber = 1
                            transformerStarImpedance = TransformerStarImpedance().apply {
                                r = 3.1
                                r0 = 3.2
                                x = 3.3
                                x0 = 3.4
                            }
                        })
                    })
                }
            }
        }

        validateResistanceReactance(end.resistanceReactance(), 3.1, 3.2, 3.3, 3.4)
    }

    @Test
    internal fun calculatesOffEndInfoTestsIfAvailable() {
        val end = PowerTransformerEnd().apply {
            endNumber = 1
            powerTransformer = PowerTransformer().apply {
                assetInfo = PowerTransformerInfo().apply {
                    addTransformerTankInfo(TransformerTankInfo().apply {
                        addTransformerEndInfo(TransformerEndInfo().apply {
                            endNumber = 1
                            // https://app.clickup.com/t/6929263/EWB-615 Add test classes with 4.1, 4.2, 4.3, 4.4
                        })
                    })
                }
            }
        }

        // https://app.clickup.com/t/6929263/EWB-615 Check for 4.1, 4.2, 4.3, 4.4
        validateResistanceReactance(end.resistanceReactance(), Double.NaN, Double.NaN, Double.NaN, Double.NaN)
    }

    @Test
    internal fun mergesIncompleteWithPrecedence() {
        val end = PowerTransformerEnd().apply {
            r = 1.1
            endNumber = 1
            starImpedance = TransformerStarImpedance().apply {
                r0 = 2.2
            }
            powerTransformer = PowerTransformer().apply {
                assetInfo = PowerTransformerInfo().apply {
                    addTransformerTankInfo(TransformerTankInfo().apply {
                        addTransformerEndInfo(TransformerEndInfo().apply {
                            endNumber = 1
                            transformerStarImpedance = TransformerStarImpedance().apply {
                                x = 3.3
                            }
                            // https://app.clickup.com/t/6929263/EWB-615 Add test classes with 4.4
                        })
                    })
                }
            }
        }

        // https://app.clickup.com/t/6929263/EWB-615 Check for 4.4
        validateResistanceReactance(end.resistanceReactance(), 1.1, 2.2, 3.3, Double.NaN)
    }

    private fun validateResistanceReactance(rr: ResistanceReactance, r: Double, r0: Double, x: Double, x0: Double) {
        assertThat(rr.r, equalTo(r))
        assertThat(rr.r0, equalTo(r0))
        assertThat(rr.x, equalTo(x))
        assertThat(rr.x0, equalTo(x0))
    }

}
