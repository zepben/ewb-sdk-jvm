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
import com.zepben.evolve.cim.iec61970.base.meas.MeasurementValue
import com.zepben.evolve.services.measurement.MeasurementService
import com.zepben.evolve.services.measurement.MeasurementServiceComparator
import com.zepben.evolve.services.measurement.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeasurementTranslatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val comparator = MeasurementServiceComparator()

    @Test
    internal fun convertsCorrectly() {
        val msToPb = MeasurementCimToProto()
        
        /************ IEC61970 BASE MEAS ************/
        validate({ AnalogValue() }, { it.fillFields() }, { ns, it -> ns.addFromPb(msToPb.toPb(it)) })
        validate({ AccumulatorValue() }, { it.fillFields() }, { ns, it -> ns.addFromPb(msToPb.toPb(it)) })
        validate({ DiscreteValue() }, { it.fillFields() }, { ns, it -> ns.addFromPb(msToPb.toPb(it)) })
    }

    private inline fun <reified T : MeasurementValue> validate(creator: () -> T, filler: (T) -> Unit, adder: (MeasurementService, T) -> T?) {
        val cim = creator()
        val blankDifferences = comparator.compare(cim, adder(MeasurementService(), cim)!!).differences
        assertThat("Failed to convert blank ${T::class.simpleName}:${blankDifferences}", blankDifferences, anEmptyMap())

        filler(cim)

        val populatedDifferences = comparator.compare(cim, adder(MeasurementService(), cim)!!).differences
        assertThat("Failed to convert populated ${T::class.simpleName}:${populatedDifferences}", populatedDifferences, anEmptyMap())
    }

}
