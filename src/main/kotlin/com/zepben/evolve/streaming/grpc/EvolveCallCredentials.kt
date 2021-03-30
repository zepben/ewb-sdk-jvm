/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object EvolveCallCredentials {

    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    /**
     * Create a CallCredentials for authenticating with a JWT based OAuth2 system like Auth0
     *
     * @param clientId The Client ID for the authentication service
     * @param clientSecret The corresponding secret for the client ID.
     * @param confAddress A HTTP URL for looking up AuthConfig for the server. If null you must provide [config].
     * @param config The [AuthConfig] for the OAuth2 server. If null you must provide a [confAddress].
     */
    @JvmStatic
    @JvmOverloads
    fun create(
        clientId: String,
        clientSecret: String,
        confAddress: String? = null,
        config: AuthConfig? = null,
    ): JwtCredentials {
        val client = HttpClient.newBuilder().build()
        val authConfig = confAddress?.let {
            val response = client.send(HttpRequest.newBuilder(URI.create(confAddress)).build(), HttpResponse.BodyHandlers.ofString())
            jsonFormatter.decodeFromString<AuthConfig>(response.body())
        } ?: config ?: throw AuthException("One of confAddress or config must be provided.")

        return JwtCredentials(clientId, clientSecret, authConfig)
    }
}