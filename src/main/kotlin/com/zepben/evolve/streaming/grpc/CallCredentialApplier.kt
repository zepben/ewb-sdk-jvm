/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.*

/**
 * This client interceptor applies a call credential to each client call for a gRPC channel.
 */
class CallCredentialApplier(private val callCredentials: CallCredentials): ClientInterceptor {

    override fun <ReqT, RespT> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        channel: Channel
    ): ClientCall<ReqT, RespT> = channel.newCall(methodDescriptor, callOptions.withCallCredentials(callCredentials))

}
