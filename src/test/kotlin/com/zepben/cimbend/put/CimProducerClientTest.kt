/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.put

import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.grpc.CaptureLastRpcErrorHandler
import com.zepben.testutils.exception.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CimProducerClientTest {

    @Test
    internal fun `tryRpc handles exceptions`() {
        val handler = CaptureLastRpcErrorHandler()
        val ex = RuntimeException()
        val client = object : CimProducerClient<BaseService>() {
            override fun send(service: BaseService) {
                tryRpc { throw ex }
            }
        }.apply { addErrorHandler(handler) }

        client.send(object : BaseService("test") {})

        assertThat(handler.lastError, equalTo(ex))
    }

    @Test
    internal fun `tryRpc throw unhandled exception`() {
        val handler = object : RpcErrorHandler {
            override fun onError(t: Throwable) = false
        }

        val client = object : CimProducerClient<BaseService>() {
            override fun send(service: BaseService) {
                tryRpc { throw RuntimeException() }
            }
        }.apply { addErrorHandler(handler) }

        expect { client.send(object : BaseService("test") {}) }
            .toThrow(RuntimeException::class.java)
    }

    @Test
    internal fun `can remove error handler`() {
        val handler = object : RpcErrorHandler {
            var count = 0
            override fun onError(t: Throwable): Boolean {
                ++count
                return true
            }
        }

        val client = object : CimProducerClient<BaseService>() {
            override fun send(service: BaseService) {
                tryRpc { throw RuntimeException() }
            }
        }.apply { addErrorHandler(handler) }

        client.send(object : BaseService("test") {})
        client.removeErrorHandler(handler)

        expect { client.send(object : BaseService("test") {}) }
            .toThrow(RuntimeException::class.java)

        assertThat(handler.count, equalTo(1))
    }
}
