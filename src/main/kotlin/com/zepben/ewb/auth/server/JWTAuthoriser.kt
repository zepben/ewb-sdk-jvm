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
import org.slf4j.LoggerFactory

object JWTAuthoriser {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val claimChecks = listOf("permissions", "roles")

    /**
     * Authorise a JWT.
     * This function will check that a JWT has the required claims. The claims will be extracted from "permissions" (Auth0) or "roles" (EntraID), if "permissions" field is missing.
     *
     * @param token The JWT
     * @param requiredClaim The claim to authorise.
     */
    @JvmStatic
    fun authorise(token: DecodedJWT, requiredClaim: String): AuthResponse =
        authorise(token, setOf(requiredClaim))

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

        val permissions = claimChecks.firstNotNullOfOrNull {
            token.getClaim(it).asList(String::class.java)
        }.orEmpty().toSet()

        if (permissions.intersect(requiredClaims).size == requiredClaims.size)
            return AuthResponse(StatusCode.OK)

        if (logger.isDebugEnabled)
            logger.debug("Token was missing a required claim. Had [${permissions.joinToString(", ")}] but needed [${requiredClaims.joinToString(", ")}]")

        // NOTE: We deliberately drop the actual claims from the response to prevent security leaks to the client.
        return AuthResponse(StatusCode.UNAUTHENTICATED, "Token was missing a required claim.")
    }

}
