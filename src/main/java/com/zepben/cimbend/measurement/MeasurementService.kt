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
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

/*
 * Maintains an in-memory model of measurements.
 */
class MeasurementService {
    private val _measurements = mutableListOf<MeasurementValue>()

    fun add(accumulatorValue: AccumulatorValue): Boolean = _measurements.add(accumulatorValue)
    fun remove(accumulatorValue: AccumulatorValue): Boolean = _measurements.remove(accumulatorValue)

    fun add(analogValue: AnalogValue): Boolean = _measurements.add(analogValue)
    fun remove(analogValue: AnalogValue): Boolean = _measurements.remove(analogValue)

    fun add(discreteValue: DiscreteValue): Boolean = _measurements.add(discreteValue)
    fun remove(discreteValue: DiscreteValue): Boolean = _measurements.remove(discreteValue)

    fun num(): Int = _measurements.size
    fun <T : MeasurementValue> listOf(clazz: KClass<T>): List<T> =
            _measurements.filter { clazz.isSuperclassOf(it::class) }.map { it as T }
}
