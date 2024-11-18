/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.auth.client.*
import com.zepben.auth.common.AuthException
import com.zepben.auth.common.AuthMethod
import com.zepben.testutils.exception.ExpectException
import io.mockk.*
import io.vertx.core.json.JsonObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.http.HttpClient
import javax.net.ssl.SSLContext

internal class ConnectTest {

    private val gcbWithAddress = mockk<GrpcChannelBuilder>()
    private val gcbWithTls = mockk<GrpcChannelBuilder>()
    private val gcbWithOutTls = mockk<GrpcChannelBuilder>()
    private val gcbWithToken = mockk<GrpcChannelBuilder>()
    private val gcbWithTlsWithToken = mockk<GrpcChannelBuilder>()
    private val gcbWithAuth = mockk<GrpcChannelBuilder>()

    private val grpcChannel = mockk<GrpcChannel>()
    private val grpcChannelWithTls = mockk<GrpcChannel>()
    private val grpcChannelWithToken = mockk<GrpcChannel>()
    private val grpcChannelWithAuth = mockk<GrpcChannel>()

    private val tokenFetcher = mockk<ZepbenTokenFetcher>()
    private val tokenRequestData = JsonObject()

    @BeforeEach
    internal fun beforeEach() {
        tokenRequestData.clear()

        every { gcbWithAddress.makeSecure("caFilename") } returns gcbWithTls
        every { gcbWithTls.withTokenFetcher(tokenFetcher) } returns gcbWithAuth
        every { gcbWithTls.withTokenString("accessToken") } returns gcbWithTlsWithToken

        every { gcbWithAddress.makeInsecure() } returns gcbWithOutTls
        every { gcbWithOutTls.withTokenString("accessToken") } returns gcbWithToken

        every { gcbWithAddress.build() } returns grpcChannel
        every { gcbWithTls.build() } returns grpcChannelWithTls
        every { gcbWithTlsWithToken.build() } returns grpcChannelWithToken
        every { gcbWithToken.build() } returns grpcChannelWithToken
        every { gcbWithAuth.build() } returns grpcChannelWithAuth

        every { tokenFetcher.tokenRequestData } returns tokenRequestData

        mockkConstructor(GrpcChannelBuilder::class)
        every { constructedWith<GrpcChannelBuilder>().forAddress("hostname", 1234) } returns gcbWithAddress
    }

    @AfterEach
    fun teardownMockks() {
        unmockkAll()
    }

    @Test
    internal fun connectInsecure() {
        assertThat(Connect.connectInsecure("hostname", 1234), equalTo(grpcChannel))
    }

    @Test
    internal fun connectWithAccessTokenInsecure() {
        assertThat(Connect.connectWithAccessTokenInsecure("hostname", 1234, "accessToken"), equalTo(grpcChannelWithToken))
    }

    @Test
    internal fun connectWithAccessToken() {
        assertThat(Connect.connectWithAccessToken("hostname", 1234, "accessToken", "caFilename"), equalTo(grpcChannelWithToken))
    }

    @Test
    internal fun connectTls() {
        assertThat(Connect.connectTls("hostname", 1234, "caFilename"), equalTo(grpcChannelWithTls))
    }

    @Test
    internal fun connectWithSecret() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), any())
        } returns tokenFetcher

        val grpcChannel = Connect.connectWithSecret(
            "clientId",
            "clientSecret",
            "hostname",
            1234,
            "confAddress",
            "confCAFilename",
            "authCAFilename",
            "caFilename"
        )

        assertThat(grpcChannel, equalTo(grpcChannelWithAuth))
        assertThat(
            tokenRequestData, equalTo(
                JsonObject(
                    """
                {
                    "client_id": "clientId",
                    "client_secret": "clientSecret",
                    "grant_type": "client_credentials"
                }
            """.trimIndent()
                )
            )
        )
    }

    @Test
    internal fun connectWithSecretFailsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")

        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), true)
        } throws AuthException(
            1,
            "Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches confAddress"
        )
        ExpectException.expect {
            Connect.connectWithSecret("clientId", "clientSecret", "hostname", 1234, "confAddress", "confCAFilename", "authCAFilename", "caFilename")
        }.toThrow<AuthException>().withMessage("Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches confAddress")
    }


    @Test
    internal fun connectWithSecretWithKnownTokenFetcherConfig() {
        val httpClient = mockk<HttpClient>()
        val sslContext = mockk<SSLContext>()

        val builder = mockk<HttpClient.Builder>().also {
            every { it.sslContext(sslContext) } returns it
            every { it.build() } returns httpClient
        }

        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher(AuthMethod.ENTRAID, "https://logi/contoso.onmicrosoft.not.real/v2.0", "audience2", httpClient, false)
        } returns tokenFetcher

        mockkStatic(HttpClient::newBuilder)
        every { HttpClient.newBuilder() } returns builder

        mockkObject(SSLContextUtils)
        every { SSLContextUtils.singleCACertSSLContext("authCaFilename") } returns sslContext

        every { tokenFetcher.tokenRequestData } returns tokenRequestData

        val grpcChannel = Connect.connectWithSecret(
            "clientId",
            "clientSecret",
            "audience2",
            "https://logi/contoso.onmicrosoft.not.real/v2.0",
            "hostname",
            1234,
            authMethod = AuthMethod.ENTRAID,
            caFilename = "caFilename",
            authCAFilename = "authCaFilename"
        )
        assertThat(grpcChannel, equalTo(grpcChannelWithAuth))
        assertThat(
            tokenRequestData, equalTo(
                JsonObject(
                    """
                        {
                            "client_id": "clientId",
                            "client_secret": "clientSecret",
                            "grant_type": "client_credentials"
                        }
                    """.trimIndent()
                )
            )
        )

        verifySequence {
            HttpClient.newBuilder()
            SSLContextUtils.singleCACertSSLContext("authCaFilename")
            builder.sslContext(sslContext)
            builder.build()
            createTokenFetcher(AuthMethod.ENTRAID, "https://logi/contoso.onmicrosoft.not.real/v2.0", "audience2", httpClient, false)
            sslContext wasNot called
            httpClient wasNot called
        }
    }

    @Test
    internal fun connectWithPassword() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), any())
        } returns tokenFetcher

        val grpcChannel = Connect.connectWithPassword(
            "clientId",
            "username",
            "password",
            "hostname",
            1234,
            "confAddress",
            "confCAFilename",
            "authCAFilename",
            "caFilename"
        )

        assertThat(grpcChannel, equalTo(grpcChannelWithAuth))
        assertThat(
            tokenRequestData, equalTo(
                JsonObject(
                    """
                {
                    "client_id": "clientId",
                    "username": "username",
                    "password": "password",
                    "grant_type": "password",
                    "scope": "offline_access"
                }
            """.trimIndent()
                )
            )
        )
    }

    @Test
    internal fun connectWithPasswordConnectsFailsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")

        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), true)
        } throws AuthException(
            1,
            "Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches confAddress"
        )

        mockkStatic(Connect::class)
        every {
            Connect.connectTls("hostname", 1234, "caFilename")
        } returns grpcChannel

        ExpectException.expect {
            Connect.connectWithPassword("clientId", "username", "password", "hostname", 1234, "confAddress", "confCAFilename", "authCAFilename", "caFilename")
        }.toThrow<AuthException>()
            .withMessage("Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches confAddress")
    }

    @Test
    internal fun connectWithPasswordWithKnownTokenFetcherConfig() {
        val httpClient = mockk<HttpClient>()
        val sslContext = mockk<SSLContext>()

        val builder = mockk<HttpClient.Builder>().also {
            every { it.sslContext(sslContext) } returns it
            every { it.build() } returns httpClient
        }

        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher(AuthMethod.ENTRAID, "httpz://offline_test", "audience", httpClient, false)
        } returns tokenFetcher

        mockkStatic(HttpClient::newBuilder)
        every { HttpClient.newBuilder() } returns builder

        every { tokenFetcher.tokenRequestData } returns tokenRequestData

        val grpcChannel = Connect.connectWithPassword(
            "clientId",
            "username",
            "password",
            "audience",
            "httpz://offline_test",
            host = "hostname",
            rpcPort = 1234,
            authMethod = AuthMethod.ENTRAID,
            caFilename = "caFilename"
        )

        assertThat(grpcChannel, equalTo(grpcChannelWithAuth))
        assertThat(
            tokenRequestData, equalTo(
                JsonObject(
                    """
                        {
                            "client_id": "clientId",
                            "username": "username",
                            "password": "password",
                            "grant_type": "password",
                            "scope": "offline_access"
                        }
                    """.trimIndent()
                )
            )
        )
        verifySequence {
            HttpClient.newBuilder()
            builder.build()
            createTokenFetcher(AuthMethod.ENTRAID, "httpz://offline_test", "audience", httpClient, false)
            sslContext wasNot called
            httpClient wasNot called
        }
    }

    @Test
    internal fun connectInsecureJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every { Connect.connectInsecure("localhost", 50051) } returns grpcChannel

        assertThat(Connect.connectInsecure(), equalTo(grpcChannel))
        assertThat(Connect.connectInsecure("localhost"), equalTo(grpcChannel))
    }

    @Test
    internal fun connectTlsJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every { Connect.connectTls("localhost", 50051, null) } returns grpcChannelWithTls

        assertThat(Connect.connectTls(), equalTo(grpcChannelWithTls))
        assertThat(Connect.connectTls("localhost"), equalTo(grpcChannelWithTls))
        assertThat(Connect.connectTls("localhost", 50051), equalTo(grpcChannelWithTls))
    }

    @Test
    internal fun connectWithSecretAndConfAddressJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithSecret("clientId", "clientSecret", "localhost", 50051, null, null, null, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithSecret("clientId", "clientSecret"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "localhost"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "localhost", 50051), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "localhost", 50051, null), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "localhost", 50051, null, null), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "localhost", 50051, null, null, null), equalTo(grpcChannelWithAuth))
    }

    @Test
    internal fun connectWithSecretAndAuthConfJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051, AuthMethod.ENTRAID, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", authMethod = AuthMethod.ENTRAID), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", authMethod = AuthMethod.ENTRAID), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051, authMethod = AuthMethod.ENTRAID), equalTo(grpcChannelWithAuth))
        assertThat(
            Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051, authMethod = AuthMethod.ENTRAID),
            equalTo(grpcChannelWithAuth)
        )
    }

    @Test
    internal fun connectWithPasswordAndConfAddressJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithPassword("clientId", "username", "password", "localhost", 50051, null, null, null, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithPassword("clientId", "username", "password"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "localhost"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "localhost", 50051), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "localhost", 50051, null), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "localhost", 50051, null, null), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "localhost", 50051, null, null, null), equalTo(grpcChannelWithAuth))
    }

    @Test
    internal fun connectWithPasswordAndAuthConfJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithPassword("clientId", "username", "password", "audience", "issuer", "localhost", 50051, AuthMethod.ENTRAID, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuer", authMethod = AuthMethod.ENTRAID), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuer", "localhost", authMethod = AuthMethod.ENTRAID), equalTo(grpcChannelWithAuth))
        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "audience", "issuer", "localhost", 50051, authMethod = AuthMethod.ENTRAID),
            equalTo(grpcChannelWithAuth)
        )
        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "audience", "issuer", "localhost", 50051, authMethod = AuthMethod.ENTRAID),
            equalTo(grpcChannelWithAuth)
        )
    }

    @Test
    internal fun connectWithIdentity() {
        mockkStatic(::createTokenFetcherManagedIdentity)
        every { createTokenFetcherManagedIdentity("identityUrl") } returns tokenFetcher
        assertThat(Connect.connectWithIdentity("identityUrl", "hostname", 1234, "caFilename"), equalTo(grpcChannelWithAuth))
    }

}
