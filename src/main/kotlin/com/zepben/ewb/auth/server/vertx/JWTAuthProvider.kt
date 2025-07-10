/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.server.vertx

import com.zepben.ewb.auth.*
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.TokenAuthenticator
import com.zepben.ewb.auth.server.asHttpException
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authentication.AuthenticationProvider
import io.vertx.ext.auth.authentication.Credentials

/**
 * An implementation of an [AuthenticationProvider] that performs JWT authentication with the provided [tokenAuthenticator]
 *
 * @property tokenAuthenticator The Authenticator to use for authentication.
 */
class JWTAuthProvider(private val tokenAuthenticator: TokenAuthenticator) : AuthenticationProvider {

    @Deprecated("Deprecated in Java")
    override fun authenticate(authInfo: JsonObject?, resultHandler: Handler<AsyncResult<User>>?) {
        val token: String? = authInfo?.getString("jwt")
        val resp = tokenAuthenticator.authenticate(token)
        if (resp.statusCode !== StatusCode.OK) {
            resultHandler?.handle(Future.failedFuture(resp.asHttpException()))
            return
        }

        resp.token?.let {
            val user = User.create(JsonObject().put("access_token", it.token), JsonObject().put("token", it))
            resultHandler?.handle(Future.succeededFuture(user))
        } ?: resultHandler?.handle(
            Future.failedFuture("Token was missing on successful auth - this is a bug.")
        )
    }

    /**
     * Authenticate a client based on the provided [authInfo].
     * @param A [JsonObject] with a "jwt" entry with the JWT for this client.
     */
    override fun authenticate(credentials: Credentials?, resultHandler: Handler<AsyncResult<User>>?) = authenticate(credentials?.toJson(), resultHandler)

}
