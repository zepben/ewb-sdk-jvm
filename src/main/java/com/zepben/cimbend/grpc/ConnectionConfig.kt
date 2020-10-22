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
 * @property enableTls Indicates if TLS should be enabled for the connection [default: false].
 * @property trustCertPath The path of the trust certificate that signed the server certificate, null to use system default [default: use system default].
 * @property authCertPath The path of the client certificate to use for authentication (must be signed by the servers authentication trust certificate). Use null to disable [default: disabled].
 * @property authKeyPath The path of the private key for the client authentication certificate if one is specified, otherwise ignored.
 */
data class ConnectionConfig(
    val host: String,
    val port: Int,
    val enableTls: Boolean = false,
    val trustCertPath: String? = null,
    val authCertPath: String? = null,
    val authKeyPath: String? = null
)
