/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.auth.client.ZepbenTokenFetcher
import com.zepben.auth.client.createTokenFetcher
import com.zepben.auth.common.AuthMethod
import io.mockk.*
import io.vertx.core.json.JsonObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ConnectTest {

    private val gcbWithAddress = mockk<GrpcChannelBuilder>()
    private val gcbWithTls = mockk<GrpcChannelBuilder>()
    private val gcbWithAuth = mockk<GrpcChannelBuilder>()

    private val grpcChannel = mockk<GrpcChannel>()
    private val grpcChannelWithTls = mockk<GrpcChannel>()
    private val grpcChannelWithAuth = mockk<GrpcChannel>()

    private val tokenFetcher = mockk<ZepbenTokenFetcher>()
    private val tokenRequestData = JsonObject()

    @BeforeEach
    fun beforeEach() {
        tokenRequestData.clear()

        every { gcbWithAddress.makeSecure("caFilename") } returns gcbWithTls
        every { gcbWithTls.withTokenFetcher(tokenFetcher) } returns gcbWithAuth

        every { gcbWithAddress.build() } returns grpcChannel
        every { gcbWithTls.build() } returns grpcChannelWithTls
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
    fun connectInsecure() {
        assertThat(Connect.connectInsecure("hostname", 1234), equalTo(grpcChannel))
    }

    @Test
    fun connectTls() {
        assertThat(Connect.connectTls("hostname", 1234, "caFilename"), equalTo(grpcChannelWithTls))
    }

    @Test
    fun connectWithSecret() {
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
        assertThat(tokenRequestData, equalTo(
            JsonObject("""
                {
                    "client_id": "clientId",
                    "client_secret": "clientSecret",
                    "grant_type": "client_credentials"
                }
            """.trimIndent())
        ))
    }

    @Test
    fun connectWithSecretConnectsWithTlsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), any(), any())
        } returns null

        mockkStatic(Connect::class)
        every {
            Connect.connectTls("hostname", 1234, "caFilename")
        } returns grpcChannelWithTls

        assertThat(
            Connect.connectWithSecret("clientId", "clientSecret", "hostname", 1234, "confAddress", "confCAFilename", "authCAFilename", "caFilename"),
            equalTo(grpcChannelWithTls)
        )
    }

    @Test
    fun connectWithSecretWithKnownTokenFetcherConfig() {
        mockkConstructor(ZepbenTokenFetcher::class)
        every {
            anyConstructed<ZepbenTokenFetcher>().tokenRequestData
        } returns tokenRequestData

        every { gcbWithTls.withTokenFetcher(any()) } returns gcbWithAuth

        val grpcChannel = Connect.connectWithSecret(
            "clientId",
            "clientSecret",
            "audience",
            "issuerDomain",
            "hostname",
            1234,
            AuthMethod.OAUTH,
            caFilename = "caFilename"
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
    fun connectWithPassword() {
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
        assertThat(tokenRequestData, equalTo(
            JsonObject("""
                {
                    "client_id": "clientId",
                    "username": "username",
                    "password": "password",
                    "grant_type": "password",
                    "scope": "offline_access"
                }
            """.trimIndent())
        ))
    }

    @Test
    fun connectWithPasswordConnectsWithTlsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any(), any())
        } returns null

        mockkStatic(Connect::class)
        every {
            Connect.connectTls("hostname", 1234, "caFilename")
        } returns grpcChannel

        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "hostname", 1234, "confAddress", "confCAFilename", "authCAFilename", "caFilename"),
            equalTo(grpcChannel)
        )
    }

    @Test
    fun connectWithPasswordWithKnownTokenFetcherConfig() {
        mockkConstructor(ZepbenTokenFetcher::class)
        every {
            anyConstructed<ZepbenTokenFetcher>().tokenRequestData
        } returns tokenRequestData

        every { gcbWithTls.withTokenFetcher(any()) } returns gcbWithAuth

        val grpcChannel = Connect.connectWithPassword(
            "clientId",
            "username",
            "password",
            "audience",
            "issuerDomain",
            host = "hostname",
            rpcPort = 1234,
            AuthMethod.OAUTH,
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
    }

    @Test
    fun connectInsecureJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every { Connect.connectInsecure("localhost", 50051) } returns grpcChannel

        assertThat(Connect.connectInsecure(), equalTo(grpcChannel))
        assertThat(Connect.connectInsecure("localhost"), equalTo(grpcChannel))
    }

    @Test
    fun connectTlsJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every { Connect.connectTls("localhost", 50051, null) } returns grpcChannelWithTls

        assertThat(Connect.connectTls(), equalTo(grpcChannelWithTls))
        assertThat(Connect.connectTls("localhost"), equalTo(grpcChannelWithTls))
        assertThat(Connect.connectTls("localhost", 50051), equalTo(grpcChannelWithTls))
    }

    @Test
    fun connectWithSecretAndConfAddressJvmOverloadsCoverage() {
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
    fun connectWithSecretAndAuthConfJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051, AuthMethod.OAUTH, null, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithSecret("clientId", "clientSecret", "audience", "issuerDomain", "localhost", 50051, AuthMethod.OAUTH), equalTo(grpcChannelWithAuth))
    }

    @Test
    fun connectWithPasswordAndConfAddressJvmOverloadsCoverage() {
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
    fun connectWithPasswordAndAuthConfJvmOverloadsCoverage() {
        mockkStatic(Connect::class)
        every {
            Connect.connectWithPassword("clientId", "username", "password", "audience", "issuerDomain", "localhost", 50051, AuthMethod.OAUTH, null, null)
        } returns grpcChannelWithAuth

        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuerDomain"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuerDomain", "localhost"), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuerDomain", "localhost", 50051), equalTo(grpcChannelWithAuth))
        assertThat(Connect.connectWithPassword("clientId", "username", "password", "audience", "issuerDomain", "localhost", 50051, AuthMethod.OAUTH), equalTo(grpcChannelWithAuth))
    }

}
