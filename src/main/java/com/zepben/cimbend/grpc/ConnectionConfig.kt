/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.grpc

/**
 * Configuration for connecting to a gRPC Producer server.
 *
 * @property host The host of the producer server
 * @property port The port the producer server is listening on
 * @property certChainFilePath
 * @property privateKeyFilePath
 * @property trustCertCollectionFilePath
 * @property disableTls Indicates if TLS should be disabled for the connection [default: false].
 */
data class ConnectionConfig(
    val host: String,
    val port: Int,
    val certChainFilePath: String? = null,
    val privateKeyFilePath: String? = null,
    val trustCertCollectionFilePath: String? = null,
    val enableTls: Boolean = false
)
