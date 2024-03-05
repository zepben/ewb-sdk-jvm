/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.measurement

import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
import com.zepben.evolve.cim.iec61970.base.meas.MeasurementValue
import com.zepben.evolve.services.common.InvokeChecker
import com.zepben.evolve.services.common.InvokedChecker
import com.zepben.evolve.services.common.NeverInvokedChecker
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeasurementServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    // Function references to functions with generics are not yet supported.
    // So, we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function,
    // then update this one to match and then update the tests.
    private fun whenMeasurementServiceObjectProxy(
        measurementValue: MeasurementValue,
        isAnalogValue: (AnalogValue) -> String,
        isAccumulatorValue: (AccumulatorValue) -> String,
        isDiscreteValue: (DiscreteValue) -> String,
        isOther: (MeasurementValue) -> String
    ): String = whenMeasurementServiceObject(
        measurementValue,
        isAnalogValue = isAnalogValue,
        isAccumulatorValue = isAccumulatorValue,
        isDiscreteValue = isDiscreteValue,
        isOther = isOther
    )

    private fun whenMeasurementServiceObjectTester(
        measurementValue: MeasurementValue,
        isAnalogValue: InvokeChecker<AnalogValue> = NeverInvokedChecker(),
        isAccumulatorValue: InvokeChecker<AccumulatorValue> = NeverInvokedChecker(),
        isDiscreteValue: InvokeChecker<DiscreteValue> = NeverInvokedChecker(),
        isOther: InvokeChecker<MeasurementValue> = NeverInvokedChecker()
    ) {
        val returnValue = whenMeasurementServiceObjectProxy(
            measurementValue,
            isAnalogValue = isAnalogValue,
            isAccumulatorValue = isAccumulatorValue,
            isDiscreteValue = isDiscreteValue,
            isOther = isOther
        )

        assertThat(returnValue, equalTo(measurementValue.toString()))
        isAnalogValue.verifyInvoke()
        isAccumulatorValue.verifyInvoke()
        isDiscreteValue.verifyInvoke()
        isOther.verifyInvoke()
    }

    @Test
    internal fun `invokes correct function`() {
        AnalogValue().also { whenMeasurementServiceObjectTester(it, isAnalogValue = InvokedChecker(it)) }
        AccumulatorValue().also { whenMeasurementServiceObjectTester(it, isAccumulatorValue = InvokedChecker(it)) }
        DiscreteValue().also { whenMeasurementServiceObjectTester(it, isDiscreteValue = InvokedChecker(it)) }
        object : MeasurementValue() {}.also { whenMeasurementServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}
