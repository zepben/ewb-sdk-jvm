/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CurveTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

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

        assertThat(curve.data.map { it.xValue }, contains(1f, 2f, 3f, 4f))
    }

    @Test
    internal fun `can't add duplicate curve data`() {
        val curve = object : Curve() {}

        curve.addData(1f, 1f, 2f, 3f)
        curve.validateDuplicateError(1f) { addData(1f, 1.1f, 2.1f, 3.1f) }
        curve.validateDuplicateError(1f) { addData(CurveData(1f, 1.2f, 2.2f, 3.2f)) }

        curve.addData(CurveData(2f, 1f, 2f, 3f))
        curve.validateDuplicateError(2f) { addData(2f, 1.1f, 2.1f, 3.1f) }
        curve.validateDuplicateError(2f) { addData(CurveData(2f, 1.2f, 2.2f, 3.2f)) }
    }

    private fun Curve.validateDuplicateError(x: Float, addData: Curve.() -> Unit) {
        expect { addData() }
            .toThrow<IllegalArgumentException>()
            .withMessage(
                "Unable to add datapoint to ${typeNameAndMRID()}. " +
                    "xValue $x is invalid, as data with same xValue already exist in this Curve. "
            )
    }

}
