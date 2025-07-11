/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkException
import com.auth0.jwk.SigningKeyNotFoundException
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.*
import com.zepben.ewb.auth.client.ProviderDetails
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.JWTAuthoriser.authorise
import com.zepben.testutils.auth.*
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test

fun createAuthenticator(aud: String, issuer: String): JWTAuthenticator {
    return JWTAuthenticator(
        aud, listOf(TrustedIssuer(issuer, ProviderDetails("dunno", "https://whereileftmy/keys/"))),
        verifierBuilder = JWTMultiIssuerVerifierBuilder(
            aud,
            listOf(TrustedIssuer(issuer, ProviderDetails("dunno", "https://whereileft.my/keys/"))),
            true,
            JWKHolder(true) { _ -> MockJwksUrlProvider().all.associateBy { it.id } })
    )
}

class JWTAuthenticatorTest {

    @Test
    fun testAuth() {
        var ta = createAuthenticator("https://fake-aud/", "https://issuer/")

        var authResp = ta.authenticate(TOKEN)
        assertThat(authResp.statusCode, equalTo(StatusCode.OK))
        val successfulToken = authResp.token!!
        authResp = authorise(successfulToken, "write:network")
        assertThat(authResp.statusCode, equalTo(StatusCode.OK))

        authResp = authorise(successfulToken, "bacon")
        assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
        assertThat(
            authResp.message,
            equalTo("Token was missing a required claim. Had [read:network, read:ewb, write:metrics, write:network] but needed [bacon]")
        )

        authResp = ta.authenticate("broken")
        assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
        assertThat(authResp.cause, instanceOf(JWTDecodeException::class.java))

        authResp = ta.authenticate(TOKEN_RS512)
        assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
        assertThat(authResp.cause, instanceOf(AlgorithmMismatchException::class.java))

        authResp = ta.authenticate(TOKEN_BAD_SIG)
        assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
        assertThat(authResp.cause, instanceOf(SignatureVerificationException::class.java))

        authResp = ta.authenticate(TOKEN_EXPIRED)
        assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
        assertThat(authResp.cause, instanceOf(TokenExpiredException::class.java))

        ta = createAuthenticator("https://wrong-aud/", "https://issuer/")

        authResp = ta.authenticate(TOKEN)
        assertThat(authResp.statusCode, equalTo(StatusCode.PERMISSION_DENIED))
        assertThat(authResp.cause, instanceOf(InvalidClaimException::class.java))
        assertThat(authResp.message, equalTo("The Claim 'aud' value doesn't contain the required audience."))

        ta = createAuthenticator("https://fake-aud/", "wrong-issuer")

        authResp = ta.authenticate(TOKEN)
        assertThat(authResp.statusCode, equalTo(StatusCode.PERMISSION_DENIED))
        assertThat(authResp.cause, instanceOf(InvalidClaimException::class.java))
        assertThat(authResp.message, equalTo("Unknown or untrusted issuer: https://issuer/"))
    }

    @Test
    fun `authenticator returns unauthenticated on JwkExceptions`() {
        val ta = createAuthenticator("https://fake-aud/", "https://issuer/")

        mockkStatic(JWT::class) {
            every { JWT.decode(any()) } throws SigningKeyNotFoundException("some message", null)

            val authResp = ta.authenticate(TOKEN)
            assertThat(authResp.statusCode, equalTo(StatusCode.UNAUTHENTICATED))
            assertThat(authResp.cause, instanceOf(SigningKeyNotFoundException::class.java))
            assertThat(authResp.message, equalTo("some message"))
        }
    }

    @Test
    fun `authenticator throws exception on unhandled error`() {
        val ta = createAuthenticator("https://fake-aud/", "https://issuer/")

        mockkStatic(JWT::class) {
            every { JWT.decode(any()) } throws Exception("some message")

            expect {
                ta.authenticate(TOKEN)
            }.toThrow<Exception>()
        }
    }

    @Test
    fun `keys are updated when unknown key is provided`() {
        val jwk = Jwk("fakekid", "RSA", "RS256", "", emptyList(), "", emptyList(), "", attribs)
        val mockJWK = mockk<UrlJwkProvider> {
            every { all } returns listOf(jwk)
        }

        val ta = JWTMultiIssuerVerifierBuilder(
            "https://fake-aud/", listOf(TrustedIssuer("https://issuer/", ProviderDetails("dunno", "https://whereileftmy/keys/"))), true,
            JWKHolder(true) { _ -> mockJWK.all.associateBy { it.id } })
        assertThat(ta.jwkHolder.getKeyFromJwk("fakekid", TrustedIssuer("ignored_for_now", ProviderDetails("", ""))), equalTo(jwk))

        expect {
            ta.jwkHolder.getKeyFromJwk("fakekey", TrustedIssuer("ignored_for_now", ProviderDetails("", "")))
        }.toThrow(JwkException::class.java)
            .withMessage("Unable to find key fakekey in jwk endpoint. Check your JWK URL.")
    }


}
