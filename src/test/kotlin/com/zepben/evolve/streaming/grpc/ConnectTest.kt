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
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.vertx.core.json.JsonObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.io.File

internal class ConnectTest {

    private val gcbWithAddress = mock<GrpcChannelBuilder>()
    private val gcbWithTls = mock<GrpcChannelBuilder>()
    private val gcbWithAuth = mock<GrpcChannelBuilder>()

    private val grpcChannel = mock<GrpcChannel>()
    private val grpcChannelWithTls = mock<GrpcChannel>()
    private val grpcChannelWithAuth = mock<GrpcChannel>()

    private val caFile = mock<File>()
    private val tokenFetcher = mock<ZepbenTokenFetcher>()
    private val tokenRequestData = JsonObject()

    init {
        doReturn(gcbWithTls).`when`(gcbWithAddress).makeSecure(eq(caFile), any(), any())
        doReturn(gcbWithAuth).`when`(gcbWithTls).withTokenFetcher(eq(tokenFetcher))

        doReturn(grpcChannel).`when`(gcbWithAddress).build()
        doReturn(grpcChannelWithTls).`when`(gcbWithTls).build()
        doReturn(grpcChannelWithAuth).`when`(gcbWithAuth).build()

        doReturn(tokenRequestData).`when`(tokenFetcher).tokenRequestData
    }

    @BeforeEach
    fun clearTokenRequestData() {
        tokenRequestData.clear()
    }

    @AfterEach
    fun teardownMockks() {
        unmockkAll()
    }

    @Test
    fun connectInsecure() {
        mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
            doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            assertThat(Connect.connectInsecure("hostname", 1234), equalTo(grpcChannel))
        }
    }

    @Test
    fun connectTls() {
        mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
            doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            assertThat(Connect.connectTls("hostname", 1234, caFile), equalTo(grpcChannelWithTls))
        }
    }

    @Test
    fun connectWithSecret() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), "confCAFilename", "authCAFilename")
        } returns tokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
            doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            val grpcChannel = Connect.connectWithSecret(
                "clientId",
                "clientSecret",
                "confAddress",
                "confCAFilename",
                "authCAFilename",
                "hostname",
                1234,
                caFile
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
    }

    @Test
    fun connectWithSecretConnectsWithTlsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), "confCAFilename", "authCAFilename")
        } returns null

        mockkObject(Connect)
        every {
            Connect.connectTls("hostname", 1234, caFile)
        } returns grpcChannelWithTls

        assertThat(
            Connect.connectWithSecret("clientId", "clientSecret", "confAddress", "confCAFilename", "authCAFilename", "hostname", 1234, caFile),
            equalTo(grpcChannelWithTls)
        )
    }

    @Test
    fun connectWithSecretWithKnownTokenFetcherConfig() {
        mockConstruction(ZepbenTokenFetcher::class.java) { tf, context ->
            val arguments = context.arguments()
            assertThat(arguments[0], equalTo("audience"))
            assertThat(arguments[1], equalTo("issuerDomain"))
            assertThat(arguments[2], equalTo(AuthMethod.AUTH0))
            doReturn(tokenRequestData).`when`(tf).tokenRequestData
            doReturn(gcbWithAuth).`when`(gcbWithTls).withTokenFetcher(tf)
        }.use {
            mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
                doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
            }.use {
                val grpcChannel = Connect.connectWithSecret(
                    "clientId",
                    "clientSecret",
                    "audience",
                    "issuerDomain",
                    AuthMethod.AUTH0,
                    host = "hostname",
                    rpcPort = 1234,
                    ca = caFile
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
        }
    }

    @Test
    fun connectWithPassword() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), "confCAFilename", "authCAFilename")
        } returns tokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
            doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            val grpcChannel = Connect.connectWithPassword(
                "clientId",
                "username",
                "password",
                "confAddress",
                "confCAFilename",
                "authCAFilename",
                "hostname",
                1234,
                caFile
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
    }

    @Test
    fun connectWithPasswordConnectsWithTlsIfNoAuth() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), "confCAFilename", "authCAFilename")
        } returns null

        mockkObject(Connect)
        every {
            Connect.connectTls("hostname", 1234, caFile)
        } returns grpcChannel

        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "confAddress", "confCAFilename", "authCAFilename", "hostname", 1234, caFile),
            equalTo(grpcChannel)
        )
    }

    @Test
    fun connectWithPasswordWithKnownTokenFetcherConfig() {
        mockConstruction(ZepbenTokenFetcher::class.java) { tf, context ->
            val arguments = context.arguments()
            assertThat(arguments[0], equalTo("audience"))
            assertThat(arguments[1], equalTo("issuerDomain"))
            assertThat(arguments[2], equalTo(AuthMethod.AUTH0))
            doReturn(tokenRequestData).`when`(tf).tokenRequestData
            doReturn(gcbWithAuth).`when`(gcbWithTls).withTokenFetcher(tf)
        }.use {
            mockConstruction(GrpcChannelBuilder::class.java) { gcbBase, _ ->
                doReturn(gcbWithAddress).`when`(gcbBase).forAddress(eq("hostname"), eq(1234))
            }.use {
                val grpcChannel = Connect.connectWithPassword(
                    "clientId",
                    "username",
                    "password",
                    "audience",
                    "issuerDomain",
                    AuthMethod.AUTH0,
                    host = "hostname",
                    rpcPort = 1234,
                    ca = caFile
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
        }
    }
}