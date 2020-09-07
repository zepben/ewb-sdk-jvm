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

package com.zepben.cimbend.put

import com.zepben.cimbend.common.BaseService
import com.zepben.test.util.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CimProducerClientTest {

    @Test
    internal fun `onRpcError Coverage`() {
        val handler = CaptureLastRpcErrorHandler()
        val client = object : CimProducerClient<BaseService>(handler) {
            override fun send(service: BaseService) {}
        }

        assertThat(client.onRpcError, equalTo(handler))
    }

    @Test
    internal fun `tryRpc handles exceptions`() {
        val handler = CaptureLastRpcErrorHandler()
        val ex = RuntimeException()
        val client = object : CimProducerClient<BaseService>(handler) {
            override fun send(service: BaseService) {
                tryRpc { throw ex }
            }
        }

        client.send(object: BaseService("test") {})

        assertThat(handler.lastError, equalTo(ex))
    }

    @Test
    internal fun `tryRpc throw unhandled exception`() {
        val handler = object : RpcErrorHandler {
            override fun onError(t: Throwable) {}
            override fun handles(t: Throwable): Boolean = false
        }

        val client = object : CimProducerClient<BaseService>(handler) {
            override fun send(service: BaseService) {
                tryRpc { throw RuntimeException() }
            }
        }

        expect { client.send(object: BaseService("test") {}) }
            .toThrow(RuntimeException::class.java)
    }
}
