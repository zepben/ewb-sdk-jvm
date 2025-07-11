/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwk.JwkException
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.interfaces.DecodedJWT
import com.zepben.ewb.auth.common.AuthException
import com.zepben.ewb.auth.common.StatusCode
import io.vertx.ext.web.handler.HttpException
import org.slf4j.LoggerFactory

const val CONTENT_TYPE = "Content-Type"

data class AuthResponse(
    val statusCode: StatusCode,
    val message: String? = null,
    val cause: Throwable? = null,
    val token: DecodedJWT? = null
)

fun AuthResponse.asException(): AuthException = AuthException(statusCode.code, message)
fun AuthResponse.asHttpException(): HttpException = HttpException(statusCode.code, message)

interface TokenAuthenticator {
    fun authenticate(token: String?): AuthResponse
}

/**
 * A TokenAuthenticator that authenticates JWTs using a retrievable JWK
 *
 * @property audience The audience required for the token to be authenticated.
 * @property trustedIssuers List of domains hosting JWKS's.
 * @property verifierBuilder A [JWTMultiIssuerVerifierBuilder] used for constructing a [JWTVerifier] for authenticating JWTs.
 */
open class JWTAuthenticator(
    audience: String,
    trustedIssuers: List<TrustedIssuer>,
    verifyCertificates: Boolean = true,
    private val verifierBuilder: JWTMultiIssuerVerifierBuilder = JWTMultiIssuerVerifierBuilder(
        requiredAudience = audience,
        trustedIssuers = trustedIssuers,
        verifyCertificates = verifyCertificates
    )
) : TokenAuthenticator {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun authenticate(token: String?): AuthResponse =
        if (token.isNullOrEmpty()) {
            AuthResponse(StatusCode.UNAUTHENTICATED, "No token was provided")
        } else {
            try {
                val decoded = JWT.decode(token)
                val verifier = verifierBuilder.getVerifier(decoded)
                verifier.verify(decoded)

                AuthResponse(StatusCode.OK, token = decoded)
            } catch (je: JWTDecodeException) {
                AuthResponse(StatusCode.UNAUTHENTICATED, je.message, je)
            } catch (alg: AlgorithmMismatchException) {
                AuthResponse(StatusCode.UNAUTHENTICATED, alg.message, alg)
            } catch (sig: SignatureVerificationException) {
                AuthResponse(StatusCode.UNAUTHENTICATED, sig.message, sig)
            } catch (exp: TokenExpiredException) {
                AuthResponse(StatusCode.UNAUTHENTICATED, exp.message, exp)
            } catch (claim: InvalidClaimException) {
                AuthResponse(StatusCode.PERMISSION_DENIED, claim.message, claim)
            } catch (i: IllegalArgumentException) {
                AuthResponse(StatusCode.MALFORMED_TOKEN, i.message, i)
            } catch (j: JwkException) {
                AuthResponse(StatusCode.UNAUTHENTICATED, j.message, j)
            }
        }
}
