/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.client

import com.auth0.jwt.JWT
import com.zepben.ewb.auth.common.AuthException
import com.zepben.ewb.auth.common.AuthMethod
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.CONTENT_TYPE
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant

/**
 * Fetches access tokens from an authentication provider using the OAuth 2.0 protocol.
 *
 * @property audience Audience to use when requesting tokens.
 * @property tokenEndpoint The token endpoint of the issuer; used to fetch the token.
 * @property authMethod The authentication method used by the server.
 * @property tokenRequestData Data to pass in token requests.
 * @property refreshRequestData Data to pass in refresh token requests.
 * @property client HTTP client used to retrieve tokens. Defaults to HttpClient.newHttpClient().
 * @property refreshToken Refresh Token; will be used if defined (fetched previously).
 */
class ZepbenTokenFetcher(
    val audience: String,
    val tokenEndpoint: String,
    val authMethod: AuthMethod = AuthMethod.OAUTH,
    val tokenRequestData: JsonObject = JsonObject(),
    val refreshRequestData: JsonObject = JsonObject(),
    private val client: HttpClient = HttpClient.newHttpClient(),
    private var refreshToken: String? = null,
    val requestBuilder: (String, String) -> HttpRequest = { issuerURL, body ->
        HttpRequest.newBuilder()
            .uri(URI(issuerURL))
            .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
    }
) {

    private var accessToken: String? = null
    private var tokenExpiry: Instant = Instant.MIN
    private var tokenType: String? = null

    /**
     * Create a ZepbenTokenFetcher with the option of turning off certificate verification for the token provider.
     *
     * @property audience Audience to use when requesting tokens.
     * @property tokenEndpoint The token endpoint of the issuer; used to fetch the token.
     * @property authMethod The authentication method used by the server.
     * @property verifyCertificate Whether to verify the SSL certificate of the token provider when making requests.
     * @property tokenRequestData Data to pass in token requests.
     * @property refreshRequestData Data to pass in refresh token requests.
     */
    constructor(
        audience: String,
        tokenEndpoint: String,
        authMethod: AuthMethod,
        verifyCertificate: Boolean,
        tokenRequestData: JsonObject = JsonObject(),
        refreshRequestData: JsonObject = JsonObject(),
    ) : this(
        audience = audience,
        tokenEndpoint = tokenEndpoint,
        authMethod = authMethod,
        tokenRequestData = tokenRequestData,
        refreshRequestData = refreshRequestData,
        client = if (verifyCertificate) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build()
    )

    /**
     * Create a ZepbenTokenFetcher that uses a given CA to verify the token provider.
     *
     * @property audience Audience to use when requesting tokens.
     * @property tokenEndpoint The token endpoint of the issuer; used to fetch the token.
     * @property authMethod The authentication method used by the server.
     * @property caFilename Filename of X.509 CA certificate used to verify HTTPS responses from token service.
     * @property tokenRequestData Data to pass in token requests.
     * @property refreshRequestData Data to pass in refresh token requests.
     */
    constructor(
        audience: String,
        tokenEndpoint: String,
        authMethod: AuthMethod,
        caFilename: String?,
        tokenRequestData: JsonObject = JsonObject(),
        refreshRequestData: JsonObject = JsonObject(),
    ) : this(
        audience = audience,
        tokenEndpoint = tokenEndpoint,
        authMethod = authMethod,
        tokenRequestData = tokenRequestData,
        refreshRequestData = refreshRequestData,
        client = caFilename?.let {
            HttpClient.newBuilder().sslContext(SSLContextUtils.singleCACertSSLContext(caFilename)).build()
        } ?: HttpClient.newHttpClient()
    )

    init {
        when (authMethod) {
            AuthMethod.AUTH0 -> {
                tokenRequestData.put("audience", audience)
                refreshRequestData.put("audience", audience)
            }

            AuthMethod.ENTRAID -> {
                tokenRequestData.put("scope", "${audience}/.default")
                refreshRequestData.put("scope", "${audience}/.default")
            }

            else -> {}
        }
    }

    /**
     * Returns a JWT access token and its type in the form of '<type> <3 part JWT>', retrieved from the configured
     * OAuth2 token provider. Throws an Exception if an access token request fails.
     */
    fun fetchToken(): String {
        if (Instant.now() > tokenExpiry) {
            // Stored token has expired, try to refresh
            accessToken = null
            if (!refreshToken.isNullOrEmpty()) {
                fetchOAuthToken(useRefresh = true)
            }

            if (accessToken == null) {
                // If using the refresh token did not work for any reason, self.accessToken will still be None.
                // and thus we must try to get a fresh access token using credentials instead.
                fetchOAuthToken()
            }

            if (tokenType.isNullOrEmpty() or accessToken.isNullOrEmpty()) {
                throw Exception(
                    "Token couldn't be retrieved from ${URL(tokenEndpoint)} using " +
                        "audience: $audience, token endpoint: $tokenEndpoint"
                )
            }
        }

        return "$tokenType $accessToken"
    }

    private fun fetchOAuthToken(useRefresh: Boolean = false) {
        // createBody will take the Json object and create a query params string out of it
        // as "key=value&key=value&key=value..."
        val createBody: (JsonObject) -> String = { json -> json.joinToString("&") { m -> "${m.key}=${m.value}" } }

        // Generate request body
        val body = if (useRefresh) {
            refreshRequestData.put("refresh_token", refreshToken)
            createBody(refreshRequestData)
        } else createBody(tokenRequestData)

        val request = requestBuilder(tokenEndpoint, body)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != StatusCode.OK.code) {
            throw AuthException(
                response.statusCode(),
                "Token fetch failed, Error was: ${response.statusCode()} - ${response.body()}"
            )
        }

        val data: JsonObject
        try {
            data = Json.decodeValue(response.body()) as JsonObject
        } catch (e: DecodeException) {
            throw AuthException(
                response.statusCode(),
                "Response did not contain valid JSON - response was: ${response.body()}"
            )
        } catch (e: ClassCastException) {
            throw AuthException(
                response.statusCode(),
                "Response was not a JSON object - response was: ${response.body()}"
            )
        }

        if (data.containsKey("error") or !data.containsKey("access_token")) {
            throw AuthException(
                response.statusCode(),
                (data.getString("error") ?: "Access Token absent in token response") + " - " +
                    (data.getString("error_description") ?: "Response was: $data")
            )
        }

        tokenType = data.getString("token_type")
        accessToken = data.getString("access_token")
        tokenExpiry = JWT.decode(accessToken).getClaim("exp")?.asDate()?.toInstant() ?: Instant.MIN

        if (useRefresh) {
            refreshToken = data.getString("refresh_token")
        }
    }

}

/**
 * Helper method to construct auth related configuration from `issuer` and `audience` and create a `ZepbenTokenFetcher`.
 *
 * @param issuer The issuer for fetching the provider params and the token.
 * @param audience The audience for fetching the provider params and the token.
 * @param verifyCertificates Whether to verify the SSL certificate when making requests.
 * @param authClient HTTP client used to retrieve tokens.
 *
 * @returns: A `ZepbenTokenFetcher` if the server reported authentication was configured, otherwise None.
 */
fun createTokenFetcher(
    authMethod: AuthMethod,
    issuer: String,
    audience: String,
    authClient: HttpClient? = null,
    verifyCertificates: Boolean,
): ZepbenTokenFetcher {

    val client =
        authClient ?: if (verifyCertificates) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext())
            .build()
    val config = AuthProviderConfig(
        authMethod = authMethod,
        issuer = issuer,
        audience = audience,
        providerDetails = fetchProviderDetails(issuer = issuer, httpClientCreator = { client })
    )

    return ZepbenTokenFetcher(
        authMethod = config.authMethod,
        audience = config.audience,
        tokenEndpoint = config.providerDetails.tokenEndpoint,
        client = client,
    )
}

/**
 * Helper method to fetch auth related configuration from `confAddress` and create a `ZepbenTokenFetcher`.
 *
 * @param confAddress Location to retrieve authentication configuration from. Must be a HTTP address that returns a JSON response.
 * @param confClient HTTP client used to retrieve authentication configuration.
 * @param authClient HTTP client used to retrieve tokens.
 * @param audienceField The field name to look up in the JSON response from the confAddress for `audience`.
 * @param issuerField The field name to look up in the JSON response from the confAddress for `issuer`.
 *
 * @returns: A `ZepbenTokenFetcher` if the server reported authentication was configured, otherwise None.
 */
fun createTokenFetcher(
    confAddress: String,
    confClient: HttpClient,
    authClient: HttpClient,
    audienceField: String = "audience",
    issuerField: String = "issuer",
): ZepbenTokenFetcher {
    val config = createProviderConfig(
        httpClientCreator = { confClient },
        confAddress = confAddress,
        audienceField = audienceField,
        issuerField = issuerField,
    )

    return ZepbenTokenFetcher(
        authMethod = config.authMethod,
        audience = config.audience,
        tokenEndpoint = config.providerDetails.tokenEndpoint,
        client = authClient,
    )
}

/**
 * Helper method to fetch auth related configuration from `confAddress` and create a `ZepbenTokenFetcher`.
 * You may use `verififyCertificates` to specify whether to verify the certificates for the domains serving the
 * authentication configuration and the authentication provider.
 *
 * @param confAddress Location to retrieve authentication configuration from. Must be a HTTP address that returns a JSON response.
 * @param verifyCertificates: Whether to verify the certificate when making HTTPS requests. Note you should only use a trusted server
 *                            and never set this to False in a production environment.
 * @param audienceField The field name to look up in the JSON response from the confAddress for `audience`.
 * @param issuerField The field name to look up in the JSON response from the confAddress for `issuer`.
 *
 * @returns: A `ZepbenTokenFetcher` if the server reported authentication was configured, otherwise None.
 */
fun createTokenFetcher(
    confAddress: String,
    verifyCertificates: Boolean,
    audienceField: String = "audience",
    issuerField: String = "issuer",
) = createTokenFetcher(
    confAddress = confAddress,
    confClient = if (verifyCertificates) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build(),
    authClient = if (verifyCertificates) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build(),
    audienceField = audienceField,
    issuerField = issuerField
)

/**
 * Helper method to fetch auth related configuration from `confAddress` and create a `ZepbenTokenFetcher`.
 * You may use `confCAFilename` and `authCAFilename` to specify the CAs used to verify the certificates for the domains serving the
 * authentication configuration and the authentication provider.
 *
 * @param confAddress Location to retrieve authentication configuration from. Must be a HTTP address that returns a JSON response.
 * @param confCAFilename Filename of X.509 CA certificate used to verify HTTPS responses from configuration service. Leave as null to use system CAs.
 * @param authCAFilename Filename of X.509 CA certificate used to verify HTTPS responses from token service. Leave as null to use system CAs.
 * @param audienceField The field name to look up in the JSON response from the confAddress for `audience`.
 * @param issuerField The field name to look up in the JSON response from the confAddress for `issuer`.
 * @param verifyCertificates Whether to verify the certificate when making HTTPS requests. Note you should only use a trusted server
 *                            and never set this to False in a production environment.
 *
 * @returns: A `ZepbenTokenFetcher` if the server reported authentication was configured, otherwise None.
 */
fun createTokenFetcher(
    confAddress: String,
    confCAFilename: String? = null,
    authCAFilename: String? = null,
    audienceField: String = "audience",
    issuerField: String = "issuer",
    verifyCertificates: Boolean = true,
) = createTokenFetcher(
    confAddress = confAddress,
    confClient = confCAFilename?.let {
        HttpClient.newBuilder().sslContext(SSLContextUtils.singleCACertSSLContext(it)).build()
    } ?: if (verifyCertificates) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build(),
    authClient = authCAFilename?.let {
        HttpClient.newBuilder().sslContext(SSLContextUtils.singleCACertSSLContext(it)).build()
    } ?: if (verifyCertificates) HttpClient.newHttpClient() else HttpClient.newBuilder().sslContext(SSLContextUtils.allTrustingSSLContext()).build(),
    audienceField = audienceField,
    issuerField = issuerField,
)

/**
 * Create a token fetcher which uses an EntraID (Azure) managed identity to fetch tokens from a well known token provider endpoint.
 *
 * @param identityUrl The URL to use for fetching a token. Typically a well known URL like:
 * http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01&resource=<SOME_RESOURCE_ID>
 */
fun createTokenFetcherManagedIdentity(identityUrl: String): ZepbenTokenFetcher =
    ZepbenTokenFetcher(audience = "", tokenEndpoint = "", authMethod = AuthMethod.OAUTH, requestBuilder = { _, _ ->
        HttpRequest.newBuilder()
            .uri(URI(identityUrl))
            .header("Metadata", "true")
            .GET()
            .build()
    })
