/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.kotlinFunction

abstract class BaseFillFieldsTest(val service: Any, val clazz: Class<*>) {
    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `surface unpopulated fields`() {
        clazz.declaredMethods
            .filter { it.name == "fillFields" && !Modifier.isAbstract(it.parameterTypes[0].modifiers) }
            .mapNotNull {
                val type = it.parameterTypes[0]
                val cim = try {
                    type.getDeclaredConstructor(String::class.java).newInstance(generateId())
                } catch(_: Exception) {
                    type.getDeclaredConstructor().newInstance()
                }

                val kFunc = it.kotlinFunction!!
                if (kFunc.parameters.size < 3)
                    kFunc.callBy(mapOf(kFunc.parameters[0] to cim)) // objects that don't need to be added to the service
                else
                    kFunc.callBy(mapOf(kFunc.parameters[0] to cim, kFunc.parameters[1] to service))

                val unpopulated = findUnpopulated(cim)
                if (unpopulated.isEmpty()) null else type.simpleName to unpopulated
            }.also {
                // add field name to `ignoredProperties` if they should be ignored
                assertThat("filler left attributes unpopulated",
                    it, empty())
            }
    }

    private fun findUnpopulated(
        root: Any
    ): List<String> {
        val issues = mutableListOf<String>()
        val type = root::class.simpleName ?: "?"

        for (prop in root::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }) {
            val key = "$type.${prop.name}"
            if (key in ignoredProperties || prop.name in ignoredProperties) continue

            val value = try {
                prop.getter.call(root)
            } catch (e: Exception) {
                issues += "${prop.name} ${e.message}"
                continue
            }

            when {
                value == null -> issues += "${prop.name} is null"
                // nullable: default is null, so any non-null value (0, "", false, empty collection, first enum) is valid
                prop.returnType.isMarkedNullable -> continue
                value is String && value.isEmpty() -> issues += "${prop.name} is empty string"
                value is Boolean && !value -> issues += "${prop.name} is false (default)"
                value is Int && (value == 0 || value == Int.MAX_VALUE) -> issues += "${prop.name} is default/sentinel int"
                value is Double && (value == 0.0 || value.isNaN()) -> issues += "${prop.name} is default double"
                value is Enum<*> && value.name.startsWith("UNKNOWN") -> issues += "${prop.name} is ${value.name}"
                value is Collection<*> && value.isEmpty() -> issues += "${prop.name} is empty collection"
                // anything else (a CIM object reference, a populated collection, etc.) is considered populated
            }
        }
        return issues
    }

    /**
     * Properties that the fillers intentionally did not populate. These are ignored by
     * [findUnpopulated] checks in this test, matched by property name on any type.
     */
    protected open val ignoredProperties: Set<String> = setOf()
}
