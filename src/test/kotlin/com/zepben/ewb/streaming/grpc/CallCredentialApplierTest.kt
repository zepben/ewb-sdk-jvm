/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

import io.grpc.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class CallCredentialApplierTest {

    @Test
    internal fun interceptCallAppliesCallCredentials() {
        val callCredentials = mock<CallCredentials>()
        val callOptions = mock<CallOptions>()
        val callOptionsWithCredentials = mock<CallOptions>()
        doReturn(callOptionsWithCredentials).`when`(callOptions).withCallCredentials(callCredentials)

        val methodDescriptor = mock<MethodDescriptor<Any, Any>>()
        val channel = mock<Channel>()
        val clientCallWithCredentials = mock<ClientCall<Any, Any>>()
        doReturn(clientCallWithCredentials).`when`(channel).newCall(methodDescriptor, callOptionsWithCredentials)

        val callCredentialApplier = CallCredentialApplier(callCredentials)
        assertThat(callCredentialApplier.interceptCall(methodDescriptor, callOptions, channel), equalTo(clientCallWithCredentials))
    }
}
