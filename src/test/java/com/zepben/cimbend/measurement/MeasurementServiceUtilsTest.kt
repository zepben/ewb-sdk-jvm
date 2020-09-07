/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.measurement

import com.zepben.cimbend.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.cimbend.cim.iec61970.base.meas.AnalogValue
import com.zepben.cimbend.cim.iec61970.base.meas.DiscreteValue
import com.zepben.cimbend.cim.iec61970.base.meas.MeasurementValue
import com.zepben.cimbend.common.InvokeChecker
import com.zepben.cimbend.common.InvokedChecker
import com.zepben.cimbend.common.NeverInvokedChecker
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
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

