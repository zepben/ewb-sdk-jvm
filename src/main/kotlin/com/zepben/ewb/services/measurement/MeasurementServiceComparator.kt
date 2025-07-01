/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.measurement

import com.zepben.ewb.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.ewb.cim.iec61970.base.meas.AnalogValue
import com.zepben.ewb.cim.iec61970.base.meas.DiscreteValue
import com.zepben.ewb.cim.iec61970.base.meas.MeasurementValue
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.ObjectDifference

//
// NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
//       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
//       function, so make sure you check the code coverage
//
@Suppress("unused")
class MeasurementServiceComparator : BaseServiceComparator() {

    private fun compare(source: AccumulatorValue, target: AccumulatorValue): ObjectDifference<AccumulatorValue> =
        ObjectDifference(source, target).apply {
            compareMeasurementValue()

            compareValues(AccumulatorValue::accumulatorMRID, AccumulatorValue::value)
        }

    private fun compare(source: AnalogValue, target: AnalogValue): ObjectDifference<AnalogValue> =
        ObjectDifference(source, target).apply {
            compareMeasurementValue()

            compareValues(AnalogValue::value, AnalogValue::analogMRID)
        }

    private fun compare(source: DiscreteValue, target: DiscreteValue): ObjectDifference<DiscreteValue> =
        ObjectDifference(source, target).apply {
            compareMeasurementValue()

            compareValues(DiscreteValue::discreteMRID, DiscreteValue::value)
        }

    private fun ObjectDifference<out MeasurementValue>.compareMeasurementValue(): ObjectDifference<out MeasurementValue> =
        apply {
            compareValues(MeasurementValue::timeStamp)
        }

}
