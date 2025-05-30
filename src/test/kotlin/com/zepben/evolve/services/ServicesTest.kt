/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services

import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ServicesTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val expectedNs = NetworkService()
    private val expectedDs = DiagramService()
    private val expectedCs = CustomerService()

    private val services = Services(expectedNs, expectedDs, expectedCs)

    @Test
    internal fun accessors() {
        assertThat(services.networkService, sameInstance(expectedNs))
        assertThat(services.diagramService, sameInstance(expectedDs))
        assertThat(services.customerService, sameInstance(expectedCs))
    }

    @Test
    internal fun `supports destructuring`() {
        services.also { (ns, ds, cs) ->
            assertThat(ns, sameInstance(expectedNs))
            assertThat(ds, sameInstance(expectedDs))
            assertThat(cs, sameInstance(expectedCs))
        }
    }

}
