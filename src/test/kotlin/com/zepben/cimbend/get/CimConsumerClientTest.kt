/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.grpc.CaptureLastRpcErrorHandler
import com.zepben.cimbend.grpc.GrpcResult
import com.zepben.cimbend.put.RpcErrorHandler
import com.zepben.testutils.exception.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CimConsumerClientTest {

    @Test
    internal fun `tryRpc handles exceptions`() {
        val handler = CaptureLastRpcErrorHandler()
        val ex = RuntimeException()
        val client = ThrowingCimConsumerClient(ex).apply { addErrorHandler(handler) }

        client.throwViaTryRpc()

        assertThat(handler.lastError, equalTo(ex))
    }

    @Test
    internal fun `tryRpc throw unhandled exception`() {
        val handler = object : RpcErrorHandler {
            override fun onError(t: Throwable) = false
        }

        val client = ThrowingCimConsumerClient().apply { addErrorHandler(handler) }

        expect { client.throwViaTryRpc() }
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

        val client = ThrowingCimConsumerClient()

        client.apply { addErrorHandler(handler) }

        client.throwViaTryRpc()
        client.removeErrorHandler(handler)

        expect { client.throwViaTryRpc() }
            .toThrow(RuntimeException::class.java)

        assertThat(handler.count, equalTo(1))
    }

    private class ThrowingCimConsumerClient(private val ex: Throwable = RuntimeException()) : CimConsumerClient<BaseService>() {

        override fun getIdentifiedObject(service: BaseService, mRID: String): GrpcResult<IdentifiedObject> {
            return tryRpc { throw ex }
        }

        override fun getIdentifiedObjects(service: BaseService, mRIDs: Iterable<String>): GrpcResult<Map<String, IdentifiedObject>> {
            return tryRpc { throw ex }
        }

        fun throwViaTryRpc(): GrpcResult<Unit> {
            return tryRpc { throw ex }
        }

    }

}
