/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:JvmName("MeasurementServiceUtils")

package com.zepben.evolve.services.measurement

import com.zepben.evolve.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.evolve.cim.iec61970.base.meas.AnalogValue
import com.zepben.evolve.cim.iec61970.base.meas.DiscreteValue
import com.zepben.evolve.cim.iec61970.base.meas.MeasurementValue

/**
 * A function that provides an exhaustive `when` style statement for all [MeasurementValue] leaf types supported by
 * the [MeasurementService]. If the provided [MeasurementValue] is not supported by the service the [isOther] handler
 * is invoked which by default will throw an [IllegalArgumentException]
 *
 * By using this function, you acknowledge that if any new types are added to the measurement service, and thus this
 * function, it will cause a compilation error when updating to the new version. This should reduce errors due to
 * missed handling of new types introduced to the model. As this is intended behaviour it generally will not be
 * considered a breaking change in terms of semantic versioning of this library.
 *
 * If it is not critical that all types within the service are always handled, it is recommended to use a typical
 * `when` statement (Kotlin) or if-else branch (Java) and update new cases as required without breaking your code.
 *
 * @param measurementValue The measurement value object to handle.
 * @param isAnalogValue Handler when the [MeasurementValue] is a [AnalogValue]
 * @param isAccumulatorValue Handler when the [MeasurementValue] is a [AccumulatorValue]
 * @param isDiscreteValue Handler when the [MeasurementValue] is a [DiscreteValue]
 * @param isOther Handler when the [MeasurementValue] is not supported by the [MeasurementService].
 */
@JvmOverloads
inline fun <R> whenMeasurementServiceObject(
    measurementValue: MeasurementValue,
    crossinline isAnalogValue: (AnalogValue) -> R,
    crossinline isAccumulatorValue: (AccumulatorValue) -> R,
    crossinline isDiscreteValue: (DiscreteValue) -> R,
    crossinline isOther: (MeasurementValue) -> R = { idObj: MeasurementValue ->
        throw IllegalArgumentException("MeasurementValue object type ${idObj::class} is not supported by the measurement service")
    }
): R = when (measurementValue) {
    is AnalogValue -> isAnalogValue(measurementValue)
    is AccumulatorValue -> isAccumulatorValue(measurementValue)
    is DiscreteValue -> isDiscreteValue(measurementValue)
    else -> isOther(measurementValue)
}
