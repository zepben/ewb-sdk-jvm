/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
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
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * OAuth2 configuration. A Moshi JSON adapter will be generated for this class.
 *
 * @param tokenUrl The URL to use when fetching tokens.
 * @param audience The audience to fetch a token for.
 * @param authType The type of auth.
 */
@Serializable
data class AuthConfig(@SerialName("issuer") val tokenUrl: String, val audience: String, val authType: AuthType = AuthType.AUTH0)

/**
 * A JSON Web Token response retrieved from an OAuth2 service (Auth0).
 * Either [token_type] and [access_token] or [error] and [error_description] will be set, but never both.
 * A Moshi JSON adapter will be generated for this class.
 *
 * @param token_type The type of token (generally Bearer)
 * @param access_token The JWT.
 * @param error The error, if one occurred.
 * @param error_description The description of the error, if one occurred.
 */
@Serializable
internal data class JWTResponse(
    val token_type: String? = null,
    val access_token: String? = null,
    val error: String? = null,
    val error_description: String? = null
)

/**
 * Describes a JWT token request to Auth0
 * A Moshi JSON adapter will be generated for this class.
 *
 * @param client_id The client ID to request a token for
 * @param client_secret The corresponding secret.
 * @param audience The audience to request a JWT for.
 * @param grant_type The type of grant to request.
 */
@Serializable
internal data class TokenRequest(val client_id: String, val client_secret: String, val audience: String, val grant_type: String = "client_credentials")


internal val AUTHORISATION_METADATA_KEY: Metadata.Key<String> = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)

/**
 * [CallCredentials] for fetching and managing refresh tokens from an OAuth2 service, and attaching the token in the Authorization header of requests.
 *
 * @property clientId Client ID for authenticating to OAuth2 provider
 * @property clientSecret Client secret for authenticating to OAuth2 provider
 * @param authConfig An [AuthConfig] to be used for fetching OAuth2 refresh tokens.
 */
class JwtCredentials(
    private val clientId: String,
    private val clientSecret: String,
    val authConfig: AuthConfig
) : CallCredentials() {

    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    @Volatile
    private var token: String? = null

    @Volatile
    private var tokenExpiry: Instant = Instant.MIN
    private val lock = ReentrantLock()

    override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
        appExecutor.execute {
            try {
                val now = Instant.now()
                // We don't want to lock just to check expiry, so we check expiry again within the lock so that we don't refresh multiple times unnecessarily
                // if multiple threads were waiting on the lock.
                // TODO: This could be done in the background just before the token expires so that we don't hold up requests.
                if (now > tokenExpiry) {
                    lock.withLock {
                        if (now > tokenExpiry)
                            refreshToken()
                    }
                }

                val headers = Metadata()
                token?.let { headers.put(AUTHORISATION_METADATA_KEY, token) }
                applier.apply(headers)
            } catch (t: Throwable) {
                applier.fail(Status.UNAUTHENTICATED.withCause(t).withDescription(t.message))
            }
        }
    }

    /**
     * Refresh the token. Will update the token if an [authConfig.tokenUrl] is configured.
     * No verification of the token is performed, we simply trust we've been given the correct token.
     */
    @Synchronized
    internal fun refreshToken() {
        val jwt = getToken()
        if (jwt == null) {  // Auth is disabled
            tokenExpiry = Instant.MAX
            return
        }

        token = "${jwt.token_type} ${jwt.access_token}"

        val decodedJWT = com.auth0.jwt.JWT.decode(jwt.access_token)
        tokenExpiry = decodedJWT.expiresAt.toInstant()
    }

    /**
     * Get a token from an OAuth2 JWT provider from [AuthConfig.tokenUrl] for [AuthConfig.audience].
     *
     * @return a [JWTResponse] if one could be retrieved, or null if authentication wasn't required.
     * @throws AuthException if a token was failed to be retrieved or contained an error.
     * @throws Exception Any exception possible from [HttpClient.send]
     */
    @Synchronized
    private fun getToken(): JWTResponse? {
        val client = HttpClient.newBuilder().build()
        if (authConfig.authType == AuthType.NONE) {
            return null
        }

        val tokenRequest = TokenRequest(clientId, clientSecret, authConfig.audience)

        val response = client.send(
            HttpRequest.newBuilder(URI.create(authConfig.tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(tokenRequest)))
                .setHeader("content-type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )

        if (response.statusCode() != 200)
            throw AuthException("Failed to fetch token. ${response.statusCode()} ${response.body()}")

        return try {
            val token = jsonFormatter.decodeFromString<JWTResponse>(response.body())
            if (token.error != null)
                throw AuthException("${token.error}: ${token.error_description}")
            token
        } catch (e: SerializationException) {
            throw AuthException("Did not receive a valid JWT from ${authConfig.tokenUrl}", e)
        }

    }


    override fun thisUsesUnstableApi() {}

}


class AuthException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)