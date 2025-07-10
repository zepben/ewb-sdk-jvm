/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import java.security.interfaces.RSAPublicKey


class JWTMultiIssuerVerifierBuilder(
    private val requiredAudience: String,
    private val trustedIssuers: List<TrustedIssuer>,
    private val verifyCertificates: Boolean,
    internal val jwkHolder: JWKHolder = JWKHolder(verifyCertificates)
) {
    fun getVerifier(token: DecodedJWT): JWTVerifier {
        val issuer = trustedIssuers.firstOrNull { it.issuerDomain == token.issuer }
            ?: throw InvalidClaimException("Unknown or untrusted issuer: ${token.issuer}")

        // Get the key ID for the key that was used to sign this key, and look it up against our stored keys.
        val rsaKey = jwkHolder.getKeyFromJwk(token.keyId, issuer)
        val rsaAlg = Algorithm.RSA256(rsaKey.publicKey as RSAPublicKey?, null)

        return JWT.require(rsaAlg)
            .withAudience(requiredAudience)
            .withIssuer(issuer.issuerDomain)
            .acceptLeeway(60) // Extend valid window by 60 seconds in both directions
            .build()
    }
}
