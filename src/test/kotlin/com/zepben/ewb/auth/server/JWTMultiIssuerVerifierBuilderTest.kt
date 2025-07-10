/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwk.Jwk
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Verification
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.security.interfaces.RSAPublicKey

class JWTMultiIssuerVerifierBuilderTest {

    private val trustedIssuerOne = mockk<TrustedIssuer> {
        every { issuerDomain } returns "one"
    }

    private val trustedIssuerTwo = mockk<TrustedIssuer> {
        every { issuerDomain } returns "two"
    }

    private val trustedIssuers = listOf(
        trustedIssuerOne,
        trustedIssuerTwo
    )

    private val jwkHolder = mockk<JWKHolder>()

    @Test
    fun `getVerifier throws on missing iss`() {
        val underTest = JWTMultiIssuerVerifierBuilder(
            requiredAudience = "aud",
            trustedIssuers = trustedIssuers,
            jwkHolder = jwkHolder,
            verifyCertificates = true
        )

        val token = mockk<DecodedJWT> {
            every { issuer } returns null
        }
        expect {
            underTest.getVerifier(token)
        }.toThrow<InvalidClaimException>().withMessage("Unknown or untrusted issuer: null")

        verifySequence {
            trustedIssuerOne.issuerDomain
            token.issuer
            trustedIssuerTwo.issuerDomain
            token.issuer
            token.issuer
            jwkHolder wasNot called
        }
    }

    @Test
    fun `getVerifier throws on unknown issuer`() {
        val underTest = JWTMultiIssuerVerifierBuilder(
            requiredAudience = "aud",
            trustedIssuers = trustedIssuers,
            jwkHolder = jwkHolder,
            verifyCertificates = true
        )

        val token = mockk<DecodedJWT> {
            every { issuer } returns "three"
        }
        expect {
            underTest.getVerifier(token)
        }.toThrow<InvalidClaimException>().withMessage("Unknown or untrusted issuer: three")

        verifySequence {
            trustedIssuerOne.issuerDomain
            token.issuer
            trustedIssuerTwo.issuerDomain
            token.issuer
            token.issuer
            jwkHolder wasNot called
        }
    }

    @Test
    fun `getVerifier throws on missing empty trustedIssuers`() {
        val underTest = JWTMultiIssuerVerifierBuilder(
            requiredAudience = "aud",
            trustedIssuers = emptyList(),
            jwkHolder = jwkHolder,
            verifyCertificates = true
        )

        val token = mockk<DecodedJWT> {
            every { issuer } returns "three"
        }
        expect {
            underTest.getVerifier(token)
        }.toThrow<InvalidClaimException>().withMessage("Unknown or untrusted issuer: three")

        verifySequence {
            token.issuer
            jwkHolder wasNot called
        }
    }

    @Test
    fun `getVerifier passes the correct things to JWTVerifier builder`() {
        val underTest = JWTMultiIssuerVerifierBuilder(
            requiredAudience = "aud",
            trustedIssuers = trustedIssuers,
            jwkHolder = jwkHolder,
            verifyCertificates = true
        )

        val token = mockk<DecodedJWT> {
            every { issuer } returns "two"
            every { keyId } returns "keyId_27"
        }

        val publicKey = mockk<RSAPublicKey>()

        val jwk = mockk<Jwk>().also {
            every { it.publicKey } returns publicKey
        }

        every { jwkHolder.getKeyFromJwk("keyId_27", trustedIssuerTwo) } returns jwk

        val alg = mockk<Algorithm>()

        val verifier = mockk<com.auth0.jwt.JWTVerifier>()

        val verification = mockk<Verification> {
            every { withAudience("aud") } returns this@mockk
            every { withIssuer("two") } returns this@mockk
            every { acceptLeeway(60) } returns this@mockk
            every { build() } returns verifier
        }

        mockkStatic(Algorithm::class) {
            every { Algorithm.RSA256(any(), null) } returns alg
            mockkStatic(JWT::class) {
                every { JWT.require(alg) } returns verification
                assertThat(underTest.getVerifier(token), equalTo(verifier))
            }
        }

        verifySequence {
            trustedIssuerOne.issuerDomain
            token.issuer
            trustedIssuerTwo.issuerDomain
            token.issuer
            token.keyId
            jwkHolder.getKeyFromJwk("keyId_27", trustedIssuerTwo)
            jwk.publicKey
            Algorithm.RSA256(publicKey, null)
            JWT.require(alg)
            verification.withAudience("aud")
            trustedIssuerTwo.issuerDomain
            verification.withIssuer("two")
            verification.acceptLeeway(60)
            verification.build()
            alg wasNot called
        }
    }
}
