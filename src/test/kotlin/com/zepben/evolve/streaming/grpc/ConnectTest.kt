/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.io.File

internal class ConnectTest {

    @Test
    fun connectInsecure() {
        val mockedGcbWithAddress = mock<GrpcChannelBuilder>()
        val mockedGrpcChannel = mock<GrpcChannel>()

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
            doReturn(mockedGrpcChannel).`when`(mockedGcbWithAddress).build()
        }.use {
            assertThat(Connect.connectInsecure("hostname", 1234), equalTo(mockedGrpcChannel))
        }
    }

    @Test
    fun connectTls() {
        val mockedGcbWithAddress = mock<GrpcChannelBuilder>()
        val mockedGcbWithTls = mock<GrpcChannelBuilder>()
        val mockedGrpcChannel = mock<GrpcChannel>()
        val mockedCaFile = mock<File>()

        mockConstruction(GrpcChannelBuilder::class.java) { mockedGcbBase, _ ->
            doReturn(mockedGcbWithAddress).`when`(mockedGcbBase).forAddress(eq("hostname"), eq(1234))
            doReturn(mockedGcbWithTls).`when`(mockedGcbWithAddress).makeSecure(eq(mockedCaFile), any(), any())
            doReturn(mockedGrpcChannel).`when`(mockedGcbWithTls).build()
        }.use {
            assertThat(Connect.connectTls("hostname", 1234, mockedCaFile), equalTo(mockedGrpcChannel))
        }
    }

    @Test
    fun connectWithSecret() {
    }

    @Test
    fun testConnectWithSecret() {
    }

    @Test
    fun connectWithPassword() {
    }

    @Test
    fun testConnectWithPassword() {
    }
}