/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.customer

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CustomerServiceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val service = CustomerService()

    @Test
    internal fun supportsCustomer() {
        val customer = Customer()
        assertThat(service.add(customer), equalTo(true))
        assertThat(service.remove(customer), equalTo(true))
    }

    @Test
    internal fun supportsCustomerAgreement() {
        val customerAgreement = CustomerAgreement()
        assertThat(service.add(customerAgreement), equalTo(true))
        assertThat(service.remove(customerAgreement), equalTo(true))
    }

    @Test
    internal fun supportsOrganisation() {
        val organisation = Organisation()
        assertThat(service.add(organisation), equalTo(true))
        assertThat(service.remove(organisation), equalTo(true))
    }

    @Test
    internal fun supportsPricingStructure() {
        val pricingStructure = PricingStructure()
        assertThat(service.add(pricingStructure), equalTo(true))
        assertThat(service.remove(pricingStructure), equalTo(true))
    }

    @Test
    internal fun supportsTariff() {
        val tariff = Tariff()
        assertThat(service.add(tariff), equalTo(true))
        assertThat(service.remove(tariff), equalTo(true))
    }
}
