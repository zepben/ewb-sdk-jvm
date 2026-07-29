/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.server.vertx

import com.auth0.jwt.interfaces.DecodedJWT
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.TokenAuthenticator
import com.zepben.ewb.auth.server.asHttpException
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authentication.AuthenticationProvider
import io.vertx.ext.auth.authentication.Credentials
import io.vertx.ext.auth.authentication.TokenCredentials

/**
 * An implementation of an [AuthenticationProvider] that performs JWT authentication with the provided [tokenAuthenticator]
 *
 * @property tokenAuthenticator The Authenticator to use for authentication.
 */
class JWTAuthProvider(
    private val tokenAuthenticator: TokenAuthenticator,
) : AuthenticationProvider {

    /**
     * Authenticate a client based on the provided [credentials].
     * @param credentials A [Credentials] for this client request.
     * @return A future [User] if the [credentials] were a valid JWT.
     */
    override fun authenticate(credentials: Credentials): Future<User> =
        when (credentials) {
            is TokenCredentials -> authenticateToken(credentials)
            else -> Future.failedFuture("Unable to authenticate credentials of type ${credentials::class.simpleName}, only TokenCredentials are supported.")
        }

    private fun authenticateToken(credentials: TokenCredentials): Future<User> {
        val resp = tokenAuthenticator.authenticate(credentials.token)
        return when {
            resp.statusCode !== StatusCode.OK -> Future.failedFuture(resp.asHttpException())
            resp.token != null -> Future.succeededFuture(resp.token.toUser())
            else -> Future.failedFuture("Token was missing on successful auth - this is a bug.")
        }
    }

    private fun DecodedJWT.toUser(): User =
        User.create(
            JsonObject().put("access_token", token),
            JsonObject().put("token", this),
        )

}
