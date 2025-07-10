/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.zepben.ewb.auth.client.ProviderDetails
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class TrustedIssuerTest {

    @Test
    fun lazyFetchWorksAsExpected() {
        var runCount = 0
        val underTest = TrustedIssuer(issuerDomain = "real.cool.au") { input -> runCount++; ProviderDetails("$input plus token path", "$input/jwks.json") }

        assertThat(runCount, equalTo(0))
        val providerDetails = underTest.providerDetails
        assertThat(runCount, equalTo(1))
        assertThat(providerDetails.tokenEndpoint, equalTo("real.cool.au plus token path"))
        assertThat(providerDetails.jwkUrl, equalTo("real.cool.au/jwks.json"))
        assertThat(underTest.issuerDomain, equalTo("real.cool.au"))

        //confirm that the details are only fetched once
        assertThat(underTest.providerDetails.tokenEndpoint, equalTo("real.cool.au plus token path"))
        assertThat(underTest.providerDetails.jwkUrl, equalTo("real.cool.au/jwks.json"))
        assertThat(underTest.issuerDomain, equalTo("real.cool.au"))
        assertThat(runCount, equalTo(1))
    }

    @Test
    fun secondaryConstructorWorksAsExpected() {
        val underTest = TrustedIssuer(issuerDomain = "real.cool.au", ProviderDetails("caller.provided/token", "caller.aye/other/jwks.json"))

        assertThat(underTest.providerDetails.tokenEndpoint, equalTo("caller.provided/token"))
        assertThat(underTest.providerDetails.jwkUrl, equalTo("caller.aye/other/jwks.json"))
        assertThat(underTest.issuerDomain, equalTo("real.cool.au"))
    }
}
