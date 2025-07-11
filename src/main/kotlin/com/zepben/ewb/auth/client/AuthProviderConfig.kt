/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.client

import com.zepben.ewb.auth.common.AuthException
import com.zepben.ewb.auth.common.AuthMethod
import com.zepben.ewb.auth.common.StatusCode
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

data class ProviderDetails(
    val tokenEndpoint: String,
    val jwkUrl: String
)

data class AuthProviderConfig(
    val issuer: String,
    val audience: String,
    val authMethod: AuthMethod = AuthMethod.OAUTH,
    val providerDetails: ProviderDetails = ProviderDetails("", "")
)

/**
 * Helper method to fetch the provider-related auth configuration
 *
 * @param issuer Location to retrieve the configuration from. Must be a HTTP address that returns a JSON response.
 * @param verifyCertificates If server certificates should be verified or not
 * @param httpClientCreator A function that returns a configured [HttpClient]
 * @param handler an HTTP handler used to handle the HTTP response.
 *
 * @returns: A `ProviderDetails` if successfully contacted the issuer, error otherwise
 */
fun fetchProviderDetails(
    issuer: String,
    verifyCertificates: Boolean = true,
    httpClientCreator: () -> HttpClient = {
        if (!verifyCertificates) {
            HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build()
        } else {
            HttpClient.newBuilder().build()
        }
    },
    handler: HttpResponse.BodyHandler<String> = HttpResponse.BodyHandlers.ofString()
): ProviderDetails {
    val issuerURL = "${issuer.trimEnd('/')}/.well-known/openid-configuration"
    val response: HttpResponse<String> = httpClientCreator().send(HttpRequest.newBuilder().uri(URI(issuerURL)).GET().build(), handler)
    if (response.statusCode() == StatusCode.OK.code) {
        try {
            val authConfigJson = Json.decodeValue(response.body()) as JsonObject
            return ProviderDetails(
                tokenEndpoint = authConfigJson.getString("token_endpoint", ""),
                jwkUrl = authConfigJson.getString("jwks_uri", ""),
            )

        } catch (e: DecodeException) {
            throw AuthException(
                response.statusCode(),
                "Expected JSON response from $issuerURL, but got: ${response.body()}."
            )
        } catch (e: ClassCastException) {
            throw AuthException(
                response.statusCode(),
                "Expected JSON object from $issuerURL, but got: ${response.body()}."
            )
        }
    } else {
        throw AuthException(
            response.statusCode(),
            "$issuerURL responded with error: ${response.statusCode()} - ${response.body()}"
        )
    }
}

/**
 * Helper method to fetch auth related configuration from `confAddress` and create a `ZepbenTokenFetcher`.
 *
 * @param confAddress Location to retrieve authentication configuration from. Must be a HTTP address that returns a JSON response.
 * @param httpClientCreator A function that returns a configured [HttpClient]
 * @param verifyCertificates If server certificates should be verified or not
 * @param handler an HTTP handler used to handle the HTTP response.
 * @param audienceField The field name to look up in the JSON response from the confAddress for `audience`.
 * @param issuerField The field name to look up in the JSON response from the confAddress for `issuer`.
 *
 * @returns: A `AuthProviderConfig` with `ProviderDetails` automatically populated if no errors, an error otherwise.
 */
fun createProviderConfig(
    confAddress: String,
    verifyCertificates: Boolean = true,
    httpClientCreator: () -> HttpClient = {
        if (!verifyCertificates) {
            HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build()
        } else {
            HttpClient.newBuilder().build()
        }
    },
    handler: HttpResponse.BodyHandler<String> = HttpResponse.BodyHandlers.ofString(),
    authTypeField: String = "authType",
    audienceField: String = "audience",
    issuerField: String = "issuer"
): AuthProviderConfig {

    // Fetch the auth data from EWB
    val response = try {
        // Try with https first
        httpClientCreator().send(HttpRequest.newBuilder().uri(URI(confAddress)).GET().build(), handler)
    } catch (e: IOException) {
        throw AuthException(1, "Fetching auth configuration from $confAddress failed, check you've provided the full URL with correct protocol")
    }

    return response?.let {
        if (response.statusCode() == StatusCode.OK.code) {
            try {
                val authConfigJson = Json.decodeValue(response.body()) as JsonObject
                val issuer = authConfigJson.getString(issuerField, "")
                val authMethod = AuthMethod.valueOf(authConfigJson.getString(authTypeField, "none").uppercase())

                if (authMethod == AuthMethod.NONE) {
                    throw AuthException(
                        1,
                        "Detected Auth set to NONE, this is not supported for fetching tokens! Check your configuration matches $confAddress"
                    )
                }

                AuthProviderConfig(
                    authMethod = authMethod,
                    issuer = issuer,
                    audience = authConfigJson.getString(audienceField, ""),
                    providerDetails = fetchProviderDetails(issuer, verifyCertificates, httpClientCreator, handler)
                )
            } catch (e: DecodeException) {
                throw AuthException(
                    response.statusCode(),
                    "Expected JSON response from $confAddress, but got: ${response.body()}."
                )
            } catch (e: ClassCastException) {
                throw AuthException(
                    response.statusCode(),
                    "Expected JSON object from $confAddress, but got: ${response.body()}."
                )
            }
        } else {
            throw AuthException(
                response.statusCode(),
                "$confAddress responded with error: ${response.statusCode()} - ${response.body()}"
            )
        }
    } ?: throw AuthException(1, "Unexpected error while attempting to fetch auth details from $confAddress")
}
