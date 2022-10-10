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
import com.zepben.auth.common.AuthMethod
import java.io.File

/**
 * A collection of functions that return a channel that connects to Evolve's gRPC service, given address and authentication details.
 */
object Connect {

    fun connectInsecure(
        host: String = "localhost",
        rpcPort: Int = 50051
    ): GrpcChannel =
        GrpcChannelBuilder().forAddress(host, rpcPort).build()

    fun connectTls(
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel =
        GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = ca).build()

    fun connectWithSecret(
        clientId: String,
        clientSecret: String,
        confAddress: String,
        confCAFilename: String? = null,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        val tokenFetcher = createTokenFetcher(confAddress, confCAFilename = confCAFilename, authCAFilename = authCAFilename)
            ?: return connectTls(host, rpcPort, ca)

        return connectWithSecretUsingTokenFetcher(tokenFetcher, clientId, clientSecret, host, rpcPort, ca)
    }

    fun connectWithSecret(
        clientId: String,
        clientSecret: String,
        audience: String,
        issuerDomain: String,
        authMethod: AuthMethod,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        val tokenFetcher = ZepbenTokenFetcher(audience = audience, issuerDomain = issuerDomain, authMethod = authMethod)

        return connectWithSecretUsingTokenFetcher(tokenFetcher, clientId, clientSecret, host, rpcPort, ca)
    }

    fun connectWithPassword(
        clientId: String,
        username: String,
        password: String,
        confAddress: String,
        confCAFilename: String? = null,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        val tokenFetcher = createTokenFetcher(confAddress, confCAFilename = confCAFilename, authCAFilename = authCAFilename)
            ?: return connectTls(host, rpcPort, ca)

        return connectWithPasswordUsingTokenFetcher(tokenFetcher, clientId, username, password, host, rpcPort, ca)
    }

    fun connectWithPassword(
        clientId: String,
        username: String,
        password: String,
        audience: String,
        issuerDomain: String,
        authMethod: AuthMethod,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        val tokenFetcher = ZepbenTokenFetcher(audience = audience, issuerDomain = issuerDomain, authMethod = authMethod)

        return connectWithPasswordUsingTokenFetcher(tokenFetcher, clientId, username, password, host, rpcPort, ca)
    }

    private fun connectWithSecretUsingTokenFetcher(
        tokenFetcher: ZepbenTokenFetcher,
        clientId: String,
        clientSecret: String,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        tokenFetcher.tokenRequestData.put("client_id", clientId)
        tokenFetcher.tokenRequestData.put("client_secret", clientSecret)
        tokenFetcher.tokenRequestData.put("grant_type", "client_credentials")

        return GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = ca).withTokenFetcher(tokenFetcher).build()
    }

    private fun connectWithPasswordUsingTokenFetcher(
        tokenFetcher: ZepbenTokenFetcher,
        clientId: String,
        username: String,
        password: String,
        host: String = "localhost",
        rpcPort: Int = 50051,
        ca: File? = null
    ): GrpcChannel {
        tokenFetcher.tokenRequestData.put("client_id", clientId)
        tokenFetcher.tokenRequestData.put("username", username)
        tokenFetcher.tokenRequestData.put("password", password)
        tokenFetcher.tokenRequestData.put("grant_type", "password")
        tokenFetcher.tokenRequestData.put("scope", "offline_access")

        return GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = ca).withTokenFetcher(tokenFetcher).build()
    }

}
