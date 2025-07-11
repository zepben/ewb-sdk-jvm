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
import com.zepben.ewb.auth.client.ProviderDetails
import com.zepben.testutils.exception.ExpectException
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.URL

class JWKHolderTest {

    private val jwk33 = mockk<Jwk>()
    private val jwk5006 = mockk<Jwk>()
    private val jwkCommonOne = mockk<Jwk>()
    private val jwkCommonTwo = mockk<Jwk>()

    private val trustedIssuerOne = mockk<TrustedIssuer> {
        every { issuerDomain } returns "one"
    }

    private val trustedIssuerTwo = mockk<TrustedIssuer> {
        every { issuerDomain } returns "two"
    }

    private val jwkProvider = mockk<(TrustedIssuer) -> Map<String, Jwk>>().also {
        every { it(trustedIssuerOne) } returns mapOf("keyId_33" to jwk33, "keyId_5006" to jwk5006, "common_key_id" to jwkCommonOne)
        every { it(trustedIssuerTwo) } returns mapOf("common_key_id" to jwkCommonTwo)
    }

    private val holderUnderTest = JWKHolder(true, jwkProvider)

    @Test
    fun `JWKHolder refreshes keys from issuer if kid not found in cache`() {
        assertThat(holderUnderTest.getKeyFromJwk("keyId_33", trustedIssuerOne), equalTo(jwk33))

        validateKeyRequests(
            listOf(
                KeyRequestCheck(trustedIssuerOne, jwkProvider, true),
            )
        )
    }

    @Test
    fun `JWKHolder takes key from cache if found`() {
        assertThat(holderUnderTest.getKeyFromJwk("keyId_33", trustedIssuerOne), equalTo(jwk33))
        assertThat(holderUnderTest.getKeyFromJwk("keyId_33", trustedIssuerOne), equalTo(jwk33))

        validateKeyRequests(
            listOf(
                KeyRequestCheck(trustedIssuerOne, jwkProvider, true),
                KeyRequestCheck(trustedIssuerOne, jwkProvider, false),
            )
        )
    }

    @Test
    fun `JWKHolder handles kid collision`() {

        assertThat(holderUnderTest.getKeyFromJwk("common_key_id", trustedIssuerOne), equalTo(jwkCommonOne))
        assertThat(holderUnderTest.getKeyFromJwk("common_key_id", trustedIssuerTwo), equalTo(jwkCommonTwo))
        assertThat(holderUnderTest.getKeyFromJwk("common_key_id", trustedIssuerOne), equalTo(jwkCommonOne))

        validateKeyRequests(
            listOf(
                KeyRequestCheck(trustedIssuerOne, jwkProvider, true),
                KeyRequestCheck(trustedIssuerTwo, jwkProvider, true),
                KeyRequestCheck(trustedIssuerOne, jwkProvider, false),
            )
        )
    }

    @Test
    fun `JWKHolder handles no keys returned`() {
        every { jwkProvider(trustedIssuerOne) } returns emptyMap()

        ExpectException.expect {
            holderUnderTest.getKeyFromJwk("keyId_34", trustedIssuerOne)
        }.toThrow<JwkException>().withMessage("Unable to find key keyId_34 in jwk endpoint. Check your JWK URL.")
        validateKeyRequest(trustedIssuerOne, jwkProvider, true)
    }

    @Test
    fun `JWKHolder throws on unable to find after refreshing cache`() {
        ExpectException.expect {
            holderUnderTest.getKeyFromJwk("keyId_34", trustedIssuerOne)
        }.toThrow<SigningKeyNotFoundException>().withMessage("Unable to find key keyId_34 in jwk endpoint. Check your JWK URL.")

        validateKeyRequest(trustedIssuerOne, jwkProvider, true)
    }

    data class KeyRequestCheck(
        val trustedIssuer: TrustedIssuer,
        val provider: (TrustedIssuer) -> Map<String, Jwk>,
        val expectCacheRefresh: Boolean
    )

    private fun validateKeyRequests(requests: List<KeyRequestCheck>) {
        excludeRecords {
            requests.forEach {
                it.trustedIssuer.equals(any()) //this is from the answer selection... https://github.com/mockk/mockk/issues/577
            }
        }

        verifySequence {
            requests.forEach {
                it.trustedIssuer.issuerDomain
                if (it.expectCacheRefresh) {
                    it.trustedIssuer.issuerDomain
                    it.provider(it.trustedIssuer)
                    it.trustedIssuer.issuerDomain
                }
            }
        }
    }

    private fun validateKeyRequest(trustedIssuer: TrustedIssuer, jwkProvider: (TrustedIssuer) -> Map<String, Jwk>, expectCacheRefresh: Boolean) {
        validateKeyRequests(listOf(KeyRequestCheck(trustedIssuer, jwkProvider, expectCacheRefresh)))
    }

    @Test
    fun trustedIssuerUrlJwkProviderTest() {
        val keysUrlRaw = "https://keys/"

        val providerDetails = mockk<ProviderDetails> {
            every { this@mockk.jwkUrl } returns keysUrlRaw
        }
        val issuer = mockk<TrustedIssuer> {
            every { this@mockk.providerDetails } returns providerDetails
        }

        val key1 = mockk<Jwk> { every { id } returns "1" }
        val key2 = mockk<Jwk> { every { id } returns "2" }
        val key3 = mockk<Jwk> { every { id } returns "3" }
        val key4 = mockk<Jwk> { every { id } returns "4" }
        val key5 = mockk<Jwk> { every { id } returns "5" }
        val key6 = mockk<Jwk> { every { id } returns "6" }

        val expectedResult = mapOf(
            "1" to key1,
            "2" to key2,
            "3" to key3,
            "4" to key4,
            "5" to key5,
            "6" to key6,
        )

        val returnedKeys = listOf(key1, key2, key3, key4, key5, key6)
        val mockJwkProvider = mockk<ConfigurableJwkProvider> {
            every { allKeys } returns returnedKeys
        }

        val keysUrl = URI(keysUrlRaw).toURL()

        val mockUrlJwkProviderProvider = mockk<(URL) -> ConfigurableJwkProvider> {
            every { this@mockk(keysUrl) } returns mockJwkProvider
        }

        assertThat(trustedIssuerUrlJwkProvider(issuer, urlJwkProviderProvider = mockUrlJwkProviderProvider), equalTo(expectedResult))

        verifySequence {
            issuer.providerDetails
            providerDetails.jwkUrl
            mockUrlJwkProviderProvider.invoke(keysUrl)
            mockJwkProvider.allKeys
        }
    }
}
