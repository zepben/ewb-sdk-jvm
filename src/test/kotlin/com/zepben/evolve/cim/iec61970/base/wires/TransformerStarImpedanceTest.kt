/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TransformerStarImpedanceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TransformerStarImpedance().mRID, not(equalTo("")))
        assertThat(TransformerStarImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerStarImpedance = TransformerStarImpedance()

        assertThat(transformerStarImpedance.r, notANumber())
        assertThat(transformerStarImpedance.r0, notANumber())
        assertThat(transformerStarImpedance.x, notANumber())
        assertThat(transformerStarImpedance.x0, notANumber())
        assertThat(transformerStarImpedance.transformerEndInfo, nullValue())

        transformerStarImpedance.fillFields(NetworkService())

        assertThat(transformerStarImpedance.r, equalTo(1.0))
        assertThat(transformerStarImpedance.r0, equalTo(2.0))
        assertThat(transformerStarImpedance.x, equalTo(3.0))
        assertThat(transformerStarImpedance.x0, equalTo(4.0))
        assertThat(transformerStarImpedance.transformerEndInfo, notNullValue())
    }

}
