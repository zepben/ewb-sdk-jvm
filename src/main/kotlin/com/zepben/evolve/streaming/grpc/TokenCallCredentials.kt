/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.Status
import java.util.concurrent.Executor

internal val AUTHORIZATION_METADATA_KEY: Metadata.Key<String> = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)

/**
 * Call credentials that sets the "Authorization" metadata header to a token string.
 * The token string is updated each call using a factory function specified upon instantiation.
 * In practice, this function will be a method of an object that manages token retrieval and returns a cached token if it isn't expired.
 */
class TokenCallCredentials(private val getToken: () -> String): CallCredentials() {

    override fun applyRequestMetadata(requestInfo: RequestInfo, executor: Executor, applier: MetadataApplier) {
        try {
            val headers = Metadata()
            headers.put(AUTHORIZATION_METADATA_KEY, getToken())
            applier.apply(headers)
        } catch (e: Exception) {
            applier.fail(Status.fromThrowable(e))
        }
    }

    override fun thisUsesUnstableApi() {}

}
