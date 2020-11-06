/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.grpc.CaptureLastRpcErrorHandler
import com.zepben.cimbend.grpc.GrpcResult
import com.zepben.protobuf.cim.iec61970.base.wires.TapChanger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import kotlin.reflect.full.declaredMemberFunctions

object ConsumerUtils {

    fun forEachBuilder(obj: Any, action: (Any) -> Unit) {
        obj::class.declaredMemberFunctions
            .asSequence()
            .filter { it.parameters.size == 1 }
            .filter { it.name.startsWith("get") }
            .filter { it.name.endsWith("Builder") }
            .filter { !it.name.endsWith("FieldBuilder") }
            .filter { !it.name.endsWith("OrBuilder") || it.name == "getOrBuilder" }
            .mapNotNull { it.call(obj) }
            .forEach(action)
    }

    fun buildFromBuilder(builder: Any, mRID: String): Any {
        println("-> ${builder::class.java.enclosingClass.simpleName}.${builder::class.simpleName}")
        builder::class.declaredMemberFunctions.find { it.name == "setMRID" }?.call(builder, mRID)

        // Add any customisations required to build the object at a bare minimum
        if (builder is TapChanger.Builder)
            builder.highStep = 1

        forEachBuilder(builder) { buildFromBuilder(it, mRID) }

        return builder::class.declaredMemberFunctions.single { it.name == "build" }.call(builder)!!
    }

    fun validateFailure(
        onErrorHandler: CaptureLastRpcErrorHandler,
        result: GrpcResult<*>,
        expectedEx: Throwable,
        expectHandled: Boolean
    ) {
        assertThat(result.wasFailure, equalTo(true))
        assertThat(result.thrown, equalTo(expectedEx))
        assertThat(result.wasErrorHandled, equalTo(expectHandled))
        assertThat(onErrorHandler.lastError, if (expectHandled) equalTo(expectedEx) else nullValue())
    }

}
