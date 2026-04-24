/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.conn.grpc

import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Status
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CompressionInterceptorTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    fun `test intercepts calls`() {
        val interceptor = CompressionInterceptor()

        runServerCall(
            interceptor,
            { status, _ ->
                MatcherAssert.assertThat(status!!.code, Matchers.equalTo(Status.OK.code))
            },
            { call, metadata -> call.close(Status.OK, metadata) }
        )
    }
}
