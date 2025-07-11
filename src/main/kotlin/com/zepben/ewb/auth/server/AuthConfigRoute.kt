/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.zepben.ewb.auth.common.AuthMethod
import com.zepben.vertxutils.routing.Respond
import com.zepben.vertxutils.routing.Route
import com.zepben.vertxutils.routing.RouteVersion
import com.zepben.vertxutils.routing.VersionableRoute
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

private data class AuthConfigResponse(
    val authType: AuthMethod,
    val issuer: String,
    val audience: String
)

fun routeFactory(
    availableRoute: AvailableRoute,
    audience: String,
    issuer: String,
    authType: AuthMethod = AuthMethod.OAUTH
): Route =
    when (availableRoute) {
        AvailableRoute.AUTH_CONFIG ->
            Route.builder()
                .method(HttpMethod.GET)
                .path("/auth")
                .addHandler(AuthConfigRoute(audience, issuer, authType))
                .build()

    }

enum class AvailableRoute(private val rv: RouteVersion) : VersionableRoute {
    AUTH_CONFIG(RouteVersion.since(2));

    override fun routeVersion(): RouteVersion {
        return rv
    }
}

class AuthConfigRoute(audience: String, issuer: String, authType: AuthMethod) : Handler<RoutingContext> {

    private val json: JsonObject =
        JsonObject.mapFrom(AuthConfigResponse(authType = authType, audience = audience, issuer = issuer))

    override fun handle(event: RoutingContext) {
        Respond.withJson(event, HttpResponseStatus.OK, json.encode())
    }
}
