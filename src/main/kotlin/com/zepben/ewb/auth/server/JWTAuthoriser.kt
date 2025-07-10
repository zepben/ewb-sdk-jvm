/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.server

import com.auth0.jwt.interfaces.DecodedJWT
import com.zepben.ewb.auth.common.StatusCode

object JWTAuthoriser {

    /**
     * Authorise a JWT.
     * This function will check that a JWT has the required claims. The claims will be extracted from "permissions" (Auth0) or "roles" (EntraID), if "permissions" field is missing.
     *
     * @param token The JWT
     * @param requiredClaim The claim to authorise.
     */
    @JvmStatic
    fun authorise(token: DecodedJWT, requiredClaim: String): AuthResponse {
        return authorise(token, setOf(requiredClaim))
    }

    /**
     * Authorise a JWT.
     * This function will check that a JWT has all the [requiredClaims]. The claims will be extracted from "permissions" (Auth0) or "roles" (EntraID), if "permissions" field is missing.
     *
     * @param token The JWT
     * @param requiredClaims The claims to authorise. If empty all tokens will be authorised.
     */
    @JvmStatic
    fun authorise(token: DecodedJWT, requiredClaims: Set<String>): AuthResponse {
        if (requiredClaims.isEmpty())
            return AuthResponse(StatusCode.OK)
        val permissions = run {
            token.getClaim("permissions").asList(String::class.java) ?: token.getClaim("roles").asList(String::class.java) ?: emptyList()
        }.toHashSet()
        if (permissions.intersect(requiredClaims).size == requiredClaims.size)
            return AuthResponse(StatusCode.OK)
        return AuthResponse(
            StatusCode.UNAUTHENTICATED,
            "Token was missing a required claim. Had [${permissions.joinToString(", ")}] but needed [${requiredClaims.joinToString(", ")}]"
        )
    }
}
