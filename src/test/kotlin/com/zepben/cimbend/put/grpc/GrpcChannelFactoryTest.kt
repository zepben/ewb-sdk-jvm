/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zepben.cimbend.put.grpc

import com.zepben.cimbend.put.ConnectionConfig
import org.junit.jupiter.api.Test

internal class GrpcChannelFactoryTest {

    @Test
    fun createsChannel() {
        // TODO How do we actually test the channel is configured correctly?
        val config = ConnectionConfig("localhost", 80)
        val channel = GrpcChannelFactory.create(config)
        channel.shutdownNow()
    }
}
