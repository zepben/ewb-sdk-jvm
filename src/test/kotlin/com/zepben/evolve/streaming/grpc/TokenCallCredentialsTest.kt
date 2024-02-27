/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.CallCredentials.MetadataApplier
import io.grpc.CallCredentials.RequestInfo
import io.grpc.Metadata
import io.grpc.Status
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.Executor

internal class TokenCallCredentialsTest {

    @AfterEach
    fun teardownMockks() {
        unmockkAll()
    }

    @Test
    internal fun applyRequestMetadata() {
        mockkConstructor(Metadata::class)
        val requestInfo = mockk<RequestInfo>()
        val executor = mockk<Executor>()
        val applier = mockk<MetadataApplier>()
        justRun { applier.apply(any()) }

        TokenCallCredentials { "token" }.applyRequestMetadata(requestInfo, executor, applier)
        verify { requestInfo wasNot called }
        verify { executor wasNot called }
        verify { constructedWith<Metadata>().put(AUTHORIZATION_METADATA_KEY, "token") }
        verify { applier.apply(any()) }
    }

    @Test
    internal fun applyRequestMetadataFailsWithException() {
        mockkConstructor(Metadata::class)
        val requestInfo = mockk<RequestInfo>()
        val executor = mockk<Executor>()
        val applier = mockk<MetadataApplier>()
        val exception = IOException("Example Exception")
        every { applier.apply(any()) } throws exception
        every { applier.fail(any()) } just runs

        mockkStatic(Status::class)
        val errorStatus = mockk<Status>()
        every { Status.fromThrowable(exception) } returns errorStatus

        TokenCallCredentials { "token" }.applyRequestMetadata(requestInfo, executor, applier)
        verify { requestInfo wasNot called }
        verify { executor wasNot called }
        verify { constructedWith<Metadata>().put(AUTHORIZATION_METADATA_KEY, "token") }
        verify { applier.fail(errorStatus) }
    }

    @Test
    internal fun thisUsesUnstableApiCoverage() {
        TokenCallCredentials { "token" }.thisUsesUnstableApi()
    }
}
