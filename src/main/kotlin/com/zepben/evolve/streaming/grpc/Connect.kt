/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import java.io.File

object Connect {

    fun connectInsecure(host: String = "localhost", rpcPort: Int = 50051): GrpcChannel =
        GrpcChannelBuilder().socketAddress(host, rpcPort).build()

    fun connectTls(host: String = "localhost", rpcPort: Int = 50051, ca: File? = null): GrpcChannel =
        GrpcChannelBuilder().socketAddress(host, rpcPort).makeSecure(rootCertificates = ca).build()

}