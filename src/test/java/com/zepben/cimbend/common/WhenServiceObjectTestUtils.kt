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
package com.zepben.cimbend.common

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal fun verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(supportedKClasses: Set<KClass<*>>, whenFunction: KFunction<*>) {
    // Find all the parameters that have arguments and get their first parameter.
    // These should all be IdentifiedObject leaf classes in the when*ServiceObject functions.
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
    var captured: T? = null
    override fun invoke(p1: T): String {
        captured = p1
        return p1.toString()
    }

    override fun verifyInvoke() {
        assertThat(captured, equalTo(expected))
    }
}

internal class NeverInvokedChecker<T> : InvokeChecker<T>() {
    var invoked = 0
    override fun invoke(p1: T): String {
        invoked += 1
        return ""
    }

    override fun verifyInvoke() {
        assertThat(invoked, equalTo(0))
    }
}
