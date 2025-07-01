/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.ewb.cim.iec61970.base.wires.WindingConnection
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.services.network.ResistanceReactanceTest.Companion.validateResistanceReactance
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import com.zepben.testutils.mockito.DefaultAnswer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*

internal class TransformerEndInfoTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TransformerEndInfo().mRID, not(equalTo("")))
        assertThat(TransformerEndInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerEndInfo = TransformerEndInfo()

        assertThat(transformerEndInfo.connectionKind, equalTo(WindingConnection.UNKNOWN))
        assertThat(transformerEndInfo.emergencyS, nullValue())
        assertThat(transformerEndInfo.endNumber, equalTo(0))
        assertThat(transformerEndInfo.insulationU, nullValue())
        assertThat(transformerEndInfo.phaseAngleClock, nullValue())
        assertThat(transformerEndInfo.r, nullValue())
        assertThat(transformerEndInfo.ratedS, nullValue())
        assertThat(transformerEndInfo.ratedU, nullValue())
        assertThat(transformerEndInfo.shortTermS, nullValue())
        assertThat(transformerEndInfo.transformerTankInfo, nullValue())
        assertThat(transformerEndInfo.transformerStarImpedance, nullValue())
        assertThat(transformerEndInfo.energisedEndNoLoadTests, nullValue())
        assertThat(transformerEndInfo.energisedEndShortCircuitTests, nullValue())
        assertThat(transformerEndInfo.groundedEndShortCircuitTests, nullValue())
        assertThat(transformerEndInfo.openEndOpenCircuitTests, nullValue())
        assertThat(transformerEndInfo.energisedEndOpenCircuitTests, nullValue())

        transformerEndInfo.fillFields(NetworkService())

        assertThat(transformerEndInfo.connectionKind, equalTo(WindingConnection.D))
        assertThat(transformerEndInfo.emergencyS, equalTo(1))
        assertThat(transformerEndInfo.endNumber, equalTo(2))
        assertThat(transformerEndInfo.insulationU, equalTo(3))
        assertThat(transformerEndInfo.phaseAngleClock, equalTo(4))
        assertThat(transformerEndInfo.r, equalTo(5.0))
        assertThat(transformerEndInfo.ratedS, equalTo(6))
        assertThat(transformerEndInfo.ratedU, equalTo(7))
        assertThat(transformerEndInfo.shortTermS, equalTo(8))
        assertThat(transformerEndInfo.transformerTankInfo, notNullValue())
        assertThat(transformerEndInfo.transformerStarImpedance, notNullValue())
        assertThat(transformerEndInfo.energisedEndNoLoadTests, notNullValue())
        assertThat(transformerEndInfo.energisedEndShortCircuitTests, notNullValue())
        assertThat(transformerEndInfo.groundedEndShortCircuitTests, notNullValue())
        assertThat(transformerEndInfo.openEndOpenCircuitTests, notNullValue())
        assertThat(transformerEndInfo.energisedEndOpenCircuitTests, notNullValue())
    }

    @Test
    internal fun populatesResistanceReactanceOffEndStarImpedanceIfAvailable() {
        val info = spy(TransformerEndInfo().apply {
            transformerStarImpedance = TransformerStarImpedance().apply {
                r = 1.1
                x = 1.2
                r0 = 1.3
                x0 = 1.4
            }
        })

        validateResistanceReactance(info.resistanceReactance()!!, 1.1, 1.2, 1.3, 1.4)
        verify(info, never()).calculateResistanceReactanceFromTests()
    }

    @Test
    internal fun populatesResistanceReactanceOffEndInfoTestsIfAvailable() {
        val info = spy(TransformerEndInfo())
        doReturn(ResistanceReactance(2.1, 2.2, 2.3, 2.4)).`when`(info).calculateResistanceReactanceFromTests()

        validateResistanceReactance(info.resistanceReactance()!!, 2.1, 2.2, 2.3, 2.4)
        verify(info, times(1)).calculateResistanceReactanceFromTests()
    }

    @Test
    internal fun mergesResistanceReactanceIfRequired() {
        val info = spy(TransformerEndInfo().apply {
            transformerStarImpedance =
                mock(defaultAnswer = DefaultAnswer.of(ResistanceReactance::class.java, ResistanceReactance(1.1, null, null, null)))
        })
        doReturn(ResistanceReactance(null, 2.2, null, null)).`when`(info).calculateResistanceReactanceFromTests()

        validateResistanceReactance(info.resistanceReactance()!!, 1.1, 2.2, null, null)
        verify(info, times(1)).calculateResistanceReactanceFromTests()
    }

    @Test
    internal fun calculatesResistanceReactanceOffEndInfoTestsIfAvailable() {
        val info = TransformerEndInfo()

        assertThat(info.calculateResistanceReactanceFromTests(), nullValue())
    }

    @Test
    internal fun testCalculatesResistanceReactanceOfEndInfoTestsIfAvailable() {
        val lossTest = ShortCircuitTest().apply { loss = 2020180; voltage = 11.85 }
        val lossNoVoltageTest = ShortCircuitTest().apply { loss = 2020180 }
        val lossTestWithCurrent = ShortCircuitTest().apply { loss = 2020180; voltage = 11.85; current = 4075.0 }
        val ohmicTest = ShortCircuitTest().apply { voltageOhmicPart = 0.124; voltage = 11.85 }
        val ohmicNoVoltageTest = ShortCircuitTest().apply { voltageOhmicPart = 0.124 }
        val ohmicTestWithCurrent = ShortCircuitTest().apply { voltageOhmicPart = 0.124; voltage = 11.85; current = 4075.0 }
        val voltageOnlyTest = ShortCircuitTest().apply { voltage = 11.85 }

        // check via loss
        validateResistanceReactanceFromTest(400000, 1630000000, lossTest, lossTest, ResistanceReactance(0.12, 11.63, 0.12, 11.63))
        validateResistanceReactanceFromTest(null, 1630000000, lossTest, lossTest, null)
        validateResistanceReactanceFromTest(400000, null, lossTest, lossTest, null)
        validateResistanceReactanceFromTest(400000, 1630000000, null, lossTest, ResistanceReactance(null, null, 0.12, 11.63))
        validateResistanceReactanceFromTest(400000, 1630000000, lossTest, null, ResistanceReactance(0.12, 11.63, null, null))
        validateResistanceReactanceFromTest(400000, 1630000000, lossNoVoltageTest, lossNoVoltageTest, ResistanceReactance(0.12, null, 0.12, null))
        validateResistanceReactanceFromTest(400000, 2000000000, lossTestWithCurrent, lossTestWithCurrent, ResistanceReactance(0.12, 11.63, 0.12, 11.63))

        // check via ohmic part
        validateResistanceReactanceFromTest(400000, 1630000000, ohmicTest, ohmicTest, ResistanceReactance(0.12, 11.63, 0.12, 11.63))
        validateResistanceReactanceFromTest(null, 1630000000, ohmicTest, ohmicTest, null)
        validateResistanceReactanceFromTest(400000, null, ohmicTest, ohmicTest, null)
        validateResistanceReactanceFromTest(400000, 1630000000, null, ohmicTest, ResistanceReactance(null, null, 0.12, 11.63))
        validateResistanceReactanceFromTest(400000, 1630000000, ohmicTest, null, ResistanceReactance(0.12, 11.63, null, null))
        validateResistanceReactanceFromTest(400000, 1630000000, ohmicNoVoltageTest, ohmicNoVoltageTest, ResistanceReactance(0.12, null, 0.12, null))
        validateResistanceReactanceFromTest(400000, 2000000000, ohmicTestWithCurrent, ohmicTestWithCurrent, ResistanceReactance(0.12, 11.63, 0.12, 11.63))

        // check invalid
        validateResistanceReactanceFromTest(400000, 1630000000, voltageOnlyTest, voltageOnlyTest, null)
    }

    private fun validateResistanceReactanceFromTest(
        ratedU: Int?,
        ratedS: Int?,
        energisedTest: ShortCircuitTest?,
        groundedTest: ShortCircuitTest?,
        expectedRr: ResistanceReactance?
    ) {
        val info = TransformerEndInfo().apply {
            this.ratedU = ratedU
            this.ratedS = ratedS
            groundedEndShortCircuitTests = groundedTest
            energisedEndShortCircuitTests = energisedTest
        }

        expectedRr?.let {
            assertThat(info.calculateResistanceReactanceFromTests(), equalTo(expectedRr))
        } ?: assertThat(info.calculateResistanceReactanceFromTests(), nullValue())
    }
}
