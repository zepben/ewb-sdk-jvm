/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance

private val logger = LoggerFactory.getLogger("verifyWhenServiceFunctionSupportsAllServiceTypes")

internal fun verifyWhenServiceFunctionSupportsAllServiceTypes(
    supportedKClasses: Set<KClass<*>>,
    whenFunction: KFunction<*>,
    subjectField: String = "identifiedObject",
    createUnknownClass: () -> Any = { object : IdentifiedObject() {} }
) {
    // Find all the parameters that have arguments and get their first parameter.
    // These should all be IdentifiedObject leaf classes in the "when*ServiceObject" functions.
    val functionParamIdentifiedObjectTypes = whenFunction
        .parameters
        .asSequence()
        .filter { it.name != subjectField && it.name != "isOther" }
        .map { it.type }
        .filter { it.arguments.isNotEmpty() }
        .map { it.arguments[0] }
        .map { it.type?.classifier as KClass<*> }
        .toSet()

    assertThat(functionParamIdentifiedObjectTypes, equalTo(supportedKClasses))

    // Make sure each object calls the correct callback by building a map containing an InvokedChecker for the expected callback, and a
    // NeverInvokedChecker for each other callback.
    val paramsByName = whenFunction.parameters.associateBy { it.name }
    val neverInvokedParams = supportedKClasses.map { NeverInvokedChecker(it) }.associateBy { paramsByName.ensureParam(it.expectedCallback) } +
        mapOf(paramsByName.ensureParam("isOther") to neverInvokedChecker<IdentifiedObject>())

    supportedKClasses.forEach { check ->
        whenFunction.validateObjectCallback(check.createInstance(), check.expectedCallback, neverInvokedParams, paramsByName, subjectField)
    }

    // Make sure unknown objects call the error handler.
    whenFunction.validateObjectCallback(createUnknownClass(), "isOther", neverInvokedParams, paramsByName, subjectField)
}

private fun <T : Any> KFunction<*>.validateObjectCallback(
    obj: T,
    expectedCallback: String,
    neverInvokedParams: Map<KParameter, NeverInvokedChecker<*>>,
    paramsByName: Map<String?, KParameter>,
    subjectField: String
) {
    // Add the object and replace the expected callback with a check that makes sure it was invoked.
    val params = neverInvokedParams + mapOf(
        paramsByName.ensureParam(subjectField) to obj,
        paramsByName.ensureParam(expectedCallback) to InvokedChecker(obj),
    )

    logger.info("checking ${obj::class.simpleName ?: "unknown/anonymous"}")
    callBy(params)

    params.values.filterIsInstance<InvokeChecker<*>>().forEach { it.verifyInvoke() }
}

internal val KClass<*>.expectedCallback: String get() = "is${this.simpleName}"

fun Map<String?, KParameter>.ensureParam(name: String): KParameter =
    requireNotNull(get(name)) { "no param name $name was found. Please add it to the `when` function" }

internal sealed class InvokeChecker<T> : (T) -> String {
    abstract fun verifyInvoke()
}

internal class InvokedChecker<T>(val expected: T) : InvokeChecker<T>() {
    private var captured: T? = null
    override fun invoke(p1: T): String {
        captured = p1
        return p1.toString()
    }

    override fun verifyInvoke() {
        assertThat(captured, equalTo(expected))
    }
}

internal inline fun <reified T : Any> neverInvokedChecker() = NeverInvokedChecker(T::class)
internal class NeverInvokedChecker<T : Any>(clazz: KClass<T>) : InvokeChecker<T>() {

    val expectedCallback: String = clazz.expectedCallback
    private var invoked = 0

    override fun invoke(p1: T): String {
        invoked += 1
        return ""
    }

    override fun verifyInvoke() {
        assertThat(invoked, equalTo(0))
    }

}
