/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.measurement.translator

import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
import com.zepben.evolve.services.measurement.testdata.fillFields
import org.junit.jupiter.api.Test

internal class MeasurementCimToProtoTest {

    private val validator = MeasurementCimToProtoTestValidator()

    //
    // NOTE: We can't use the same method for testing the measurement service as the others because there is no comparator and the
    //

    @Test
    internal fun convertsAnalogValue() {
        val cim = AnalogValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(), cim.toPb())
    }

    @Test
    internal fun convertsAccumulatorValue() {
        val cim = AccumulatorValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(), cim.toPb())
    }

    @Test
    internal fun convertsDiscreteValue() {
        val cim = DiscreteValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(), cim.toPb())
    }

}
