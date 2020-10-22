/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.grpc

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GrpcResultTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun canCreateResult() {
        val result: GrpcResult<Int> = GrpcResult.of(1)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.result, equalTo(1))
        assertThat(result.thrown, nullValue())
        assertThat(result.wasHandled, equalTo(false))
    }

    @Test
    internal fun canCreateNullResult() {
        val result: GrpcResult<Int> = GrpcResult.of(null)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.result, nullValue())
        assertThat(result.thrown, nullValue())
        assertThat(result.wasHandled, equalTo(false))
    }

    @Test
    internal fun canCreateHandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, true)

        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.result, nullValue())
        assertThat(result.thrown, equalTo(exception))
        assertThat(result.wasHandled, equalTo(true))
    }

    @Test
    internal fun canCreateUnhandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, false)

        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.result, nullValue())
        assertThat(result.thrown, equalTo(exception))
        assertThat(result.wasHandled, equalTo(false))
    }

}
