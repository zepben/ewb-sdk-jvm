/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.server

import com.auth0.jwt.interfaces.DecodedJWT
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.vertx.JWTAuthProvider
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.VertxException
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authentication.Credentials
import io.vertx.ext.auth.authentication.TokenCredentials
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.HttpException
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl

/**
 * A route handler that supports the Auth0/Entra JWT flow, with a customisable auth provider.
 */
class Auth0AuthHandler(
    authProvider: JWTAuthProvider,
    requiredClaims: Set<String>,
    private val skip: String? = null,
) : AuthenticationHandlerImpl<JWTAuthProvider>(authProvider) {

    // NOTE: We take a copy of the required claims to make sure they can't be modified after being passed in.
    private val requiredClaims = requiredClaims.toSet()

    override fun authenticate(context: RoutingContext): Future<User> {
        val promise = Promise.promise<User>()

        // Check if this route has been excluded from auth.
        if (context.shouldSkipRoute()) {
            context.next()
            return Future.succeededFuture()
        }

        // parse the request in order to extract the credentials object
        context.parseCredentials()
            .onSuccess { credentials ->
                if (credentials == null) {
                    // A success with no credentials indicates that auth isn't wanted, so just complete with no authenticated user.
                    promise.complete()
                } else {
                    // proceed to authN
                    authProvider.authenticate(credentials).onSuccess { authenticated ->
                        promise.complete(authenticated)
                    }.onFailure { cause ->
                        when (cause) {
                            is HttpException -> promise.fail(cause)
                            else -> promise.fail(HttpException(401, cause))
                        }
                    }
                }
            }.onFailure {
                promise.fail(it)
            }

        return promise.future()
    }

    override fun postAuthentication(ctx: RoutingContext) {
        // Check if this route has been excluded from auth.
        if (ctx.shouldSkipRoute())
            return

        val user = ctx.user()
        when {
            requiredClaims.isEmpty() -> super.postAuthentication(ctx) // No auth required
            user == null -> ctx.fail(403, VertxException("No user was found, you must authenticate first", true))
            else -> {
                val token = user.attributes().getValue("token") as DecodedJWT
                val resp = JWTAuthoriser.authorise(token, requiredClaims)
                if (resp.statusCode !== StatusCode.OK) {
                    ctx.fail(resp.statusCode.code, VertxException(resp.message, true))
                    return
                }
                super.postAuthentication(ctx)
            }
        }
    }

    private fun RoutingContext.parseCredentials(): Future<Credentials?> {
        // Check if this route has been excluded from auth.
        if (shouldSkipRoute()) {
            next()
            return Future.succeededFuture()
        }

        val promise = Promise.promise<Credentials>()

        getBearerToken()
            .onSuccess { promise.complete(TokenCredentials(it)) }
            .onFailure { promise.fail(it) }

        return promise.future()
    }

    private fun RoutingContext.getBearerToken(): Future<String> {
        val request = request()
        val authorization = request.headers()[HttpHeaders.AUTHORIZATION]
            ?: return Future.failedFuture(HttpException(401, "Missing Authorization header"))

        return try {
            val idx = authorization.indexOf(' ')
            when {
                idx <= 0 -> Future.failedFuture(HttpException(400, "Badly formed Authorization header"))
                authorization.substring(0, idx) != "Bearer" -> Future.failedFuture(HttpException(401, "Missing Bearer token from Authorization header"))
                else -> Future.succeededFuture(authorization.substring(idx + 1))
            }
        } catch (e: RuntimeException) {
            Future.failedFuture(e)
        }
    }

    private fun RoutingContext.shouldSkipRoute(): Boolean =
        (skip != null) && normalizedPath().startsWith(skip)

}
