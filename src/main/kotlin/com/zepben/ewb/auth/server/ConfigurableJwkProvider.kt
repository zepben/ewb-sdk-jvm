/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.SigningKeyNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import com.zepben.ewb.auth.client.SSLContextUtils
import com.zepben.ewb.auth.common.StatusCode
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * This class exists to allow us to configure the HTTP client used when fetching Jwks keys.
 *
 * There are cases in both local dev and customer environments where we need to fetch keys from endpoints that are signed by self-signed certificates.
 * Unfortunately, the [com.auth0.jwk.UrlJwkProvider] does not provide a way to patch the underlying client.
 *
 * This code looks very Kotlin and is sourced from that class. This is because in order to convert the Jwks response into the [Jwk] object, we need to use
 * the [Jwk.fromValues] function. This function requires a Map<String, Object> to work, so the logic from the Auth0 implementation was used.
 */
class ConfigurableJwkProvider(
    private val url: URL,
    verifyCertificates: Boolean,
    val httpClientCreator: () -> HttpClient = {
        if (!verifyCertificates) {
            HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build()
        } else {
            HttpClient.newBuilder().build()
        }
    }
) : JwkProvider {
    val allKeys by lazy { getAll() }

    private fun getAll(): List<Jwk> {
        val client = httpClientCreator()
        val request = HttpRequest.newBuilder()
            .uri(url.toURI())
            .header(CONTENT_TYPE, "application/json")
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != StatusCode.OK.code) {
            throw SigningKeyNotFoundException("Cannot obtain jwks from url $url", null)
        }

        val reader = ObjectMapper().readerFor(MutableMap::class.java)
        val thing = reader.readValue<Map<String, Any>>(response.body())

        @Suppress("UNCHECKED_CAST")
        val keys = thing["keys"] as List<Map<String, Any>>

        return keys.map { Jwk.fromValues(it) }
    }

    override fun get(keyId: String?): Jwk {
        return allKeys.firstOrNull { it.id == keyId } ?: throw SigningKeyNotFoundException("No key found in $url with kid $keyId", null)
    }
}
