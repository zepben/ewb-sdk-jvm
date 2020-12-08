/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put.grpc

import com.zepben.evolve.streaming.grpc.BadConfigException
import com.zepben.evolve.streaming.grpc.ConnectionConfig
import com.zepben.evolve.streaming.grpc.GrpcChannelFactory
import com.zepben.testutils.exception.ExpectException.expect
import org.junit.jupiter.api.Test

internal class GrpcChannelFactoryTest {

    @Test
    fun createsChannel() {
        // TODO How do we actually test the channel is configured correctly?
        val config = ConnectionConfig("localhost", 80)
        val channel = GrpcChannelFactory.create(config)
        channel.shutdownNow()
    }

    @Test
    internal fun requiresKeyAndCertForAuth() {
        val config = ConnectionConfig("localhost", 80, enableTls = true, authCertPath = "someFile")
        expect { GrpcChannelFactory.create(config) }
            .toThrow(BadConfigException::class.java)
            .withMessage("If TLS auth is enabled you must specify a key and cert")
    }

    @Test
    fun onlyRequiresKeyAndCertForAuthWithTls() {
        val config = ConnectionConfig("localhost", 80, enableTls = false, authCertPath = "someFile")
        val channel = GrpcChannelFactory.create(config)
        channel.shutdownNow()
    }

}
