/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.utils.PrivateCollectionValidator
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
    internal fun curveData() {
        // NOTE: We test the curve data collection with "unordered" even though it is actually sorted since the "index" of this collection is the
        //       xValue which is a float and acts like a key. We will run a separate test to check the ordering and additional `addData` call.
        PrivateCollectionValidator.validateUnordered(
            { object : Curve() {} },
            { CurveData(it + 0.1f, it + 0.2f, it + 0.3f, it + 0.4f) },
            Curve::data,
            Curve::numData,
            Curve::getData,
            Curve::addData,
            Curve::removeData,
            Curve::clearData,
            CurveData::xValue
        )
    }

    @Test
    internal fun `add curveData by passing in the values and data is sorted by xValue in ascending order when retrieved`() {
        val curve = object : Curve() {}

        curve.addData(4f, 3f, 2f, 1f)
        curve.addData(2f, 1f, 2f, 3f)
        curve.addData(1f, 1f, 2f, 3f)
        curve.addData(3f, 1f, 2f, 3f)

        assertThat(curve.numData(), equalTo(4))

        curve.data.also { curveData ->
            repeat(4) { index ->
                assertThat("retrieved data should be sorted", curveData[index].xValue == index.inc().toFloat())
            }
        }
    }
}
