/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.*
import io.grpc.CallCredentials.RequestInfo
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall

class CallCredentialApplier(private val callCredentials: CallCredentials): ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        channel: Channel?
    ): ClientCall<ReqT, RespT> = channel!!.newCall(methodDescriptor, callOptions!!.withCallCredentials(callCredentials))
}
