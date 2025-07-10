/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.client

import java.io.FileInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SSLContextUtils {
    /**
     * Trust manager that does not check certificates.
     */
    private val allTrustingTrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

    /**
     * Used to override default HTTPS security for HttpsClient.
     */
    fun allTrustingSSLContext(): SSLContext {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(emptyArray(), arrayOf(allTrustingTrustManager), SecureRandom())
        return sslContext
    }

    /**
     * Make SSLContext that trusts a single X.509 CA certificate.
     */
    fun singleCACertSSLContext(caCertFilename: String): SSLContext {
        val cf = CertificateFactory.getInstance("X.509")
        val caCert = cf.generateCertificates(FileInputStream(caCertFilename))

        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null) // Initialise to empty keystore
        caCert.forEachIndexed { i, cert ->
            ks.setCertificateEntry("caCert$i", cert)
        }

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(ks)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(emptyArray(), tmf.trustManagers, SecureRandom())
        return sslContext
    }
}
