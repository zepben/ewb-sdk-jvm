/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CurveTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Curve() {}.mRID, not(equalTo("")))
        assertThat(object : Curve("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val curve = object : Curve() {}

        assertThat(curve.hasCurveData(), equalTo(false))

        curve.fillFields(NetworkService())

        assertThat(curve.hasCurveData(), equalTo(true))
    }

    @Test
    internal fun `add curveData`() {
        val curve = object : Curve() {}
        assertThat(curve.hasCurveData(), equalTo(false))

        curve.addCurveData(CurveData(1f, 1f, 2f, 3f))

        assertThat(curve.hasCurveData(), equalTo(true))
    }

    @Test
    internal fun `add curveData by passing in the values`() {
        val curve = object : Curve() {}

        curve.addCurveData(1f, 1f, 2f, 3f)

        assertThat(curve.hasCurveData(), equalTo(true))
    }

    @Test
    internal fun `get curveData with x values`() {
        val curve = object : Curve() {}
        curve.fillFields(NetworkService())

        assertThat(curve.getCurveData(1f), not(equalTo(null)))
    }

    @Test
    internal fun `remove curveData by removing the same curveData`() {
        val curveData = CurveData(1f, 1f)
        val curve = object : Curve() {}
        curve.fillFields(NetworkService())

        assertThat(curve.removeCurveData(curveData), equalTo(true))
        assertThat(curve.hasCurveData(), equalTo(false))
    }

    @Test
    internal fun `remove curveData with same x value`() {
        val curve = object : Curve() {}
        curve.fillFields(NetworkService())

        assertThat(curve.removeCurveData(1f), equalTo(true))
    }

    @Test
    internal fun `clear curveData`() {
        val curve = object : Curve() {}
        curve.fillFields(NetworkService())
        assertThat(curve.hasCurveData(), equalTo(true))

        curve.clearCurveDatas()
        assertThat(curve.hasCurveData(), equalTo(false))
    }
}
