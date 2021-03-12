/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61970.base.wires.WindingConnection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

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

        assertThat(transformerEndInfo.connectionKind, equalTo(WindingConnection.UNKNOWN_WINDING))
        assertThat(transformerEndInfo.emergencyS, equalTo(0))
        assertThat(transformerEndInfo.endNumber, equalTo(0))
        assertThat(transformerEndInfo.insulationU, equalTo(0))
        assertThat(transformerEndInfo.phaseAngleClock, equalTo(0))
        assertThat(transformerEndInfo.r, equalTo(0.0))
        assertThat(transformerEndInfo.ratedS, equalTo(0))
        assertThat(transformerEndInfo.ratedU, equalTo(0))
        assertThat(transformerEndInfo.shortTermS, equalTo(0))
        assertThat(transformerEndInfo.transformerTankInfo, nullValue())
        assertThat(transformerEndInfo.transformerStarImpedance, nullValue())

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
    }

}
