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


@file:JvmName("MeasurementServiceUtils")

package com.zepben.cimbend.measurement

import com.zepben.cimbend.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.cimbend.cim.iec61970.base.meas.AnalogValue
import com.zepben.cimbend.cim.iec61970.base.meas.DiscreteValue
import com.zepben.cimbend.cim.iec61970.base.meas.MeasurementValue

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
