/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services

import com.zepben.ewb.annotations.ZepbenExperimental
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.VariantService
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ServicesTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val expectedNs = NetworkService()
    private val expectedDs = DiagramService()
    private val expectedCs = CustomerService()
    private val expectedVs = VariantService()
    private val expectedCds = DiagramService()

    @OptIn(ZepbenExperimental::class)
    private val services = Services(expectedNs, expectedDs, expectedCs, expectedVs, expectedCds)

    @OptIn(ZepbenExperimental::class)
    @Test
    internal fun accessors() {
        assertThat(services.networkService, sameInstance(expectedNs))
        assertThat(services.diagramService, sameInstance(expectedDs))
        assertThat(services.customerService, sameInstance(expectedCs))
        assertThat(services.variantService, sameInstance(expectedVs))
        assertThat(services.customerDiagramService, sameInstance(expectedCds))
    }

    @Test
    internal fun `supports destructuring`() {
        services.also { (ns, ds, cs, vs, cds) ->
            assertThat(ns, sameInstance(expectedNs))
            assertThat(ds, sameInstance(expectedDs))
            assertThat(cs, sameInstance(expectedCs))
            assertThat(vs, sameInstance(expectedVs))
            assertThat(cds, sameInstance(expectedCds))
        }
    }

    @Test
    internal fun whichAll() {
        expectedNs.add(Organisation("mrid-1"))
        expectedCs.add(Organisation("mrid-1"))

        assertThat(services.whichAll("mrid-1"), contains(expectedNs, expectedCs))
    }

}
