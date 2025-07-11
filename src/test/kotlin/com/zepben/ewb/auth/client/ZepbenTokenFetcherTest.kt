/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.client

import com.zepben.ewb.auth.common.AuthException
import com.zepben.ewb.auth.common.AuthMethod
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.testutils.auth.TOKEN
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.vertxutils.testing.TestHttpServer
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import java.net.http.HttpClient
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import javax.net.ssl.SSLContext

internal class ZepbenTokenFetcherTest {

    private lateinit var server: TestHttpServer
    private var port = 8080

    private val client = mock<HttpClient>()
    private val response = mock<HttpResponse<String>>()

    private val secureSSLContext = mock<SSLContext>()
    private val secureConfSSLContext = mock<SSLContext>()
    private val secureAuthSSLContext = mock<SSLContext>()
    private val insecureSSLContext = mock<SSLContext>()

    private val secureClient = mock<HttpClient>()
    private val secureConfClient = mock<HttpClient>()
    private val secureAuthClient = mock<HttpClient>()
    private val insecureClient = mock<HttpClient>()

    private val secureTokenFetcher = mock<ZepbenTokenFetcher>()
    private val insecureTokenFetcher = mock<ZepbenTokenFetcher>()

    @BeforeEach
    fun beforeEach() {
        server = TestHttpServer()
        port = server.listen()
        doReturn(response).`when`(client).send(any(), any<HttpResponse.BodyHandler<String>>())

        mockkObject(SSLContextUtils)
        every { SSLContextUtils.allTrustingSSLContext() } returns insecureSSLContext
        every { SSLContextUtils.singleCACertSSLContext("confCAFilename") } returns secureConfSSLContext
        every { SSLContextUtils.singleCACertSSLContext("authCAFilename") } returns secureAuthSSLContext

        mockkStatic(HttpClient::class)
        every { HttpClient.newHttpClient() } returns secureClient
        every { HttpClient.newBuilder().sslContext(insecureSSLContext).build() } returns insecureClient
        every { HttpClient.newBuilder().sslContext(secureConfSSLContext).build() } returns secureConfClient
        every { HttpClient.newBuilder().sslContext(secureAuthSSLContext).build() } returns secureAuthClient
    }

    @AfterEach
    fun afterEach() {
        server.close()
        unmockkAll()
    }

    @Test
    fun testCreateTokenFetcherSuccess() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn(
            "{\"authType\": \"OAUTH\", \"audience\": \"test_audience\", \"issuer\": \"https://testissuer\", \"jwks_uri\": \"https://jwkuri\", \"token_endpoint\": \"https://tokenEndpoint\"}"
        ).`when`(response).body()

        val tokenFetcher = createTokenFetcher("https://testaddress", confClient = client, authClient = client)
        assertThat(tokenFetcher.audience, equalTo("test_audience"))
        assertThat(tokenFetcher.tokenEndpoint, equalTo("https://tokenEndpoint"))
    }

    @Test
    fun testCreateTokenFetcherNoAuth() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn(
            "{\"authType\": \"NONE\", \"audience\": \"\", \"issuer\": \"https://tokenEndpoint\"}"
        ).`when`(response).body()

        expect {
            createTokenFetcher("https://testaddress", confClient = client, authClient = client)
        }.toThrow(AuthException::class.java)
            .withMessage("Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches https://testaddress")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(1))
            }
    }

    @Test
    fun testCreateTokenFetcherBadResponse() {
        doReturn(StatusCode.NOT_FOUND.code).`when`(response).statusCode()
        doReturn("Not found").`when`(response).body()

        expect {
            createTokenFetcher("https://testaddress", confClient = client, authClient = client)
        }.toThrow(AuthException::class.java)
            .withMessage("https://testaddress responded with error: 404 - Not found")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.NOT_FOUND.code))
            }
    }

    @Test
    fun testCreateTokenFetcherMissingJson() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("test text").`when`(response).body()

        expect {
            createTokenFetcher("https://testaddress", confClient = client, authClient = client)
        }.toThrow(AuthException::class.java)
            .withMessage("Expected JSON response from https://testaddress, but got: test text.")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.OK.code))
            }
    }

    @Test
    fun testCreateTokenFetcherNonObjectJson() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("[\"authType\"]").`when`(response).body()

        expect {
            createTokenFetcher("https://testaddress", confClient = client, authClient = client)
        }.toThrow(AuthException::class.java)
            .withMessage("Expected JSON object from https://testaddress, but got: [\"authType\"].")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.OK.code))
            }
    }

    @Test
    fun testCreateTokenFetcherManagedIdentity() {
        val tokenFetcher = createTokenFetcherManagedIdentity("https://testaddress")
        assertThat(tokenFetcher, notNullValue())
        assertThat(tokenFetcher.authMethod, equalTo(AuthMethod.OAUTH))
    }

    @Test
    fun testFetchAuth0TokenSuccessful() {
        testSuccessfulFetch(AuthMethod.AUTH0, "audience=test_audience")
    }

    @Test
    fun testFetchEntraTokenSuccessful() {
        testSuccessfulFetch(AuthMethod.ENTRAID, "scope=test_audience/.default")
    }

    @Test
    fun testFetchTokenThrowsExceptionOnBadResponse() {
        doReturn(StatusCode.NOT_FOUND.code).`when`(response).statusCode()
        doReturn("test text").`when`(response).body()

        val tokenFetcher = ZepbenTokenFetcher(
            audience = "test_audience",
            tokenEndpoint = "https://testissuer.com.au",
            authMethod = AuthMethod.OAUTH,
            client = client
        )
        verify(client, never()).send(any(), any<HttpResponse.BodyHandler<String>>())
        expect {
            tokenFetcher.fetchToken()
        }.toThrow(AuthException::class.java)
            .withMessage("Token fetch failed, Error was: 404 - test text")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.NOT_FOUND.code))
            }
    }

    @Test
    fun testFetchTokenThrowsExceptionOnMissingJson() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("test text").`when`(response).body()

        val tokenFetcher = ZepbenTokenFetcher(
            audience = "test_audience",
            tokenEndpoint = "https://testissuer.com.au",
            authMethod = AuthMethod.OAUTH,
            client = client
        )
        verify(client, never()).send(any(), any<HttpResponse.BodyHandler<String>>())
        expect {
            tokenFetcher.fetchToken()
        }.toThrow(AuthException::class.java)
            .withMessage("Response did not contain valid JSON - response was: test text")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.OK.code))
            }
    }

    @Test
    fun testFetchTokenThrowsExceptionOnNonObjectJson() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("[\"test text\"]").`when`(response).body()

        val tokenFetcher = ZepbenTokenFetcher(
            audience = "test_audience",
            tokenEndpoint = "https://testissuer.com.au",
            authMethod = AuthMethod.OAUTH,
            client = client
        )
        verify(client, never()).send(any(), any<HttpResponse.BodyHandler<String>>())
        expect {
            tokenFetcher.fetchToken()
        }.toThrow(AuthException::class.java)
            .withMessage("Response was not a JSON object - response was: [\"test text\"]")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.OK.code))
            }
    }

    @Test
    fun testFetchTokenThrowsExceptionOnMissingAccessToken() {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("{\"test\":\"fail\"}").`when`(response).body()

        val tokenFetcher = ZepbenTokenFetcher(
            audience = "test_audience",
            tokenEndpoint = "https://testissuer.com.au",
            authMethod = AuthMethod.OAUTH,
            client = client
        )
        verify(client, never()).send(any(), any<HttpResponse.BodyHandler<String>>())
        expect {
            tokenFetcher.fetchToken()
        }.toThrow(AuthException::class.java)
            .withMessage("Access Token absent in token response - Response was: {\"test\":\"fail\"}")
            .exception
            .apply {
                verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
                assertThat(statusCode, equalTo(StatusCode.OK.code))
            }
    }

    @Test
    fun testFetchAuth0TokenSuccessfulUsingRefresh() {
        testSuccessfulFetch(AuthMethod.AUTH0, "audience=test_audience", "refresh_token")
    }

    @Test
    fun testFetchEntraTokenSuccessfulUsingRefresh() {
        testSuccessfulFetch(AuthMethod.ENTRAID, "scope=test_audience/.default", "refresh_token")
    }

    private fun testSuccessfulFetch(authMethod: AuthMethod, expectedScope: String, refreshToken: String? = null) {
        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn(
            "{\"access_token\":\"$TOKEN\", ${refreshToken?.let { "\"refresh_token\": \"$it\"," } ?: ""} \"token_type\":\"Bearer\"}"
        ).`when`(response).body()

        mockStatic(BodyPublishers::class.java, CALLS_REAL_METHODS).use { bodyPublishers ->
            val tokenFetcher = ZepbenTokenFetcher(
                audience = "test_audience",
                tokenEndpoint = "https://testissuer.com.au",
                authMethod = authMethod,
                client = client,
                refreshToken = refreshToken
            )
            verify(client, never()).send(any(), any<HttpResponse.BodyHandler<String>>())
            val token = tokenFetcher.fetchToken()
            verify(client).send(any(), any<HttpResponse.BodyHandler<String>>())
            bodyPublishers.verify {
                BodyPublishers.ofString(matches("${expectedScope}${refreshToken?.let { "&refresh_token=$it" } ?: ""}"))
                assertThat(token, equalTo("Bearer $TOKEN"))
            }

        }
    }

    @Test
    fun testConstructorWithVerifyCertificatesOption() {
        doReturn(response).`when`(secureClient).send(any(), any<HttpResponse.BodyHandler<String>>())
        doReturn(response).`when`(insecureClient).send(any(), any<HttpResponse.BodyHandler<String>>())

        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("{\"access_token\":\"$TOKEN\", \"token_type\":\"Bearer\"}").`when`(response).body()

        assertThat(
            ZepbenTokenFetcher(
                audience = "audience",
                tokenEndpoint = "https://testissuer",
                authMethod = AuthMethod.OAUTH,
                verifyCertificate = true
            ).fetchToken(),
            equalTo("Bearer $TOKEN")
        )
        assertThat(
            ZepbenTokenFetcher("audience", "https://testissuer", AuthMethod.OAUTH, verifyCertificate = false).fetchToken(),
            equalTo("Bearer $TOKEN")
        )
    }

    @Test
    fun testConstructorWithCAFilename() {
        doReturn(response).`when`(secureAuthClient).send(any(), any<HttpResponse.BodyHandler<String>>())

        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("{\"access_token\":\"$TOKEN\", \"token_type\":\"Bearer\"}").`when`(response).body()

        assertThat(
            ZepbenTokenFetcher("audience", "https://tokenEndpoint", AuthMethod.OAUTH, caFilename = "authCAFilename").fetchToken(),
            equalTo("Bearer $TOKEN")
        )
    }

    @Test
    fun testConstructorWithDefaultTls() {
        doReturn(response).`when`(secureClient).send(any(), any<HttpResponse.BodyHandler<String>>())

        doReturn(StatusCode.OK.code).`when`(response).statusCode()
        doReturn("{\"access_token\":\"$TOKEN\", \"token_type\":\"Bearer\"}").`when`(response).body()

        assertThat(
            ZepbenTokenFetcher("audience", "https://tokenEndpoint", AuthMethod.OAUTH).fetchToken(),
            equalTo("Bearer $TOKEN")
        )
    }

    @Test
    fun testCreateTokenFetcherWithVerifyCertificatesOption() {
        mockkStatic("com.zepben.ewb.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", secureClient, secureClient, "audienceField", "issuerField")
        } returns secureTokenFetcher
        every {
            createTokenFetcher("confAddress", insecureClient, insecureClient, "audienceField", "issuerField")
        } returns insecureTokenFetcher

        assertThat(createTokenFetcher("confAddress", true, "audienceField", "issuerField"), equalTo(secureTokenFetcher))
        assertThat(createTokenFetcher("confAddress", false, "audienceField", "issuerField"), equalTo(insecureTokenFetcher))
    }

    @Test
    fun testCreateTokenFetcherWithCAFilenames() {
        mockkStatic("com.zepben.ewb.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", secureConfClient, secureAuthClient, "audienceField", "issuerField")
        } returns secureTokenFetcher

        assertThat(
            createTokenFetcher(
                "confAddress",
                confCAFilename = "confCAFilename",
                authCAFilename = "authCAFilename",
                audienceField = "audienceField",
                issuerField = "issuerField",
                verifyCertificates = true
            ),
            equalTo(secureTokenFetcher)
        )
    }

    @Test
    fun testCreateTokenFetcherWithDefaultTls() {
        mockkStatic("com.zepben.ewb.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", secureClient, secureClient, "audienceField", "issuerField")
        } returns secureTokenFetcher

        assertThat(
            createTokenFetcher(
                "confAddress",
                audienceField = "audienceField",
                issuerField = "issuerField",
                verifyCertificates = true
            ),
            equalTo(secureTokenFetcher)
        )
    }
}
