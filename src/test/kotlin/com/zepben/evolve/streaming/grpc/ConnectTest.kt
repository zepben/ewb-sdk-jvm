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
import java.net.http.HttpClient

internal class ConnectTest {

    private val mockedGcbWithAddress = mock<GrpcChannelBuilder>()
    private val mockedGcbWithTls = mock<GrpcChannelBuilder>()
    private val mockedGcbWithAuth = mock<GrpcChannelBuilder>()

    private val mockedGrpcChannel = mock<GrpcChannel>()
    private val mockedGrpcChannelWithTls = mock<GrpcChannel>()
    private val mockedGrpcChannelWithAuth = mock<GrpcChannel>()

    private val mockedCaFile = mock<File>()
    private val mockedTokenFetcher = mock<ZepbenTokenFetcher>()
    private val tokenRequestData = JsonObject()

    init {
        doReturn(mockedGcbWithTls).`when`(mockedGcbWithAddress).makeSecure(eq(mockedCaFile), any(), any())
        doReturn(mockedGcbWithAuth).`when`(mockedGcbWithTls).withTokenFetcher(mockedTokenFetcher)

        doReturn(mockedGrpcChannel).`when`(mockedGcbWithAddress).build()
        doReturn(mockedGrpcChannelWithTls).`when`(mockedGcbWithTls).build()
        doReturn(mockedGrpcChannelWithAuth).`when`(mockedGcbWithAuth).build()

        doReturn(tokenRequestData).`when`(mockedTokenFetcher).tokenRequestData
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
        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            assertThat(Connect.connectInsecure("hostname", 1234), equalTo(mockedGrpcChannel))
        }
    }

    @Test
    fun connectTls() {
        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            assertThat(Connect.connectTls("hostname", 1234, mockedCaFile), equalTo(mockedGrpcChannelWithTls))
        }
    }

    @Test
    fun connectWithSecret() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any<String>(), any())
        } returns mockedTokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            val grpcChannel = Connect.connectWithSecret(
                "clientId",
                "clientSecret",
                "confAddress",
                "confCAFilename",
                "authCAFilename",
                "hostname",
                1234,
                mockedCaFile
            )

            assertThat(grpcChannel, equalTo(mockedGrpcChannelWithAuth))
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
    fun connectWithSecretNoCaFilenames() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), any<HttpClient>(), any())
        } returns mockedTokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            val grpcChannel = Connect.connectWithSecret("clientId", "clientSecret", "confAddress", "hostname", 1234, mockedCaFile)

            assertThat(grpcChannel, equalTo(mockedGrpcChannelWithAuth))
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
    fun connectWithSecretUsesConnectWithTlsAsFallback() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any<String>(), any())
        } returns null
        every {
            createTokenFetcher("confAddress", any(), any(), any(), any<HttpClient>(), any())
        } returns null

        mockkObject(Connect)
        every {
            Connect.connectTls("hostname", 1234, mockedCaFile)
        } returns mockedGrpcChannelWithTls

        assertThat(
            Connect.connectWithSecret("clientId", "clientSecret", "confAddress", "confCAFilename", "authCAFilename", "hostname", 1234, mockedCaFile),
            equalTo(mockedGrpcChannelWithTls)
        )
        assertThat(
            Connect.connectWithSecret("clientId", "clientSecret", "confAddress", "hostname", 1234, mockedCaFile),
            equalTo(mockedGrpcChannelWithTls)
        )
    }

    @Test
    fun connectWithPassword() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any<String>(), any())
        } returns mockedTokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
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
                mockedCaFile
            )

            assertThat(grpcChannel, equalTo(mockedGrpcChannelWithAuth))
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
    fun connectWithPasswordNoCaFilenames() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", any(), any(), any(), any<HttpClient>(), any())
        } returns mockedTokenFetcher

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
        }.use {
            val grpcChannel = Connect.connectWithPassword("clientId", "username", "password", "confAddress", "hostname", 1234, mockedCaFile)

            assertThat(grpcChannel, equalTo(mockedGrpcChannelWithAuth))
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
    fun connectWithPasswordUsesConnectWithTlsAsFallback() {
        mockkStatic("com.zepben.auth.client.ZepbenTokenFetcherKt")
        every {
            createTokenFetcher("confAddress", "confCAFilename", "authCAFilename", any(), any<String>(), any())
        } returns null
        every {
            createTokenFetcher("confAddress", any(), any(), any(), any<HttpClient>(), any())
        } returns null

        mockkObject(Connect)
        every {
            Connect.connectTls("hostname", 1234, mockedCaFile)
        } returns mockedGrpcChannel

        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "confAddress", "confCAFilename", "authCAFilename", "hostname", 1234, mockedCaFile),
            equalTo(mockedGrpcChannel)
        )
        assertThat(
            Connect.connectWithPassword("clientId", "username", "password", "confAddress", "hostname", 1234, mockedCaFile),
            equalTo(mockedGrpcChannel)
        )
    }
}