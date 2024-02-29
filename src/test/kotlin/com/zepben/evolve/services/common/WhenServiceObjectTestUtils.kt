/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal fun verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(supportedKClasses: Set<KClass<*>>, whenFunction: KFunction<*>) {
    // Find all the parameters that have arguments and get their first parameter.
    // These should all be IdentifiedObject leaf classes in the "when*ServiceObject" functions.
    val functionParamIdentifiedObjectTypes = whenFunction
        .parameters
        .asSequence()
        .filter { it.name != "identifiedObject" && it.name != "isOther" }
        .map { it.type }
        .filter { it.arguments.isNotEmpty() }
        .map { it.arguments[0] }
        .map { it.type?.classifier as KClass<*> }
        .toSet()

    assertThat(functionParamIdentifiedObjectTypes, equalTo(supportedKClasses))
}

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

internal class NeverInvokedChecker<T> : InvokeChecker<T>() {
    private var invoked = 0
    override fun invoke(p1: T): String {
        invoked += 1
        return ""
    }

    override fun verifyInvoke() {
        assertThat(invoked, equalTo(0))
    }
}
