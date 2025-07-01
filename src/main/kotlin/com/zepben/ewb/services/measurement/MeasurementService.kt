/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.measurement

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.meas.AccumulatorValue
import com.zepben.ewb.cim.iec61970.base.meas.AnalogValue
import com.zepben.ewb.cim.iec61970.base.meas.DiscreteValue
import com.zepben.ewb.cim.iec61970.base.meas.MeasurementValue
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf

/*
 * Maintains an in-memory model of measurements.
 */
class MeasurementService {

    private val _measurements = mutableListOf<MeasurementValue>()

    // ######################
    // # IEC61970 Base Meas #
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

    //region Code copied from BaseService
    //
    // NOTE: This is a copy/paste from BaseService because this service doesn't inherit from it. There is no point making BaseService the base class as
    //       the items stored in this service don't inherit from IdentifiedObject, which is what BaseService is designed for.
    //
    //       The only difference is the base class used for finding the add/remove functions. This copy uses MeasurementValue, while the copy in BaseService
    //       uses IdentifiedObject. Someone at some stage can probably move this to a reusable utils class and make it take the base class as a parameter.
    //
    private val addFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("add")
    private val removeFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("remove")

    /**
     * A list of Kotlin classes supported by this service.
     */
    val supportedKClasses: Set<KClass<out IdentifiedObject>> get() = addFunctions.keys

    init {
        check(addFunctions.keys == removeFunctions.keys) {
            "Add and remove functions should be defined in matching pairs. They don't seem to match...\n" +
                "add   : ${addFunctions.keys.sortedBy { it.simpleName }}\n" +
                "remove: ${removeFunctions.keys.sortedBy { it.simpleName }}"
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findFunctionsForDispatch(name: String): Map<KClass<out IdentifiedObject>, KFunction<*>> {
        val idObjType = MeasurementValue::class.createType()
        return this::class.declaredMemberFunctions.asSequence()
            .filter { it.name == name }
            .filter { it.parameters.size == 2 }
            .filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.parameters[1].type.isSubtypeOf(idObjType) }
            .map { (it.parameters[1].type.classifier as KClass<out IdentifiedObject>) to it }
            .onEach {
                require(it.second.returnType.classifier == Boolean::class) {
                    "return type for '${it.second}' needs to be Boolean"
                }

                require((it.second.parameters[0].type.classifier as KClass<*>).isFinal) {
                    "${it.second} does not accept a leaf class. " +
                        "Only leafs should be used to reduce chances of edge case issues and potential undefined behaviour"
                }
            }
            .toMap()
    }
    //endregion

}
