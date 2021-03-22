/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.zepben.testutils.auth.TOKEN
import com.zepben.testutils.auth.TOKEN_EXPIRED
import com.zepben.testutils.exception.ExpectException
import io.grpc.CallCredentials
import io.grpc.CallCredentials.MetadataApplier
import io.grpc.Metadata
import io.grpc.Status
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.net.ServerSocket
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread


class JwtCredentialsTest {

    private val port = ServerSocket(0).use { socket -> socket.localPort }
    private val wireMock: WireMockServer = WireMockServer(options().port(port).notifier(ConsoleNotifier(true)))
    private val address
        get() = "http://localhost:$port"

    @BeforeEach
    fun setUp() {
        wireMock.start()
    }

    @AfterEach
    fun tearDown() {
        wireMock.resetAll()
        wireMock.stop()
    }

    @Test
    fun `construction fails if one of confAddress or config isn't set`() {
        ExpectException.expect {
            EvolveCallCredentials.create("a", "b")
        }.toThrow(AuthException::class.java).withMessage("One of confAddress or config must be provided.")

        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "AUTH0"
                }
            """.trimIndent()
        }
        var cred = EvolveCallCredentials.create("a", "b", "$address/auth")
        assertThat(cred.authConfig.audience, equalTo("https://test-audience/"))
        assertThat(cred.authConfig.tokenUrl, equalTo("http://localhost:$port/test"))
        assertThat(cred.authConfig.authType, equalTo(AuthType.AUTH0))

        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "NONE"
                }
            """.trimIndent()
        }
        cred = JwtCredentials("a", "b", authConfig = AuthConfig("http://localhost:$port/test", "https://test-audience/", AuthType.NONE))
        assertThat(cred.authConfig.audience, equalTo("https://test-audience/"))
        assertThat(cred.authConfig.tokenUrl, equalTo("http://localhost:$port/test"))
        assertThat(cred.authConfig.authType, equalTo(AuthType.NONE))
    }

    @Test
    fun `get token returns null if authType is none`() {
        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "NONE"
                }
            """.trimIndent()
        }

        val cred = EvolveCallCredentials.create("test_client", "test_secret", "$address/auth")
        cred.applyRequestMetadata(mock(CallCredentials.RequestInfo::class.java), { it.run() }, object : MetadataApplier() {
            override fun apply(headers: Metadata) {
                assertThat(headers.get(AUTHORISATION_METADATA_KEY), nullValue())
            }

            override fun fail(status: Status?) {
            }
        })
    }

    @Test
    fun `token is passed in headers`() {
        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "AUTH0"
                }
            """.trimIndent()
        }

        wireMock.post {
            url equalTo "/test"
        }.returnsJson {
            body = """
                {
                  "token_type": "test_type",
                  "access_token": "$TOKEN"
                }
            """.trimIndent()
        }

        val cred = EvolveCallCredentials.create("test_client", "test_secret", "$address/auth")
        cred.applyRequestMetadata(mock(CallCredentials.RequestInfo::class.java), { it.run() }, object : MetadataApplier() {
            override fun apply(headers: Metadata) {
                assertThat(headers.get(AUTHORISATION_METADATA_KEY), equalTo("test_type $TOKEN"))
            }

            override fun fail(status: Status?) {
                org.junit.jupiter.api.fail("should not be called")
            }
        })
    }

    @Test
    fun `expired token calls refresh`() {
        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "AUTH0"
                }
            """.trimIndent()
        }

        wireMock.post {
            url equalTo "/test"
        }.returnsJson {
            body = """
                {
                  "token_type": "test_type",
                  "access_token": "$TOKEN_EXPIRED"
                }
                """.trimIndent()
            toState = "called_once"
        }

        wireMock.post {
            url equalTo "/test"
            whenState = "called_once"
        }.returnsJson {
            body = """
                {
                  "token_type": "test_type",
                  "access_token": "$TOKEN"
                }
                """.trimIndent()
        }

        val cred = EvolveCallCredentials.create("test_client", "test_secret", "$address/auth")
        cred.refreshToken()
        cred.applyRequestMetadata(mock(CallCredentials.RequestInfo::class.java), { it.run() }, object : MetadataApplier() {
            override fun apply(headers: Metadata) {
                assertThat(headers.get(AUTHORISATION_METADATA_KEY), equalTo("test_type $TOKEN"))
            }

            override fun fail(status: Status?) {
                org.junit.jupiter.api.fail("should not be called")
            }
        })
    }

    @Test
    fun `error response is correctly propagated`() {
        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "AUTH0"
                }
            """.trimIndent()
        }

        wireMock.post {
            url equalTo "/test"
        }.returnsJson {
            body = """
                {
                  "error": "test error",
                  "error_description": "could not fetch a token"
                }
                """.trimIndent()
        }
        val cred = EvolveCallCredentials.create("test_client", "test_secret", "$address/auth")
        cred.applyRequestMetadata(mock(CallCredentials.RequestInfo::class.java), { it.run() }, object : MetadataApplier() {
            override fun apply(headers: Metadata) {
                org.junit.jupiter.api.fail("should not be called")
            }

            override fun fail(status: Status) {
                assertThat(status.code, equalTo(Status.UNAUTHENTICATED.code))
                assertThat(status.cause, instanceOf(AuthException::class.java))
                assertThat(status.description, equalTo("test error: could not fetch a token"))
            }
        })
    }

    @Test
    fun `concurrent requests dont cause multiple refreshes of the token`() {
        wireMock.get {
            url equalTo "/auth"
        }.returnsJson {
            body = """
                {
                  "audience": "https://test-audience/",
                  "issuer": "http://localhost:$port/test",
                  "authType": "AUTH0"
                }
            """.trimIndent()
        }

        wireMock.post {
            url equalTo "/test"
        }.returnsJson {
            body = """
                {
                  "token_type": "type1",
                  "access_token": "$TOKEN"
                }
                """.trimIndent()
            Thread.sleep(TimeUnit.SECONDS.toMillis(1))
            toState = "called"
        }

        wireMock.post {
            url equalTo "/test"
            whenState = "called"
        }.returnsJson {
            body = """
                {
                  "token_type": "type2",
                  "access_token": "$TOKEN_EXPIRED"
                }
                """.trimIndent()
            toState = null
        }

        val called = AtomicInteger()
        val cred = EvolveCallCredentials.create("test_client", "test_secret", "$address/auth")
        val threads = mutableListOf<Thread>()
        for (i in 0..10) {
            threads.add(thread {
                cred.applyRequestMetadata(mock(CallCredentials.RequestInfo::class.java), { it.run() }, object : MetadataApplier() {
                    override fun apply(headers: Metadata) {
                        assertThat(headers.get(AUTHORISATION_METADATA_KEY), equalTo("type1 $TOKEN"))
                        called.incrementAndGet()
                    }

                    override fun fail(status: Status) {
                        org.junit.jupiter.api.fail("should not be called ${status.description}")
                    }
                })
            })
        }
        threads.forEach {
            it.join()
        }
        assertThat(called.get(), equalTo(11))
    }

}
