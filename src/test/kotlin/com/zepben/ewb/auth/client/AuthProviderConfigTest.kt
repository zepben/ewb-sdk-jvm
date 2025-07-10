/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.client

import com.zepben.ewb.auth.common.StatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler

class AuthProviderConfigTest {

    val handler = mockk<BodyHandler<String>>()
    val response = mockk<HttpResponse<String>> {
        every { statusCode() } returns StatusCode.OK.code
        every { body() } returns "{}"
    }
    val client = mockk<HttpClient> {
        every { send(any(), handler) } returns response
    }

    val clientCreator = { client }
    val verifyCertificates = true

    @Test
    fun `handles handles issuers with and without slashes`() {
        var issuer = "https://some-issuer/"

        fetchProviderDetails(issuer, verifyCertificates, clientCreator, handler)
        verify {
            client.send(HttpRequest.newBuilder().uri(URI("https://some-issuer/.well-known/openid-configuration")).GET().build(), handler)
        }

        issuer = "https://some-issuer"

        fetchProviderDetails(issuer, verifyCertificates, clientCreator, handler)
        verify {
            client.send(HttpRequest.newBuilder().uri(URI("https://some-issuer/.well-known/openid-configuration")).GET().build(), handler)
        }
    }
}
