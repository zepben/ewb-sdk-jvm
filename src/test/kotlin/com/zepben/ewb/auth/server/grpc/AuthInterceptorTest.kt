/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server.grpc

import com.zepben.ewb.auth.server.JWTAuthenticator
import com.zepben.ewb.auth.server.createAuthenticator
import com.zepben.testutils.auth.MockServerCall
import com.zepben.testutils.auth.MockServerCallHandler
import com.zepben.testutils.auth.TOKEN
import io.grpc.Metadata
import io.grpc.Status
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

const val write_network_scope = "write:network"

class AuthInterceptorTest {

    @Test
    fun testIntercept() {
        val ta = createAuthenticator("https://fake-aud/", "https://issuer/")

        val requiredScopes = mapOf(
            "zepben.protobuf.np.NetworkProducer" to write_network_scope
        )
        val authInterceptor = AuthInterceptor(ta, requiredScopes)
        var sc = MockServerCall<Int, Int>({ status, _ ->
            assertThat(status!!.code, equalTo(Status.UNAUTHENTICATED.code))
            assertThat(status.description, equalTo("Authorization token is missing"))
        })
        authInterceptor.interceptCall(sc, Metadata(), null)

        val mdNotBearer = Metadata().apply { put(AUTHORIZATION_METADATA_KEY, "NotBearer ayyyyy") }
        sc = MockServerCall({ status, _ ->
            assertThat(status!!.code, equalTo(Status.UNAUTHENTICATED.code))
            assertThat(status.description, equalTo("Unknown authorization type"))
        })
        authInterceptor.interceptCall(sc, mdNotBearer, null)

        val mdWithBearer = Metadata().apply { put(AUTHORIZATION_METADATA_KEY, "Bearer $TOKEN") }
        sc = MockServerCall({ _, _ -> })
        var callWasMade = false
        val sch = MockServerCallHandler<Int, Int> { _, metadata ->
            // not really important assert - just to make sure no one balls'd up the test
            assertThat("Metadata is missing bearer token.", metadata.containsKey(AUTHORIZATION_METADATA_KEY))
            callWasMade = true
        }
        authInterceptor.interceptCall(sc, mdWithBearer, sch)
        assertThat("Call was not made to the authenticator.", callWasMade)

        callWasMade = false
        val mdWithBadBearer = Metadata().apply { put(AUTHORIZATION_METADATA_KEY, "Bearer aoeu") }
        sc = MockServerCall({ status, _ ->
            assertThat(status!!.code, equalTo(Status.UNAUTHENTICATED.code))
            callWasMade = true
        })
        authInterceptor.interceptCall(sc, mdWithBadBearer, sch)
        assertThat("Call was not made to the authenticator.", callWasMade)
    }

    @Test
    fun `test provided authorise function is used`() {
        val ta = createAuthenticator("https://fake-aud/", "https://issuer/")

        var authoriseCalled = false
        val mdWithBearer = Metadata().apply { put(AUTHORIZATION_METADATA_KEY, "Bearer $TOKEN") }
        val sc = MockServerCall<Int, Int>({ _, _ -> })
        var callWasMade = false
        val sch = MockServerCallHandler<Int, Int> { _, metadata ->
            // not really important assert - just to make sure no one balls'd up the test
            assertThat("Metadata is missing bearer token.", metadata.containsKey(AUTHORIZATION_METADATA_KEY))
            callWasMade = true
        }
        val authInterceptor = AuthInterceptor(ta, null) { _, _ ->
            authoriseCalled = true
            GrpcAuthResp(Status.OK)
        }

        authInterceptor.interceptCall(sc, mdWithBearer, sch)
        assertThat("Call was not made to the authenticator.", callWasMade)
        assertThat("Call was not made to the authoriser.", authoriseCalled)
    }

    @Test
    fun `test exception is handled`() {
        val ta = mockk<JWTAuthenticator> {
            every { authenticate(any()) } throws Exception("some message")
        }

        var authoriseCalled = false
        val mdWithBearer = Metadata().apply { put(AUTHORIZATION_METADATA_KEY, "Bearer $TOKEN") }
        val sc = MockServerCall<Int, Int>({ status, _ ->
            assertThat(status!!.code, equalTo(Status.UNKNOWN.code))
            assertThat(status.description, equalTo("some message"))
        })
        var callWasMade = false
        val sch = MockServerCallHandler<Int, Int> { _, _ ->
            callWasMade = true
        }
        val authInterceptor = AuthInterceptor(ta, null) { _, _ ->
            authoriseCalled = true
            GrpcAuthResp(Status.OK)
        }

        authInterceptor.interceptCall(sc, mdWithBearer, sch)
        assertThat("Call was made to the authenticator when it shouldn't have been.", !callWasMade)
        assertThat("Call was made to the authoriser when it shouldn't have been.", !authoriseCalled)
    }
}
