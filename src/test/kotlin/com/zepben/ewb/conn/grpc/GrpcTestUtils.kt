/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.conn.grpc

import com.zepben.testutils.auth.MockServerCall
import com.zepben.testutils.auth.MockServerCallHandler
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerInterceptor
import io.grpc.Status

fun runServerCall(
    interceptor: ServerInterceptor,
    sc: (Status?, Metadata?) -> Unit,
    sch: (ServerCall<Int, Int>, Metadata) -> Unit,
    metadata: Metadata = Metadata()
) {
    var wasCalled = false
    val mockSc = MockServerCall<Int, Int>({ s, m ->
        sc(s, m)
        wasCalled = true
    })
    interceptor.interceptCall(mockSc, metadata, MockServerCallHandler(sch))
    assert(wasCalled)
}
