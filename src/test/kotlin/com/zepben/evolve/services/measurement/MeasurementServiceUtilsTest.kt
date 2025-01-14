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
import com.zepben.evolve.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeasurementServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `supports all network service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(MeasurementService().supportedKClasses, ::whenMeasurementServiceObjectProxy, "measurementValue") {
            object : MeasurementValue() {}
        }
    }

    // Function references to functions with generics are not yet supported, so we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function, then update this one to match.
    internal fun whenMeasurementServiceObjectProxy(
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

}
