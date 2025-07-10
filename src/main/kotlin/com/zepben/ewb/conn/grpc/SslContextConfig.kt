/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.conn.grpc

import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth

data class SslContextConfig(
    val certChainFilePath: String? = null,
    val privateKeyFilePath: String? = null,
    val trustCertCollectionFilePath: String? = null,
    val clientAuth: ClientAuth = ClientAuth.OPTIONAL
)
