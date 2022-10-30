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

/**
 * A collection of functions that return a channel that connects to Evolve's gRPC service, given address and authentication details.
 */
object Connect {

    @JvmStatic
    fun connectInsecure(
        host: String = "localhost",
        rpcPort: Int = 50051
    ): GrpcChannel =
        GrpcChannelBuilder().forAddress(host, rpcPort).build()

    @JvmStatic
    fun connectTls(
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel =
        GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = caFilename).build()

    @JvmStatic
    fun connectWithSecret(
        clientId: String,
        clientSecret: String,
        confAddress: String? = null,
        confCAFilename: String? = null,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        val tokenFetcher = createTokenFetcher(confAddress ?: "https://$host/ewb/auth", confCAFilename = confCAFilename, authCAFilename = authCAFilename)
            ?: return connectTls(host, rpcPort, caFilename)

        return connectWithSecretUsingTokenFetcher(tokenFetcher, clientId, clientSecret, host, rpcPort, caFilename)
    }

    @JvmStatic
    fun connectWithSecret(
        clientId: String,
        clientSecret: String,
        audience: String,
        issuerDomain: String,
        authMethod: AuthMethod,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        val tokenFetcher = ZepbenTokenFetcher(audience = audience, issuerDomain = issuerDomain, authMethod = authMethod, caFilename = authCAFilename)

        return connectWithSecretUsingTokenFetcher(tokenFetcher, clientId, clientSecret, host, rpcPort, caFilename)
    }

    @JvmStatic
    fun connectWithPassword(
        clientId: String,
        username: String,
        password: String,
        confAddress: String? = null,
        confCAFilename: String? = null,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        val tokenFetcher = createTokenFetcher(confAddress ?: "https://$host/ewb/auth", confCAFilename = confCAFilename, authCAFilename = authCAFilename)
            ?: return connectTls(host, rpcPort, caFilename)

        return connectWithPasswordUsingTokenFetcher(tokenFetcher, clientId, username, password, host, rpcPort, caFilename)
    }

    @JvmStatic
    fun connectWithPassword(
        clientId: String,
        username: String,
        password: String,
        audience: String,
        issuerDomain: String,
        authMethod: AuthMethod,
        authCAFilename: String? = null,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        val tokenFetcher = ZepbenTokenFetcher(audience = audience, issuerDomain = issuerDomain, authMethod = authMethod, caFilename = authCAFilename)

        return connectWithPasswordUsingTokenFetcher(tokenFetcher, clientId, username, password, host, rpcPort, caFilename)
    }

    private fun connectWithSecretUsingTokenFetcher(
        tokenFetcher: ZepbenTokenFetcher,
        clientId: String,
        clientSecret: String,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        tokenFetcher.tokenRequestData.put("client_id", clientId)
        tokenFetcher.tokenRequestData.put("client_secret", clientSecret)
        tokenFetcher.tokenRequestData.put("grant_type", "client_credentials")

        return GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = caFilename).withTokenFetcher(tokenFetcher).build()
    }

    private fun connectWithPasswordUsingTokenFetcher(
        tokenFetcher: ZepbenTokenFetcher,
        clientId: String,
        username: String,
        password: String,
        host: String = "localhost",
        rpcPort: Int = 50051,
        caFilename: String? = null
    ): GrpcChannel {
        tokenFetcher.tokenRequestData.put("client_id", clientId)
        tokenFetcher.tokenRequestData.put("username", username)
        tokenFetcher.tokenRequestData.put("password", password)
        tokenFetcher.tokenRequestData.put("grant_type", "password")
        tokenFetcher.tokenRequestData.put("scope", "offline_access")

        return GrpcChannelBuilder().forAddress(host, rpcPort).makeSecure(rootCertificates = caFilename).withTokenFetcher(tokenFetcher).build()
    }

}
