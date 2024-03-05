/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

/*
 * Maintains an in-memory model of measurements.
 */
class MeasurementService {
    private val _measurements = mutableListOf<MeasurementValue>()

    // ######################
    // # IEC61970 BASE MEAS #
    // ######################

    fun add(accumulatorValue: AccumulatorValue): Boolean = _measurements.add(accumulatorValue)
    fun remove(accumulatorValue: AccumulatorValue): Boolean = _measurements.remove(accumulatorValue)

    fun add(analogValue: AnalogValue): Boolean = _measurements.add(analogValue)
    fun remove(analogValue: AnalogValue): Boolean = _measurements.remove(analogValue)

    fun add(discreteValue: DiscreteValue): Boolean = _measurements.add(discreteValue)
    fun remove(discreteValue: DiscreteValue): Boolean = _measurements.remove(discreteValue)

    fun num(): Int = _measurements.size

    @Suppress("UNCHECKED_CAST")
    fun <T : MeasurementValue> listOf(clazz: KClass<T>): List<T> =
        _measurements.filter { clazz.isSuperclassOf(it::class) }.map { it as T }
}
